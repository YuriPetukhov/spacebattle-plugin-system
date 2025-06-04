package org.spacebattle.security;

import java.io.File;
import java.security.PublicKey;

public interface PluginValidator {
    void validate(File jarFile) throws SecurityException;
}