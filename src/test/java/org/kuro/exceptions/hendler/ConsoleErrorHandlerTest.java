package org.kuro.exceptions.hendler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kuro.exceptions.ApplicationException;
import org.kuro.exceptions.model.ErrorCode;
import org.kuro.exceptions.special.FileException;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintStream;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConsoleErrorHandlerTest {

    private ConsoleErrorHandler errorHandler;

    @Mock
    private PrintStream mockErrStream;

    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() {
        errorHandler = new ConsoleErrorHandler();
        System.setErr(mockErrStream);
    }

    @Test
    void handle_withApplicationException_printsToConsole() {
        // Given
        ApplicationException exception = new ApplicationException(
                ErrorCode.VALIDATION_ERROR,
                "Validation failed"
        );

        // When
        errorHandler.handle(exception);

        // Then
        verify(mockErrStream).println(contains("Error ["));
        verify(mockErrStream).println(contains("Validation failed"));
    }

    @Test
    void handle_withFileException_includesFilePathInDetails() {
        // Given
        FileException exception = new FileException(
                ErrorCode.FILE_NOT_FOUND,
                "File not found",
                "/path/to/file"
        );

        // When
        errorHandler.handle(exception);

        // Then
        verify(mockErrStream).println(contains("File: /path/to/file"));
    }

    @Test
    void handleUnexpected_withException_printsToConsole() {
        // Given
        Exception exception = new RuntimeException("Unexpected error");

        // When
        errorHandler.handleUnexpected(exception);

        // Then
        verify(mockErrStream).println(contains("Error ["));
        verify(mockErrStream).println(contains("Unexpected error"));
    }

    @Test
    void tearDown() {
        System.setErr(originalErr);
    }
}
