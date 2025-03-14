package org.kuro.exceptions.special;

import org.kuro.exceptions.ApplicationException;
import org.kuro.exceptions.model.ErrorCode;

public class ProcessingException extends ApplicationException {
    public ProcessingException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ProcessingException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
