package com.tp.cicd.gestioncommandes.web;

import com.tp.cicd.gestioncommandes.exception.CommandeIntrouvableException;
import com.tp.cicd.gestioncommandes.exception.StockInsuffisantException;
import com.tp.cicd.gestioncommandes.model.Commande;
import com.tp.cicd.gestioncommandes.model.LigneCommande;
import com.tp.cicd.gestioncommandes.model.Produit;
import com.tp.cicd.gestioncommandes.model.StatutCommande;
import com.tp.cicd.gestioncommandes.repository.ProduitRepository;
import com.tp.cicd.gestioncommandes.service.CommandeService;
import com.tp.cicd.gestioncommandes.service.PrixService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Test de la couche web uniquement : le service est remplace par un mock (Mockito). */
@WebMvcTest(CommandeController.class)
class CommandeControllerTest {

    private static final String CORPS_VALIDE =
            "{\"clientNom\":\"Awa Diop\",\"telephone\":\"771234567\","
                    + "\"lignes\":[{\"produitId\":1,\"quantite\":2}]}";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommandeService commandeService;

    @MockBean
    private ProduitRepository produitRepository;

    private Commande commandeExemple() {
        return new Commande(1L, "Awa Diop", "771234567", List.of(new LigneCommande(1L, 2)),
                new PrixService().calculer(new BigDecimal("30000")),
                StatutCommande.CONFIRMEE, LocalDateTime.of(2026, 10, 29, 10, 0));
    }

    @Test
    void listeDesProduits() throws Exception {
        when(produitRepository.findAll())
                .thenReturn(List.of(new Produit(1L, "Clavier", new BigDecimal("15000"), 50)));

        mockMvc.perform(get("/api/produits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Clavier"))
                .andExpect(jsonPath("$[0].stock").value(50));
    }

    @Test
    void creerCommandeRetourne201() throws Exception {
        when(commandeService.creer(anyString(), anyString(), anyList())).thenReturn(commandeExemple());

        mockMvc.perform(post("/api/commandes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CORPS_VALIDE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.statut").value("CONFIRMEE"))
                .andExpect(jsonPath("$.montant.totalAPayer").value(37400));
    }

    @Test
    void requeteSansNomRetourne400() throws Exception {
        String corps = "{\"clientNom\":\"\",\"telephone\":\"771234567\","
                + "\"lignes\":[{\"produitId\":1,\"quantite\":2}]}";

        mockMvc.perform(post("/api/commandes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corps))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void commandeInconnueRetourne404() throws Exception {
        when(commandeService.trouver(99L)).thenThrow(new CommandeIntrouvableException(99L));

        mockMvc.perform(get("/api/commandes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void stockInsuffisantRetourne409() throws Exception {
        when(commandeService.creer(anyString(), anyString(), anyList()))
                .thenThrow(new StockInsuffisantException("Clavier", 1, 2));

        mockMvc.perform(post("/api/commandes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CORPS_VALIDE))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }
}
