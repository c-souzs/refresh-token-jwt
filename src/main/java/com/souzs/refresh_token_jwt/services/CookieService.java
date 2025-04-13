package com.souzs.refresh_token_jwt.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

@Service
public class CookieService {
    public String getItemFromCookiesByName(HttpServletRequest request, String name) {
        Cookie cookie = WebUtils.getCookie(request, name);

        if(cookie != null) return cookie.getValue();

        return null;
    }

    public ResponseCookie setItemFromCookiesByName(String key, String value, Long maxAge) {
        return ResponseCookie.from(key, value)
                .path("/")
                .maxAge(maxAge)
                .httpOnly(true)
                .build();
    }

    public ResponseCookie getCleanCookie(String key) {
        return ResponseCookie.from(key, null)
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .build();
    }
}
