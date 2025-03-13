package org.kuro.exceptions.hendler;

import org.kuro.exceptions.ApplicationException;
import org.kuro.exceptions.model.ErrorCode;
import org.kuro.exceptions.model.ErrorResponse;
import org.kuro.exceptions.special.FileException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsoleErrorHandler implements ErrorHandler {
    private static final Logger logger = Logger.getLogger(ConsoleErrorHandler.class.getName());

    @Override
    public void handle(ApplicationException exception) {
        ErrorResponse response = createErrorResponse(exception);
        logError(exception, response);
        printToConsole(response);
    }

    @Override
    public void handleUnexpected(Exception exception) {
        ErrorResponse response = new ErrorResponse.Builder()
                .withErrorCode(ErrorCode.UNEXPECTED_ERROR)
                .withDetails(exception.getMessage())
                .build();
        logger.log(Level.SEVERE, "Unexpected error: " + exception.getMessage(), exception);
        printToConsole(response);
    }

    private ErrorResponse createErrorResponse(ApplicationException exception) {
        ErrorResponse.Builder builder = new ErrorResponse.Builder()
                .withErrorCode(exception.getErrorCode())
                .withMessage(exception.getMessage());

        if (exception instanceof FileException) {
            builder.withDetails("File: " + ((FileException) exception).getFilePath());
        }

        return builder.build();
    }

    private void logError(ApplicationException exception, ErrorResponse response) {
        logger.log(Level.SEVERE,
                String.format("[%s] Error %d: %s",
                        response.getErrorId(),
                        response.getCode(),
                        exception.getMessage()),
                exception);
    }

    private void printToConsole(ErrorResponse response) {
        System.err.println("Error [" + response.getErrorId() + "]: " +
                response.getMessage() +
                (response.getDetails() != null ? " (" + response.getDetails() + ")" : ""));
    }
}