package org.kuro.exceptions.special;

import org.kuro.exceptions.ApplicationException;
import org.kuro.exceptions.model.ErrorCode;

public class ProcessingException extends ApplicationException {
    public ProcessingException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public ProcessingException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
