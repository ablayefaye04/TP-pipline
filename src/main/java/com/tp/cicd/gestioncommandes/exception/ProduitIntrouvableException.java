package com.tp.cicd.gestioncommandes.exception;

public class ProduitIntrouvableException extends RuntimeException {
    public ProduitIntrouvableException(Long id) {
        super("Produit introuvable : " + id);
    }
}
