package com.tp.cicd.gestioncommandes.model;

import java.math.BigDecimal;

/** Un produit du catalogue. Les prix sont en FCFA, hors taxes (HT). */
public record Produit(Long id, String nom, BigDecimal prixHt, int stock) {

    public Produit avecStock(int nouveauStock) {
        return new Produit(id, nom, prixHt, nouveauStock);
    }
}
