package org.kuro.exceptions.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ErrorResponse {
    private final String errorId;
    private final int code;
    private final String message;
    private final String details;
    private final LocalDateTime timestamp;

    private ErrorResponse(Builder builder) {
        this.errorId = builder.errorId;
        this.code = builder.code;
        this.message = builder.message;
        this.details = builder.details;
        this.timestamp = builder.timestamp;
    }

    public static class Builder {
        private String errorId = UUID.randomUUID().toString();
        private int code;
        private String message;
        private String details;
        private LocalDateTime timestamp = LocalDateTime.now();

        public Builder withErrorId(String errorId) {
            this.errorId = errorId;
            return this;
        }

        public Builder withErrorCode(ErrorCode errorCode) {
            this.code = errorCode.getCode();
            this.message = errorCode.getDefaultMessage();
            return this;
        }

        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder withDetails(String details) {
            this.details = details;
            return this;
        }

        public Builder withTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(this);
        }
    }
}