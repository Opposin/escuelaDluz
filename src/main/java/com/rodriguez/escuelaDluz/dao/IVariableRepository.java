package com.rodriguez.escuelaDluz.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rodriguez.escuelaDluz.entities.ManagementVariable;

public interface IVariableRepository extends JpaRepository<ManagementVariable, Long>{

}
