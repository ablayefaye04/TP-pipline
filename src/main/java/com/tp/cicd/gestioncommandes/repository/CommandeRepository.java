package com.tp.cicd.gestioncommandes.repository;

import com.tp.cicd.gestioncommandes.model.Commande;

import java.util.Optional;

public interface CommandeRepository {

    /** Enregistre la commande ; un identifiant est genere si elle n'en a pas. */
    Commande save(Commande commande);

    Optional<Commande> findById(Long id);
}
