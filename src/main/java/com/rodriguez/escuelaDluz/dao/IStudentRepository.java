package com.rodriguez.escuelaDluz.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rodriguez.escuelaDluz.entities.Student;

public interface IStudentRepository extends JpaRepository<Student, Long>{

}
