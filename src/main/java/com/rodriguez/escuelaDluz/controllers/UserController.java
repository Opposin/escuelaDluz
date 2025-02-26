package com.rodriguez.escuelaDluz.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.rodriguez.escuelaDluz.dao.IUserRepository;
import com.rodriguez.escuelaDluz.entities.Rol;
import com.rodriguez.escuelaDluz.entities.User;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(IUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** 📌 Listar todos los usuarios */
    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "users/list"; // Nombre de la plantilla en Thymeleaf
    }

    /** 📌 Mostrar formulario para crear un nuevo usuario */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", Rol.values()); // Pasamos los roles disponibles
        return "users/form";
    }
    
    @GetMapping("/recepcionistNew")
    public String showRecepcionistCreateForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", Rol.values()); // Pasamos los roles disponibles
        return "users/recepcionist";
    }

    /** 📌 Guardar nuevo usuario */
    @PostMapping("/save")
    public String saveUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", Rol.values());
            return "redirect:/users";
        }

        // Encriptar la contraseña antes de guardar
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        return "redirect:/users"; // Redirige a la lista de usuarios
    }
    
    @PostMapping("/save-recepcionist")
    public String saveRecepcionist(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", Rol.values());
            return "redirect:/users";
        }

        user.setRol(Rol.RECEPCIONISTA);
        // Encriptar la contraseña antes de guardar
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        return "redirect:/users"; // Redirige a la lista de usuarios
    }

    /** 📌 Mostrar formulario para editar un usuario */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return "redirect:/users"; // Si no existe, redirigir a la lista
        }
        model.addAttribute("user", userOptional.get());
        model.addAttribute("roles", Rol.values());
        return "users/form";
    }

    /** 📌 Eliminar usuario */
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/users";
    }
}
