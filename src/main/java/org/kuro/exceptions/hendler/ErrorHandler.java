package org.kuro.exceptions.hendler;

import org.kuro.exceptions.ApplicationException;

public interface ErrorHandler {
    void handle(ApplicationException exception);
    void handleUnexpected(Exception exception);
}
