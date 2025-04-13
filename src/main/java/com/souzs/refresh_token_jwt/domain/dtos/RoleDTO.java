package com.souzs.refresh_token_jwt.domain.dtos;

import com.souzs.refresh_token_jwt.domain.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {
    private Long id;
    private String name;

    public RoleDTO(Role role) {
        id = role.getId();
        name = role.getName();
    }
}
