package org.kuro.exceptions.model;


import lombok.Getter;

@Getter
public enum ErrorCode {
    // Ошибки конфигурации
    CONFIGURATION_NOT_FOUND(1000, "Configuration not found"),
    CONFIGURATION_INVALID(1001, "Configuration is invalid"),

    // Файловые ошибки
    FILE_NOT_FOUND(2000, "File not found"),
    FILE_READ_ERROR(2001, "Read error"),
    FILE_WRITE_ERROR(2002, "Write error"),

    // Ошибки директорий
    DIRECTORY_CREATION_ERROR(2500, "Directory creation error"),

    // Ошибки обработки данных
    PROCESSING_ERROR(3000, "Processing error"),

    // Валидационные ошибки
    VALIDATION_ERROR(4000, "Validation error"),
    INVALID_PARAMETER(4001, "Invalid parameter"),


    // Системные ошибки
    SYSTEM_ERROR(9000, "System error"),
    UNEXPECTED_ERROR(9999, "Unexpected error");




    private final int code;
    private final String defaultMessage;

    ErrorCode(int code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

}
