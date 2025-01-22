package com.rodriguez.escuelaDluz.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.rodriguez.escuelaDluz.entities.Student;

public interface IStudentService {

	public void save(Student student);
	
	public Student findById(Long id);
	
	public void delete(Long id);
	
	public List<Student> findAll();
	
	public Page<Student> findAll(Pageable pageable);
}
