package com.tp.cicd.gestioncommandes.exception;

public class StockInsuffisantException extends RuntimeException {
    public StockInsuffisantException(String nomProduit, int disponible, int demande) {
        super("Stock insuffisant pour " + nomProduit + " : " + disponible
                + " disponible(s), " + demande + " demande(s)");
    }
}
