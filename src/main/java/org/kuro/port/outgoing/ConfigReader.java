package org.kuro.port.outgoing;

import org.kuro.core.domain.Configuration;

import java.util.Optional;

public interface ConfigReader {
    Optional<Configuration> readConfiguration(String path, int id);
}