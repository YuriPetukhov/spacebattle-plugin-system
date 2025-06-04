package org.spacebattle.plugins.components;

public interface BootstrapHandler {
    boolean supports(Object instance);
    void handle(Object instance);
}
