package com.rodriguez.escuelaDluz.services;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.rodriguez.escuelaDluz.dao.IAppointmentRepository;
import com.rodriguez.escuelaDluz.dao.IEmployeeRepository;
import com.rodriguez.escuelaDluz.dao.IPaymentRepository;
import com.rodriguez.escuelaDluz.dao.IStudentRepository;
import com.rodriguez.escuelaDluz.dao.IVariableRepository;
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

	@Autowired
	IEmployeeRepository employeeRepository;

	@Autowired
	VariableService horarioService;

	@Autowired
	static IVariableRepository variableRepository;

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

		// Obtener todos los estudiantes desde la base de datos (sin paginación para
		// ordenar todo)
		Page<Student> studentsPage = studentRepository.findAll(PageRequest.of(0, Integer.MAX_VALUE));

		// Filtrar y transformar los estudiantes en StudentAppointmentDTOs con los
		// turnos más cercanos
		List<StudentAppointmentDTO> studentsWithAppointments = studentsPage.getContent().stream()
				.filter(student -> student.getStudentGraduate() != null // Incluir tanto true como false
						&& (student.getStudentInactive() == null || !student.getStudentInactive())) // Excluir inactivos
				.map(student -> {
					// Filtrar los turnos que son hoy o en el futuro y que sean "Turno Pendiente"
					List<Appointment> upcomingAppointments = student.getAppointments().stream()
							.filter(appointment -> "Turno Pendiente.".equals(appointment.getAppointmentComplete()) && // Solo
																														// incluir
																														// turnos
																														// pendientes
									(!appointment.getAppointmentDate().toLocalDate().isBefore(today)
											|| (appointment.getAppointmentDate().toLocalDate().isEqual(today)
													&& LocalTime.parse(appointment.getAppointmentTime()).isAfter(now))))
							.sorted(Comparator.comparing(Appointment::getAppointmentDate)
									.thenComparing(appointment -> LocalTime.parse(appointment.getAppointmentTime())))
							.collect(Collectors.toList());

					// Obtener el primer turno, el más cercano
					Appointment nextAppointment = (upcomingAppointments.isEmpty()) ? null : upcomingAppointments.get(0);

					// Obtener el último pago realizado
					Payment lastPayment = student.getStudentPayments().stream()
							.max(Comparator.comparing(Payment::getPaymentNumber)).orElse(null);

					// Crear el DTO incluyendo el turno más cercano y el último pago
					return new StudentAppointmentDTO(student, nextAppointment, lastPayment);
				}).collect(Collectors.toList());

		// Ordenar todos los estudiantes por su próximo turno, primero los que tienen
		// turno
		List<StudentAppointmentDTO> sortedStudents = studentsWithAppointments.stream().sorted((s1, s2) -> {
			// Si ambos estudiantes tienen un turno, los ordenamos por la fecha y hora del
			// turno
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

	@Override
	public Page<StudentAppointmentDTO> getStudentsWithNextAppointment(Pageable pageable) {
		LocalDate today = LocalDate.now();
		LocalTime now = LocalTime.now();

		// Obtener todos los estudiantes desde la base de datos (sin paginación para
		// ordenar todo)
		Page<Student> studentsPage = studentRepository.findAll(PageRequest.of(0, Integer.MAX_VALUE));

		// Filtrar y transformar los estudiantes en StudentAppointmentDTOs con los
		// turnos más cercanos
		List<StudentAppointmentDTO> studentsWithAppointments = studentsPage.getContent().stream()
				.filter(student -> !student.getStudentGraduate()
						&& (student.getStudentInactive() == null || !student.getStudentInactive()) // Filtrar
																									// estudiantes
																									// inactivos
				).map(student -> {
					// Filtrar los turnos que son hoy o en el futuro, que sean "Turno Pendiente" y
					// ordenar por fecha y hora
					List<Appointment> upcomingAppointments = student.getAppointments().stream()
							.filter(appointment -> "Turno Pendiente.".equals(appointment.getAppointmentComplete()) && // Filtrar
																														// solo
																														// los
																														// turnos
																														// pendientes
									(!appointment.getAppointmentDate().toLocalDate().isBefore(today)
											|| (appointment.getAppointmentDate().toLocalDate().isEqual(today)
													&& LocalTime.parse(appointment.getAppointmentTime()).isAfter(now))))
							.sorted(Comparator.comparing(Appointment::getAppointmentDate)
									.thenComparing(appointment -> LocalTime.parse(appointment.getAppointmentTime())))
							.collect(Collectors.toList());

					// Obtener el primer turno, el más cercano
					Appointment nextAppointment = (upcomingAppointments.isEmpty()) ? null : upcomingAppointments.get(0);

					// Obtener el último pago realizado
					Payment lastPayment = student.getStudentPayments().stream()
							.max(Comparator.comparing(Payment::getPaymentNumber)).orElse(null);

					// Crear el DTO incluyendo el turno más cercano y el último pago
					return new StudentAppointmentDTO(student, nextAppointment, lastPayment);
				}).collect(Collectors.toList());

		// Ordenar todos los estudiantes por su próximo turno, primero los que tienen
		// turno
		List<StudentAppointmentDTO> sortedStudents = studentsWithAppointments.stream().sorted((s1, s2) -> {
			// Si ambos estudiantes tienen un turno, los ordenamos por la fecha y hora del
			// turno
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
//	public List<Map<String, Object>> generarFechasDisponibles() {
//		List<Map<String, Object>> fechasDisponibles = new ArrayList<>();
//		LocalDate fechaActual = LocalDate.now();
//		int diasGenerados = 0;
//
//		// Iterar para los próximos 10 días hábiles incluyendo sábados
//		while (diasGenerados < 10) {
//			if (fechaActual.getDayOfWeek() != DayOfWeek.SUNDAY) { // Solo excluimos los domingos
//
//				Date sqlDate = Date.valueOf(fechaActual);
//				List<Appointment> turnosDelDia = appointmentRepository.findByAppointmentDate(sqlDate);
//
//				// Contar turnos por horario
//				Map<String, Long> conteoHorarios = turnosDelDia.stream()
//						.collect(Collectors.groupingBy(Appointment::getAppointmentTime, Collectors.counting()));
//
//				// Filtrar horarios disponibles
//				List<String> horariosDisponibles = HORARIOS.stream()
//						.filter(horario -> conteoHorarios.getOrDefault(horario, 0L) < 2).collect(Collectors.toList());
//
//				if (!horariosDisponibles.isEmpty()) {
//					Map<String, Object> fechaHorarios = new HashMap<>();
//					fechaHorarios.put("fechaISO", fechaActual.toString());
//					fechaHorarios.put("fecha", fechaActual.format(DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy")));
//					fechaHorarios.put("horariosDisponibles", horariosDisponibles);
//					fechasDisponibles.add(fechaHorarios);
//					diasGenerados++;
//				}
//			}
//			fechaActual = fechaActual.plusDays(1);
//		}
//
//		return fechasDisponibles;
//	}
//
//	@Override
//	public List<String> obtenerHorariosDisponiblesPorFecha(LocalDate fecha, Long instructorId) {
//		Date fechaSQL = Date.valueOf(fecha);
//		List<Appointment> turnosDelDia = appointmentRepository.findByAppointmentDateAndInstructorId(fechaSQL,
//				instructorId);
//
//		// Filtrar los turnos válidos (ignorar cancelados e inasistencias)
//		List<Appointment> turnosValidos = turnosDelDia.stream()
//				.filter(turno -> turno.getAppointmentComplete() == null
//						|| (!turno.getAppointmentComplete().equalsIgnoreCase("Cancelado")
//								&& !turno.getAppointmentComplete().equalsIgnoreCase("Inasistencia")))
//				.toList();
//
//		// Contar turnos del instructor por horario, considerando appointmentTime y
//		// appointmentTime2
//		Set<String> horariosOcupados = new HashSet<>();
//
//		for (Appointment turno : turnosValidos) {
//			// Marcar appointmentTime como ocupado
//			horariosOcupados.add(turno.getAppointmentTime());
//
//			// Marcar appointmentTime2 como ocupado si no es null
//			if (turno.getAppointmentTime2() != null) {
//				horariosOcupados.add(turno.getAppointmentTime2());
//			}
//		}
//
//		// Filtrar horarios disponibles para el instructor
//		return HORARIOS.stream().filter(horario -> !horariosOcupados.contains(horario)) // Excluir los horarios ocupados
//				.collect(Collectors.toList());
//	}
//
//	@Override
//	public List<String> obtenerHorariosDisponiblesConsecutivos(LocalDate fecha, Long instructorId) {
//		Date fechaSQL = Date.valueOf(fecha);
//		List<Appointment> turnosDelDia = appointmentRepository.findByAppointmentDateAndInstructorId(fechaSQL,
//				instructorId);
//
//		// Filtrar los turnos válidos (ignorar cancelados e inasistencias)
//		List<Appointment> turnosValidos = turnosDelDia.stream()
//				.filter(turno -> turno.getAppointmentComplete() == null
//						|| (!turno.getAppointmentComplete().equalsIgnoreCase("Cancelado")
//								&& !turno.getAppointmentComplete().equalsIgnoreCase("Inasistencia")))
//				.toList();
//
//		// Contar turnos del instructor por horario, considerando appointmentTime y
//		// appointmentTime2
//		Map<String, Long> conteoHorarios = new HashMap<>();
//
//		for (Appointment turno : turnosValidos) {
//			// Contar appointmentTime
//			conteoHorarios.put(turno.getAppointmentTime(),
//					conteoHorarios.getOrDefault(turno.getAppointmentTime(), 0L) + 1);
//
//			// Contar appointmentTime2 si no es null
//			if (turno.getAppointmentTime2() != null) {
//				conteoHorarios.put(turno.getAppointmentTime2(),
//						conteoHorarios.getOrDefault(turno.getAppointmentTime2(), 0L) + 1);
//			}
//		}
//
//		// Filtrar horarios disponibles consecutivos
//		List<String> horariosDisponibles = new ArrayList<>();
//		for (int i = 0; i < HORARIOS.size() - 1; i++) { // Iteramos hasta el penúltimo horario
//			String horarioActual = HORARIOS.get(i);
//			String horarioSiguiente = HORARIOS.get(i + 1);
//
//			// Verificar si ambos horarios están disponibles
//			if (conteoHorarios.getOrDefault(horarioActual, 0L) == 0
//					&& conteoHorarios.getOrDefault(horarioSiguiente, 0L) == 0) {
//				horariosDisponibles.add(horarioActual); // Agregar solo el primer horario del par consecutivo
//			}
//		}
//
//		return horariosDisponibles;
//	}

	public List<Map<String, Object>> generarFechasDisponibles() {
		List<Map<String, Object>> fechasDisponibles = new ArrayList<>();
		LocalDate fechaActual = LocalDate.now();
		int diasGenerados = 0;

		// Obtener la lista dinámica de horarios
		List<String> horariosActualizados = horarioService.horariosCompletos();

		while (diasGenerados < 10) {
			if (fechaActual.getDayOfWeek() != DayOfWeek.SUNDAY) { // Solo excluimos los domingos

				Date sqlDate = Date.valueOf(fechaActual);
				List<Appointment> turnosDelDia = appointmentRepository.findByAppointmentDate(sqlDate);

				// Contar turnos por horario
				Map<String, Long> conteoHorarios = turnosDelDia.stream()
						.collect(Collectors.groupingBy(Appointment::getAppointmentTime, Collectors.counting()));

				// Filtrar horarios disponibles
				List<String> horariosDisponibles = horariosActualizados.stream()
						.filter(horario -> conteoHorarios.getOrDefault(horario, 0L) < 2).collect(Collectors.toList());

				if (!horariosDisponibles.isEmpty()) {
					Map<String, Object> fechaHorarios = new HashMap<>();
					fechaHorarios.put("fechaISO", fechaActual.toString());
					fechaHorarios.put("fecha", fechaActual.format(DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy")));
					fechaHorarios.put("horariosDisponibles", horariosDisponibles);
					fechasDisponibles.add(fechaHorarios);
					diasGenerados++;
				}
			}
			fechaActual = fechaActual.plusDays(1);
		}

		return fechasDisponibles;
	}

	public List<String> obtenerHorariosDisponiblesPorFecha(LocalDate fecha, Long instructorId) {
		Date fechaSQL = Date.valueOf(fecha);
		List<Appointment> turnosDelDia = appointmentRepository.findByAppointmentDateAndInstructorId(fechaSQL,
				instructorId);

		// Obtener la lista dinámica de horarios
		List<String> horariosActualizados = horarioService.horariosCompletos();

		// Filtrar los turnos válidos (ignorar cancelados e inasistencias)
		List<Appointment> turnosValidos = turnosDelDia.stream()
				.filter(turno -> turno.getAppointmentComplete() == null
						|| (!turno.getAppointmentComplete().equalsIgnoreCase("Cancelado")
								&& !turno.getAppointmentComplete().equalsIgnoreCase("Inasistencia")))
				.toList();

		// Contar turnos del instructor por horario
		Set<String> horariosOcupados = new HashSet<>();

		for (Appointment turno : turnosValidos) {
			horariosOcupados.add(turno.getAppointmentTime());
			if (turno.getAppointmentTime2() != null) {
				horariosOcupados.add(turno.getAppointmentTime2());
			}
		}

		return horariosActualizados.stream().filter(horario -> !horariosOcupados.contains(horario)) // Excluir los
																									// horarios ocupados
				.collect(Collectors.toList());
	}

	public List<String> obtenerHorariosDisponiblesConsecutivos(LocalDate fecha, Long instructorId) {
		Date fechaSQL = Date.valueOf(fecha);
		List<Appointment> turnosDelDia = appointmentRepository.findByAppointmentDateAndInstructorId(fechaSQL,
				instructorId);

		// Obtener la lista dinámica de horarios
		List<String> horariosActualizados = horarioService.horariosCompletos();

		// Filtrar los turnos válidos (ignorar cancelados e inasistencias)
		List<Appointment> turnosValidos = turnosDelDia.stream()
				.filter(turno -> turno.getAppointmentComplete() == null
						|| (!turno.getAppointmentComplete().equalsIgnoreCase("Cancelado")
								&& !turno.getAppointmentComplete().equalsIgnoreCase("Inasistencia")))
				.toList();

		// Contar turnos del instructor por horario
		Map<String, Long> conteoHorarios = new HashMap<>();

		for (Appointment turno : turnosValidos) {
			conteoHorarios.put(turno.getAppointmentTime(),
					conteoHorarios.getOrDefault(turno.getAppointmentTime(), 0L) + 1);
			if (turno.getAppointmentTime2() != null) {
				conteoHorarios.put(turno.getAppointmentTime2(),
						conteoHorarios.getOrDefault(turno.getAppointmentTime2(), 0L) + 1);
			}
		}

		// Filtrar horarios disponibles consecutivos
		List<String> horariosDisponibles = new ArrayList<>();
		for (int i = 0; i < horariosActualizados.size() - 1; i++) { // Iteramos hasta el penúltimo horario
			String horarioActual = horariosActualizados.get(i);
			String horarioSiguiente = horariosActualizados.get(i + 1);

			// Verificar si ambos horarios están disponibles
			if (conteoHorarios.getOrDefault(horarioActual, 0L) == 0
					&& conteoHorarios.getOrDefault(horarioSiguiente, 0L) == 0) {
				horariosDisponibles.add(horarioActual); // Agregar solo el primer horario del par consecutivo
			}
		}

		return horariosDisponibles;
	}

	@Override

	public List<Appointment> getPastAppointments() {
		List<Appointment> allAppointments = appointmentRepository.findAll();

		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

		return allAppointments.stream()
				.filter(appointment -> "Turno Pendiente.".equals(appointment.getAppointmentComplete())) // Filtra por
																										// estado
				.filter(appointment -> {
					LocalDate appointmentDate = appointment.getAppointmentDate().toLocalDate();
					LocalTime appointmentTime;

					try {
						appointmentTime = LocalTime.parse(appointment.getAppointmentTime(), timeFormatter);
					} catch (Exception e) {
						return false; // Si hay error en el formato de la hora, ignoramos este turno
					}

					LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, appointmentTime);
					return appointmentDateTime.isBefore(now); // Verifica si el turno ya pasó
				}).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void updateAppointmentsAfterCancellation(Appointment canceledAppointment) {
		if (canceledAppointment == null || canceledAppointment.getStudent() == null) {
			return;
		}

		Student student = canceledAppointment.getStudent();
		List<Appointment> subsequentAppointments = appointmentRepository.findAppointmentsAfter(student.getId(),
				canceledAppointment.getAppointmentDate(), canceledAppointment.getAppointmentTime());

		// Disminuir el número de clase en los turnos posteriores
		if (canceledAppointment.getAppointmentClassNumber2() != null) {
			for (Appointment appointment : subsequentAppointments) {
				if (appointment.getAppointmentClassNumber2() != null) {
					appointment.setAppointmentClassNumber(appointment.getAppointmentClassNumber() - 1);
					appointment.setAppointmentClassNumber2(appointment.getAppointmentClassNumber2() - 1);
				} else {
					appointment.setAppointmentClassNumber(appointment.getAppointmentClassNumber() - 2);
				}
			}
		} else {
			for (Appointment appointment : subsequentAppointments) {
				if (appointment.getAppointmentClassNumber2() != null) {
					appointment.setAppointmentClassNumber(appointment.getAppointmentClassNumber() - 1);
					appointment.setAppointmentClassNumber2(appointment.getAppointmentClassNumber2() - 1);
				} else {
					appointment.setAppointmentClassNumber(appointment.getAppointmentClassNumber() - 1);
				}
			}
		}

		// Guardar cambios en la base de datos
		appointmentRepository.saveAll(subsequentAppointments);
	}

	@Override
	public List<Appointment> getPendingAppointmentsWithInactiveEmployees() {
		return appointmentRepository.findAll().stream()
				.filter(a -> "Turno Pendiente.".equals(a.getAppointmentComplete())) // Filtra por appointmentComplete
				.filter(a -> a.getEmployee() != null && Boolean.FALSE.equals(a.getEmployee().getEmployeeActive())) // Filtra
																													// por
																													// employeeActive
																													// false
				.filter(a -> a.getAppointmentInactiveInstructorAlert() == null
						|| !a.getAppointmentInactiveInstructorAlert()) // Ignora si es true, incluye si es null o false
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void actualizarClasesDelStudent(Student student) {
		// Filtrar los appointments válidos (excluyendo Cancelado e Inasistencia)
		List<Appointment> validAppointments = student.getStudentAppointments().stream()
				.filter(a -> !a.getAppointmentComplete().equalsIgnoreCase("Cancelado")
						&& !a.getAppointmentComplete().equalsIgnoreCase("Inasistencia"))
				.sorted(Comparator.comparing(Appointment::getAppointmentDate).thenComparing(a -> {
					String[] timeParts = a.getAppointmentTime().split(":");
					return Integer.parseInt(timeParts[0]) * 60 + Integer.parseInt(timeParts[1]);
				})).collect(Collectors.toList());

		if (validAppointments.isEmpty()) {
			return; // No hay citas válidas para procesar
		}

		Long nextClassNumber = student.getStudentClass() + 1; // Se inicia con studentClass
		if (nextClassNumber == null || nextClassNumber == 0) {
			nextClassNumber = 1L; // Si es null, se inicia en 1
		}

		for (Appointment appointment : validAppointments) {
			// Si hay un turno anterior, ajustar el número de clase

			appointment.setAppointmentClassNumber(nextClassNumber);

			// Si appointmentClassNumber2 no era null, se asigna como appointmentClassNumber
			// + 1
			if (appointment.getAppointmentClassNumber2() != null) {
				appointment.setAppointmentClassNumber2(appointment.getAppointmentClassNumber() + 1);
			}

			// Determinar el incremento para el siguiente turno
			if (appointment.getAppointmentClassNumber2() != null) {
				nextClassNumber += 2; // Si appointmentClassNumber2 existía, sumamos 2
			} else {
				nextClassNumber += 1; // Si no existía, solo sumamos 1
			}
		}
//		System.out.println(validAppointments);
		// Guardar los cambios en la base de datos
		appointmentRepository.saveAll(validAppointments);
	}

}
