package com.rodriguez.escuelaDluz.services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

//	@Override
//	public Page<StudentAppointmentDTO> getStudentsWithNextAppointment(Pageable pageable) {
//		LocalDate today = LocalDate.now();
//		LocalTime now = LocalTime.now();
//
//		// Obtener los estudiantes paginados
//		Page<Student> studentsPage = studentRepository.findAll(pageable);
//
//		// Transformar los estudiantes en StudentAppointmentDTOs con los turnos más
//		// cercanos
//		Page<StudentAppointmentDTO> studentsWithAppointments = studentsPage.map(student -> {
//			// Filtrar los turnos para este estudiante que sean hoy o en el futuro
//			List<Appointment> upcomingAppointments = student.getAppointments().stream()
//					.filter(appointment -> !appointment.getAppointmentDate().toLocalDate().isBefore(today)
//							|| (appointment.getAppointmentDate().toLocalDate().isEqual(today)
//									&& LocalTime.parse(appointment.getAppointmentTime()).isAfter(now)))
//					.sorted(Comparator.comparing(Appointment::getAppointmentDate)
//							.thenComparing(appointment -> LocalTime.parse(appointment.getAppointmentTime())))
//					.collect(Collectors.toList());
//
//			// Si el estudiante tiene turnos disponibles, agregar el más cercano
//			Appointment nextAppointment = (upcomingAppointments.isEmpty()) ? null : upcomingAppointments.get(0);
//
//			return new StudentAppointmentDTO(student, nextAppointment);
//		});
//
//		return studentsWithAppointments;
//	}

	public Page<StudentAppointmentDTO> getStudentsWithNextAppointment(Pageable pageable) {
	    LocalDate today = LocalDate.now();
	    LocalTime now = LocalTime.now();

	    // Obtener todos los estudiantes desde la base de datos (sin paginación para ordenar todo)
	    Page<Student> studentsPage = studentRepository.findAll(PageRequest.of(0, Integer.MAX_VALUE)); // Usamos un tamaño máximo para obtener todos los estudiantes.

	    // Transformar los estudiantes en StudentAppointmentDTOs con los turnos más cercanos
	    List<StudentAppointmentDTO> studentsWithAppointments = studentsPage.getContent().stream()
	        .map(student -> {
	            // Filtrar los turnos que son hoy o en el futuro y ordenar por fecha y hora
	            List<Appointment> upcomingAppointments = student.getAppointments().stream()
	                .filter(appointment -> !appointment.getAppointmentDate().toLocalDate().isBefore(today)
	                    || (appointment.getAppointmentDate().toLocalDate().isEqual(today)
	                        && LocalTime.parse(appointment.getAppointmentTime()).isAfter(now)))
	                .sorted(Comparator.comparing(Appointment::getAppointmentDate)
	                    .thenComparing(appointment -> LocalTime.parse(appointment.getAppointmentTime())))
	                .collect(Collectors.toList());

	            // Obtener el primer turno, el más cercano
	            Appointment nextAppointment = (upcomingAppointments.isEmpty()) ? null : upcomingAppointments.get(0);

	            return new StudentAppointmentDTO(student, nextAppointment);
	        })
	        .collect(Collectors.toList());

	    // Ordenar todos los estudiantes por su próximo turno, primero los que tienen turno
	    List<StudentAppointmentDTO> sortedStudents = studentsWithAppointments.stream()
	        .sorted((s1, s2) -> {
	            // Si ambos estudiantes tienen un turno, los ordenamos por la fecha y hora del turno
	            if (s1.getAppointmentDate() != null && s2.getAppointmentDate() != null) {
	                int dateComparison = s1.getAppointmentDate().compareTo(s2.getAppointmentDate());
	                if (dateComparison == 0) {
	                    return s1.getAppointmentTime().compareTo(s2.getAppointmentTime());
	                }
	                return dateComparison;
	            }
	            // Si uno de los estudiantes no tiene turno, se coloca al final
	            return s1.getAppointmentDate() == null ? 1 : -1;
	        })
	        .collect(Collectors.toList());

	    // Paginación manual sobre la lista ordenada
	    int totalElements = sortedStudents.size();
	    int pageSize = pageable.getPageSize();
	    int start = (int) pageable.getOffset();
	    int end = Math.min((start + pageSize), totalElements);

	    List<StudentAppointmentDTO> paginatedStudents = sortedStudents.subList(start, end);

	    // Devolver un Page con los estudiantes ordenados y paginados
	    return new PageImpl<>(paginatedStudents, pageable, totalElements);
	}
	
}
