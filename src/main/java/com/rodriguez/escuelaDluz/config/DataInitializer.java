package com.rodriguez.escuelaDluz.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.rodriguez.escuelaDluz.dao.IUserRepository;
import com.rodriguez.escuelaDluz.entities.Rol;
import com.rodriguez.escuelaDluz.entities.User;

@Component
public class DataInitializer implements CommandLineRunner {

	private final IUserRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;

	public DataInitializer(IUserRepository usuarioRepository, PasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) {
		if (usuarioRepository.findByUsername("admin").isEmpty()) {
			User admin = new User();
			admin.setUsername("admin");
			admin.setPassword(passwordEncoder.encode("admin2477"));
			admin.setRol(Rol.ADMIN);
			usuarioRepository.save(admin);
		}

		if (usuarioRepository.findByUsername("recepcionista").isEmpty()) {
			User recep = new User();
			recep.setUsername("recepcionista");
			recep.setPassword(passwordEncoder.encode("recepcionista123"));
			recep.setRol(Rol.RECEPCIONISTA);
			usuarioRepository.save(recep);
		}
	}

}
