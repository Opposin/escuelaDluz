package com.rodriguez.escuelaDluz.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rodriguez.escuelaDluz.entities.Exam;
import com.rodriguez.escuelaDluz.entities.Student;

public interface IExamRepository extends JpaRepository<Exam, Long>{

	List<Exam> findByExamAlertTrue();
	
	 void deleteAllByStudent(Student student);
}
