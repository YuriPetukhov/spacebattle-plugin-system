package org.spacebattle.exceptions;

/**
 * Выбрасывается, если зависимость не найдена в IoC.
 */
public class DependencyNotFoundException extends RuntimeException {
    public DependencyNotFoundException(String key) {
        super("Dependency not found: " + key);
    }
}