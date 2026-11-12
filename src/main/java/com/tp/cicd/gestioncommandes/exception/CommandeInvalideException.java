package com.tp.cicd.gestioncommandes.exception;

public class CommandeInvalideException extends RuntimeException {
    public CommandeInvalideException(String message) {
        super(message);
    }
}
