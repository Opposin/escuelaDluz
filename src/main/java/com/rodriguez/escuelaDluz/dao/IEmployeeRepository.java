package com.rodriguez.escuelaDluz.dao;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rodriguez.escuelaDluz.entities.Appointment;
import com.rodriguez.escuelaDluz.entities.Employee;

public interface IEmployeeRepository extends JpaRepository<Employee, Long> {

	@Query("SELECT e FROM Employee e WHERE e.employeeType = 'recepcionista' AND e.employeeActive = true")
	List<Employee> findRecepcionistas();

	@Query("SELECT e FROM Employee e WHERE e.employeeType = 'instructor' AND e.employeeActive = true")
	List<Employee> findInstructores();


}
