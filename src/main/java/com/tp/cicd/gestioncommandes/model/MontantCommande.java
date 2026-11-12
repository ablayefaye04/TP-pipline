package com.tp.cicd.gestioncommandes.model;

import java.math.BigDecimal;

/** Detail du calcul d'une commande (tous les montants en FCFA, sans decimales). */
public record MontantCommande(
        BigDecimal sousTotalHt,
        BigDecimal remise,
        BigDecimal totalHtApresRemise,
        BigDecimal tva,
        BigDecimal totalTtc,
        BigDecimal fraisLivraison,
        BigDecimal totalAPayer) {
}
