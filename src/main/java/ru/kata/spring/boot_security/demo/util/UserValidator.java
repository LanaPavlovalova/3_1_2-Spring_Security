package ru.kata.spring.boot_security.demo.util;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.UserService;

@Component
public class UserValidator implements Validator {

    private final UserService userService;

    public UserValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;

        // Проверка уникальности username
        if (userService.existsByUsername(user.getUsername())) {
            errors.rejectValue("username", "", "This username is already taken");
        }

        // Проверка уникальности email
        if (userService.existsByEmail(user.getEmail())) {
            errors.rejectValue("email", "", "This email is already registered");
        }

        // Проверка возраста
        if (user.getAge() == null || user.getAge() < 1 || user.getAge() > 120) {
            errors.rejectValue("age", "", "Age must be between 1 and 120");
        }

        // Проверка пароля
        if (user.getPassword() == null || user.getPassword().length() < 5) {
            errors.rejectValue("password", "", "Password must be at least 5 characters long");
        }
    }
}