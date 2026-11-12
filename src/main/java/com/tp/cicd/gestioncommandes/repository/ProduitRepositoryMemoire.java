package com.tp.cicd.gestioncommandes.repository;

import com.tp.cicd.gestioncommandes.model.Produit;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;

/** Catalogue en memoire (pas de base de donnees : on se concentre sur le pipeline CI/CD). */
@Repository
public class ProduitRepositoryMemoire implements ProduitRepository {

    private final Map<Long, Produit> produits = new ConcurrentSkipListMap<>();

    public ProduitRepositoryMemoire() {
        save(new Produit(1L, "Clavier", new BigDecimal("15000"), 50));
        save(new Produit(2L, "Souris", new BigDecimal("8000"), 100));
        save(new Produit(3L, "Ecran 24 pouces", new BigDecimal("95000"), 20));
        save(new Produit(4L, "Ordinateur portable", new BigDecimal("350000"), 5));
    }

    @Override
    public List<Produit> findAll() {
        return new ArrayList<>(produits.values());
    }

    @Override
    public Optional<Produit> findById(Long id) {
        return Optional.ofNullable(produits.get(id));
    }

    @Override
    public Produit save(Produit produit) {
        produits.put(produit.id(), produit);
        return produit;
    }
}
