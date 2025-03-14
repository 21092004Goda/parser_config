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
    void handleWithApplicationExceptionPrintsToConsole() {
        // Given
        ApplicationException exception = new ApplicationException(
                ErrorCode.VALIDATION_ERROR
        );

        // When
        errorHandler.handle(exception);

        // Then
        verify(mockErrStream).println(contains("Error ["));
    }

    @Test
    void handleWithFileExceptionIncludesFilePathInDetails() {
        // Given
        FileException exception = new FileException(
                ErrorCode.FILE_NOT_FOUND,
                "/path/to/file"
        );

        // When
        errorHandler.handle(exception);

        // Then
        verify(mockErrStream).println(contains("File: /path/to/file"));
    }

    @Test
    void handleUnexpectedWithExceptionPrintsToConsole() {
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
