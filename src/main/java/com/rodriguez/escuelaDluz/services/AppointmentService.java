package com.rodriguez.escuelaDluz.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.rodriguez.escuelaDluz.dao.IAppointmentRepository;
import com.rodriguez.escuelaDluz.dao.IStudentRepository;
import com.rodriguez.escuelaDluz.entities.Appointment;
import com.rodriguez.escuelaDluz.entities.Student;
import com.rodriguez.escuelaDluz.entities.StudentAppointmentDTO;

import jakarta.transaction.Transactional;

@Service
public class AppointmentService implements IAppointmentService {

	@Autowired
	IAppointmentRepository appointmentRepository;

	@Autowired
	IStudentRepository studentRepository;

	@Override
	@Transactional
	public void save(Appointment appointment) {
		appointmentRepository.save(appointment);

	}

	@Override
	public Appointment findById(Long id) {
		return appointmentRepository.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		appointmentRepository.deleteById(id);

	}

	@Override
	public List<Appointment> findAll() {
		return (List<Appointment>) appointmentRepository.findAll();
	}

	@Override
	public Page<StudentAppointmentDTO> getStudentsWithNextAppointment(Pageable pageable) {
		LocalDate today = LocalDate.now();
		LocalTime now = LocalTime.now();

		// Obtener los estudiantes paginados
		Page<Student> studentsPage = studentRepository.findAll(pageable);

		// Transformar los estudiantes en StudentAppointmentDTOs con los turnos más
		// cercanos
		Page<StudentAppointmentDTO> studentsWithAppointments = studentsPage.map(student -> {
			// Filtrar los turnos para este estudiante que sean hoy o en el futuro
			List<Appointment> upcomingAppointments = student.getAppointments().stream()
					.filter(appointment -> !appointment.getAppointmentDate().toLocalDate().isBefore(today)
							|| (appointment.getAppointmentDate().toLocalDate().isEqual(today)
									&& LocalTime.parse(appointment.getAppointmentTime()).isAfter(now)))
					.sorted(Comparator.comparing(Appointment::getAppointmentDate)
							.thenComparing(appointment -> LocalTime.parse(appointment.getAppointmentTime())))
					.collect(Collectors.toList());

			// Si el estudiante tiene turnos disponibles, agregar el más cercano
			Appointment nextAppointment = (upcomingAppointments.isEmpty()) ? null : upcomingAppointments.get(0);

			return new StudentAppointmentDTO(student, nextAppointment);
		});

		return studentsWithAppointments;
	}

}
