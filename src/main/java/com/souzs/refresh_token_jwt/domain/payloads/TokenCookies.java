package com.souzs.refresh_token_jwt.domain.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseCookie;

@Data
@AllArgsConstructor
public class TokenCookies {
    private ResponseCookie accessToken;
    private ResponseCookie refreshToken;
}
