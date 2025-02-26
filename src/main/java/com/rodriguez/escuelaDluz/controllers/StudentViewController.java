package com.rodriguez.escuelaDluz.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.rodriguez.escuelaDluz.entities.Appointment;
import com.rodriguez.escuelaDluz.entities.Student;
import com.rodriguez.escuelaDluz.services.IAppointmentService;
import com.rodriguez.escuelaDluz.services.IStudentService;

@Controller
public class StudentViewController {

	private IStudentService studentService;
	private IAppointmentService appointmentService;
	
	public StudentViewController(IStudentService studentService, IAppointmentService appointmentService) {
		this.studentService = studentService;
		this.appointmentService = appointmentService;
	}
	
	@GetMapping("/studentAppointmentsView")
	public String studentView(Model model, @RequestParam String Dni, RedirectAttributes redirectAttributes) {
		
		Long searchDni;
		
		try {
			searchDni = Long.parseLong(Dni);
        } catch (NumberFormatException e) {
        	redirectAttributes.addFlashAttribute("msj", "No se permiten caracteres no numericos.");
			redirectAttributes.addFlashAttribute("tipoMsj", "danger");
			return "redirect:/";
        }
		
	    Student student = studentService.searchByDNI(searchDni);
	    
	    if (student == null) {
	    	redirectAttributes.addFlashAttribute("msj", "Alumno no encontrado.");
			redirectAttributes.addFlashAttribute("tipoMsj", "danger");
			return "redirect:/";
	    } else {
	    	List<Appointment> studentAppointments = student.getAppointments();

		    // Obtener la fecha y hora actual
		    LocalDateTime now = LocalDateTime.now();

		    // Formato de la hora esperada (asumiendo "HH:mm")
		    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

		    // Separar citas pasadas y futuras
		    List<Appointment> pastAppointments = studentAppointments.stream()
		        .filter(app -> 
		            !"Turno Pendiente.".equals(app.getAppointmentComplete()) || // Si no está pendiente
		            isPastAppointment(app, now, timeFormatter)) // O si ya pasó la fecha/hora
		        .sorted((a, b) -> compareAppointmentsByDateTime(a, b, timeFormatter)) // Ordenar por más reciente
		        .collect(Collectors.toList());

		    List<Appointment> nextAppointments = studentAppointments.stream()
		        .filter(app -> 
		            "Turno Pendiente.".equals(app.getAppointmentComplete()) && 
		            !isPastAppointment(app, now, timeFormatter)) // Solo futuros pendientes
		        .sorted((a, b) -> compareAppointmentsByDateTime(a, b, timeFormatter)) // Ordenar por proximidad
		        .collect(Collectors.toList());

		    // Pasar las listas al modelo
		    String nombre = student.getFirstName() + " " + student.getLastName();
		    
//		    System.out.println(nombre);
		    
		    model.addAttribute("nombre", nombre);
		    model.addAttribute("pastAppointments", pastAppointments);
		    model.addAttribute("nextAppointments", nextAppointments);

		    return "studentPublicView";
	    }
	}

	// Verifica si la cita ya ha pasado
	private boolean isPastAppointment(Appointment appointment, LocalDateTime now, DateTimeFormatter timeFormatter) {
	    LocalDate appointmentDate = appointment.getAppointmentDate().toLocalDate();
	    
	    // Manejar posible null o vacío en la hora
	    if (appointment.getAppointmentTime() == null || appointment.getAppointmentTime().isEmpty()) {
	        return appointmentDate.isBefore(now.toLocalDate()); // Si no hay hora, solo compara la fecha
	    }

	    LocalTime appointmentTime = LocalTime.parse(appointment.getAppointmentTime(), timeFormatter);
	    LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, appointmentTime);

	    return appointmentDateTime.isBefore(now);
	}

	// Comparador para ordenar por fecha y hora
	private int compareAppointmentsByDateTime(Appointment a, Appointment b, DateTimeFormatter timeFormatter) {
	    LocalDate appointmentDateA = a.getAppointmentDate().toLocalDate();
	    LocalDate appointmentDateB = b.getAppointmentDate().toLocalDate();

	    LocalTime appointmentTimeA = LocalTime.MIDNIGHT; // Por defecto, medianoche
	    LocalTime appointmentTimeB = LocalTime.MIDNIGHT;

	    // Verificar si hay una hora válida antes de convertir
	    if (a.getAppointmentTime() != null && !a.getAppointmentTime().isEmpty()) {
	        appointmentTimeA = LocalTime.parse(a.getAppointmentTime(), timeFormatter);
	    }
	    if (b.getAppointmentTime() != null && !b.getAppointmentTime().isEmpty()) {
	        appointmentTimeB = LocalTime.parse(b.getAppointmentTime(), timeFormatter);
	    }

	    LocalDateTime dateTimeA = LocalDateTime.of(appointmentDateA, appointmentTimeA);
	    LocalDateTime dateTimeB = LocalDateTime.of(appointmentDateB, appointmentTimeB);

	    return dateTimeA.compareTo(dateTimeB);
	}
	
}
