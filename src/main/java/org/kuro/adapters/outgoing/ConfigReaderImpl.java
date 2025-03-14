package org.kuro.adapters.outgoing;

import org.kuro.core.domain.ActionType;
import org.kuro.core.domain.ModeType;
import org.kuro.exceptions.model.ErrorCode;
import org.kuro.exceptions.special.ConfigurationException;
import org.kuro.exceptions.special.FileException;
import org.kuro.port.outgoing.ConfigReader;
import org.kuro.core.domain.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ConfigReaderImpl implements ConfigReader {

    @Override
    public Optional<Configuration> readConfiguration(String filePath, int configId) {
        List<String> lines = readFileLines(filePath);
        return parseConfiguration(lines, configId);
    }

    private List<String> readFileLines(String filePath) {
        try {
            return Files.readAllLines(Path.of(filePath));
        } catch (NoSuchFileException e) {
            throw new FileException(
                    ErrorCode.FILE_NOT_FOUND,
                    filePath,
                    e
            );
        } catch (IOException e) {
            throw new FileException(
                    ErrorCode.FILE_READ_ERROR,
                    filePath,
                    e
            );
        }
    }

    private Optional<Configuration> parseConfiguration(List<String> lines, int configId) {
        int id = -1;
        ModeType mode = null;
        List<String> path = null;
        ActionType action = null;
        boolean isReadingConfig = false;

        for (String line : lines) {
            String trimmedLine = line.trim();

            if (trimmedLine.startsWith("#id:")) {
                int currentId = Integer.parseInt(trimmedLine.substring(4).trim());
                isReadingConfig = (currentId == configId);
                if (isReadingConfig) id = currentId;
            }

            if (isReadingConfig) {
                if (trimmedLine.startsWith("#mode:")) {
                    mode = parseMode(trimmedLine.substring(6).trim());
                } else if (trimmedLine.startsWith("#path:")) {
                    path = Arrays.stream(trimmedLine.substring(6).trim().split(",")).toList();
                } else if (trimmedLine.startsWith("#action:")) {
                    action = parseAction(trimmedLine.substring(8).trim());
                } else if (trimmedLine.isEmpty()) {
                    break;
                }
            }
        }

        if (id != -1 && mode != null && path != null && action != null) {
            return Optional.of(new Configuration(id, mode, path, action));
        } else {
            throw new ConfigurationException(
                    ErrorCode.CONFIGURATION_NOT_FOUND
            );
        }
    }

    private ModeType parseMode(String modeString) {
        try {
            return ModeType.valueOf(modeString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ConfigurationException(
                    ErrorCode.CONFIGURATION_INVALID,
                    e
            );
        }
    }

    private ActionType parseAction(String actionString) {
        try {
            return ActionType.valueOf(actionString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ConfigurationException(
                    ErrorCode.CONFIGURATION_INVALID,
                    e
            );
        }
    }
}
