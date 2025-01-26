package com.rodriguez.escuelaDluz.services;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import com.rodriguez.escuelaDluz.dao.IAppointmentRepository;
import com.rodriguez.escuelaDluz.dao.IPaymentRepository;
import com.rodriguez.escuelaDluz.dao.IStudentRepository;
import com.rodriguez.escuelaDluz.entities.Appointment;
import com.rodriguez.escuelaDluz.entities.Payment;
import com.rodriguez.escuelaDluz.entities.Student;
import com.rodriguez.escuelaDluz.entities.StudentAppointmentDTO;

import jakarta.transaction.Transactional;

@Service
public class AppointmentService implements IAppointmentService {

	@Autowired
	IAppointmentRepository appointmentRepository;

	@Autowired
	IStudentRepository studentRepository;
	
	@Autowired
	IPaymentRepository paymentRepository;

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
	public Page<StudentAppointmentDTO> getStudentsWithNextAppointmentGraduate(Pageable pageable) {
	    LocalDate today = LocalDate.now();
	    LocalTime now = LocalTime.now();

	    // Obtener todos los estudiantes desde la base de datos (sin paginación para ordenar todo)
	    Page<Student> studentsPage = studentRepository.findAll(PageRequest.of(0, Integer.MAX_VALUE));

	    // Transformar los estudiantes en StudentAppointmentDTOs con los turnos más cercanos y último pago
	    List<StudentAppointmentDTO> studentsWithAppointments = studentsPage.getContent().stream().map(student -> {
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

	        // Obtener el último pago realizado
	        Payment lastPayment = student.getStudentPayments().stream()
	                .max(Comparator.comparing(Payment::getPaymentNumber))
	                .orElse(null);

	        // Crear el DTO incluyendo el turno más cercano y el último pago
	        return new StudentAppointmentDTO(student, nextAppointment, lastPayment);
	    }).collect(Collectors.toList());

	    // Ordenar todos los estudiantes por su próximo turno, primero los que tienen turno
	    List<StudentAppointmentDTO> sortedStudents = studentsWithAppointments.stream().sorted((s1, s2) -> {
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
	    }).collect(Collectors.toList());

	    // Paginación manual sobre la lista ordenada
	    int totalElements = sortedStudents.size();
	    int pageSize = pageable.getPageSize();
	    int start = (int) pageable.getOffset();
	    int end = Math.min((start + pageSize), totalElements);

	    List<StudentAppointmentDTO> paginatedStudents = sortedStudents.subList(start, end);

	    // Devolver un Page con los estudiantes ordenados y paginados
	    return new PageImpl<>(paginatedStudents, pageable, totalElements);
	}

//	@Override
//	public Page<StudentAppointmentDTO> getStudentsWithNextAppointmentGraduate(Pageable pageable) {
//		LocalDate today = LocalDate.now();
//		LocalTime now = LocalTime.now();
//
//		// Obtener todos los estudiantes desde la base de datos (sin paginación para
//		// ordenar todo)
//		Page<Student> studentsPage = studentRepository.findAll(PageRequest.of(0, Integer.MAX_VALUE)); // Usamos un
//																										// tamaño máximo
//																										// para obtener
//																										// todos los
//																										// estudiantes.
//
//		// Transformar los estudiantes en StudentAppointmentDTOs con los turnos más
//		// cercanos
//		List<StudentAppointmentDTO> studentsWithAppointments = studentsPage.getContent().stream().map(student -> {
//			// Filtrar los turnos que son hoy o en el futuro y ordenar por fecha y hora
//			List<Appointment> upcomingAppointments = student.getAppointments().stream()
//					.filter(appointment -> !appointment.getAppointmentDate().toLocalDate().isBefore(today)
//							|| (appointment.getAppointmentDate().toLocalDate().isEqual(today)
//									&& LocalTime.parse(appointment.getAppointmentTime()).isAfter(now)))
//					.sorted(Comparator.comparing(Appointment::getAppointmentDate)
//							.thenComparing(appointment -> LocalTime.parse(appointment.getAppointmentTime())))
//					.collect(Collectors.toList());
//
//			// Obtener el primer turno, el más cercano
//			Appointment nextAppointment = (upcomingAppointments.isEmpty()) ? null : upcomingAppointments.get(0);
//
//			return new StudentAppointmentDTO(student, nextAppointment);
//		}).collect(Collectors.toList());
//
//		// Ordenar todos los estudiantes por su próximo turno, primero los que tienen
//		// turno
//		List<StudentAppointmentDTO> sortedStudents = studentsWithAppointments.stream().sorted((s1, s2) -> {
//			// Si ambos estudiantes tienen un turno, los ordenamos por la fecha y hora del
//			// turno
//			if (s1.getAppointmentDate() != null && s2.getAppointmentDate() != null) {
//				int dateComparison = s1.getAppointmentDate().compareTo(s2.getAppointmentDate());
//				if (dateComparison == 0) {
//					return s1.getAppointmentTime().compareTo(s2.getAppointmentTime());
//				}
//				return dateComparison;
//			}
//			// Si uno de los estudiantes no tiene turno, se coloca al final
//			return s1.getAppointmentDate() == null ? 1 : -1;
//		}).collect(Collectors.toList());
//
//		// Paginación manual sobre la lista ordenada
//		int totalElements = sortedStudents.size();
//		int pageSize = pageable.getPageSize();
//		int start = (int) pageable.getOffset();
//		int end = Math.min((start + pageSize), totalElements);
//
//		List<StudentAppointmentDTO> paginatedStudents = sortedStudents.subList(start, end);
//
//		// Devolver un Page con los estudiantes ordenados y paginados
//		return new PageImpl<>(paginatedStudents, pageable, totalElements);
//	}

//	@Override
//	public Page<StudentAppointmentDTO> getStudentsWithNextAppointment(Pageable pageable) {
//		LocalDate today = LocalDate.now();
//		LocalTime now = LocalTime.now();
//
//		// Obtener todos los estudiantes desde la base de datos (sin paginación para
//		// ordenar todo)
//		Page<Student> studentsPage = studentRepository.findAll(PageRequest.of(0, Integer.MAX_VALUE));
//
//		// Filtrar y transformar los estudiantes en StudentAppointmentDTOs con los
//		// turnos más cercanos
//		List<StudentAppointmentDTO> studentsWithAppointments = studentsPage.getContent().stream()
//				.filter(student -> !student.getStudentGraduate()) // Filtrar estudiantes con studentGraduate == true
//				.map(student -> {
//					// Filtrar los turnos que son hoy o en el futuro y ordenar por fecha y hora
//					List<Appointment> upcomingAppointments = student.getAppointments().stream()
//							.filter(appointment -> !appointment.getAppointmentDate().toLocalDate().isBefore(today)
//									|| (appointment.getAppointmentDate().toLocalDate().isEqual(today)
//											&& LocalTime.parse(appointment.getAppointmentTime()).isAfter(now)))
//							.sorted(Comparator.comparing(Appointment::getAppointmentDate)
//									.thenComparing(appointment -> LocalTime.parse(appointment.getAppointmentTime())))
//							.collect(Collectors.toList());
//
//					// Obtener el primer turno, el más cercano
//					Appointment nextAppointment = (upcomingAppointments.isEmpty()) ? null : upcomingAppointments.get(0);
//
//					return new StudentAppointmentDTO(student, nextAppointment);
//				}).collect(Collectors.toList());
//
//		// Ordenar todos los estudiantes por su próximo turno, primero los que tienen
//		// turno
//		List<StudentAppointmentDTO> sortedStudents = studentsWithAppointments.stream().sorted((s1, s2) -> {
//			// Si ambos estudiantes tienen un turno, los ordenamos por la fecha y hora del
//			// turno
//			if (s1.getAppointmentDate() != null && s2.getAppointmentDate() != null) {
//				int dateComparison = s1.getAppointmentDate().compareTo(s2.getAppointmentDate());
//				if (dateComparison == 0) {
//					return s1.getAppointmentTime().compareTo(s2.getAppointmentTime());
//				}
//				return dateComparison;
//			}
//			// Si uno de los estudiantes no tiene turno, se coloca al final
//			return s1.getAppointmentDate() == null ? 1 : -1;
//		}).collect(Collectors.toList());
//
//		// Paginación manual sobre la lista ordenada
//		int totalElements = sortedStudents.size();
//		int pageSize = pageable.getPageSize();
//		int start = (int) pageable.getOffset();
//		int end = Math.min((start + pageSize), totalElements);
//
//		List<StudentAppointmentDTO> paginatedStudents = sortedStudents.subList(start, end);
//
//		// Devolver un Page con los estudiantes ordenados y paginados
//		return new PageImpl<>(paginatedStudents, pageable, totalElements);
//	}

	@Override
	public Page<StudentAppointmentDTO> getStudentsWithNextAppointment(Pageable pageable) {
	    LocalDate today = LocalDate.now();
	    LocalTime now = LocalTime.now();

	    // Obtener todos los estudiantes desde la base de datos (sin paginación para ordenar todo)
	    Page<Student> studentsPage = studentRepository.findAll(PageRequest.of(0, Integer.MAX_VALUE));

	    // Filtrar y transformar los estudiantes en StudentAppointmentDTOs con los turnos más cercanos
	    List<StudentAppointmentDTO> studentsWithAppointments = studentsPage.getContent().stream()
	            .filter(student -> !student.getStudentGraduate()) // Filtrar estudiantes con studentGraduate == true
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

	                // Obtener el último pago realizado
	                Payment lastPayment = student.getStudentPayments().stream()
	                        .max(Comparator.comparing(Payment::getPaymentNumber))
	                        .orElse(null);

	                // Crear el DTO incluyendo el turno más cercano y el último pago
	                return new StudentAppointmentDTO(student, nextAppointment, lastPayment);
	            }).collect(Collectors.toList());

	    // Ordenar todos los estudiantes por su próximo turno, primero los que tienen turno
	    List<StudentAppointmentDTO> sortedStudents = studentsWithAppointments.stream().sorted((s1, s2) -> {
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
	    }).collect(Collectors.toList());

	    // Paginación manual sobre la lista ordenada
	    int totalElements = sortedStudents.size();
	    int pageSize = pageable.getPageSize();
	    int start = (int) pageable.getOffset();
	    int end = Math.min((start + pageSize), totalElements);

	    List<StudentAppointmentDTO> paginatedStudents = sortedStudents.subList(start, end);

	    // Devolver un Page con los estudiantes ordenados y paginados
	    return new PageImpl<>(paginatedStudents, pageable, totalElements);
	}
	
	private static final List<String> HORARIOS = Arrays.asList("09:00", "09:45", "10:30", "11:15", "12:00", "12:45",
			"13:30", "14:15", "15:00", "15:45", "16:30", "17:15", "18:00");

	@Override
	public List<Map<String, Object>> generarFechasDisponibles() {
		List<Map<String, Object>> fechasDisponibles = new ArrayList<>();
		LocalDate fechaActual = LocalDate.now();
		int diasGenerados = 0;

		// Iterar para los próximos 10 días hábiles
		while (diasGenerados < 10) {
			if (fechaActual.getDayOfWeek() != DayOfWeek.SATURDAY && fechaActual.getDayOfWeek() != DayOfWeek.SUNDAY) {

				Date sqlDate = Date.valueOf(fechaActual);
				List<Appointment> turnosDelDia = appointmentRepository.findByAppointmentDate(sqlDate);

				// Contar turnos por horario
				Map<String, Long> conteoHorarios = turnosDelDia.stream()
						.collect(Collectors.groupingBy(Appointment::getAppointmentTime, Collectors.counting()));

				// Filtrar horarios disponibles
				List<String> horariosDisponibles = HORARIOS.stream()
						.filter(horario -> conteoHorarios.getOrDefault(horario, 0L) < 2).collect(Collectors.toList());

				if (!horariosDisponibles.isEmpty()) {
					Map<String, Object> fechaHorarios = new HashMap<>();
					fechaHorarios.put("fechaISO", fechaActual.toString());
//                    fechaHorarios.put("fecha", fechaActual.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
					fechaHorarios.put("fecha", fechaActual.format(DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy")));
//                    fechaHorarios.put("fecha", fechaActual.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
					fechaHorarios.put("horariosDisponibles", horariosDisponibles);
					fechasDisponibles.add(fechaHorarios);
					diasGenerados++;
				}
			}
			fechaActual = fechaActual.plusDays(1);
		}

		return fechasDisponibles;
	}

	@Override
	public List<String> obtenerHorariosDisponiblesPorFecha(LocalDate fecha) {
		// Convertir LocalDate a java.sql.Date para usar con JPA
		Date fechaSQL = Date.valueOf(fecha);

		// Obtener los turnos existentes para la fecha seleccionada
		List<Appointment> turnosDelDia = appointmentRepository.findByAppointmentDate(fechaSQL);

		// Contar la cantidad de turnos por horario
		Map<String, Long> conteoHorarios = turnosDelDia.stream()
				.collect(Collectors.groupingBy(Appointment::getAppointmentTime, Collectors.counting()));

		// Filtrar los horarios disponibles
		return HORARIOS.stream().filter(horario -> conteoHorarios.getOrDefault(horario, 0L) < 2)
				.collect(Collectors.toList());
	}

	
	
	
	@Override
	public List<Appointment> getPastAppointments() {
		String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
		return appointmentRepository.findPastAppointments(currentTime);
	}

}
