package io;

class Error {
    private String message;
    private ErrorType type;

    Error(String message, ErrorType type) {
        this.message = message;
        this.type = type;
    }

    String getMessage() {
        return message;
    }

    ErrorType getType() {
        return type;
    }

    enum ErrorType {
        LexicalError,
        SyntaxError
    }
}
