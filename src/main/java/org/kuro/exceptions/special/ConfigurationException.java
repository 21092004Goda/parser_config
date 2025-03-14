package org.kuro.exceptions.special;

import org.kuro.exceptions.ApplicationException;
import org.kuro.exceptions.model.ErrorCode;

public class ConfigurationException extends ApplicationException {

    public ConfigurationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ConfigurationException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

}
