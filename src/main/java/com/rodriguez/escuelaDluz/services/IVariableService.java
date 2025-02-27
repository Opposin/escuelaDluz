package com.rodriguez.escuelaDluz.services;

import java.util.List;

import com.rodriguez.escuelaDluz.entities.ManagementVariable;

public interface IVariableService {

	
	public void save(ManagementVariable variable);
	
	public ManagementVariable findById(Long id);
	
	public void delete(Long id);
	
	public List<ManagementVariable> findAll();
	
	public List<String> horariosCompletos();
}
