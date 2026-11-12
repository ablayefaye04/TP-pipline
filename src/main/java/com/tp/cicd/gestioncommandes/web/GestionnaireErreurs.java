package com.tp.cicd.gestioncommandes.web;

import com.tp.cicd.gestioncommandes.exception.CommandeIntrouvableException;
import com.tp.cicd.gestioncommandes.exception.CommandeInvalideException;
import com.tp.cicd.gestioncommandes.exception.ProduitIntrouvableException;
import com.tp.cicd.gestioncommandes.exception.StockInsuffisantException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GestionnaireErreurs {

    public record ErreurApi(int status, String message) {
    }

    @ExceptionHandler({ProduitIntrouvableException.class, CommandeIntrouvableException.class})
    public ResponseEntity<ErreurApi> introuvable(RuntimeException ex) {
        return reponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(StockInsuffisantException.class)
    public ResponseEntity<ErreurApi> stockInsuffisant(StockInsuffisantException ex) {
        return reponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(CommandeInvalideException.class)
    public ResponseEntity<ErreurApi> commandeInvalide(CommandeInvalideException ex) {
        return reponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErreurApi> requeteInvalide(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + " : " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return reponse(HttpStatus.BAD_REQUEST, "Requete invalide - " + detail);
    }

    private ResponseEntity<ErreurApi> reponse(HttpStatus statut, String message) {
        return ResponseEntity.status(statut).body(new ErreurApi(statut.value(), message));
    }
}
