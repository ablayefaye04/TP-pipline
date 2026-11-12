package com.tp.cicd.gestioncommandes.model;

import java.time.LocalDateTime;
import java.util.List;

public record Commande(
        Long id,
        String clientNom,
        String telephone,
        List<LigneCommande> lignes,
        MontantCommande montant,
        StatutCommande statut,
        LocalDateTime date) {

    public Commande avecId(Long nouvelId) {
        return new Commande(nouvelId, clientNom, telephone, lignes, montant, statut, date);
    }

    public Commande avecStatut(StatutCommande nouveauStatut) {
        return new Commande(id, clientNom, telephone, lignes, montant, nouveauStatut, date);
    }
}
