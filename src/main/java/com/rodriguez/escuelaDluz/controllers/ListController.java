package com.rodriguez.escuelaDluz.controllers;

import java.sql.Date;
import java.time.LocalDate;
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

import com.rodriguez.escuelaDluz.entities.Appointment;
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

	    // Crear el Pageable con la página solicitada y el tamaño
	    Pageable pageable = PageRequest.of(pagina, size);

	    // Obtener la página de estudiantes con sus turnos
	    Page<StudentAppointmentDTO> studentsWithAppointments = appointmentService
	            .getStudentsWithNextAppointment(pageable);
	    
	    List<Appointment> pastAppointments = appointmentService.getPastAppointments();

	    // Agregar la lista de estudiantes al modelo
	    model.addAttribute("students", studentsWithAppointments.getContent());
	    model.addAttribute("pastAppointments", pastAppointments);
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

	    // Si el parámetro de filtro está vacío, redirige a la página "home"
	    if (param == null || param.isEmpty()) {
	        return "redirect:/home";
	    }

	    // Crear un objeto Pageable para la paginación
	    Pageable pageable = PageRequest.of(pagina, size);

	    // Obtener todos los estudiantes con citas (sin filtrar)
	    Page<StudentAppointmentDTO> studentsWithAppointments = appointmentService
	            .getStudentsWithNextAppointment(PageRequest.of(0, Integer.MAX_VALUE)); // Sin paginación

	    // Filtrar la lista de estudiantes según los parámetros de filtro
	    List<StudentAppointmentDTO> filteredStudents = studentsWithAppointments.getContent();

	    // Aplica los filtros según el tipo recibido
	    if ("name".equalsIgnoreCase(filterType)) {
	        filteredStudents = filteredStudents.stream()
	                .filter(student -> student.getStudentName().toLowerCase().contains(param.toLowerCase())
	                        || student.getStudentLastName().toLowerCase().contains(param.toLowerCase()))
	                .collect(Collectors.toList());
	    } else if ("nonAtten".equalsIgnoreCase(filterType)) {
	        try {
	            long inasistencias = Long.parseLong(param);
	            filteredStudents = filteredStudents.stream()
	                    .filter(student -> student.getStudentNonAtten() >= inasistencias)
	                    .collect(Collectors.toList());
	        } catch (NumberFormatException e) {
	            model.addAttribute("error", "El parámetro de inasistencias debe ser un número válido.");
	        }
	    } else if ("DNI".equalsIgnoreCase(filterType)) {
	        try {
	            long DNI = Long.parseLong(param);
	            filteredStudents = filteredStudents.stream()
	                    .filter(student -> student.getStudentDNI() == DNI)
	                    .collect(Collectors.toList());
	        } catch (NumberFormatException e) {
	            model.addAttribute("error", "El parámetro de DNI debe ser un número válido.");
	        }
	    } else if ("Age".equalsIgnoreCase(filterType)) {
	        try {
	            long age = Long.parseLong(param);
	            filteredStudents = filteredStudents.stream()
	                    .filter(student -> student.getStudentAge() >= age)
	                    .collect(Collectors.toList());
	        } catch (NumberFormatException e) {
	            model.addAttribute("error", "El parámetro de Edad debe ser un número válido.");
	        }
	    } else if ("appointmentDate".equalsIgnoreCase(filterType)) {
	        try {
	            LocalDate appointmentDate = LocalDate.parse(param); // Parseamos la fecha
	            filteredStudents = filteredStudents.stream()
	                    .filter(student -> student.getAppointmentDate() != null
	                            && student.getAppointmentDate().toLocalDate().equals(appointmentDate))
	                    .collect(Collectors.toList());
	        } catch (DateTimeParseException e) {
	            model.addAttribute("error", "El parámetro de fecha debe tener el formato correcto (yyyy-MM-dd).");
	        }
	    }

	    // Paginación de los estudiantes filtrados
	    int totalStudents = filteredStudents.size();
	    int totalPages = (int) Math.ceil((double) totalStudents / size);
	    int fromIndex = Math.min(pagina * size, totalStudents);
	    int toIndex = Math.min((pagina + 1) * size, totalStudents);
	    List<StudentAppointmentDTO> studentsPage = filteredStudents.subList(fromIndex, toIndex);

	    // Crear un nuevo objeto Page a partir de los estudiantes filtrados
	    Page<StudentAppointmentDTO> filteredPage = new PageImpl<>(studentsPage, pageable, totalStudents);

	    // Agregar atributos al modelo
	    model.addAttribute("students", filteredPage);
	    model.addAttribute("actual", pagina + 1); // Página actual (1-indexed)
	    model.addAttribute("titulo", "Listado de Alumnos filtrado");

	    // Paginación: obtener la lista de páginas disponibles
	    if (totalPages > 0) {
	        List<Integer> paginas = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
	        model.addAttribute("paginas", paginas);
	    }

	    // Pasar los parámetros de filtro a la vista para mantener el estado de búsqueda
	    model.addAttribute("filter", param);
	    model.addAttribute("filterType", filterType);

	    return "homeFilter"; // Nombre de la vista Thymeleaf
	}
	
	@GetMapping({"/home/graduate"})
	public String homeListGraduate(Model model,
	        @RequestParam(name = "pagina", required = false, defaultValue = "0") Integer pagina,
	        @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {

	    // Crear el Pageable con la página solicitada y el tamaño
	    Pageable pageable = PageRequest.of(pagina, size);

	    // Obtener la página de estudiantes con sus turnos
	    Page<StudentAppointmentDTO> studentsWithAppointments = appointmentService
	            .getStudentsWithNextAppointmentGraduate(pageable);

	    // Agregar la lista de estudiantes al modelo
	    model.addAttribute("students", studentsWithAppointments.getContent());
	    model.addAttribute("actual", pagina + 1);
	    model.addAttribute("titulo", "Listado de Alumnos");

	    // Paginación
	    int totalPages = studentsWithAppointments.getTotalPages();
	    if (totalPages > 0) {
	        List<Integer> paginas = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
	        model.addAttribute("paginas", paginas);
	    }

	    return "homeGraduates";
	}
	
	@GetMapping({ "/filter/graduate"})
	public String homeListGraduateFilter(Model model,
	        @RequestParam(name = "pagina", required = false, defaultValue = "0") Integer pagina,
	        @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
	        @RequestParam(name = "filter") String param, @RequestParam(name = "filterType") String filterType) {

	    // Si el parámetro de filtro está vacío, redirige a la página "home"
	    if (param == null || param.isEmpty()) {
	        return "redirect:/home/graduate";
	    }

	    // Crear un objeto Pageable para la paginación
	    Pageable pageable = PageRequest.of(pagina, size);

	    // Obtener todos los estudiantes con citas (sin filtrar)
	    Page<StudentAppointmentDTO> studentsWithAppointments = appointmentService
	            .getStudentsWithNextAppointmentGraduate(PageRequest.of(0, Integer.MAX_VALUE)); // Sin paginación

	    // Filtrar la lista de estudiantes según los parámetros de filtro
	    List<StudentAppointmentDTO> filteredStudents = studentsWithAppointments.getContent();

	    // Aplica los filtros según el tipo recibido
	    if ("name".equalsIgnoreCase(filterType)) {
	        filteredStudents = filteredStudents.stream()
	                .filter(student -> student.getStudentName().toLowerCase().contains(param.toLowerCase())
	                        || student.getStudentLastName().toLowerCase().contains(param.toLowerCase()))
	                .collect(Collectors.toList());
	    } else if ("nonAtten".equalsIgnoreCase(filterType)) {
	        try {
	            long inasistencias = Long.parseLong(param);
	            filteredStudents = filteredStudents.stream()
	                    .filter(student -> student.getStudentNonAtten() >= inasistencias)
	                    .collect(Collectors.toList());
	        } catch (NumberFormatException e) {
	            model.addAttribute("error", "El parámetro de inasistencias debe ser un número válido.");
	        }
	    } else if ("DNI".equalsIgnoreCase(filterType)) {
	        try {
	            long DNI = Long.parseLong(param);
	            filteredStudents = filteredStudents.stream()
	                    .filter(student -> student.getStudentDNI() == DNI)
	                    .collect(Collectors.toList());
	        } catch (NumberFormatException e) {
	            model.addAttribute("error", "El parámetro de DNI debe ser un número válido.");
	        }
	    } else if ("Age".equalsIgnoreCase(filterType)) {
	        try {
	            long age = Long.parseLong(param);
	            filteredStudents = filteredStudents.stream()
	                    .filter(student -> student.getStudentAge() >= age)
	                    .collect(Collectors.toList());
	        } catch (NumberFormatException e) {
	            model.addAttribute("error", "El parámetro de Edad debe ser un número válido.");
	        }
	    } else if ("appointmentDate".equalsIgnoreCase(filterType)) {
	        try {
	            LocalDate appointmentDate = LocalDate.parse(param); // Parseamos la fecha
	            filteredStudents = filteredStudents.stream()
	                    .filter(student -> student.getAppointmentDate() != null
	                            && student.getAppointmentDate().toLocalDate().equals(appointmentDate))
	                    .collect(Collectors.toList());
	        } catch (DateTimeParseException e) {
	            model.addAttribute("error", "El parámetro de fecha debe tener el formato correcto (yyyy-MM-dd).");
	        }
	    }

	    // Paginación de los estudiantes filtrados
	    int totalStudents = filteredStudents.size();
	    int totalPages = (int) Math.ceil((double) totalStudents / size);
	    int fromIndex = Math.min(pagina * size, totalStudents);
	    int toIndex = Math.min((pagina + 1) * size, totalStudents);
	    List<StudentAppointmentDTO> studentsPage = filteredStudents.subList(fromIndex, toIndex);

	    // Crear un nuevo objeto Page a partir de los estudiantes filtrados
	    Page<StudentAppointmentDTO> filteredPage = new PageImpl<>(studentsPage, pageable, totalStudents);

	    // Agregar atributos al modelo
	    model.addAttribute("students", filteredPage);
	    model.addAttribute("actual", pagina + 1); // Página actual (1-indexed)
	    model.addAttribute("titulo", "Listado de Alumnos filtrado");

	    // Paginación: obtener la lista de páginas disponibles
	    if (totalPages > 0) {
	        List<Integer> paginas = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
	        model.addAttribute("paginas", paginas);
	    }

	    // Pasar los parámetros de filtro a la vista para mantener el estado de búsqueda
	    model.addAttribute("filter", param);
	    model.addAttribute("filterType", filterType);

	    return "homeFilterGraduates"; // Nombre de la vista Thymeleaf
	}
}
