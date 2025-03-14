package org.kuro.core.application;

import org.kuro.core.domain.Configuration;
import org.kuro.core.domain.ModeType;
import org.kuro.exceptions.ApplicationException;
import org.kuro.exceptions.model.ErrorCode;
import org.kuro.exceptions.special.ConfigurationException;
import org.kuro.exceptions.special.FileException;
import org.kuro.exceptions.special.ProcessingException;
import org.kuro.port.outgoing.ConfigReader;
import org.kuro.port.outgoing.FileReader;
import org.kuro.port.outgoing.SourceWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class ConfigService {
    private final SourceWriter writer;
    private final FileReader fileReader;
    private final ConfigReader configReader;

    public ConfigService(SourceWriter writer, FileReader fileReader, ConfigReader configReader) {
        this.writer = writer;
        this.fileReader = fileReader;
        this.configReader = configReader;
    }


    public String processConfig(String configFile, int configId) {
        Configuration config = configReader.readConfiguration(configFile, configId)
                .orElseThrow(() -> new ConfigurationException(
                        ErrorCode.CONFIGURATION_NOT_FOUND
                ));


        List<String> filePaths;

        try {
            if (config.getMode().equals(ModeType.DIRECTORIES)) {
                filePaths = Files.list(Path.of(config.getPath().getFirst()))
                        .filter(Files::isRegularFile)
                        .map(Path::toString)
                        .toList();
            } else {
                filePaths = config.getPath();
            }
        } catch (IOException e) {
            throw new FileException(
                    ErrorCode.FILE_READ_ERROR,
                    config.getPath().toString(),
                    e
            );
        }

        List<List<String>> fileContents = fileReader.readFiles(filePaths);
        Map<Integer, Map<Integer, String>> result = fileReader.processFiles(fileContents, config.getAction());

        Map<String, Object> outputData = new LinkedHashMap<>();
        outputData.put("configFile", configFile);
        outputData.put("configurationID", configId);
        outputData.put("configurationData", Map.of("mode", config.getMode(), "path", config.getPath()));
        outputData.put("out", result);

        Path projectRoot = Paths.get("").toAbsolutePath();
        Path resultDir = projectRoot.resolve("result");

        try {
            if (Files.notExists(resultDir)) {
                Files.createDirectories(resultDir);
            }
        } catch (IOException e) {
            throw new FileException(
                    ErrorCode.DIRECTORY_CREATION_ERROR,
                    result.toString(),
                    e
            );
        }

        Path filePath = resultDir.resolve("output_config_" + configId + ".json");

        return writer.saveTo(outputData, filePath.toString());
    }
}
