package com.rodriguez.escuelaDluz.dao;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rodriguez.escuelaDluz.entities.Student;

import jakarta.transaction.Transactional;

public interface IStudentRepository extends JpaRepository<Student, Long> {

	@Modifying
	@Transactional
	@Query("DELETE FROM Student s WHERE s.studentInactiveDate <= :sixMonthsAgo")
	void deleteInactiveStudentsOlderThan(@Param("sixMonthsAgo") Date sixMonthsAgo);

	@Query("SELECT s FROM Student s WHERE s.studentInactiveDate <= :sixMonthsAgo")
	List<Student> findInactiveStudentsOlderThan(@Param("sixMonthsAgo") Date sixMonthsAgo);
	
	Optional<Student> findBystudentDNI(Long dNI);
}
