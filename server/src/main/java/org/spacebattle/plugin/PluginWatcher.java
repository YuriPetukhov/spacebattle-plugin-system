package org.spacebattle.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

/**
 * Наблюдатель за директорией с плагинами. При появлении новых .jar файлов
 * пытается их загрузить через PluginLoader.
 */
public class PluginWatcher implements Runnable {

    private final File pluginsDir;
    private final PluginLoader loader;

    /**
     * @param pluginsDir директория с плагинами
     * @param loader объект, загружающий плагины
     */
    public PluginWatcher(File pluginsDir, PluginLoader loader) {
        this.pluginsDir = pluginsDir;
        this.loader = loader;
    }

    @Override
    public void run() {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            Path path = pluginsDir.toPath();
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            System.out.println("Watching for new plugins...");

            while (true) {
                WatchKey key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        Path filename = (Path) event.context();
                        if (filename.toString().endsWith(".jar")) {
                            File newJar = new File(pluginsDir, filename.toString());
                            System.out.println("New plugin detected: " + newJar.getName());

                            // Ждём завершения копирования файла
                            if (waitForFileReady(newJar, 5)) {
                                loader.loadPlugin(newJar);
                            } else {
                                System.err.println("Не удалось загрузить: файл занят или недоступен.");
                            }
                        }
                    }
                }

                key.reset();
            }
        } catch (Exception e) {
            System.err.println("PluginWatcher failed: " + e.getMessage());
        }
    }

    /**
     * Ожидает, пока файл перестанет изменяться (до maxWait секунд)
     */
    boolean waitForFileReady(File file, int maxWaitSeconds) {
        long previousLength = -1;
        int waited = 0;

        while (waited < maxWaitSeconds * 2) {
            long currentLength = file.length();

            if (currentLength > 0 && currentLength == previousLength) {
                return true;
            }

            previousLength = currentLength;
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException ignored) {}
            waited++;
        }

        return false;
    }

    protected WatchService createWatchService() throws IOException {
        return FileSystems.getDefault().newWatchService();
    }
}
