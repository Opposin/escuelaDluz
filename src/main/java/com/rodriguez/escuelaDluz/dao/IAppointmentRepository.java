package com.rodriguez.escuelaDluz.dao;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rodriguez.escuelaDluz.entities.Appointment;

public interface IAppointmentRepository extends JpaRepository<Appointment, Long>{

	List<Appointment> findByAppointmentDate(Date sqlDate);

}
