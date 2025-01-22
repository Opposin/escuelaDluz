package com.rodriguez.escuelaDluz.services;

import java.util.List;

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
	
}
