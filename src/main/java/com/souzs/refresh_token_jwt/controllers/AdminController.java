package com.souzs.refresh_token_jwt.controllers;

import com.souzs.refresh_token_jwt.domain.dtos.UserDTO;
import com.souzs.refresh_token_jwt.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<?> getMessage() {
        UserDTO userDTO = authService.currentUser();

        String message = String.format("Ola admin %s! Somente usuários admins podem ver essa menssagem.", userDTO.getEmail());

        return ResponseEntity.ok().body(message);
    }

}
