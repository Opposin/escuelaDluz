package com.rodriguez.escuelaDluz.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.rodriguez.escuelaDluz.entities.Appointment;
import com.rodriguez.escuelaDluz.services.IAppointmentService;
import com.rodriguez.escuelaDluz.services.IStudentService;

@Controller
public class AppointmentListController {

	private IStudentService studentService;
	private IAppointmentService appointmentService;

	public AppointmentListController(IStudentService studentService, IAppointmentService appointmentService) {
		this.studentService = studentService;
		this.appointmentService = appointmentService;
	}

	@GetMapping("/appointments/view")
	public String getSortedAppointments(
			@RequestParam(name = "pagina", required = false, defaultValue = "0") Integer pagina,
			@RequestParam(name = "size", required = false, defaultValue = "6") Integer size, Model model) {

		Pageable pageable = PageRequest.of(pagina, size);

		List<Appointment> appointments = appointmentService.findAll();
		LocalDateTime now = LocalDateTime.now();

		// Ordenar las citas
		List<Appointment> sortedAppointments = appointments.stream().sorted((a1, a2) -> {
			// Convertir las fechas y horas a LocalDateTime
			LocalDateTime dateTime1 = LocalDateTime.of(a1.getAppointmentDate().toLocalDate(),
					LocalTime.parse(a1.getAppointmentTime()));
			LocalDateTime dateTime2 = LocalDateTime.of(a2.getAppointmentDate().toLocalDate(),
					LocalTime.parse(a2.getAppointmentTime()));

			// Priorizar "Turno pendiente"
			if (a1.getAppointmentComplete().equals("Turno Pendiente.")
					&& !a2.getAppointmentComplete().equals("Turno Pendiente.")) {
				return -1;
			} else if (!a1.getAppointmentComplete().equals("Turno Pendiente.")
					&& a2.getAppointmentComplete().equals("Turno Pendiente.")) {
				return 1;
			}

			// Ordenar por proximidad a la fecha y hora actual
			return dateTime1.compareTo(dateTime2);
		}).toList();

		// Paginar los resultados
		int start = Math.min(pagina * size, sortedAppointments.size());
		int end = Math.min((pagina + 1) * size, sortedAppointments.size());
		List<Appointment> paginatedAppointments = sortedAppointments.subList(start, end);
		Page<Appointment> appointmentsPage = new PageImpl<>(paginatedAppointments, pageable, sortedAppointments.size());

		// Agregar atributos al modelo
		model.addAttribute("appointments", appointmentsPage.getContent());
		model.addAttribute("actual", pagina + 1); // Página actual (1-indexed)
		int totalPages = appointmentsPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> paginas = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("paginas", paginas);
		}
		return "appointmentsView"; // Cambia al nombre de tu plantilla Thymeleaf
	}

//	@GetMapping("/appointments/view/filter")
//	public String getSortedAndFilteredAppointments(
//	        @RequestParam(name = "pagina", required = false, defaultValue = "0") Integer pagina,
//	        @RequestParam(name = "size", required = false, defaultValue = "6") Integer size,
//	        @RequestParam(name = "filter", required = false) String param,
//	        @RequestParam(name = "filterType", required = false) String filterType,
//	        @RequestParam(name = "orderType", required = false) String orderType,
//	        Model model) {
//
//	    Pageable pageable = PageRequest.of(pagina, size);
//	    List<Appointment> appointments = appointmentService.findAll();
//
//	    // Filtrar por `filterType` y `param`
//	    if (param != null && !param.isEmpty() && filterType != null) {
//	        switch (filterType.toLowerCase()) {
//	            case "appointmentdate":
//	                try {
//	                    LocalDate filterDate = LocalDate.parse(param); // Parsear la fecha
//	                    appointments = appointments.stream()
//	                            .filter(app -> app.getAppointmentDate() != null &&
//	                                    app.getAppointmentDate().toLocalDate().equals(filterDate))
//	                            .toList();
//	                } catch (DateTimeParseException e) {
//	                    model.addAttribute("error", "El parámetro de fecha debe tener el formato correcto (yyyy-MM-dd).");
//	                }
//	                break;
//	            case "appointmenttype":
//	                appointments = appointments.stream()
//	                        .filter(app -> app.getAppointmentType() != null &&
//	                                app.getAppointmentType().toLowerCase().contains(param.toLowerCase()))
//	                        .toList();
//	                break;
//	            case "appointmentcomplete":
//	                appointments = appointments.stream()
//	                        .filter(app -> app.getAppointmentComplete() != null &&
//	                                app.getAppointmentComplete().toLowerCase().contains(param.toLowerCase()))
//	                        .toList();
//	                break;
//	            case "name":
//	                appointments = appointments.stream()
//	                        .filter(app -> app.getStudent() != null &&
//	                                ((app.getStudent().getFistName() != null &&
//	                                        app.getStudent().getFistName().toLowerCase().contains(param.toLowerCase())) ||
//	                                        (app.getStudent().getLastName() != null &&
//	                                                app.getStudent().getLastName().toLowerCase().contains(param.toLowerCase()))))
//	                        .toList();
//	                break;
//	            case "appointmentclassnumber":
//	                try {
//	                    long classNumber = Long.parseLong(param);
//	                    appointments = appointments.stream()
//	                            .filter(app -> app.getAppointmentClassNumber() != null &&
//	                                    app.getAppointmentClassNumber() == classNumber)
//	                            .toList();
//	                } catch (NumberFormatException e) {
//	                    model.addAttribute("error", "El parámetro de clase debe ser un número válido.");
//	                }
//	                break;
//	            default:
//	                break;
//	        }
//	    }
//
//	    // Ordenar por `orderType`
//	    if (orderType != null && !orderType.isEmpty()) {
//	        Comparator<Appointment> comparator = null;
//
//	        // Comparador para ordenar por nombre del estudiante
//	        Comparator<Appointment> nameComparator = (a1, a2) -> {
//	            String name1 = (a1.getStudent() != null && a1.getStudent().getFistName() != null)
//	                    ? a1.getStudent().getFistName().toLowerCase() : "";
//	            String name2 = (a2.getStudent() != null && a2.getStudent().getFistName() != null)
//	                    ? a2.getStudent().getFistName().toLowerCase() : "";
//	            return name1.compareTo(name2);
//	        };
//
//	        // Comparador para ordenar por clase de menor a mayor
//	        Comparator<Appointment> classAscComparator = (a1, a2) -> {
//	            Long class1 = a1.getAppointmentClassNumber();
//	            Long class2 = a2.getAppointmentClassNumber();
//	            return Long.compare(class1 != null ? class1 : Long.MAX_VALUE, class2 != null ? class2 : Long.MAX_VALUE);
//	        };
//
//	        // Comparador para ordenar por clase de mayor a menor
//	        Comparator<Appointment> classDescComparator = (a1, a2) -> {
//	            Long class1 = a1.getAppointmentClassNumber();
//	            Long class2 = a2.getAppointmentClassNumber();
//	            return Long.compare(class2 != null ? class2 : Long.MIN_VALUE, class1 != null ? class1 : Long.MIN_VALUE);
//	        };
//
//	        // Seleccionar el comparador basado en `orderType`
//	        switch (orderType.toLowerCase()) {
//	            case "name":
//	                comparator = nameComparator;
//	                break;
//	            case "namereverse":
//	                comparator = nameComparator.reversed();
//	                break;
//	            case "classasc":
//	                comparator = classAscComparator;
//	                break;
//	            case "classdesc":
//	                comparator = classDescComparator;
//	                break;
//	            default:
//	                comparator = null; // Sin ordenamiento adicional
//	        }
//
//	        // Aplicar el comparador si está definido
//	        if (comparator != null) {
//	            appointments = appointments.stream()
//	                    .sorted(comparator)
//	                    .toList();
//	        }
//	    }
//
//	    // Ordenar por proximidad a la fecha actual y "Turno Pendiente"
//	    LocalDateTime now = LocalDateTime.now();
//	    appointments = appointments.stream().sorted((a1, a2) -> {
//	        // Convertir las fechas y horas a LocalDateTime
//	        LocalDateTime dateTime1 = LocalDateTime.of(a1.getAppointmentDate().toLocalDate(),
//	                LocalTime.parse(a1.getAppointmentTime()));
//	        LocalDateTime dateTime2 = LocalDateTime.of(a2.getAppointmentDate().toLocalDate(),
//	                LocalTime.parse(a2.getAppointmentTime()));
//
//	        // Priorizar "Turno pendiente"
//	        if ("Turno Pendiente.".equals(a1.getAppointmentComplete())
//	                && !"Turno Pendiente.".equals(a2.getAppointmentComplete())) {
//	            return -1;
//	        } else if (!"Turno Pendiente.".equals(a1.getAppointmentComplete())
//	                && "Turno Pendiente.".equals(a2.getAppointmentComplete())) {
//	            return 1;
//	        }
//
//	        // Ordenar por proximidad a la fecha y hora actual
//	        return dateTime1.compareTo(dateTime2);
//	    }).toList();
//
//	    // Paginar los resultados
//	    int start = Math.min(pagina * size, appointments.size());
//	    int end = Math.min((pagina + 1) * size, appointments.size());
//	    List<Appointment> paginatedAppointments = appointments.subList(start, end);
//	    Page<Appointment> appointmentsPage = new PageImpl<>(paginatedAppointments, pageable, appointments.size());
//
//	    // Agregar atributos al modelo
//	    model.addAttribute("appointments", appointmentsPage.getContent());
//	    model.addAttribute("actual", pagina + 1); // Página actual (1-indexed)
//	    int totalPages = appointmentsPage.getTotalPages();
//	    if (totalPages > 0) {
//	        List<Integer> paginas = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
//	        model.addAttribute("paginas", paginas);
//	    }
//
//	    return "appointmentsViewFilter"; // Cambia al nombre de tu plantilla Thymeleaf
//	}
	
	@GetMapping("/appointments/view/filter")
	public String getSortedAndFilteredAppointments(
	        @RequestParam(name = "pagina", required = false, defaultValue = "0") Integer pagina,
	        @RequestParam(name = "size", required = false, defaultValue = "6") Integer size,
	        @RequestParam(name = "filter", required = false) String param,
	        @RequestParam(name = "filterType", required = false) String filterType,
	        @RequestParam(name = "orderType", required = false) String orderType,
	        Model model) {

	    Pageable pageable = PageRequest.of(pagina, size);
	    List<Appointment> appointments = appointmentService.findAll();

	    // Filtrar por `filterType` y `param`
	    if (param != null && !param.isEmpty() && filterType != null) {
	        switch (filterType.toLowerCase()) {
	            case "appointmentdate":
	                try {
	                    LocalDate filterDate = LocalDate.parse(param); // Parsear la fecha
	                    appointments = appointments.stream()
	                            .filter(app -> app.getAppointmentDate() != null &&
	                                    app.getAppointmentDate().toLocalDate().equals(filterDate))
	                            .toList();
	                } catch (DateTimeParseException e) {
	                    model.addAttribute("error", "El parámetro de fecha debe tener el formato correcto (yyyy-MM-dd).");
	                }
	                break;
	            case "appointmenttype":
	                appointments = appointments.stream()
	                        .filter(app -> app.getAppointmentType() != null &&
	                                app.getAppointmentType().toLowerCase().contains(param.toLowerCase()))
	                        .toList();
	                break;
	            case "appointmentcomplete":
	                appointments = appointments.stream()
	                        .filter(app -> app.getAppointmentComplete() != null &&
	                                app.getAppointmentComplete().toLowerCase().contains(param.toLowerCase()))
	                        .toList();
	                break;
	            case "name":
	                appointments = appointments.stream()
	                        .filter(app -> app.getStudent() != null &&
	                                ((app.getStudent().getFistName() != null &&
	                                        app.getStudent().getFistName().toLowerCase().contains(param.toLowerCase())) ||
	                                        (app.getStudent().getLastName() != null &&
	                                                app.getStudent().getLastName().toLowerCase().contains(param.toLowerCase()))))
	                        .toList();
	                break;
	            case "appointmentclassnumber":
	                try {
	                    long classNumber = Long.parseLong(param);
	                    appointments = appointments.stream()
	                            .filter(app -> app.getAppointmentClassNumber() != null &&
	                                    app.getAppointmentClassNumber() == classNumber)
	                            .toList();
	                } catch (NumberFormatException e) {
	                    model.addAttribute("error", "El parámetro de clase debe ser un número válido.");
	                }
	                break;
	            default:
	                break;
	        }
	    }

	    // Ordenar por `orderType`
	    if (orderType != null && !orderType.isEmpty()) {
	        Comparator<Appointment> comparator = null;

	        // Comparador para ordenar por nombre del estudiante
	        Comparator<Appointment> nameComparator = Comparator.comparing(
	                (Appointment app) -> {
	                    // Obtener el nombre del estudiante o una cadena vacía si es null
	                    String firstName = (app.getStudent() != null && app.getStudent().getFistName() != null)
	                            ? app.getStudent().getFistName().toLowerCase()
	                            : "";
	                    return firstName;
	                }
	        );

	        // Comparador para ordenar por clase de menor a mayor
	        Comparator<Appointment> classAscComparator = Comparator.comparingLong(
	                app -> {
	                    // Retornar el número de clase o un valor predeterminado si es null
	                    Long classNumber = app.getAppointmentClassNumber();
	                    return classNumber != null ? classNumber : Long.MAX_VALUE;
	                }
	        );

	        // Comparador para ordenar por clase de mayor a menor
	        Comparator<Object> classDescComparator = Comparator.comparingLong(
	                app -> {
	                    // Retornar el número de clase o un valor predeterminado si es null
	                    Long classNumber = ((Appointment) app).getAppointmentClassNumber();
	                    return classNumber != null ? classNumber : Long.MIN_VALUE;
	                }
	        ).reversed();

	        // Seleccionar el comparador basado en `orderType`
	        switch (orderType.toLowerCase()) {
	            case "name":
	                comparator = nameComparator;
	                break;
	            case "namereverse":
	                comparator = nameComparator.reversed();
	                break;
	            case "classasc":
	                comparator = classAscComparator;
	                break;
	            default:
	                break;
	        }

	        // Aplicar el comparador si está definido
	        if (comparator != null) {
	            appointments = appointments.stream()
	                    .sorted(comparator)
	                    .toList();
	        }
	    }

	    // Paginar los resultados
	    int totalItems = appointments.size();
	    int startIndex = Math.min(pagina * size, totalItems);
	    int endIndex = Math.min(startIndex + size, totalItems);

	    List<Appointment> paginatedAppointments = appointments.subList(startIndex, endIndex);
	    Page<Appointment> appointmentsPage = new PageImpl<>(paginatedAppointments, pageable, totalItems);

	    // Agregar atributos al modelo
	    model.addAttribute("filter", param);
	    model.addAttribute("filterType", filterType);
	    model.addAttribute("orderType", orderType);
	    model.addAttribute("appointments", appointmentsPage.getContent());
	    model.addAttribute("actual", pagina + 1); // Página actual (1-indexed)
	    int totalPages = appointmentsPage.getTotalPages();
	    if (totalPages > 0) {
	        List<Integer> paginas = IntStream.rangeClosed(1, totalPages).boxed().toList();
	        model.addAttribute("paginas", paginas);
	    }

	    return "appointmentsViewFilter"; // Cambia al nombre de tu plantilla Thymeleaf
	}
}
