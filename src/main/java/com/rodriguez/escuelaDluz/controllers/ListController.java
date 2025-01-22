package com.rodriguez.escuelaDluz.controllers;

import java.sql.Date;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.rodriguez.escuelaDluz.entities.StudentAppointmentDTO;
import com.rodriguez.escuelaDluz.services.IAppointmentService;
import com.rodriguez.escuelaDluz.services.IStudentService;

@Controller
public class ListController {
	private IStudentService studentService;
	private IAppointmentService appointmentService;

	public ListController(IStudentService studentService, IAppointmentService appointmentService) {
		this.studentService = studentService;
		this.appointmentService = appointmentService;
	}

	@GetMapping({ "/", "/home", "/index" })
	public String homeList(Model model,
			@RequestParam(name = "pagina", required = false, defaultValue = "0") Integer pagina,
			@RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {

//		Pageable pageable = PageRequest.of(pagina, size);
//
//		Page<StudentAppointmentDTO> studentsWithAppointments = appointmentService
//				.getStudentsWithNextAppointment(pageable);
//
//		model.addAttribute("students", studentsWithAppointments);
//		model.addAttribute("actual", pagina + 1);
//		model.addAttribute("titulo", "Listado de Alumnos");
//
//		int totalPages = studentsWithAppointments.getTotalPages();
//		if (totalPages > 0) {
//			List<Integer> paginas = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
//			model.addAttribute("paginas", paginas);
//		}
//
//		return "home";
		Pageable pageable = PageRequest.of(pagina, size);

		// Obtener la página de estudiantes con sus turnos
		Page<StudentAppointmentDTO> studentsWithAppointments = appointmentService
				.getStudentsWithNextAppointment(pageable);

		// Filtrar y ordenar los estudiantes
		List<StudentAppointmentDTO> sortedStudents = studentsWithAppointments.getContent().stream().sorted((s1, s2) -> {
			// Primero, verificar si ambos tienen un turno asignado
			if (s1.getAppointmentDate() == null || s1.getAppointmentTime().isEmpty()) {
				return 1; // s1 va al final si no tiene turno
			}
			if (s2.getAppointmentDate() == null || s2.getAppointmentTime().isEmpty()) {
				return -1; // s2 va al final si no tiene turno
			}
			// Comparar los turnos por appointmentDate y luego por appointmentTime
			int dateComparison = s1.getAppointmentDate().compareTo(s2.getAppointmentDate());
			if (dateComparison != 0) {
				return dateComparison; // Si las fechas son diferentes, ordena por fecha
			}
			// Si las fechas son iguales, compara por hora (appointmentTime)
			return s1.getAppointmentTime().compareTo(s2.getAppointmentTime());
		}).collect(Collectors.toList());

		// Separar los que tienen y no tienen turno
		List<StudentAppointmentDTO> studentsWithAppointment = sortedStudents.stream()
				.filter(s -> s.getAppointmentDate() != null && !s.getAppointmentTime().isEmpty())
				.collect(Collectors.toList());

		List<StudentAppointmentDTO> studentsWithoutAppointment = sortedStudents.stream()
				.filter(s -> s.getAppointmentDate() == null || s.getAppointmentTime().isEmpty())
				.collect(Collectors.toList());

		// Combinar las dos listas (primero los que tienen turno, luego los que no
		// tienen)
		studentsWithAppointment.addAll(studentsWithoutAppointment);

		// Agregar la lista al modelo
		model.addAttribute("students", studentsWithAppointment);
		model.addAttribute("actual", pagina + 1);
		model.addAttribute("titulo", "Listado de Alumnos");

		// Paginación
		int totalPages = studentsWithAppointments.getTotalPages();
		if (totalPages > 0) {
			List<Integer> paginas = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("paginas", paginas);
		}

		return "home";

	}

	@GetMapping({ "/filter", "/homeFilter", "/indexFilter" })
	public String homeListFilter(Model model,
			@RequestParam(name = "pagina", required = false, defaultValue = "0") Integer pagina,
			@RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
			@RequestParam(name = "filter") String param, @RequestParam(name = "filterType") String filterType) {

		Pageable pageable = PageRequest.of(pagina, size);

		// Obtener la página de estudiantes con citas
		Page<StudentAppointmentDTO> studentsWithAppointments = appointmentService
				.getStudentsWithNextAppointment(pageable);

		List<StudentAppointmentDTO> filteredStudents;

		if (param.equals("")) {

			List<StudentAppointmentDTO> sortedStudents = studentsWithAppointments.getContent().stream()
					.sorted((s1, s2) -> {
						// Primero, verificar si ambos tienen un turno asignado
						if (s1.getAppointmentDate() == null || s1.getAppointmentTime().isEmpty()) {
							return 1; // s1 va al final si no tiene turno
						}
						if (s2.getAppointmentDate() == null || s2.getAppointmentTime().isEmpty()) {
							return -1; // s2 va al final si no tiene turno
						}
						// Comparar los turnos por appointmentDate y luego por appointmentTime
						int dateComparison = s1.getAppointmentDate().compareTo(s2.getAppointmentDate());
						if (dateComparison != 0) {
							return dateComparison; // Si las fechas son diferentes, ordena por fecha
						}
						// Si las fechas son iguales, compara por hora (appointmentTime)
						return s1.getAppointmentTime().compareTo(s2.getAppointmentTime());
					}).collect(Collectors.toList());

			// Separar los que tienen y no tienen turno
			List<StudentAppointmentDTO> studentsWithAppointment = sortedStudents.stream()
					.filter(s -> s.getAppointmentDate() != null && !s.getAppointmentTime().isEmpty())
					.collect(Collectors.toList());

			List<StudentAppointmentDTO> studentsWithoutAppointment = sortedStudents.stream()
					.filter(s -> s.getAppointmentDate() == null || s.getAppointmentTime().isEmpty())
					.collect(Collectors.toList());

			// Combinar las dos listas (primero los que tienen turno, luego los que no
			// tienen)
			studentsWithAppointment.addAll(studentsWithoutAppointment);

			// Agregar el objeto Page al modelo
			model.addAttribute("msj", "No se encontro ningun alumno con los parametros especificados...");
			model.addAttribute("tipoMsj", "danger");
			model.addAttribute("students", studentsWithAppointments);
			model.addAttribute("actual", pagina + 1); // Página actual (1-indexed)
			model.addAttribute("titulo", "Listado de Alumnos filtrado");

			// Paginación: obtener la lista de páginas disponibles
			int totalPages = studentsWithAppointments.getTotalPages();
			if (totalPages > 0) {
				List<Integer> paginas = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
				model.addAttribute("paginas", paginas);
			}

			return "home"; // Nombre de la vista Thymeleaf
		}

		if ("name".equalsIgnoreCase(filterType)) {
			// Filter by name or last name
			filteredStudents = studentsWithAppointments.getContent().stream()
					.filter(student -> student.getStudentName().toLowerCase().contains(param.toLowerCase())
							|| student.getStudentLastName().toLowerCase().contains(param.toLowerCase()))
					.collect(Collectors.toList());
		} else if ("nonAtten".equalsIgnoreCase(filterType)) {
			// Filter by student non assitances
			try {
				long inasistencias = Long.parseLong(param);
				filteredStudents = studentsWithAppointments.getContent().stream()
						.filter(student -> student.getStudentNonAtten() >= inasistencias).collect(Collectors.toList());
			} catch (NumberFormatException e) {
				filteredStudents = new ArrayList<>();
				model.addAttribute("error", "El parámetro de inasistencias debe ser un número válido.");
			}
		} else if ("DNI".equalsIgnoreCase(filterType)) {
			// Filter by student DNI
			try {
				long DNI = Long.parseLong(param);
				filteredStudents = studentsWithAppointments.getContent().stream()
						.filter(student -> student.getStudentDNI() == DNI).collect(Collectors.toList());
			} catch (NumberFormatException e) {
				filteredStudents = new ArrayList<>();
				model.addAttribute("error", "El parámetro de DNI debe ser un número válido.");
			}
		} else if ("Age".equalsIgnoreCase(filterType)) {
			// Filtrar por cantidad de inasistencias
			try {
				long age = Long.parseLong(param); // Convertir el parámetro a long
				filteredStudents = studentsWithAppointments.getContent().stream()
						.filter(student -> student.getStudentAge() >= age).collect(Collectors.toList());
			} catch (NumberFormatException e) {
				// Si el parámetro no es un número válido, manejamos el error
				filteredStudents = new ArrayList<>();
				model.addAttribute("error", "El parámetro de Edad debe ser un número válido.");
			}
		} else if ("appointmentDate".equalsIgnoreCase(filterType)) {
			// Filtrar por fecha de cita
			try {
				// Convertir el parámetro a java.sql.Date
				Date appointmentDate = Date.valueOf(param); // Asegúrate de que la fecha esté en formato correcto
															// (yyyy-MM-dd)
				filteredStudents = studentsWithAppointments.getContent().stream()
						.filter(student -> student.getAppointmentDate() != null
								&& student.getAppointmentDate().equals(appointmentDate)) // Filtra por la fecha exacta
						.collect(Collectors.toList());
			} catch (DateTimeParseException e) {
				// Si la fecha no tiene el formato correcto, manejamos el error
				filteredStudents = new ArrayList<>();
				model.addAttribute("error", "El parámetro de fecha debe tener el formato correcto (yyyy-MM-dd).");
			}
		} else {
			// Si el tipo de filtro no es reconocido, devolvemos todos los estudiantes sin
			// filtrar
			filteredStudents = studentsWithAppointments.getContent();
		}

		// Crear una nueva Page a partir de los estudiantes filtrados
		Page<StudentAppointmentDTO> filteredPage = new PageImpl<>(filteredStudents, pageable,
				studentsWithAppointments.getTotalElements());

		// Agregar el objeto Page al modelo
		model.addAttribute("students", filteredPage);
		model.addAttribute("actual", pagina + 1); // Página actual (1-indexed)
		model.addAttribute("titulo", "Listado de Alumnos filtrado");

		// Paginación: obtener la lista de páginas disponibles
		int totalPages = filteredPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> paginas = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("paginas", paginas);
		}

		return "home"; // Nombre de la vista Thymeleaf
	}
}
