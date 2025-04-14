package com.souzs.refresh_token_jwt.services;

import com.souzs.refresh_token_jwt.domain.dtos.UserBasicDTO;
import com.souzs.refresh_token_jwt.domain.dtos.UserDTO;
import com.souzs.refresh_token_jwt.domain.entities.Role;
import com.souzs.refresh_token_jwt.domain.entities.User;
import com.souzs.refresh_token_jwt.domain.enums.ERole;
import com.souzs.refresh_token_jwt.domain.payloads.TokenCookies;
import com.souzs.refresh_token_jwt.domain.dtos.UserAuthDTO;
import com.souzs.refresh_token_jwt.domain.entities.RefreshToken;
import com.souzs.refresh_token_jwt.repositories.RefreshTokenRepository;
import com.souzs.refresh_token_jwt.repositories.UserRepository;
import com.souzs.refresh_token_jwt.security.UserDetailsImpl;
import com.souzs.refresh_token_jwt.security.jwt.JwtUtils;
import com.souzs.refresh_token_jwt.services.exceptions.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    public UserDTO currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();

        return new UserDTO(user);
    }

    @Transactional
    public TokenCookies signin(UserAuthDTO userDTO) {
        Authentication authentication = null;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDTO.getEmail(), userDTO.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new UsernameNotFoundException("Usuário ou senha inválidos.");
        }

        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();

        String accessToken = jwtUtils.generateAccessJwt(user);
        ResponseCookie accessTokenCookie = jwtUtils.setAccessTokenCookie(accessToken);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUser().getId());
        ResponseCookie refreshTokenCookie = jwtUtils.setRefreshTokenCookie(refreshToken.getToken());

        return new TokenCookies(accessTokenCookie, refreshTokenCookie);
    }

    @Transactional
    public TokenCookies refreshToken(HttpServletRequest request) {
        String accessToken = jwtUtils.getAccessTokenFromCookies(request);

        if(jwtUtils.validateJwtToken(accessToken)) throw new AuthException("Token de acesso ainda válido.");

        String refreshTokenCookie = jwtUtils.getRefreshTokenFromCookies(request);
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenCookie).orElseThrow(
                () -> new AuthException("Refresh Token inválido.")
        );

        if(
                refreshToken != null &&
                jwtUtils.validateJwtToken(refreshTokenCookie) &&
                refreshTokenCookie.equals(refreshToken.getToken())
        ) {
            String newAccessToken = jwtUtils.generateAccessJwt(refreshToken.getUser());
            ResponseCookie cookieAccessToken = jwtUtils.setAccessTokenCookie(newAccessToken);

            return new TokenCookies(cookieAccessToken, null);
        } else {
            throw new AuthException("Refresh token expirado.");
        }
    }

    @Transactional
    public TokenCookies signout(HttpServletRequest request) {
        String refreshTokenCookie = jwtUtils.getRefreshTokenFromCookies(request);
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenCookie).orElseThrow(
                () -> new AuthException("Refresh Token inválido.")
        );

        refreshTokenRepository.deleteByUser(refreshToken.getUser());

        ResponseCookie accessClear = jwtUtils.getCleanAccessToken();
        ResponseCookie refreshClear = jwtUtils.getCleanRefreshToken();

        return new TokenCookies(accessClear, refreshClear);
    }

    @Transactional
    public UserDTO signup(UserBasicDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());

        Role basic = new Role();
        basic.setId(ERole.ROLE_USER.getId());

        user.setRoles(Set.of(basic));

        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        user = userRepository.save(user);

        return new UserDTO(user);
    }
}
