package com.tp.cicd.gestioncommandes.web;

import com.tp.cicd.gestioncommandes.model.Commande;
import com.tp.cicd.gestioncommandes.model.Produit;
import com.tp.cicd.gestioncommandes.repository.ProduitRepository;
import com.tp.cicd.gestioncommandes.service.CommandeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommandeController {

    private final CommandeService commandeService;
    private final ProduitRepository produitRepository;

    public CommandeController(CommandeService commandeService, ProduitRepository produitRepository) {
        this.commandeService = commandeService;
        this.produitRepository = produitRepository;
    }

    @GetMapping("/produits")
    public List<Produit> produits() {
        return produitRepository.findAll();
    }

    @PostMapping("/commandes")
    public ResponseEntity<Commande> creer(@Valid @RequestBody CommandeRequest requete) {
        Commande commande = commandeService.creer(requete.clientNom(), requete.telephone(), requete.lignes());
        return ResponseEntity.status(HttpStatus.CREATED).body(commande);
    }

    @GetMapping("/commandes/{id}")
    public Commande trouver(@PathVariable("id") Long id) {
        return commandeService.trouver(id);
    }

    @PostMapping("/commandes/{id}/annuler")
    public Commande annuler(@PathVariable("id") Long id) {
        return commandeService.annuler(id);
    }
}
