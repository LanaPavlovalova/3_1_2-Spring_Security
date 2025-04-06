package ru.kata.spring.boot_security.demo.repository;

import ru.kata.spring.boot_security.demo.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository {
    Optional<User> findByUsernameWithRoles(String username);
    Optional<User> findByIdWithRoles(Long id);
    List<User> findAllWithRoles();
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    void save(User user);
    void update(User user);
    void delete(Long id);
    Set<Long> findRoleIdsByUserId(Long userId);
}