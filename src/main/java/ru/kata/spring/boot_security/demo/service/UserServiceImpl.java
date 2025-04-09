package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleService roleService,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> findAllWithRoles() {
        return userRepository.findAllWithRoles();
    }

    @Override
    public Optional<User> findByIdWithRoles(Long id) {
        return userRepository.findByIdWithRoles(id);
    }

    @Override
    @Transactional
    public void create(User user, Set<Long> roleIds) {
        System.out.println("Raw password: " + user.getPassword()); // Для отладки

        validateUserBeforeCreate(user);

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        System.out.println("Encoded password: " + encodedPassword); // Для отладки

        user.setPassword(encodedPassword);

        Set<Role> roles = getValidatedRoles(roleIds);
        user.setRoles(roles);

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void update(Long id, User updatedUser, Set<Long> roleIds) {
        User existingUser = findByIdWithRoles(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        validateUserBeforeUpdate(existingUser, updatedUser);

        if (shouldUpdatePassword(updatedUser, existingUser)) {
            String encodedPassword = passwordEncoder.encode(updatedUser.getPassword());
            existingUser.setPassword(encodedPassword);
        }

        Set<Role> roles = getValidatedRoles(roleIds);
        existingUser.setRoles(roles);

        updateUserFields(existingUser, updatedUser);
        userRepository.update(existingUser);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.delete(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Set<Long> getUserRoleIds(Long userId) {
        return userRepository.findRoleIdsByUserId(userId);
    }

    @Override
    public Optional<User> findByUsernameWithRoles(String username) {
        return userRepository.findByUsernameWithRoles(username);
    }

    private void validateUserBeforeCreate(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
    }

    private void validateUserBeforeUpdate(User existingUser, User updatedUser) {
        if (!existingUser.getUsername().equals(updatedUser.getUsername())
                && userRepository.existsByUsername(updatedUser.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (!existingUser.getEmail().equals(updatedUser.getEmail())
                && userRepository.existsByEmail(updatedUser.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
    }

    private Set<Role> getValidatedRoles(Set<Long> roleIds) {
        Set<Role> roles = roleIds.stream()
                .map(roleService::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        if (roles.size() != roleIds.size()) {
            throw new RuntimeException("One or more roles not found");
        }
        return roles;
    }

    private boolean shouldUpdatePassword(User updatedUser, User existingUser) {
        return updatedUser.getPassword() != null
                && !updatedUser.getPassword().isEmpty()
                && !passwordEncoder.matches(updatedUser.getPassword(), existingUser.getPassword());
    }

    private void updateUserFields(User existingUser, User updatedUser) {
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setAge(updatedUser.getAge());
    }
}