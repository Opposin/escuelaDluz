package com.rodriguez.escuelaDluz.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.rodriguez.escuelaDluz.dao.IStudentRepository;
import com.rodriguez.escuelaDluz.entities.Student;

import jakarta.transaction.Transactional;

@Service
public class StudentService implements IStudentService {
	
	@Autowired
	private IStudentRepository studentRepository;
	
	@Override
	@Transactional
	public void save(Student student) {
		studentRepository.save(student);
	}

	@Override
	public Student findById(Long id) {
		return studentRepository.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		studentRepository.deleteById(id);

	}

	@Override
	public List<Student> findAll() {
		return (List<Student>) studentRepository.findAll();
	}

	@Override
	public Page<Student> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return studentRepository.findAll(pageable);
	}

}
