package com.dam.accesodatos.exception;

public class UserNotFoundException extends RuntimeException {

    private final String userId;

    public UserNotFoundException(String userId) {
        super("Usuario no encontrado con ID: " + userId);
        this.userId = userId;
    }

    public UserNotFoundException(String message, String userId) {
        super(message);
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
