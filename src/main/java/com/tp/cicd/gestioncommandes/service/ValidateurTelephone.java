package com.tp.cicd.gestioncommandes.service;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Valide un numero de mobile senegalais : 9 chiffres commencant par 70, 75, 76, 77 ou 78,
 * avec un indicatif facultatif (+221 ou 00221). Espaces, points et tirets sont ignores.
 */
@Component
public class ValidateurTelephone {

    private static final Pattern MOBILE_SN = Pattern.compile("^(\\+221|00221)?(70|75|76|77|78)\\d{7}$");

    public boolean estValide(String telephone) {
        if (telephone == null) {
            return false;
        }
        String nettoye = telephone.replaceAll("[\\s.-]", "");
        return MOBILE_SN.matcher(nettoye).matches();
    }
}
