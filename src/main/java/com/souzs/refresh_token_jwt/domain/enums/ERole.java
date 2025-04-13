package com.souzs.refresh_token_jwt.domain.enums;

public enum ERole {
    ROLE_ADMIN(1L),
    ROLE_MODERATOR(2L),
    ROLE_USER(3L);

    private Long id;

    ERole(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
