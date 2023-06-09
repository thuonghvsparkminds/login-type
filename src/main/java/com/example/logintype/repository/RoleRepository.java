package com.example.logintype.repository;

import com.example.logintype.entity.Role;
import com.example.logintype.entity.enumrated.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(RoleEnum name);
}
