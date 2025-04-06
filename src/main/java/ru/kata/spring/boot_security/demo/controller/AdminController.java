package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;
import java.util.Set;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public String adminPage(Model model) {
        model.addAttribute("users", userService.findAllWithRoles());
        return "admin";
    }

    @GetMapping("/add")
    @Transactional(readOnly = true)
    public String showAddForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.findAll());
        return "add_user";
    }

    @PostMapping("/add")
    @Transactional
    public String addUser(@ModelAttribute("user") User user,
                          @RequestParam("roleIds") Set<Long> roleIds,
                          RedirectAttributes redirectAttributes) {
        try {
            userService.create(user, roleIds);
            redirectAttributes.addFlashAttribute("success", "User added successfully");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/add";
        }
        return "redirect:/admin";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.findByIdWithRoles(id);
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.findAll());
        model.addAttribute("userRoleIds", userService.getUserRoleIds(id));
        return "edit_user";
    }


    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute("user") User user,
                             @RequestParam("roleIds") Set<Long> roleIds,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.update(id, user, roleIds);
            redirectAttributes.addFlashAttribute("success", "User updated successfully");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/edit?id=" + id;
        }
        return "redirect:/admin";
    }

    @PostMapping("/delete/{id}")
    @Transactional
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.delete(id);
        redirectAttributes.addFlashAttribute("success", "User deleted successfully");
        return "redirect:/admin";
    }
}