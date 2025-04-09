package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    List<User> findAllWithRoles();
    Optional<User> findByIdWithRoles(Long id); // Возвращаем Optional
    void create(User user, Set<Long> roleIds);
    void update(Long id, User updatedUser, Set<Long> roleIds);
    void delete(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Set<Long> getUserRoleIds(Long userId);
    Optional<User> findByUsernameWithRoles(String username);
}