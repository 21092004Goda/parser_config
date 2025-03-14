package org.kuro.adapters.outgoing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.kuro.exceptions.model.ErrorCode;
import org.kuro.exceptions.special.FileException;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JsonWriterImplTest {

    private JsonWriterImpl jsonWriter;

    @TempDir
    Path tempDir;

    private Map<String, Object> testData;

    @BeforeEach
    void setUp() {
        jsonWriter = new JsonWriterImpl();
        testData = new HashMap<>();
        testData.put("key1", "value1");
        testData.put("key2", 123);
        testData.put("key3", Map.of("nestedKey", "nestedValue"));
    }

    @Test
    void saveToShouldWriteJsonToExistingDirectory() throws IOException {
        // Given
        String outputPath = tempDir.resolve("output.json").toString();

        // When
        String result = jsonWriter.saveTo(testData, outputPath);

        // Then
        assertEquals(outputPath, result);
        assertTrue(Files.exists(Path.of(outputPath)));
        String content = Files.readString(Path.of(outputPath));
        assertTrue(content.contains("\"key1\" : \"value1\""));
        assertTrue(content.contains("\"key2\" : 123"));
        assertTrue(content.contains("\"nestedKey\" : \"nestedValue\""));
    }

    @Test
    void saveToShouldCreateDirectoriesIfNeeded() {
        // Given
        String outputPath = tempDir.resolve("new_dir/nested/output.json").toString();

        // When
        String result = jsonWriter.saveTo(testData, outputPath);

        // Then
        assertEquals(outputPath, result);
        assertTrue(Files.exists(Path.of(outputPath)));
    }

    @Test
    void saveToShouldThrowExceptionWhenFileCannotBeWritten() {
        // Given
        Path dirPath = tempDir.resolve("cannot-write-here");
        try {
            Files.createDirectory(dirPath);
        } catch (IOException e) {
            fail("Failed to set up test: " + e.getMessage());
        }

        // When & Then
        FileException exception = assertThrows(FileException.class, () ->
                jsonWriter.saveTo(testData, dirPath.toString())
        );
        assertEquals(ErrorCode.FILE_WRITE_ERROR, exception.getErrorCode());
    }

    @Test
    void saveToShouldThrowExceptionWhenDirectoryCannotBeCreated() {
        // Given
        File readOnlyDir = null;
        try {
            readOnlyDir = Files.createDirectory(tempDir.resolve("readonly")).toFile();
            if (!readOnlyDir.setReadOnly()) {
                return;
            }
        } catch (IOException e) {
            fail("Failed to set up test: " + e.getMessage());
        }

        // When & Then
        String outputPath = new File(readOnlyDir, "impossible/output.json").getPath();

        assertThrows(FileException.class, () ->
                jsonWriter.saveTo(testData, outputPath)
        );

        if (readOnlyDir != null) {
            readOnlyDir.setWritable(true);
        }
    }
}