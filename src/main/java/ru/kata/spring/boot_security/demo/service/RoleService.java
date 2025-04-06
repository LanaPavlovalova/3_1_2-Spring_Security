package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.entity.Role;

import java.util.List;
import java.util.Set;


public interface RoleService {
    Set<Role> findAllByIds(Set<Long> ids);
    List<Role> findAll();
}