package com.tp.cicd.gestioncommandes.service;

import com.tp.cicd.gestioncommandes.exception.CommandeIntrouvableException;
import com.tp.cicd.gestioncommandes.exception.CommandeInvalideException;
import com.tp.cicd.gestioncommandes.exception.ProduitIntrouvableException;
import com.tp.cicd.gestioncommandes.exception.StockInsuffisantException;
import com.tp.cicd.gestioncommandes.model.Commande;
import com.tp.cicd.gestioncommandes.model.LigneCommande;
import com.tp.cicd.gestioncommandes.model.StatutCommande;
import com.tp.cicd.gestioncommandes.repository.CommandeRepositoryMemoire;
import com.tp.cicd.gestioncommandes.repository.ProduitRepositoryMemoire;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Test unitaire du service avec des depots en memoire (aucun contexte Spring : test rapide). */
class CommandeServiceTest {

    private static final String TEL = "771234567";

    private ProduitRepositoryMemoire produits;
    private CommandeService service;

    @BeforeEach
    void init() {
        produits = new ProduitRepositoryMemoire();
        service = new CommandeService(produits, new CommandeRepositoryMemoire(),
                new PrixService(), new ValidateurTelephone());
    }

    private static void assertMontant(long attendu, BigDecimal reel) {
        assertEquals(0, BigDecimal.valueOf(attendu).compareTo(reel),
                "attendu " + attendu + " mais obtenu " + reel);
    }

    private int stockDe(long produitId) {
        return produits.findById(produitId).orElseThrow().stock();
    }

    @Test
    @DisplayName("Une commande valide est confirmee, calculee et decremente le stock")
    void creerCommandeValide() {
        Commande c = service.creer("Awa Diop", TEL, List.of(new LigneCommande(1L, 2)));

        assertNotNull(c.id());
        assertEquals(StatutCommande.CONFIRMEE, c.statut());
        assertMontant(30000, c.montant().sousTotalHt());
        assertMontant(5400, c.montant().tva());
        assertMontant(2000, c.montant().fraisLivraison());
        assertMontant(37400, c.montant().totalAPayer());
        assertEquals(48, stockDe(1L));
    }

    @Test
    @DisplayName("Une grosse commande beneficie de la remise de 10 %")
    void creerCommandeAvecRemise() {
        Commande c = service.creer("Moussa Ndiaye", TEL, List.of(new LigneCommande(3L, 2)));

        assertMontant(190000, c.montant().sousTotalHt());
        assertMontant(19000, c.montant().remise());
        assertMontant(30780, c.montant().tva());
        assertMontant(201780, c.montant().totalAPayer());
    }

    @Test
    @DisplayName("Les lignes portant sur le meme produit sont fusionnees")
    void lignesDupliqueesFusionnees() {
        Commande c = service.creer("Awa Diop", TEL,
                List.of(new LigneCommande(2L, 3), new LigneCommande(2L, 4)));

        assertEquals(1, c.lignes().size());
        assertEquals(7, c.lignes().get(0).quantite());
        assertEquals(93, stockDe(2L));
    }

    @Test
    @DisplayName("Un produit inconnu leve ProduitIntrouvableException")
    void produitInconnu() {
        assertThrows(ProduitIntrouvableException.class,
                () -> service.creer("Awa Diop", TEL, List.of(new LigneCommande(99L, 1))));
    }

    @Test
    @DisplayName("Un stock insuffisant refuse la commande et ne modifie aucun stock")
    void stockInsuffisant() {
        assertThrows(StockInsuffisantException.class,
                () -> service.creer("Awa Diop", TEL,
                        List.of(new LigneCommande(1L, 5), new LigneCommande(4L, 6))));

        assertEquals(50, stockDe(1L));
        assertEquals(5, stockDe(4L));
    }

    @Test
    @DisplayName("Telephone invalide, nom vide, aucune ligne : commande refusee")
    void donneesInvalides() {
        List<LigneCommande> lignes = List.of(new LigneCommande(1L, 1));
        assertThrows(CommandeInvalideException.class, () -> service.creer("Awa Diop", "12345", lignes));
        assertThrows(CommandeInvalideException.class, () -> service.creer("  ", TEL, lignes));
        assertThrows(CommandeInvalideException.class, () -> service.creer(null, TEL, lignes));
        assertThrows(CommandeInvalideException.class, () -> service.creer("Awa Diop", TEL, List.of()));
        assertThrows(CommandeInvalideException.class, () -> service.creer("Awa Diop", TEL, null));
    }

    @Test
    @DisplayName("Une quantite nulle ou negative est refusee")
    void quantiteInvalide() {
        assertThrows(CommandeInvalideException.class,
                () -> service.creer("Awa Diop", TEL, List.of(new LigneCommande(1L, 0))));
        assertThrows(CommandeInvalideException.class,
                () -> service.creer("Awa Diop", TEL, List.of(new LigneCommande(1L, -2))));
    }

    @Test
    @DisplayName("Annuler une commande remet le stock et change le statut")
    void annulerCommande() {
        Commande c = service.creer("Awa Diop", TEL, List.of(new LigneCommande(1L, 10)));
        assertEquals(40, stockDe(1L));

        Commande annulee = service.annuler(c.id());

        assertEquals(StatutCommande.ANNULEE, annulee.statut());
        assertEquals(50, stockDe(1L));
    }

    @Test
    @DisplayName("Annuler deux fois la meme commande est refuse")
    void annulerDeuxFois() {
        Commande c = service.creer("Awa Diop", TEL, List.of(new LigneCommande(1L, 1)));
        service.annuler(c.id());

        assertThrows(CommandeInvalideException.class, () -> service.annuler(c.id()));
        assertEquals(50, stockDe(1L));
    }

    @Test
    @DisplayName("Une commande inconnue leve CommandeIntrouvableException")
    void commandeInconnue() {
        assertThrows(CommandeIntrouvableException.class, () -> service.trouver(999L));
        assertThrows(CommandeIntrouvableException.class, () -> service.annuler(999L));
    }

    @Test
    @DisplayName("On retrouve une commande par son identifiant")
    void trouverCommande() {
        Commande c = service.creer("Awa Diop", TEL, List.of(new LigneCommande(2L, 1)));
        assertEquals("Awa Diop", service.trouver(c.id()).clientNom());
    }
}
