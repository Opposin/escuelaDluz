package com.rodriguez.escuelaDluz.dao;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.rodriguez.escuelaDluz.entities.Appointment;

public interface IAppointmentRepository extends JpaRepository<Appointment, Long>{

	List<Appointment> findByAppointmentDate(Date sqlDate);
	

	@Query("SELECT a FROM Appointment a WHERE " +
		       "(a.appointmentDate < CURRENT_DATE OR " +
		       "(a.appointmentDate = CURRENT_DATE AND a.appointmentTime < :currentTime)) AND " +
		       "(a.appointmentComplete IS NULL OR a.appointmentComplete = '0' OR a.appointmentComplete = 'Turno pendiente.')")
    List<Appointment> findPastAppointments(String currentTime);

}
