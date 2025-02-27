package com.rodriguez.escuelaDluz.controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.rodriguez.escuelaDluz.dao.IUserRepository;
import com.rodriguez.escuelaDluz.entities.Rol;
import com.rodriguez.escuelaDluz.entities.User;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/users")
public class UserController {

	private final IUserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserController(IUserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping
	public String listUsers(Model model) {
		List<User> allUsers = userRepository.findAll();

		// Filtrar usuarios con rol ADMIN o RECEPCIONISTA
		List<User> adminAndRecepcionistaUsers = allUsers.stream()
				.filter(user -> user.getRol() == Rol.ADMIN || user.getRol() == Rol.RECEPCIONISTA)
				.collect(Collectors.toList());

		// Filtrar usuarios con rol ALUMNO
		List<User> alumnoUsers = allUsers.stream().filter(user -> user.getRol() == Rol.ALUMNO)
				.collect(Collectors.toList());

		// Agregar ambas listas al modelo
		model.addAttribute("adminAndRecepcionistaUsers", adminAndRecepcionistaUsers);
		model.addAttribute("alumnoUsers", alumnoUsers);
		return "users/list"; // Nombre de la plantilla en Thymeleaf
	}

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

	@PostMapping("/save")
	public String saveUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("roles", Rol.values());
			return "redirect:/users";
		}

		if (userRepository.existsByUsername(user.getUsername())) {
			throw new RuntimeException("El nombre de usuario ya existe.");
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

		if (userRepository.existsByUsername(user.getUsername())) {
			throw new RuntimeException("El nombre de usuario ya existe.");
		}

		user.setRol(Rol.RECEPCIONISTA);
		// Encriptar la contraseña antes de guardar
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		userRepository.save(user);
		return "redirect:/users"; // Redirige a la lista de usuarios
	}

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

	@GetMapping("/delete/{id}")
	public String deleteUser(@PathVariable Long id) {
		userRepository.deleteById(id);
		return "redirect:/users";
	}
}
