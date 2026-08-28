package com.seneau.tankchlore_service.web.controller;


import com.seneau.tankchlore_service.web.dto.response.CurrentUserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/starter")
public class StarterController {

    /**
     * Endpoint d'exemple à supprimer lors de la création du vrai service.
     * Les headers sont un contexte transmis par le Gateway ; le JWT reste la
     * source d'authentification du microservice.
     */
    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> currentUser(
            @RequestHeader("X-User-email") String email,
            @RequestHeader(value = "X-User-privileges", required = false) List<String> privileges
    ) {
        return ResponseEntity.ok(new CurrentUserResponse(
                email,
                privileges == null ? List.of() : privileges
        ));
    }
}
