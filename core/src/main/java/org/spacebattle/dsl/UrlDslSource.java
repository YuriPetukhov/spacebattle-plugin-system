package org.spacebattle.dsl;

import java.io.InputStream;
import java.net.URL;

/**
 * Источник данных DSL, основанный на {@link URL}.
 * Позволяет загружать DSL-описания (например, YAML) по сетевому адресу или из classpath.
 */
public class UrlDslSource implements DslSource {
    private final URL url;

    /**
     * Создаёт источник DSL из URL.
     *
     * @param url URL к ресурсу DSL
     */
    public UrlDslSource(URL url) {
        this.url = url;
    }

    /**
     * Открывает поток ввода из указанного URL.
     *
     * @return {@link InputStream} с содержимым DSL-файла
     * @throws Exception если не удаётся открыть поток
     */
    @Override
    public InputStream openStream() throws Exception {
        return url.openStream();
    }
}
