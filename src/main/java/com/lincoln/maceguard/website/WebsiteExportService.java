package com.lincoln.maceguard.website;

import com.google.gson.Gson;
import com.lincoln.maceguard.MaceGuardPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/** Game state is captured by a sync task; database/network work runs off-thread. */
public final class WebsiteExportService implements AutoCloseable {
    private final MaceGuardPlugin plugin;
    private final Gson gson = new Gson();
    private volatile boolean closed;
    private BukkitTask task;
    private CompletableFuture<?> pending;
    private HttpClient client;
    private java.util.concurrent.ThreadPoolExecutor readerPool;
    private URI endpoint;
    private String secret;
    public WebsiteExportService(MaceGuardPlugin plugin) { this.plugin = plugin; }
    public void start() {
        File file = new File(plugin.getDataFolder(), "website-export.yml");
        try {
            if (!file.exists()) plugin.saveResource("website-export.yml", false);
            var settings = new YamlConfiguration(); settings.load(file);
            if (!settings.getBoolean("enabled", false)) return;
            endpoint = WebsiteSignedExport.endpoint(settings.getString("endpoint", ""));
            String variable = settings.getString("secret-environment-variable", "COMMUNITY_INGEST_SECRET");
            if (!variable.matches("[A-Za-z_][A-Za-z0-9_]*")) throw new IllegalArgumentException();
            secret = System.getenv(variable);
            if (secret == null || secret.length() < 32) throw new IllegalArgumentException();
            int seconds = settings.getInt("interval-seconds", 300);
            if (seconds < 60 || seconds > 3600) throw new IllegalArgumentException();
            client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5))
                    .followRedirects(HttpClient.Redirect.NEVER).build();
            readerPool = new java.util.concurrent.ThreadPoolExecutor(1,1,0L,java.util.concurrent.TimeUnit.SECONDS,
                    new java.util.concurrent.ArrayBlockingQueue<>(1),work -> {
                        Thread thread=new Thread(work,"maceguard-website-reader");thread.setDaemon(true);return thread;
                    });
            task = plugin.getServer().getScheduler().runTaskTimer(plugin, this::publish, 20L, seconds * 20L);
        } catch (Exception error) {
            plugin.getLogger().warning("Website export disabled; check configuration and its secret environment variable.");
            close();
        }
    }
    private void publish() {
        if (closed || pending != null && !pending.isDone() || readerPool.getActiveCount()>0) return;
        var container = plugin.runtime();
        if (container == null || container.warzone() == null) return;
        var runtime = container.warzone().runtime();
        if (runtime == null || !runtime.config().enabled() || !runtime.region().fullyResolved()) return;
        long now = System.currentTimeMillis(), week = PublicWarzoneSnapshot.weekStart(now);
        Map<String, Object> snapshot;
        try { snapshot = PublicWarzoneSnapshot.capture(runtime, now); }
        catch (RuntimeException error) { plugin.getLogger().warning("Website rotation capture unavailable."); return; }
        var duels = plugin.getServer().getPluginManager().getPlugin("WarzoneDuels");
        Method method = null;
        if (duels != null && duels.isEnabled()) {
            try { method = duels.getClass().getMethod("websiteWeeklyResults", long.class, long.class); }
            catch (ReflectiveOperationException ignored) {
                try { method=duels.getClass().getMethod("websiteWeeklyWins",long.class,long.class); }
                catch(ReflectiveOperationException absent) { /* Optional API; retain unavailable status. */ }
            }
        }
        Method reader = method;
        pending = CompletableFuture.supplyAsync(() -> {
            Object leaders = null;
            if (reader != null) {
                try { leaders = reader.invoke(duels, week, now + 1); }
                catch (ReflectiveOperationException | RuntimeException ignored) { /* Fail closed. */ }
            }
            return gson.toJson(PublicWarzoneSnapshot.withLeaders(snapshot, leaders));
        },readerPool).orTimeout(10, java.util.concurrent.TimeUnit.SECONDS).thenCompose(body -> {
            if (closed) return CompletableFuture.failedFuture(new IllegalStateException("Exporter closed"));
            var request = WebsiteSignedExport.request(endpoint, secret, body, System.currentTimeMillis(), UUID.randomUUID().toString());
            return client.sendAsync(request, HttpResponse.BodyHandlers.discarding());
        }).whenComplete((response, error) -> {
            if (!closed && (error != null || response.statusCode() < 200 || response.statusCode() >= 300))
                plugin.getLogger().warning("Website export delivery failed; retrying on the next interval.");
        });
    }
    @Override public void close() {
        closed = true;
        if (task != null) task.cancel();
        if (pending != null) pending.cancel(true);
        if (readerPool != null) readerPool.shutdownNow();
        if (client != null) client.shutdownNow();
    }
}
