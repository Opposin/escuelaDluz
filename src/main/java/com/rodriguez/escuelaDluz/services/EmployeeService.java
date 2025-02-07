package com.rodriguez.escuelaDluz.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rodriguez.escuelaDluz.dao.IEmployeeRepository;
import com.rodriguez.escuelaDluz.entities.Employee;

import jakarta.transaction.Transactional;

@Service
public class EmployeeService implements IEmployeeService {

	
	@Autowired
	IEmployeeRepository EmployeeRepostory;
	
	@Override
	@Transactional
	public void save(Employee employee) {
		EmployeeRepostory.save(employee);

	}

	@Override
	public Employee findById(Long id) {
		return EmployeeRepostory.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		EmployeeRepostory.deleteById(id);

	}

	@Override
	public List<Employee> findAll() {
		return (List<Employee>) EmployeeRepostory.findAll();
	}
	
	@Override
	public List<Employee> findInstructores(){
		return (List<Employee>) EmployeeRepostory.findInstructores();
	}

	@Override
	public List<Employee> findRecepcionistas(){
		return (List<Employee>) EmployeeRepostory.findRecepcionistas();
	}
}
