package me.mmmjjkx.fpmbungee.utils;

import me.mmmjjkx.fpmbungee.FakePlayerMakerBungee;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.logging.Level;

public class Updater {
    public Updater(int resourceId, BiConsumer<String, Boolean> consumer) {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                InputStream stream = (new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId)).openStream();

                String var4;
                try {
                    Scanner scanner = new Scanner(stream);

                    try {
                        StringBuilder builder = new StringBuilder();

                        while(scanner.hasNextLine()) {
                            builder.append(scanner.nextLine());
                        }

                        var4 = builder.toString();
                    } catch (Throwable var7) {
                        try {
                            scanner.close();
                        } catch (Throwable var6) {
                            var7.addSuppressed(var6);
                        }

                        throw var7;
                    }

                    scanner.close();
                } catch (Throwable var8) {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (Throwable var5) {
                            var8.addSuppressed(var5);
                        }
                    }

                    throw var8;
                }

                stream.close();
                return var4;
            } catch (IOException var9) {
                return null;
            }
        });

        future = within(future, 10L, TimeUnit.SECONDS);

        String ver;
        try {
            ver = future.join();
        } catch (Exception var7) {
            FakePlayerMakerBungee.getInstance().getLogger().log(Level.WARNING, "Failed to check plugin update", var7);

            ver = null;
        }

        consumer.accept(ver, ver != null);
    }

    private <T> CompletableFuture<T> within(CompletableFuture<T> future, long timeout, TimeUnit unit) {
        final CompletableFuture<T> timeoutFuture = timeoutAfter(timeout, unit);

        return future.applyToEither(timeoutFuture, t -> null);
    }

    private <T> CompletableFuture<T> timeoutAfter(long timeout, TimeUnit unit) {
        CompletableFuture<T> result = new CompletableFuture<>();
        Delayer.delayer.schedule(() -> result.completeExceptionally(new TimeoutException()), timeout, unit);
        return result;
    }

    private static final class Delayer {
        static final class DaemonThreadFactory implements ThreadFactory {
            @Override
            public Thread newThread(@Nonnull Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                t.setName("CompletableFutureDelayScheduler");
                return t;
            }
        }

        static final ScheduledThreadPoolExecutor delayer;

        static {
            (delayer = new ScheduledThreadPoolExecutor(
                    1, new DaemonThreadFactory())).
                    setRemoveOnCancelPolicy(true);
        }
    }
}
