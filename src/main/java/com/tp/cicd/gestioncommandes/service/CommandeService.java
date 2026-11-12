package com.tp.cicd.gestioncommandes.service;

import com.tp.cicd.gestioncommandes.exception.CommandeIntrouvableException;
import com.tp.cicd.gestioncommandes.exception.CommandeInvalideException;
import com.tp.cicd.gestioncommandes.exception.ProduitIntrouvableException;
import com.tp.cicd.gestioncommandes.exception.StockInsuffisantException;
import com.tp.cicd.gestioncommandes.model.Commande;
import com.tp.cicd.gestioncommandes.model.LigneCommande;
import com.tp.cicd.gestioncommandes.model.MontantCommande;
import com.tp.cicd.gestioncommandes.model.Produit;
import com.tp.cicd.gestioncommandes.model.StatutCommande;
import com.tp.cicd.gestioncommandes.repository.CommandeRepository;
import com.tp.cicd.gestioncommandes.repository.ProduitRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommandeService {

    private final ProduitRepository produitRepository;
    private final CommandeRepository commandeRepository;
    private final PrixService prixService;
    private final ValidateurTelephone validateurTelephone;

    public CommandeService(ProduitRepository produitRepository,
                           CommandeRepository commandeRepository,
                           PrixService prixService,
                           ValidateurTelephone validateurTelephone) {
        this.produitRepository = produitRepository;
        this.commandeRepository = commandeRepository;
        this.prixService = prixService;
        this.validateurTelephone = validateurTelephone;
    }

    public Commande creer(String clientNom, String telephone, List<LigneCommande> lignes) {
        if (clientNom == null || clientNom.isBlank()) {
            throw new CommandeInvalideException("Le nom du client est obligatoire");
        }
        if (!validateurTelephone.estValide(telephone)) {
            throw new CommandeInvalideException("Numero de telephone invalide : " + telephone);
        }
        if (lignes == null || lignes.isEmpty()) {
            throw new CommandeInvalideException("La commande doit contenir au moins une ligne");
        }

        // 1) Fusionner les lignes qui portent sur le meme produit
        Map<Long, Integer> quantites = new LinkedHashMap<>();
        for (LigneCommande ligne : lignes) {
            if (ligne == null || ligne.produitId() == null) {
                throw new CommandeInvalideException("Ligne de commande invalide");
            }
            if (ligne.quantite() <= 0) {
                throw new CommandeInvalideException("La quantite doit etre strictement positive");
            }
            quantites.merge(ligne.produitId(), ligne.quantite(), Integer::sum);
        }

        // 2) Verifier le stock et calculer le sous-total (aucune modification a ce stade)
        BigDecimal sousTotal = BigDecimal.ZERO;
        List<Produit> produitsMisAJour = new ArrayList<>();
        List<LigneCommande> lignesFusionnees = new ArrayList<>();
        for (Map.Entry<Long, Integer> entree : quantites.entrySet()) {
            Long produitId = entree.getKey();
            int quantite = entree.getValue();
            Produit produit = produitRepository.findById(produitId)
                    .orElseThrow(() -> new ProduitIntrouvableException(produitId));
            if (produit.stock() < quantite) {
                throw new StockInsuffisantException(produit.nom(), produit.stock(), quantite);
            }
            sousTotal = sousTotal.add(prixService.sousTotal(produit.prixHt(), quantite));
            produitsMisAJour.add(produit.avecStock(produit.stock() - quantite));
            lignesFusionnees.add(new LigneCommande(produitId, quantite));
        }

        // 3) Tout est valide : on decremente le stock et on enregistre la commande
        produitsMisAJour.forEach(produitRepository::save);
        MontantCommande montant = prixService.calculer(sousTotal);
        Commande commande = new Commande(null, clientNom.trim(), telephone, lignesFusionnees,
                montant, StatutCommande.CONFIRMEE, LocalDateTime.now());
        return commandeRepository.save(commande);
    }

    public Commande trouver(Long id) {
        return commandeRepository.findById(id)
                .orElseThrow(() -> new CommandeIntrouvableException(id));
    }

    public Commande annuler(Long id) {
        Commande commande = trouver(id);
        if (commande.statut() == StatutCommande.ANNULEE) {
            throw new CommandeInvalideException("La commande " + id + " est deja annulee");
        }
        for (LigneCommande ligne : commande.lignes()) {
            Produit produit = produitRepository.findById(ligne.produitId())
                    .orElseThrow(() -> new ProduitIntrouvableException(ligne.produitId()));
            produitRepository.save(produit.avecStock(produit.stock() + ligne.quantite()));
        }
        return commandeRepository.save(commande.avecStatut(StatutCommande.ANNULEE));
    }
}
