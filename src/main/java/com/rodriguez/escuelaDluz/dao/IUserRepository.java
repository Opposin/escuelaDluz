package com.rodriguez.escuelaDluz.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rodriguez.escuelaDluz.entities.User;

public interface IUserRepository extends JpaRepository<User, Long>{

	Optional<User> findByUsername(String username);
}
