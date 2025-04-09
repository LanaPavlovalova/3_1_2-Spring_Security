package ru.kata.spring.boot_security.demo.repository;

import ru.kata.spring.boot_security.demo.entity.Role;
import java.util.List;

public interface RoleRepository {
    Role findByName(String name);
    Role findById(Long id);
    List<Role> findAll();
    void save(Role role);
}