package org.kuro.exceptions.special;

import org.kuro.exceptions.ApplicationException;
import org.kuro.exceptions.model.ErrorCode;

public class ConfigurationException extends ApplicationException {

    public ConfigurationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public ConfigurationException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

}
