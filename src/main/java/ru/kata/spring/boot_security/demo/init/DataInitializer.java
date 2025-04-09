package ru.kata.spring.boot_security.demo.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserService userService,
                           RoleService roleService,
                           PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        initRoles();
        initUsers();
    }

    private void initRoles() {
        if (roleService.findAll().isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            roleService.save(adminRole);

            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            roleService.save(userRole);
        }
    }

    private void initUsers() {
        if (userService.findAllWithRoles().isEmpty()) {
            Role adminRole = roleService.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));
            Role userRole = roleService.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("User role not found"));

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin");
            admin.setEmail("admin@example.com");
            admin.setAge(30);
            admin.setRoles(Set.of(adminRole, userRole));
            userService.create(admin, Set.of(adminRole.getId(), userRole.getId()));

            User user = new User();
            user.setUsername("user");
            user.setPassword("user");
            user.setEmail("user@example.com");
            user.setAge(25);
            user.setRoles(Set.of(userRole));
            userService.create(user, Set.of(userRole.getId()));

            verifyPasswords();
        }
    }

    private void verifyPasswords() {
        User admin = userService.findByUsernameWithRoles("admin")
                .orElseThrow(() -> new RuntimeException("Admin user not found"));
        User user = userService.findByUsernameWithRoles("user")
                .orElseThrow(() -> new RuntimeException("Regular user not found"));

        System.out.println("Admin password in DB: " + admin.getPassword());
        System.out.println("User password in DB: " + user.getPassword());

        boolean adminCheck = passwordEncoder.matches("admin", admin.getPassword());
        boolean userCheck = passwordEncoder.matches("user", user.getPassword());

        System.out.println("Admin password check: " + adminCheck);
        System.out.println("User password check: " + userCheck);

        if (!adminCheck || !userCheck) {
            throw new IllegalStateException("");
        }
    }
}