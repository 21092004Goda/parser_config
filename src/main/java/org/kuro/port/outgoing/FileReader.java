package org.kuro.port.outgoing;

import org.kuro.core.domain.ActionType;

import java.util.List;
import java.util.Map;

public interface FileReader {
    List<List<String>> readFiles(List<String> paths);
    Map<Integer, Map<Integer, String>> processFiles(List<List<String>> fileContents, ActionType actionType);
}