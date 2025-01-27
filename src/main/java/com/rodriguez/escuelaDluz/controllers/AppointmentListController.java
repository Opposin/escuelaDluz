package com.rodriguez.escuelaDluz.controllers;

import java.time.LocalDateTime;
import java.time.LocalTime;
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
}
