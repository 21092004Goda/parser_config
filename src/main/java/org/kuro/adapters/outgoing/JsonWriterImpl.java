package org.kuro.adapters.outgoing;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kuro.exceptions.model.ErrorCode;
import org.kuro.exceptions.special.FileException;
import org.kuro.port.outgoing.SourceWriter;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class JsonWriterImpl  implements SourceWriter {
    @Override
    public String saveTo(Map<String, Object> data, String outputPath) {
        ObjectMapper mapper = new ObjectMapper();

        File outputFile = new File(outputPath);
        File parentDir = outputFile.getParentFile();

        try {
            if (parentDir != null && !parentDir.exists()) {
                boolean dirCreated = parentDir.mkdirs();
                if (!dirCreated) {
                    throw new FileException(
                            ErrorCode.DIRECTORY_CREATION_ERROR,
                            parentDir.getAbsolutePath()
                    );
                }
            }

            mapper.writerWithDefaultPrettyPrinter().writeValue(outputFile, data);
            return outputPath;

        } catch (IOException e) {
            throw new FileException(
                    ErrorCode.FILE_WRITE_ERROR,
                    outputPath,
                    e
            );
        }
    }
}
