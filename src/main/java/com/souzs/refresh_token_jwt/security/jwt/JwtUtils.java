package com.souzs.refresh_token_jwt.security.jwt;

import com.souzs.refresh_token_jwt.domain.entities.User;
import com.souzs.refresh_token_jwt.security.UserDetailsImpl;
import com.souzs.refresh_token_jwt.services.CookieService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.accessNameCookie}")
    private String accessNameCookie;

    @Value("${jwt.refreshNameCookie}")
    private String refreshNameCookie;

    @Value("${jwt.accessExp}")
    private Long accessExp;

    @Value("${jwt.refreshExp}")
    private Long refreshExp;

    @Autowired
    private CookieService cookieService;

    public String generateAccessJwt(UserDetailsImpl user) {
        List<String> roles = user.getAuthorities().stream().map(authority -> authority.getAuthority()).toList();
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        claims.put("id", user.getUser().getId());

        return generateToken(user.getUsername(), claims, accessExp);
    }

    public String generateAccessJwt(User user) {
        List<String> roles = user.getRoles().stream().map(authority -> authority.getName()).toList();
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        claims.put("id", user.getId());

        return generateToken(user.getUsername(), claims, accessExp);
    }

    public String generateRefreshJwt(User user) {
        return generateToken(user.getUsername(), null, refreshExp);
    }

    public String getEmailFromJwtToken(String token) {
        return Jwts.parser().verifyWith(key()).build()
                .parseSignedClaims(token)
                .getPayload().getSubject();
    }

    public ResponseCookie getCleanAccessToken() {
        return cookieService.getCleanCookie(accessNameCookie);
    }

    public ResponseCookie getCleanRefreshToken() {
        return cookieService.getCleanCookie(refreshNameCookie);
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser().verifyWith(key()).build().parse(token);
            return true;
        } catch (MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getAccessTokenFromCookies(HttpServletRequest request) {
        return cookieService.getItemFromCookiesByName(request, accessNameCookie);
    }

    public String getRefreshTokenFromCookies(HttpServletRequest request) {
        return cookieService.getItemFromCookiesByName(request, refreshNameCookie);
    }

    public ResponseCookie setAccessTokenCookie(String token) {
        return cookieService.setItemFromCookiesByName(accessNameCookie, token, accessExp / 1000);
    }

    public ResponseCookie setRefreshTokenCookie(String token) {
        return cookieService.setItemFromCookiesByName(refreshNameCookie, token, refreshExp / 1000);
    }

    private String generateToken(String email, Map<String, Object> claims,  Long exp) {
        return Jwts.builder()
                .subject(email)
                .claims(claims)
                .issuedAt(new Date())
                .expiration((new Date((new Date()).getTime() + exp)))
                .signWith(key(), Jwts.SIG.HS256)
                .compact();
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
}
