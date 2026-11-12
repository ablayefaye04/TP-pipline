package com.tp.cicd.gestioncommandes;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Test d'integration : on demarre toute l'application (vrais beans, depots en memoire). */
@SpringBootTest
@AutoConfigureMockMvc
class GestionCommandesIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contexteSeCharge() {
        // Si un bean est mal configure, ce test echoue au demarrage du contexte Spring.
    }

    @Test
    void catalogueContientQuatreProduits() throws Exception {
        mockMvc.perform(get("/api/produits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));
    }

    @Test
    void parcoursCompletCreationPuisLecture() throws Exception {
        String corps = "{\"clientNom\":\"Awa Diop\",\"telephone\":\"771234567\","
                + "\"lignes\":[{\"produitId\":2,\"quantite\":3}]}";

        // 3 souris x 8 000 = 24 000 HT ; TVA 4 320 ; TTC 28 320 ; livraison 2 000 ; total 30 320
        mockMvc.perform(post("/api/commandes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corps))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.montant.totalAPayer").value(30320));
    }

    @Test
    void produitInconnuRetourne404() throws Exception {
        String corps = "{\"clientNom\":\"Awa Diop\",\"telephone\":\"771234567\","
                + "\"lignes\":[{\"produitId\":999,\"quantite\":1}]}";

        mockMvc.perform(post("/api/commandes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corps))
                .andExpect(status().isNotFound());
    }
}
