package org.kuro.exceptions.special;

import lombok.Getter;
import org.kuro.exceptions.ApplicationException;
import org.kuro.exceptions.model.ErrorCode;

@Getter
public class FileException extends ApplicationException {

    private final String filePath;

    public FileException(ErrorCode errorCode, String filePath) {
        super(errorCode);
        this.filePath = filePath;
    }

    public FileException(ErrorCode errorCode, String filePath, Throwable cause) {
        super(errorCode, cause);
        this.filePath = filePath;
    }

}
