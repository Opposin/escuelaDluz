package com.rodriguez.escuelaDluz.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.rodriguez.escuelaDluz.entities.Appointment;
import com.rodriguez.escuelaDluz.entities.StudentAppointmentDTO;
import com.rodriguez.escuelaDluz.services.IAppointmentService;
import com.rodriguez.escuelaDluz.services.IExamService;
import com.rodriguez.escuelaDluz.services.IStudentService;

@Controller
public class ListController {
	private IStudentService studentService;
	private IAppointmentService appointmentService;
	private IExamService examService;

	public ListController(IStudentService studentService, IAppointmentService appointmentService,
			IExamService examService) {
		this.studentService = studentService;
		this.appointmentService = appointmentService;
		this.examService = examService;
	}

//	@GetMapping("/error")
//	public String error(Model model, RedirectAttributes redirectAttributes) {
//		redirectAttributes.addFlashAttribute("msj", "Direccion no encontrada, redireccionando a listado.");
//		redirectAttributes.addFlashAttribute("tipoMsj", "danger");
//		
//		return "redirect:/home";
//	}

	@GetMapping({ "/", "/home", "/index" })
	public ModelAndView homeList(Model model,
			@RequestParam(name = "pagina", required = false, defaultValue = "0") Integer pagina,
			@RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {

		ModelAndView modelAndView = new ModelAndView();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

//		if (authentication != null && authentication.isAuthenticated()
//				&& authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN")
//						|| role.getAuthority().equals("ROLE_RECEPCIONISTA"))) {
//
//			// Crear el Pageable con la página solicitada y el tamaño
//			Pageable pageable = PageRequest.of(pagina, size);
//			studentService.deleteInactiveStudents();
//			// Obtener la página de estudiantes con sus turnos
//			Page<StudentAppointmentDTO> studentsWithAppointments = appointmentService
//					.getStudentsWithNextAppointment(pageable);
//
//			List<Appointment> pastAppointments = appointmentService.getPastAppointments();
//
//			model.addAttribute("pastAppointments", pastAppointments);
//			model.addAttribute("students", studentsWithAppointments.getContent());
//			model.addAttribute("inactiveAppointments",
//					appointmentService.getPendingAppointmentsWithInactiveEmployees());
//			model.addAttribute("examAlerts", examService.findAlerts());
//			model.addAttribute("inactiveStudents", studentService.setInactiveStudents());
//			model.addAttribute("actual", pagina + 1);
//			model.addAttribute("titulo", "Listado de Alumnos");
//
//			// Paginación
//			int totalPages = studentsWithAppointments.getTotalPages();
//			if (totalPages > 0) {
//				List<Integer> paginas = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
//				model.addAttribute("paginas", paginas);
//			}
//			modelAndView.setViewName("home");
//		} else {
//            modelAndView.setViewName("redirect:/studentAppointmentsView"); // Redirige a la vista StudentView
//        }
		if (authentication != null && authentication.isAuthenticated()) {
		    boolean isAdminOrRecepcionista = authentication.getAuthorities().stream()
		            .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN") || role.getAuthority().equals("ROLE_RECEPCIONISTA"));

		    boolean isEstudiante = authentication.getAuthorities().stream()
		            .anyMatch(role -> role.getAuthority().equals("ROLE_ALUMNO"));

		    if (isAdminOrRecepcionista) {
		        // Crear el Pageable con la página solicitada y el tamaño
		        Pageable pageable = PageRequest.of(pagina, size);
		        studentService.deleteInactiveStudents();

		        // Obtener la página de estudiantes con sus turnos
		        Page<StudentAppointmentDTO> studentsWithAppointments = appointmentService
		                .getStudentsWithNextAppointment(pageable);

		        List<Appointment> pastAppointments = appointmentService.getPastAppointments();

		        model.addAttribute("pastAppointments", pastAppointments);
		        model.addAttribute("students", studentsWithAppointments.getContent());
		        model.addAttribute("inactiveAppointments", appointmentService.getPendingAppointmentsWithInactiveEmployees());
		        model.addAttribute("examAlerts", examService.findAlerts());
		        model.addAttribute("inactiveStudents", studentService.setInactiveStudents());
		        model.addAttribute("actual", pagina + 1);
		        model.addAttribute("titulo", "Listado de Alumnos");

		        // Paginación
		        int totalPages = studentsWithAppointments.getTotalPages();
		        if (totalPages > 0) {
		            List<Integer> paginas = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
		            model.addAttribute("paginas", paginas);
		        }

		        modelAndView.setViewName("home");
		    } else if (isEstudiante) {
		        modelAndView.setViewName("redirect:/studentAppointmentsView"); // Redirige a la vista StudentView
		    } else {
		        modelAndView.setViewName("login"); // Redirigir a una página de error en caso de un rol no reconocido
		    }
		}


		return modelAndView;
	}

	@GetMapping({ "/filter", "/homeFilter", "/indexFilter" })
	public String homeListFilter(Model model,
			@RequestParam(name = "pagina", required = false, defaultValue = "0") Integer pagina,
			@RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
			@RequestParam(name = "filter", required = false) String param,
			@RequestParam(name = "filterType", required = false) String filterType,
			@RequestParam(name = "orderType", required = false, defaultValue = "appointmentDate") String orderType) {

		// Crear un objeto Pageable para la paginación
		Pageable pageable = PageRequest.of(pagina, size);

		// Obtener todos los estudiantes con citas (sin filtrar)
		Page<StudentAppointmentDTO> studentsWithAppointments = appointmentService
				.getStudentsWithNextAppointment(PageRequest.of(0, Integer.MAX_VALUE)); // Sin paginación

		// Convertir la lista de contenido a una lista mutable
		List<StudentAppointmentDTO> filteredStudents = new ArrayList<>(studentsWithAppointments.getContent());

		// Filtrar la lista de estudiantes si hay un parámetro de filtro
//	    if (param != null && !param.isEmpty() && filterType != null) {
//	        if ("name".equalsIgnoreCase(filterType)) {
//	            filteredStudents = filteredStudents.stream()
//	                    .filter(student -> student.getStudentName() != null &&
//	                            (student.getStudentName().toLowerCase().contains(param.toLowerCase())
//	                                    || student.getStudentLastName() != null &&
//	                                    student.getStudentLastName().toLowerCase().contains(param.toLowerCase())))
//	                    .collect(Collectors.toList());
//	        } else if ("nonAtten".equalsIgnoreCase(filterType)) {
//	            try {
//	                long inasistencias = Long.parseLong(param);
//	                filteredStudents = filteredStudents.stream()
//	                        .filter(student -> student.getStudentNonAtten() != null &&
//	                                student.getStudentNonAtten() >= inasistencias)
//	                        .collect(Collectors.toList());
//	            } catch (NumberFormatException e) {
//	                model.addAttribute("error", "El parámetro de inasistencias debe ser un número válido.");
//	            }
//	        }
//	    }

		if (param != null && !param.isEmpty() && filterType != null) {
			if ("name".equalsIgnoreCase(filterType)) {
				filteredStudents = filteredStudents.stream().filter(student -> student.getStudentName() != null
						&& (student.getStudentName().toLowerCase().contains(param.toLowerCase())
								|| student.getStudentLastName() != null
										&& student.getStudentLastName().toLowerCase().contains(param.toLowerCase())))
						.collect(Collectors.toList());
			} else if ("nonAtten".equalsIgnoreCase(filterType)) {
				try {
					long inasistencias = Long.parseLong(param);
					filteredStudents = filteredStudents.stream().filter(student -> student.getStudentNonAtten() != null
							&& student.getStudentNonAtten() >= inasistencias).collect(Collectors.toList());
				} catch (NumberFormatException e) {
					model.addAttribute("error", "El parámetro de inasistencias debe ser un número válido.");
				}
			} else if ("DNI".equalsIgnoreCase(filterType)) {
				try {
					long DNI = Long.parseLong(param);
					filteredStudents = filteredStudents.stream().filter(student -> student.getStudentDNI() == DNI)
							.collect(Collectors.toList());
				} catch (NumberFormatException e) {
					model.addAttribute("error", "El parámetro de DNI debe ser un número válido.");
				}
			} else if ("cell".equalsIgnoreCase(filterType)) {
				try {
					long cell = Long.parseLong(param);
					filteredStudents = filteredStudents.stream()
							.filter(student -> student.getStudentCellphone() == cell).collect(Collectors.toList());
				} catch (NumberFormatException e) {
					model.addAttribute("error", "El parámetro de DNI debe ser un número válido.");
				}
			} else if ("Age".equalsIgnoreCase(filterType)) {
				try {
					long age = Long.parseLong(param);
					filteredStudents = filteredStudents.stream()
							.filter(student -> student.getStudentAge() != null && student.getStudentAge() >= age)
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
		}

		// Ordenar los estudiantes según el parámetro orderType
		Comparator<StudentAppointmentDTO> comparator = switch (orderType.toLowerCase()) {
		case "name" -> Comparator.comparing(StudentAppointmentDTO::getStudentName,
				Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
		case "namereverse" -> Comparator
				.comparing(StudentAppointmentDTO::getStudentName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
				.reversed();
		case "lastname" -> Comparator.comparing(StudentAppointmentDTO::getStudentLastName,
				Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
		case "lastnamereverse" -> Comparator.comparing(StudentAppointmentDTO::getStudentLastName,
				Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)).reversed();
		case "age" -> Comparator.comparingLong(StudentAppointmentDTO::getStudentAge);
		case "agemm" -> Comparator.comparingLong(StudentAppointmentDTO::getStudentAge).reversed();
		case "class" -> Comparator.comparingLong(StudentAppointmentDTO::getAppointmentClassNumber);
		case "classreverse" -> Comparator.comparingLong(StudentAppointmentDTO::getAppointmentClassNumber).reversed();
		case "payment" -> Comparator.comparing(StudentAppointmentDTO::getPaymentDate,
				Comparator.nullsLast(Comparator.naturalOrder()));
		case "paymentreverse" -> Comparator.comparing(StudentAppointmentDTO::getPaymentDate,
				Comparator.nullsLast(Comparator.reverseOrder()));
		case "nonatten" -> Comparator.comparingLong(StudentAppointmentDTO::getStudentNonAtten).reversed();
		case "nonattenreverse" -> Comparator.comparingLong(StudentAppointmentDTO::getStudentNonAtten);
		default -> Comparator.comparing(StudentAppointmentDTO::getAppointmentDate,
				Comparator.nullsLast(Comparator.naturalOrder()));
		};

		filteredStudents.sort(comparator);

		// Paginación de los estudiantes filtrados
		int totalStudents = filteredStudents.size();
		int totalPages = (int) Math.ceil((double) totalStudents / size);
		int fromIndex = Math.min(pagina * size, totalStudents);
		int toIndex = Math.min((pagina + 1) * size, totalStudents);
		List<StudentAppointmentDTO> studentsPage = filteredStudents.subList(fromIndex, toIndex);

		// Crear un nuevo objeto Page a partir de los estudiantes filtrados
		Page<StudentAppointmentDTO> filteredPage = new PageImpl<>(studentsPage, pageable, totalStudents);

		List<Appointment> pastAppointments = appointmentService.getPastAppointments();

		model.addAttribute("pastAppointments", pastAppointments);

		// Agregar atributos al modelo
		model.addAttribute("students", filteredPage);
		model.addAttribute("inactiveAppointments", appointmentService.getPendingAppointmentsWithInactiveEmployees());
		model.addAttribute("examAlerts", examService.findAlerts());
		model.addAttribute("inactiveStudents", studentService.setInactiveStudents());
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
		model.addAttribute("orderType", orderType);

		return "homeFilter"; // Nombre de la vista Thymeleaf
	}

	@GetMapping({ "/home/graduate" })
	public String homeListGraduate(Model model,
			@RequestParam(name = "pagina", required = false, defaultValue = "0") Integer pagina,
			@RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {

		// Crear el Pageable con la página solicitada y el tamaño
		Pageable pageable = PageRequest.of(pagina, size);

		// Obtener la página de estudiantes con sus turnos
		Page<StudentAppointmentDTO> studentsWithAppointments = appointmentService
				.getStudentsWithNextAppointmentGraduate(pageable);

		List<Appointment> pastAppointments = appointmentService.getPastAppointments();

		model.addAttribute("pastAppointments", pastAppointments);

		// Agregar la lista de estudiantes al modelo
		model.addAttribute("students", studentsWithAppointments.getContent());
		model.addAttribute("inactiveAppointments", appointmentService.getPendingAppointmentsWithInactiveEmployees());
		model.addAttribute("examAlerts", examService.findAlerts());
		model.addAttribute("inactiveStudents", studentService.setInactiveStudents());
		model.addAttribute("actual", pagina + 1);
		model.addAttribute("titulo", "Listado de Alumnos graduados");

		// Paginación
		int totalPages = studentsWithAppointments.getTotalPages();
		if (totalPages > 0) {
			List<Integer> paginas = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("paginas", paginas);
		}

		return "homeGraduates";
	}

	@GetMapping({ "/filter/graduate" })
	public String homeListGraduateFilter(Model model,
			@RequestParam(name = "pagina", required = false, defaultValue = "0") Integer pagina,
			@RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
			@RequestParam(name = "filter", required = false) String param,
			@RequestParam(name = "filterType", required = false) String filterType,
			@RequestParam(name = "orderType", required = false, defaultValue = "appointmentDate") String orderType) {

		// Crear un objeto Pageable para la paginación
		Pageable pageable = PageRequest.of(pagina, size);

		// Obtener todos los estudiantes graduados con citas (sin filtrar)
		Page<StudentAppointmentDTO> studentsWithAppointments = appointmentService
				.getStudentsWithNextAppointmentGraduate(PageRequest.of(0, Integer.MAX_VALUE)); // Sin paginación

		// Convertir la lista de contenido a una lista mutable
		List<StudentAppointmentDTO> filteredStudents = new ArrayList<>(studentsWithAppointments.getContent());

		// Filtrar la lista de estudiantes si hay un parámetro de filtro
		if (param != null && !param.isEmpty() && filterType != null) {
			if ("name".equalsIgnoreCase(filterType)) {
				filteredStudents = filteredStudents.stream().filter(student -> student.getStudentName() != null
						&& (student.getStudentName().toLowerCase().contains(param.toLowerCase())
								|| student.getStudentLastName() != null
										&& student.getStudentLastName().toLowerCase().contains(param.toLowerCase())))
						.collect(Collectors.toList());
			} else if ("nonAtten".equalsIgnoreCase(filterType)) {
				try {
					long inasistencias = Long.parseLong(param);
					filteredStudents = filteredStudents.stream().filter(student -> student.getStudentNonAtten() != null
							&& student.getStudentNonAtten() >= inasistencias).collect(Collectors.toList());
				} catch (NumberFormatException e) {
					model.addAttribute("msj", "El parámetro de inasistencias debe ser un número válido.");
					return "redirect:/home/graduate";
				}
			} else if ("DNI".equalsIgnoreCase(filterType)) {
				try {
					long DNI = Long.parseLong(param);
					filteredStudents = filteredStudents.stream().filter(student -> student.getStudentDNI() == DNI)
							.collect(Collectors.toList());
				} catch (NumberFormatException e) {
					model.addAttribute("msj", "El parámetro de DNI debe ser un número válido.");
					return "redirect:/home/graduate";
				}
			} else if ("cell".equalsIgnoreCase(filterType)) {
				try {
					long cell = Long.parseLong(param);
					filteredStudents = filteredStudents.stream()
							.filter(student -> student.getStudentCellphone() == cell).collect(Collectors.toList());
				} catch (NumberFormatException e) {
					model.addAttribute("msj", "El parámetro de DNI debe ser un número válido.");
					return "redirect:/home/graduate";
				}
			} else if ("Age".equalsIgnoreCase(filterType)) {
				try {
					long age = Long.parseLong(param);
					filteredStudents = filteredStudents.stream()
							.filter(student -> student.getStudentAge() != null && student.getStudentAge() >= age)
							.collect(Collectors.toList());
				} catch (NumberFormatException e) {
					model.addAttribute("msj", "El parámetro de Edad debe ser un número válido.");
					return "redirect:/home/graduate";
				}
			} else if ("appointmentDate".equalsIgnoreCase(filterType)) {
				try {
					LocalDate appointmentDate = LocalDate.parse(param); // Parseamos la fecha
					filteredStudents = filteredStudents.stream()
							.filter(student -> student.getAppointmentDate() != null
									&& student.getAppointmentDate().toLocalDate().equals(appointmentDate))
							.collect(Collectors.toList());
				} catch (DateTimeParseException e) {
					model.addAttribute("msj", "El parámetro de fecha debe tener el formato correcto (yyyy-MM-dd).");
					return "redirect:/home/graduate";
				}
			}
		}

		// Ordenar los estudiantes según el parámetro orderType
		Comparator<StudentAppointmentDTO> comparator = switch (orderType.toLowerCase()) {
		case "name" -> Comparator.comparing(StudentAppointmentDTO::getStudentName,
				Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
		case "namereverse" -> Comparator
				.comparing(StudentAppointmentDTO::getStudentName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
				.reversed();
		case "lastname" -> Comparator.comparing(StudentAppointmentDTO::getStudentLastName,
				Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
		case "lastnamereverse" -> Comparator.comparing(StudentAppointmentDTO::getStudentLastName,
				Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)).reversed();
		case "age" -> Comparator.comparingLong(StudentAppointmentDTO::getStudentAge);
		case "agemm" -> Comparator.comparingLong(StudentAppointmentDTO::getStudentAge).reversed();
		case "class" -> Comparator.comparingLong(StudentAppointmentDTO::getAppointmentClassNumber);
		case "classreverse" -> Comparator.comparingLong(StudentAppointmentDTO::getAppointmentClassNumber).reversed();
		case "payment" -> Comparator.comparing(StudentAppointmentDTO::getPaymentDate,
				Comparator.nullsLast(Comparator.naturalOrder()));
		case "paymentreverse" -> Comparator.comparing(StudentAppointmentDTO::getPaymentDate,
				Comparator.nullsLast(Comparator.reverseOrder()));
		case "nonatten" -> Comparator.comparingLong(StudentAppointmentDTO::getStudentNonAtten).reversed();
		case "nonattenreverse" -> Comparator.comparingLong(StudentAppointmentDTO::getStudentNonAtten);
		default -> Comparator.comparing(StudentAppointmentDTO::getAppointmentDate,
				Comparator.nullsLast(Comparator.naturalOrder()));
		};

		filteredStudents.sort(comparator);

		// Paginación de los estudiantes filtrados
		int totalStudents = filteredStudents.size();
		int totalPages = (int) Math.ceil((double) totalStudents / size);
		int fromIndex = Math.min(pagina * size, totalStudents);
		int toIndex = Math.min((pagina + 1) * size, totalStudents);
		List<StudentAppointmentDTO> studentsPage = filteredStudents.subList(fromIndex, toIndex);

		// Crear un nuevo objeto Page a partir de los estudiantes filtrados
		Page<StudentAppointmentDTO> filteredPage = new PageImpl<>(studentsPage, pageable, totalStudents);

		List<Appointment> pastAppointments = appointmentService.getPastAppointments();

		model.addAttribute("pastAppointments", pastAppointments);

		// Agregar atributos al modelo
		model.addAttribute("students", filteredPage);
		model.addAttribute("actual", pagina + 1); // Página actual (1-indexed)
		model.addAttribute("inactiveAppointments", appointmentService.getPendingAppointmentsWithInactiveEmployees());
		model.addAttribute("examAlerts", examService.findAlerts());
		model.addAttribute("inactiveStudents", studentService.setInactiveStudents());
		model.addAttribute("titulo", "Listado de Alumnos graduados filtrado");

		// Paginación: obtener la lista de páginas disponibles
		if (totalPages > 0) {
			List<Integer> paginas = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("paginas", paginas);
		}

		// Pasar los parámetros de filtro a la vista para mantener el estado de búsqueda
		model.addAttribute("filter", param);
		model.addAttribute("filterType", filterType);
		model.addAttribute("orderType", orderType);

		return "homeFilterGraduates"; // Nombre de la vista Thymeleaf
	}

}
