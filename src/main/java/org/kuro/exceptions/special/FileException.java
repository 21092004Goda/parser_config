package org.kuro.exceptions.special;

import lombok.Getter;
import org.kuro.exceptions.ApplicationException;
import org.kuro.exceptions.model.ErrorCode;

@Getter
public class FileException extends ApplicationException {

    private final String filePath;

    public FileException(ErrorCode errorCode, String message, String filePath) {
        super(errorCode, message);
        this.filePath = filePath;
    }

    public FileException(ErrorCode errorCode, String message, String filePath, Throwable cause) {
        super(errorCode, message, cause);
        this.filePath = filePath;
    }

}
