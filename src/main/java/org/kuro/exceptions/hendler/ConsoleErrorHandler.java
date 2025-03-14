package org.kuro.exceptions.hendler;

import org.kuro.exceptions.ApplicationException;
import org.kuro.exceptions.model.ErrorCode;
import org.kuro.exceptions.model.ErrorResponse;
import org.kuro.exceptions.special.FileException;

import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsoleErrorHandler implements ErrorHandler {
    private static final Logger logger = Logger.getLogger(ConsoleErrorHandler.class.getName());
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void handle(ApplicationException exception) {
        ErrorResponse response = createErrorResponse(exception);
        logError(response);
        printToConsole(response);
    }

    @Override
    public void handleUnexpected(Exception exception) {
        ErrorResponse response = new ErrorResponse.Builder()
                .withErrorCode(ErrorCode.UNEXPECTED_ERROR)
                .withDetails(exception.getMessage())
                .build();
        logger.log(Level.SEVERE, "Unexpected error occurred", exception);
        printToConsole(response);
    }

    private ErrorResponse createErrorResponse(ApplicationException exception) {
        ErrorResponse.Builder builder = new ErrorResponse.Builder()
                .withErrorCode(exception.getErrorCode());

        if (exception instanceof FileException) {
            builder.withDetails("File: " + ((FileException) exception).getFilePath());
        }

        return builder.build();
    }

    private void logError(ErrorResponse response) {
        logger.log(Level.SEVERE,
                String.format("[%s] [%s] Error %d: %s",
                        response.getTimestamp().format(formatter),
                        response.getErrorId(),
                        response.getCode(),
                        response.getMessage()));
    }

    private void printToConsole(ErrorResponse response) {
        System.err.printf("[%s] Error [%s] Code %d: %s%s%n",
                response.getTimestamp().format(formatter),
                response.getErrorId(),
                response.getCode(),
                response.getMessage(),
                (response.getDetails() != null ? " (" + response.getDetails() + ")" : ""));
    }
}