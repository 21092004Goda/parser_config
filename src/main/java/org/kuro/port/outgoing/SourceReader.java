package org.kuro.port.outgoing;

import org.kuro.core.domain.Configuration;

import java.io.IOException;
import java.util.Optional;

public interface SourceReader {
    Optional<Configuration> readConfiguration(String path, int id) throws IOException;
}
