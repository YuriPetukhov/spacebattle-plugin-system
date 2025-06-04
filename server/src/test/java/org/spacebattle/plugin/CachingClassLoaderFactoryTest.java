package org.spacebattle.plugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URLClassLoader;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

public class CachingClassLoaderFactoryTest {

    private CachingClassLoaderFactory factory;
    private File jarFile1;
    private File jarFile2;

    @BeforeEach
    void setUp() throws Exception {
        factory = new CachingClassLoaderFactory();
        jarFile1 = new File("hello-plugin-1.0-SNAPSHOT.jar");
        jarFile2 = new File("auth-plugin-1.0-SNAPSHOT.jar");

        if (!jarFile1.exists()) jarFile1.createNewFile();
        if (!jarFile2.exists()) jarFile2.createNewFile();
    }

    @AfterEach
    void tearDown() {
        if (jarFile1.exists()) jarFile1.delete();
        if (jarFile2.exists()) jarFile2.delete();
    }

    @Test
    void shouldCreateNewClassLoaderForJarFile() throws Exception {
        URLClassLoader cl1 = factory.create(jarFile1);
        assertNotNull(cl1);
    }

    @Test
    void shouldCacheClassLoaderForSameFile() throws Exception {
        URLClassLoader cl1 = factory.create(jarFile1);
        URLClassLoader cl2 = factory.create(jarFile1);

        assertSame(cl1, cl2, "ClassLoaders должны быть одинаковыми для одного и того же файла");
    }

    @Test
    void shouldReturnDifferentClassLoadersForDifferentFiles() throws Exception {
        URLClassLoader cl1 = factory.create(jarFile1);
        URLClassLoader cl2 = factory.create(jarFile2);

        assertNotSame(cl1, cl2, "ClassLoaders должны отличаться для разных файлов");
    }
}
