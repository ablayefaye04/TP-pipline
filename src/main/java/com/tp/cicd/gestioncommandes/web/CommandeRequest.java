package com.tp.cicd.gestioncommandes.web;

import com.tp.cicd.gestioncommandes.model.LigneCommande;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CommandeRequest(
        @NotBlank(message = "le nom du client est obligatoire") String clientNom,
        @NotBlank(message = "le telephone est obligatoire") String telephone,
        @NotEmpty(message = "au moins une ligne est requise") List<LigneCommande> lignes) {
}
