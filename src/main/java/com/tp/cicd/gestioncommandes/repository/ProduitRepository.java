package com.tp.cicd.gestioncommandes.repository;

import com.tp.cicd.gestioncommandes.model.Produit;

import java.util.List;
import java.util.Optional;

public interface ProduitRepository {

    List<Produit> findAll();

    Optional<Produit> findById(Long id);

    Produit save(Produit produit);
}
