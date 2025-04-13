package com.souzs.refresh_token_jwt.domain.dtos;


import com.souzs.refresh_token_jwt.domain.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private List<RoleDTO> roles = new ArrayList<>();

    public UserDTO(User user) {
        id = user.getId();
        username = user.getUsername();
        email = user.getEmail();

        user.getRoles().stream()
                .map(role -> new RoleDTO(role))
                .forEach(roleDTO -> roles.add(roleDTO));
    }
}
