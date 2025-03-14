package org.kuro.adapters.outgoing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.kuro.core.domain.ActionType;
import org.kuro.core.domain.Configuration;
import org.kuro.core.domain.ModeType;
import org.kuro.exceptions.special.ConfigurationException;
import org.kuro.exceptions.special.FileException;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ConfigReaderImplTest {

    private ConfigReaderImpl configReader;
    @TempDir
    Path tempDir;
    private Path configFile;

    @BeforeEach
    void setUp() {
        configReader = new ConfigReaderImpl();
    }

    @Test
    void readConfigurationWhenConfigExistShouldReturnConfiguration() throws IOException {
        // Given
        configFile = createValidConfigFile();

        // When
        Optional<Configuration> result = configReader.readConfiguration(configFile.toString(), 1);

        // Then
        assertTrue(result.isPresent());
        Configuration config = result.get();
        assertEquals(1, config.getId());
        assertEquals(ModeType.FILES, config.getMode());
        assertEquals(Arrays.asList("file1.txt", "file2.txt"), config.getPath());
        assertEquals(ActionType.STRING, config.getAction());
    }

    @Test
    void readConfigurationWhenFileDoesNotExistShouldThrowFileException() {
        // Given
        String nonExistentFile = tempDir.resolve("non-existent-file.txt").toString();

        // When & Then
        assertThrows(FileException.class, () ->
                configReader.readConfiguration(nonExistentFile, 1)
        );
    }

    @Test
    void readConfigurationWhenConfigIdDoesNotExistShouldThrowConfigurationException() throws IOException {
        // Given
        configFile = createValidConfigFile();

        // When & Then
        assertThrows(ConfigurationException.class, () ->
                configReader.readConfiguration(configFile.toString(), 99)
        );
    }

    @Test
    void readConfigurationWhenInvalidModeShouldThrowConfigurationException() throws IOException {
        // Given
        configFile = tempDir.resolve("config-invalid-mode.txt");
        List<String> lines = Arrays.asList(
                "#id: 1",
                "#mode: INVALID_MODE",
                "#path: file1.txt,file2.txt",
                "#action: STRING"
        );
        Files.write(configFile, lines);

        // When & Then
        assertThrows(ConfigurationException.class, () ->
                configReader.readConfiguration(configFile.toString(), 1)
        );
    }

    @Test
    void readConfigurationWhenInvalidActionShouldThrowConfigurationException() throws IOException {
        // Given
        configFile = tempDir.resolve("config-invalid-action.txt");
        List<String> lines = Arrays.asList(
                "#id: 1",
                "#mode: FILES",
                "#path: file1.txt,file2.txt",
                "#action: INVALID_ACTION"
        );
        Files.write(configFile, lines);

        // When & Then
        assertThrows(ConfigurationException.class, () ->
                configReader.readConfiguration(configFile.toString(), 1)
        );
    }

    @Test
    void readConfigurationWhenIncompleteConfigShouldThrowConfigurationException() throws IOException {
        // Given
        configFile = tempDir.resolve("config-incomplete.txt");
        List<String> lines = Arrays.asList(
                "#id: 1",
                "#mode: FILES"
                // Missing path and action
        );
        Files.write(configFile, lines);

        // When & Then
        assertThrows(ConfigurationException.class, () ->
                configReader.readConfiguration(configFile.toString(), 1)
        );
    }

    @Test
    void readConfigurationWithMultipleConfigurationsShouldReturnCorrectOne() throws IOException {
        // Given
        configFile = tempDir.resolve("multi-config.txt");
        List<String> lines = Arrays.asList(
                "#id: 1",
                "#mode: FILES",
                "#path: file1.txt,file2.txt",
                "#action: STRING",
                "",
                "#id: 2",
                "#mode: DIRECTORIES",
                "#path: dir1",
                "#action: COUNT"
        );
        Files.write(configFile, lines);

        // When
        Optional<Configuration> result = configReader.readConfiguration(configFile.toString(), 2);

        // Then
        assertTrue(result.isPresent());
        Configuration config = result.get();
        assertEquals(2, config.getId());
        assertEquals(ModeType.DIRECTORIES, config.getMode());
        assertEquals(List.of("dir1"), config.getPath());
        assertEquals(ActionType.COUNT, config.getAction());
    }

    private Path createValidConfigFile() throws IOException {
        Path file = tempDir.resolve("config.txt");
        List<String> lines = Arrays.asList(
                "#id: 1",
                "#mode: FILES",
                "#path: file1.txt,file2.txt",
                "#action: STRING"
        );
        return Files.write(file, lines);
    }
}