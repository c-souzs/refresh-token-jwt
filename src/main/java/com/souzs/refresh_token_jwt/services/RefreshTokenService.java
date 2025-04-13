package com.souzs.refresh_token_jwt.services;

import com.souzs.refresh_token_jwt.domain.entities.RefreshToken;
import com.souzs.refresh_token_jwt.domain.entities.User;
import com.souzs.refresh_token_jwt.repositories.RefreshTokenRepository;
import com.souzs.refresh_token_jwt.repositories.UserRepository;
import com.souzs.refresh_token_jwt.security.jwt.JwtUtils;
import com.souzs.refresh_token_jwt.services.exceptions.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class RefreshTokenService {
    @Value("${jwt.refreshExp}")
    private Long refreshExp;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("Usuário não encontrado.")
        );

        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByUser(user);

        // Se existir um refreshToken e ele for válido, retorna ele.
        if(refreshTokenOpt.isPresent()) {
            if(verifyExpiration(refreshTokenOpt.get())) return refreshTokenOpt.get();
        }

        // Se nao existir ele cria o refreshToken
        RefreshToken refreshToken = new RefreshToken();
        String refreshTokenValue = jwtUtils.generateRefreshJwt(user);

        refreshToken.setToken(refreshTokenValue);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshExp));

        refreshTokenRepository.save(refreshToken);

        return refreshToken;
    }

    @Transactional
    public boolean verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new AuthException("Refresh token expirado.");
        }

        return true;
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }
}
