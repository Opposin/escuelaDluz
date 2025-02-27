package com.rodriguez.escuelaDluz.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rodriguez.escuelaDluz.dao.IUserRepository;
import com.rodriguez.escuelaDluz.entities.User;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	 private final IUserRepository usuarioRepository;

	    public CustomUserDetailsService(IUserRepository usuarioRepository) {
	        this.usuarioRepository = usuarioRepository;
	    }

	    @Override
	    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//	        System.out.println("Buscando usuario: " + username);
	        
	        User usuario = usuarioRepository.findByUsername(username)
	                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

//	        System.out.println("Usuario encontrado: " + usuario.getUsername());
	        return usuario;
	    }
}
