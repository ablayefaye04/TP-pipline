package com.tp.cicd.gestioncommandes.repository;

import com.tp.cicd.gestioncommandes.model.Commande;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class CommandeRepositoryMemoire implements CommandeRepository {

    private final Map<Long, Commande> commandes = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    @Override
    public Commande save(Commande commande) {
        Commande aEnregistrer = commande.id() == null
                ? commande.avecId(sequence.incrementAndGet())
                : commande;
        commandes.put(aEnregistrer.id(), aEnregistrer);
        return aEnregistrer;
    }

    @Override
    public Optional<Commande> findById(Long id) {
        return Optional.ofNullable(commandes.get(id));
    }
}
