package org.kuro.adapters.incoming;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kuro.core.application.ConfigService;
import org.kuro.exceptions.ApplicationException;
import org.kuro.exceptions.hendler.ErrorHandler;
import org.kuro.exceptions.model.ErrorCode;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CliAdapterImplTest {

    @Mock
    private ConfigService configService;

    @Mock
    private ErrorHandler errorHandler;

    private CliAdapterImpl cliAdapter;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        cliAdapter = new CliAdapterImpl(configService, errorHandler);
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void processShouldCallConfigServiceAndPrintOutputWhenSuccessful() {
        // Given
        String path = "config.txt";
        int id = 1;
        String outputPath = "result/output_config_1.json";

        when(configService.processConfig(path, id)).thenReturn(outputPath);

        // When
        cliAdapter.process(path, id);

        // Then
        verify(configService).processConfig(path, id);
        verify(errorHandler, never()).handle(any(ApplicationException.class));
        verify(errorHandler, never()).handleUnexpected(any(Exception.class));
        assertTrue(outputStream.toString().contains("JSON save in: " + outputPath));
    }

    @Test
    void processShouldHandleApplicationExceptionWhenThrown() {
        // Given
        String path = "config.txt";
        int id = 1;
        ApplicationException exception = new ApplicationException(
                ErrorCode.CONFIGURATION_NOT_FOUND
        );

        when(configService.processConfig(path, id)).thenThrow(exception);

        // When
        cliAdapter.process(path, id);

        // Then
        verify(configService).processConfig(path, id);
        verify(errorHandler).handle(exception);
        verify(errorHandler, never()).handleUnexpected(any(Exception.class));
    }

    @Test
    void processShouldHandleUnexpectedExceptionWhenThrown() {
        // Given
        String path = "config.txt";
        int id = 1;
        RuntimeException exception = new RuntimeException("Unexpected error");
        when(configService.processConfig(path, id)).thenThrow(exception);

        // When
        cliAdapter.process(path, id);

        // Then
        verify(configService).processConfig(path, id);
        verify(errorHandler, never()).handle(any(ApplicationException.class));
        verify(errorHandler).handleUnexpected(exception);
    }
}
