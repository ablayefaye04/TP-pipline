package com.tp.cicd.gestioncommandes.service;

import com.tp.cicd.gestioncommandes.model.MontantCommande;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrixServiceTest {

    private PrixService prixService;

    @BeforeEach
    void init() {
        prixService = new PrixService();
    }

    /** Compare deux montants sans se soucier de l'echelle (scale) du BigDecimal. */
    private static void assertMontant(long attendu, BigDecimal reel) {
        assertEquals(0, BigDecimal.valueOf(attendu).compareTo(reel),
                "attendu " + attendu + " mais obtenu " + reel);
    }

    @Test
    @DisplayName("Le sous-total est prix x quantite")
    void sousTotal() {
        assertMontant(30000, prixService.sousTotal(new BigDecimal("15000"), 2));
    }

    @Test
    @DisplayName("Une quantite nulle ou negative est refusee")
    void sousTotalQuantiteInvalide() {
        assertThrows(IllegalArgumentException.class, () -> prixService.sousTotal(new BigDecimal("1000"), 0));
        assertThrows(IllegalArgumentException.class, () -> prixService.sousTotal(new BigDecimal("1000"), -3));
    }

    @Test
    @DisplayName("Un prix negatif ou absent est refuse")
    void sousTotalPrixInvalide() {
        assertThrows(IllegalArgumentException.class, () -> prixService.sousTotal(new BigDecimal("-1"), 1));
        assertThrows(IllegalArgumentException.class, () -> prixService.sousTotal(null, 1));
    }

    @ParameterizedTest(name = "sous-total {0} => remise {1}")
    @CsvSource({
            "0,       0",
            "49999,   0",
            "50000,   2500",
            "99999,   5000",
            "100000,  10000",
            "250000,  25000"
    })
    @DisplayName("Remise par paliers : 0 %, 5 % (>= 50 000), 10 % (>= 100 000)")
    void remiseParPaliers(String sousTotal, String remiseAttendue) {
        assertMontant(Long.parseLong(remiseAttendue.trim()),
                prixService.calculerRemise(new BigDecimal(sousTotal.trim())));
    }

    @ParameterizedTest(name = "HT {0} => TVA {1}")
    @CsvSource({
            "10000,  1800",
            "49999,  9000",
            "47500,  8550"
    })
    @DisplayName("TVA de 18 % arrondie au FCFA")
    void tva(String ht, String tvaAttendue) {
        assertMontant(Long.parseLong(tvaAttendue.trim()), prixService.calculerTva(new BigDecimal(ht.trim())));
    }

    @Test
    @DisplayName("Livraison payante sous 50 000 TTC, gratuite a partir de 50 000 TTC")
    void fraisLivraison() {
        assertMontant(2000, prixService.fraisLivraison(new BigDecimal("49999")));
        assertMontant(0, prixService.fraisLivraison(new BigDecimal("50000")));
        assertMontant(0, prixService.fraisLivraison(new BigDecimal("120000")));
    }

    @Test
    @DisplayName("Petite commande : pas de remise, livraison payante")
    void calculPetiteCommande() {
        MontantCommande m = prixService.calculer(new BigDecimal("10000"));
        assertAll(
                () -> assertMontant(0, m.remise()),
                () -> assertMontant(10000, m.totalHtApresRemise()),
                () -> assertMontant(1800, m.tva()),
                () -> assertMontant(11800, m.totalTtc()),
                () -> assertMontant(2000, m.fraisLivraison()),
                () -> assertMontant(13800, m.totalAPayer())
        );
    }

    @Test
    @DisplayName("Commande moyenne : remise de 5 %, livraison offerte")
    void calculCommandeMoyenne() {
        MontantCommande m = prixService.calculer(new BigDecimal("50000"));
        assertAll(
                () -> assertMontant(2500, m.remise()),
                () -> assertMontant(47500, m.totalHtApresRemise()),
                () -> assertMontant(8550, m.tva()),
                () -> assertMontant(56050, m.totalTtc()),
                () -> assertMontant(0, m.fraisLivraison()),
                () -> assertMontant(56050, m.totalAPayer())
        );
    }

    @Test
    @DisplayName("Grosse commande : remise de 10 %")
    void calculGrosseCommande() {
        MontantCommande m = prixService.calculer(new BigDecimal("100000"));
        assertAll(
                () -> assertMontant(10000, m.remise()),
                () -> assertMontant(90000, m.totalHtApresRemise()),
                () -> assertMontant(16200, m.tva()),
                () -> assertMontant(106200, m.totalAPayer())
        );
    }

    @Test
    @DisplayName("Un sous-total negatif est refuse")
    void calculSousTotalNegatif() {
        assertThrows(IllegalArgumentException.class, () -> prixService.calculer(new BigDecimal("-10")));
    }
}
