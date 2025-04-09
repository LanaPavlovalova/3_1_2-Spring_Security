package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.entity.Role;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleService {
    Optional<Role> findById(Long id);
    Optional<Role> findByName(String name); // Изменено на возврат Optional
    Set<Role> findAllByIds(Set<Long> ids);
    List<Role> findAll();
    void save(Role role);
}