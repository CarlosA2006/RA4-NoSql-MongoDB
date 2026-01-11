package com.dam.accesodatos.exception;

public class InvalidUserIdException extends RuntimeException {

    private final String invalidId;

    public InvalidUserIdException(String invalidId) {
        super("ID de usuario inválido: " + invalidId);
        this.invalidId = invalidId;
    }

    public InvalidUserIdException(String invalidId, Throwable cause) {
        super("ID de usuario inválido: " + invalidId, cause);
        this.invalidId = invalidId;
    }

    public String getInvalidId() {
        return invalidId;
    }
}
