package com.tp.cicd.gestioncommandes.exception;

public class CommandeIntrouvableException extends RuntimeException {
    public CommandeIntrouvableException(Long id) {
        super("Commande introuvable : " + id);
    }
}
