package com.rodriguez.escuelaDluz.services;

import java.util.List;

import com.rodriguez.escuelaDluz.entities.Employee;

public interface IEmployeeService {
	
	public void save(Employee employee);
	
	public Employee findById(Long id);
	
	public void delete(Long id);
	
	public List<Employee> findAll();
	
	public List<Employee> findInstructores();
	
	public List<Employee> findRecepcionistas();
	
}
