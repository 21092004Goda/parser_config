package org.kuro.port.outgoing;

import org.kuro.core.domain.ActionType;
import org.kuro.core.domain.Configuration;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SourceReader {
    Optional<Configuration> readConfiguration(String path, int id);
    List<List<String>> readFiles(List<String> paths);
    Map<Integer, List<String>> processFiles(List<List<String>> fileContents, ActionType actionType);
}
