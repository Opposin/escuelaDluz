package com.rodriguez.escuelaDluz.services;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.rodriguez.escuelaDluz.entities.Appointment;
import com.rodriguez.escuelaDluz.entities.StudentAppointmentDTO;

public interface IAppointmentService {
	
	public void save(Appointment appointment);
	
	public Appointment findById(Long id);
	
	public void delete(Long id);
	
	public List<Appointment> findAll();

	Page<StudentAppointmentDTO> getStudentsWithNextAppointment(Pageable pageable);
	
	Page<StudentAppointmentDTO> getStudentsWithNextAppointmentGraduate(Pageable pageable);
	
	public List<Map<String, Object>> generarFechasDisponibles();
	
	public List<String> obtenerHorariosDisponiblesPorFecha(LocalDate fecha);

    public List<Appointment> getPastAppointments();
    
    public void updateAppointmentsAfterCancellation(Appointment canceledAppointment);

//	Appointment saveOrUpdateAppointment(Appointment appointment, Long studentId);
	
}
