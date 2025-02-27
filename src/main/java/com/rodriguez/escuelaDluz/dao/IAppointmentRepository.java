package com.rodriguez.escuelaDluz.dao;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rodriguez.escuelaDluz.entities.Appointment;
import com.rodriguez.escuelaDluz.entities.Student;

public interface IAppointmentRepository extends JpaRepository<Appointment, Long> {

	List<Appointment> findByAppointmentDate(Date sqlDate);

//	@Query("SELECT a FROM Appointment a WHERE " +
//		       "(a.appointmentDate < DATE('now') OR " +
//		       "(a.appointmentDate = DATE('now') AND a.appointmentTime < :currentTime)) " +
//		       "AND (a.appointmentComplete IS NULL OR a.appointmentComplete = '0' OR a.appointmentComplete = 'Turno pendiente.')")
//		List<Appointment> findPastAppointments(String currentTime);
//
//


	@Query("SELECT a FROM Appointment a WHERE a.student.id = :studentId AND "
			+ "(a.appointmentDate > :date OR (a.appointmentDate = :date AND a.appointmentTime > :time)) "
			+ "ORDER BY a.appointmentDate ASC, a.appointmentTime ASC")
	List<Appointment> findAppointmentsAfter(Long studentId, Date date, String time);

	@Query("SELECT a FROM Appointment a WHERE a.appointmentDate = :date AND a.employee.id = :instructorId")
	List<Appointment> findByAppointmentDateAndInstructorId(@Param("date") Date date,
			@Param("instructorId") Long instructorId);
	
	 void deleteAllByStudent(Student student);
}
