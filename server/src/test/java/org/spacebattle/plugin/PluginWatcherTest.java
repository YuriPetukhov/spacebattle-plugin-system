package org.spacebattle.plugin;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PluginWatcherTest {

    @TempDir
    Path tempDir;

    @AfterEach
    void cleanupPluginsDir() throws Exception {
        tempDir.toFile().deleteOnExit();
    }

    @Test
    public void shouldReturnTrueIfFileSizeStabilizes() throws Exception {
        File tempFile = File.createTempFile("testPlugin", ".jar");
        FileWriter writer = new FileWriter(tempFile);
        writer.write("dummy data");
        writer.close();

        PluginWatcher watcher = new PluginWatcher(null, null);
        boolean ready = watcher.waitForFileReady(tempFile, 2);

        assertTrue(ready);
        tempFile.deleteOnExit();
    }

    @Test
    public void shouldReturnFalseIfFileKeepsGrowing() throws Exception {
        File tempFile = File.createTempFile("testPlugin", ".jar");
        PluginWatcher watcher = new PluginWatcher(null, null);

        // Пишем в файл с паузами в другом потоке
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try (FileWriter writer = new FileWriter(tempFile)) {
                for (int i = 0; i < 10; i++) {
                    writer.write("more data\n");
                    writer.flush();
                    Thread.sleep(300); // <--- Пауза меньше чем 500мс итерации в waitForFileReady
                }
            } catch (Exception ignored) {}
        });

        // Ждём чуть меньше времени, чем поток записи закончит свою работу
        boolean ready = watcher.waitForFileReady(tempFile, 2); // макс. 2 сек = 4 итерации

        executor.shutdownNow();
        tempFile.deleteOnExit();

        assertFalse(ready, "Файл должен считаться нестабильным, т.к. продолжается запись");
    }

    @Test
    void testWatchService_detectsNewPluginFileAndLoadsIt(@TempDir Path tempDir) throws Exception {
        File pluginsDir = tempDir.toFile();

        PluginLoader pluginLoader = mock(PluginLoader.class);

        // WatchService с реальным событием
        PluginWatcher watcher = new PluginWatcher(pluginsDir, pluginLoader) {
            @Override
            public boolean waitForFileReady(File f, int maxAttempts) {
                return true; // Не ждём
            }
        };

        // Запускаем наблюдатель
        Thread watcherThread = new Thread(watcher::run);
        watcherThread.setDaemon(true);
        watcherThread.start();

        // ДАЁМ watcher'у ЗАРЕГИСТРИРОВАТЬСЯ
        Thread.sleep(300);

        // Создаём новый .jar файл (имитируем плагин)
        File jarFile = new File(pluginsDir, "plugin.jar");
        Files.write(jarFile.toPath(), "dummy plugin content".getBytes());

        // Даём немного времени на обработку
        Thread.sleep(1000);

        // Проверка вызова загрузки
        verify(pluginLoader, timeout(2000)).loadPlugin(argThat(file ->
                file.getName().equals("plugin.jar")));

        watcherThread.interrupt();
    }

}
