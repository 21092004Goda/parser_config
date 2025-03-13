package org.kuro.adapters.outgoing;

import org.kuro.core.domain.Configuration;
import org.kuro.core.domain.ModeType;
import org.kuro.core.domain.ActionType;
import org.kuro.exceptions.model.ErrorCode;
import org.kuro.exceptions.special.ConfigurationException;
import org.kuro.exceptions.special.FileException;
import org.kuro.port.outgoing.SourceReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FileReaderImpl implements SourceReader {

    @Override
    public Optional<Configuration> readConfiguration(String filePath, int configId) {
        try {
            List<String> lines = Files.readAllLines(Path.of(filePath));

            int id = -1;
            ModeType mode = null;
            List<String> path = null;
            ActionType action = null;

            boolean isReadingConfig = false;

            for (String line : lines) {
                String trimmedLine = line.trim();

                if (trimmedLine.startsWith("#id:")) {
                    int currentId = Integer.parseInt(trimmedLine.substring(4).trim());
                    if (currentId == configId) {
                        isReadingConfig = true;
                        id = currentId;
                    } else {
                        isReadingConfig = false;
                    }
                }

                if (isReadingConfig) {
                    if (trimmedLine.startsWith("#mode:")) {
                        try {
                            mode = ModeType.valueOf(trimmedLine.substring(6).trim().toUpperCase());
                        } catch (IllegalArgumentException e) {
                            throw new ConfigurationException(
                                    ErrorCode.CONFIGURATION_INVALID,
                                    "Invalid mode: " + trimmedLine.substring(6).trim(),
                                    e
                            );
                        }
                    }
                    else if (trimmedLine.startsWith("#path:")) {
                        path = Arrays.stream(trimmedLine.substring(6).trim().split(",")).toList();
                    }
                    else if (trimmedLine.startsWith("#action:")) {
                        try {
                            action = ActionType.valueOf(trimmedLine.substring(8).trim().toUpperCase());
                        } catch (IllegalArgumentException e) {
                            throw new ConfigurationException(
                                    ErrorCode.CONFIGURATION_INVALID,
                                    "Invalid action: " + trimmedLine.substring(8).trim(),
                                    e
                            );
                        }
                    }
                    else if (trimmedLine.isEmpty()) {
                        break;
                    }
                }
            }

            if (id != -1 && mode != null && path != null && action != null) {
                return Optional.of(new Configuration(id, mode, path, action));
            } else {
                throw new ConfigurationException(
                        ErrorCode.CONFIGURATION_NOT_FOUND,
                        "Configuration with ID " + configId + " is incomplete");
            }
        } catch (NoSuchFileException e) {
            throw new FileException(
                    ErrorCode.FILE_NOT_FOUND,
                    "Configuration file not found: " + filePath,
                    filePath,
                    e
            );
        } catch (IOException e) {
            throw new FileException(
                    ErrorCode.FILE_READ_ERROR,
                    "Error reading configuration file: " + e.getMessage(),
                    filePath,
                    e
            );
        }
    }
}
