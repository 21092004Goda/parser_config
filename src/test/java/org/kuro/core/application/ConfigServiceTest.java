package org.kuro.core.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kuro.core.domain.ActionType;
import org.kuro.core.domain.Configuration;
import org.kuro.core.domain.ModeType;
import org.kuro.exceptions.special.ConfigurationException;
import org.kuro.exceptions.special.FileException;
import org.kuro.port.outgoing.SourceReader;
import org.kuro.port.outgoing.SourceWriter;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConfigServiceTest {

    @Mock
    private SourceWriter writer;

    @Mock
    private SourceReader reader;

    private ConfigService configService;

    @BeforeEach
    void setUp() {
        configService = new ConfigService(writer, reader);
    }

    @Test
    void processConfig_withValidFilesConfig_processesSingleFiles() throws IOException {
        // Given
        String configFile = "test-config.txt";
        int configId = 1;

        Configuration config = new Configuration(
                configId,
                ModeType.FILES,
                Arrays.asList("file1.txt", "file2.txt"),
                ActionType.STRING
        );

        when(reader.readConfiguration(configFile, configId)).thenReturn(Optional.of(config));
        when(reader.readFiles(Arrays.asList("file1.txt", "file2.txt"))).thenReturn(Arrays.asList(
                Arrays.asList("file1 content"),
                Arrays.asList("file2 content")
        ));
        when(reader.processFiles(Arrays.asList(
                Arrays.asList("file1 content"),
                Arrays.asList("file2 content")
        ), ActionType.STRING)).thenReturn(Map.of(
                1, Arrays.asList("file1 content"),
                2, Arrays.asList("file2 content")
        ));

        when(writer.saveTo(any(), anyString())).thenReturn("output.json");


        // When
        String result = configService.processConfig(configFile, configId);

        // Then
        assertEquals("output.json", result);

        ArgumentCaptor<Map<String, Object>> dataCaptor = ArgumentCaptor.forClass(Map.class);
        verify(writer).saveTo(dataCaptor.capture(), anyString());

        Map<String, Object> outputData = dataCaptor.getValue();
        assertEquals(configFile, outputData.get("configFile"));
        assertEquals(configId, outputData.get("configurationID"));
    }

    @Test
    void processConfig_withConfigNotFound_throwsException() throws IOException {
        // Given
        String configFile = "test-config.txt";
        int configId = 1;

        when(reader.readConfiguration(configFile, configId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ConfigurationException.class, () -> {
            configService.processConfig(configFile, configId);
        });
    }

    @Test
    void processConfig_withInvalidPath_throwsFileException() throws IOException {
        // Given
        String configFile = "test-config.txt";
        int configId = 1;

        Configuration config = new Configuration(
                configId,
                ModeType.DIRECTORIES,
                Arrays.asList("non_existent_dir"),
                ActionType.STRING
        );

        when(reader.readConfiguration(configFile, configId)).thenReturn(Optional.of(config));

        // When & Then
        assertThrows(FileException.class, () -> {
            configService.processConfig(configFile, configId);
        });
    }
}