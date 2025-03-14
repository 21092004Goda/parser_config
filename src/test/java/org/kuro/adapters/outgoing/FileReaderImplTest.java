package org.kuro.adapters.outgoing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.kuro.core.domain.ActionType;
import org.kuro.exceptions.special.FileException;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FileReaderImplTest {

    private FileReaderImpl fileReader;

    @TempDir
    Path tempDir;

    private Path file1;
    private Path file2;

    @BeforeEach
    void setUp() {
        fileReader = new FileReaderImpl();
    }

    @Test
    void readFilesShouldReadMultipleFilesSuccessfully() throws IOException {
        // Given
        file1 = createFile("file1.txt", Arrays.asList("Line 1", "Line 2"));
        file2 = createFile("file2.txt", Arrays.asList("File 2 Line 1", "File 2 Line 2"));

        // When
        List<List<String>> result = fileReader.readFiles(Arrays.asList(file1.toString(), file2.toString()));

        // Then
        assertEquals(2, result.size());
        assertEquals(Arrays.asList("Line 1", "Line 2"), result.get(0));
        assertEquals(Arrays.asList("File 2 Line 1", "File 2 Line 2"), result.get(1));
    }

    @Test
    void readFilesShouldThrowFileExceptionWhenFileNotFound() {
        // Given
        String nonExistentPath = tempDir.resolve("non-existent.txt").toString();

        // When & Then
        assertThrows(FileException.class, () ->
                fileReader.readFiles(List.of(nonExistentPath))
        );
    }

    @Test
    void processFilesWithStringActionShouldReturnOriginalContent() {
        // Given
        List<List<String>> fileContents = Arrays.asList(
                Arrays.asList("File 1 Line 1", "File 1 Line 2"),
                Arrays.asList("File 2 Line 1", "File 2 Line 2")
        );

        // When
        Map<Integer, List<String>> result = fileReader.processFiles(fileContents, ActionType.STRING);

        // Then
        assertEquals(2, result.size());
        assertEquals(Arrays.asList("File 1 Line 1", "File 2 Line 1"), result.get(1));
        assertEquals(Arrays.asList("File 1 Line 2", "File 2 Line 2"), result.get(2));
    }

    @Test
    void processFilesWithCountActionShouldCountWords() {
        // Given
        List<List<String>> fileContents = Arrays.asList(
                Arrays.asList("word1 word2 word3", "single"),
                Arrays.asList("one two", "")
        );

        // When
        Map<Integer, List<String>> result = fileReader.processFiles(fileContents, ActionType.COUNT);

        // Then
        assertEquals(2, result.size());
        assertEquals(Arrays.asList("3", "2"), result.get(1));
        assertEquals(Arrays.asList("1", "0"), result.get(2));
    }

    @Test
    void processFilesWithReplaceActionShouldReplaceCharacters() {
        // Given
        List<List<String>> fileContents = Arrays.asList(
                Arrays.asList("abc", "def"),
                Arrays.asList("abc", "xyz")
        );

        // When
        Map<Integer, List<String>> result = fileReader.processFiles(fileContents, ActionType.REPLACE);

        // Then
        assertEquals(2, result.size());
        assertEquals(Arrays.asList("234", "345"), result.get(1)); // a→2, b→3, c→4 for file1; a→3, b→4, c→5 for file2
        assertEquals(Arrays.asList("def", "xyz"), result.get(2)); // no replacements
    }

    @Test
    void processFilesWithDifferentFileLengthsShouldHandleCorrectly() {
        // Given
        List<List<String>> fileContents = Arrays.asList(
                Arrays.asList("File 1 Line 1", "File 1 Line 2", "File 1 Line 3"),
                Arrays.asList("File 2 Line 1")
        );

        // When
        Map<Integer, List<String>> result = fileReader.processFiles(fileContents, ActionType.STRING);

        // Then
        assertEquals(3, result.size());
        assertEquals(Arrays.asList("File 1 Line 1", "File 2 Line 1"), result.get(1));
        assertEquals(Arrays.asList("File 1 Line 2", ""), result.get(2));
        assertEquals(Arrays.asList("File 1 Line 3", ""), result.get(3));
    }

    @Test
    void processFilesWithEmptyFilesShouldHandleCorrectly() {
        // Given
        List<List<String>> fileContents = Arrays.asList(
                List.of(),
                List.of()
        );

        // When
        Map<Integer, List<String>> result = fileReader.processFiles(fileContents, ActionType.STRING);

        // Then
        assertEquals(0, result.size());
    }

    private Path createFile(String fileName, List<String> content) throws IOException {
        Path file = tempDir.resolve(fileName);
        return Files.write(file, content);
    }
}