package org.kuro.port.outgoing;

import java.util.Map;

public interface SourceWriter {
    String saveTo(Map<String, Object> data, String outputPath);
}
