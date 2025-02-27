package com.rodriguez.escuelaDluz.entities;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "users")
public class User implements UserDetails{

	
	 /**
	 * 
	 */
	private static final long serialVersionUID = -3593675471979350641L;

	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
		
		@Column(unique = true, nullable = false)
	    private String username;
	    private String password;
	    
	    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
	    private Student student;

	    @Enumerated(EnumType.STRING)
	    private Rol rol; // Enum para roles (ADMIN, RECEPCIONISTA)
	    
	    public User() {}
	    
	    public User(String username, String password, Rol rol) {
	        this.username = username;
	        this.password = password;
	        this.rol = rol;
	    }

	    public Long getId() {
	        return id;
	    }

	    public void setId(Long id) {
	        this.id = id;
	    }

	    public String getUsername() {
	        return username;
	    }

	    public void setUsername(String username) {
	        this.username = username;
	    }

	    public String getPassword() {
	        return password;
	    }

	    public void setPassword(String password) {
	        this.password = password;
	    }

	    public Rol getRol() {
	        return rol;
	    }

	    public void setRol(Rol rol) {
	        this.rol = rol;
	    }

	    @Override
	    public Collection<? extends GrantedAuthority> getAuthorities() {
	        return List.of(() -> "ROLE_" + rol.name());
	    }

	    @Override
	    public boolean isAccountNonExpired() {
	        return true;
	    }

	    @Override
	    public boolean isAccountNonLocked() {
	        return true;
	    }

	    @Override
	    public boolean isCredentialsNonExpired() {
	        return true;
	    }

	    @Override
	    public boolean isEnabled() {
	        return true;
	    }

		public Student getStudent() {
			return student;
		}

		public void setStudent(Student student) {
			this.student = student;
		}
	    
}
