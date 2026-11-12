package com.tp.cicd.gestioncommandes.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidateurTelephoneTest {

    private final ValidateurTelephone validateur = new ValidateurTelephone();

    @ParameterizedTest
    @ValueSource(strings = {
            "771234567",
            "77 123 45 67",
            "78-123-45-67",
            "+221771234567",
            "00221761234567",
            "+221 70 123 45 67",
            "755555555"
    })
    @DisplayName("Numeros de mobile valides")
    void numerosValides(String numero) {
        assertTrue(validateur.estValide(numero), numero);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "331234567",
            "791234567",
            "7712345",
            "77123456789",
            "abcdefghi",
            "+33771234567",
            ""
    })
    @DisplayName("Numeros invalides")
    void numerosInvalides(String numero) {
        assertFalse(validateur.estValide(numero), numero);
    }

    @Test
    @DisplayName("Un numero absent est invalide")
    void numeroNull() {
        assertFalse(validateur.estValide(null));
    }
}
