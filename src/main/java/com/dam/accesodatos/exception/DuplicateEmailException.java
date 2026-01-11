package com.dam.accesodatos.exception;

public class DuplicateEmailException extends RuntimeException {

    private final String email;

    public DuplicateEmailException(String email) {
        super("El email '" + email + "' ya está registrado");
        this.email = email;
    }

    public DuplicateEmailException(String message, String email, Throwable cause) {
        super(message, cause);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
