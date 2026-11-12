package com.tp.cicd.gestioncommandes.model;

/** Une ligne de commande : un produit et une quantite. */
public record LigneCommande(Long produitId, int quantite) {
}
