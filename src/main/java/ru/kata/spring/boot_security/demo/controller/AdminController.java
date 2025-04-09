package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.util.UserValidator;

import javax.validation.Valid;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;
    private final UserValidator userValidator;
    private PasswordEncoder passwordEncoder;

    public AdminController(UserService userService, RoleService roleService, UserValidator userValidator, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.userValidator = userValidator;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("users", userService.findAllWithRoles());
        return "admin";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.findAll());
        return "add_user";
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute("user") @Valid User user,
                          BindingResult bindingResult,
                          @RequestParam("roleIds") Set<Long> roleIds,
                          Model model,
                          RedirectAttributes redirectAttributes) {

        userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleService.findAll());
            return "add_user";
        }

        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userService.create(user, roleIds);
            redirectAttributes.addFlashAttribute("success", "User added successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding user: " + e.getMessage());
            return "redirect:/admin/add";
        }

        return "redirect:/admin";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.findByIdWithRoles(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.findAll());
        model.addAttribute("userRoleIds", userService.getUserRoleIds(id));
        return "edit_user";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute("user") @Valid User user,
                             BindingResult bindingResult,
                             @RequestParam("roleIds") Set<Long> roleIds,
                             RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user", bindingResult);
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/admin/edit/" + id;
        }

        try {
            userService.update(id, user, roleIds);
            redirectAttributes.addFlashAttribute("success", "User updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating user: " + e.getMessage());
            return "redirect:/admin/edit/" + id;
        }

        return "redirect:/admin";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.delete(id);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting user: " + e.getMessage());
        }
        return "redirect:/admin";
    }
}