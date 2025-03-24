package ru.kata.spring.boot_security.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // 🔹 Найти роли по списку ID
    public Set<Role> findAllByIds(Set<Long> ids) {
        return new HashSet<>(roleRepository.findAllById(ids));
    }

    // 🔹 Получить все роли (для формы)
    public List<Role> findAll() {
        return roleRepository.findAll();
    }
}