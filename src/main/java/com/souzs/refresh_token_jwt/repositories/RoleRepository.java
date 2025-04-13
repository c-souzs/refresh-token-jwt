package com.souzs.refresh_token_jwt.repositories;

import com.souzs.refresh_token_jwt.domain.entities.Role;
import com.souzs.refresh_token_jwt.domain.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole role);
}
