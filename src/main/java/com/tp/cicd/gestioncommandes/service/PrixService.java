package com.tp.cicd.gestioncommandes.service;

import com.tp.cicd.gestioncommandes.model.MontantCommande;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Regles de calcul des prix (FCFA) :
 * - remise de 5 % des 50 000 HT, de 10 % des 100 000 HT ;
 * - TVA de 18 % appliquee apres la remise ;
 * - livraison de 2 000 FCFA, gratuite des 50 000 TTC.
 */
@Service
public class PrixService {

    public static final BigDecimal TAUX_TVA = new BigDecimal("0.18");
    public static final BigDecimal SEUIL_REMISE_5 = new BigDecimal("50000");
    public static final BigDecimal SEUIL_REMISE_10 = new BigDecimal("100000");
    public static final BigDecimal TAUX_REMISE_5 = new BigDecimal("0.05");
    public static final BigDecimal TAUX_REMISE_10 = new BigDecimal("0.10");
    public static final BigDecimal SEUIL_LIVRAISON_GRATUITE = new BigDecimal("50000");
    public static final BigDecimal FRAIS_LIVRAISON = new BigDecimal("2000");

    public BigDecimal sousTotal(BigDecimal prixUnitaireHt, int quantite) {
        if (prixUnitaireHt == null || prixUnitaireHt.signum() < 0) {
            throw new IllegalArgumentException("Le prix unitaire doit etre positif");
        }
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantite doit etre strictement positive");
        }
        return prixUnitaireHt.multiply(BigDecimal.valueOf(quantite));
    }

    public BigDecimal tauxRemise(BigDecimal sousTotalHt) {
        if (sousTotalHt.compareTo(SEUIL_REMISE_10) >= 0) {
            return TAUX_REMISE_10;
        }
        if (sousTotalHt.compareTo(SEUIL_REMISE_5) >= 0) {
            return TAUX_REMISE_5;
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal calculerRemise(BigDecimal sousTotalHt) {
        return arrondir(sousTotalHt.multiply(tauxRemise(sousTotalHt)));
    }

    public BigDecimal calculerTva(BigDecimal montantHt) {
        return arrondir(montantHt.multiply(TAUX_TVA));
    }

    public BigDecimal fraisLivraison(BigDecimal totalTtc) {
        return totalTtc.compareTo(SEUIL_LIVRAISON_GRATUITE) >= 0
                ? BigDecimal.ZERO
                : FRAIS_LIVRAISON;
    }

    public MontantCommande calculer(BigDecimal sousTotalHt) {
        if (sousTotalHt == null || sousTotalHt.signum() < 0) {
            throw new IllegalArgumentException("Le sous-total doit etre positif");
        }
        BigDecimal remise = calculerRemise(sousTotalHt);
        BigDecimal htApresRemise = sousTotalHt.subtract(remise);
        BigDecimal tva = calculerTva(htApresRemise);
        BigDecimal ttc = htApresRemise.add(tva);
        BigDecimal frais = fraisLivraison(ttc);
        BigDecimal totalAPayer = ttc.add(frais);
        return new MontantCommande(sousTotalHt, remise, htApresRemise, tva, ttc, frais, totalAPayer);
    }

    private BigDecimal arrondir(BigDecimal valeur) {
        return valeur.setScale(0, RoundingMode.HALF_UP);
    }
}
