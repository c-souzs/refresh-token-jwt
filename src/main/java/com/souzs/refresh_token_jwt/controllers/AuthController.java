package com.souzs.refresh_token_jwt.controllers;

import com.souzs.refresh_token_jwt.domain.dtos.UserBasicDTO;
import com.souzs.refresh_token_jwt.domain.dtos.UserDTO;
import com.souzs.refresh_token_jwt.domain.payloads.TokenCookies;
import com.souzs.refresh_token_jwt.domain.dtos.UserAuthDTO;
import com.souzs.refresh_token_jwt.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("signin")
    public ResponseEntity<?> signin(@Valid @RequestBody UserAuthDTO userDTO) {
        TokenCookies cookies = authService.signin(userDTO);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookies.getAccessToken().toString())
                .header(HttpHeaders.SET_COOKIE, cookies.getRefreshToken().toString())
                .build();
    }

    @PostMapping("refreshToken")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        TokenCookies cookies = authService.refreshToken(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookies.getAccessToken().toString())
                .build();
    }

    @PostMapping("signout")
    public ResponseEntity<?> signout(HttpServletRequest request) {
        TokenCookies cookies = authService.signout(request);


        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookies.getAccessToken().toString())
                .header(HttpHeaders.SET_COOKIE, cookies.getRefreshToken().toString())
                .build();
    }

    @PostMapping("signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserBasicDTO userDTO) {
        UserDTO userCreatedDTO = authService.signup(userDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}")
                .buildAndExpand(userCreatedDTO.getId()).toUri();

        return ResponseEntity.created(uri).build();
    }
}
