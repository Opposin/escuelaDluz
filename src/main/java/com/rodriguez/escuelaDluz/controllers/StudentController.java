package com.rodriguez.escuelaDluz.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.rodriguez.escuelaDluz.entities.Appointment;
import com.rodriguez.escuelaDluz.entities.Student;
import com.rodriguez.escuelaDluz.services.IAppointmentService;
import com.rodriguez.escuelaDluz.services.IStudentService;

import jakarta.validation.Valid;

@Controller
public class StudentController {
	private IStudentService studentService;
	private IAppointmentService appointmentService;

	public StudentController(IStudentService studentService, IAppointmentService appointmentService) {
		this.studentService = studentService;
		this.appointmentService = appointmentService;
	}

	@GetMapping("/student")
	public String createStudent(Model model) {
		Student student = new Student();
		model.addAttribute("student", student);
		model.addAttribute("appointments", appointmentService.findAll());
		model.addAttribute("titulo", "Crear un nuevo alumno.");

		return "student";
	}

	@GetMapping("/student/{id}")
	public String editStudent(@PathVariable(name = "id") Long id, Model model) {
		Student student = studentService.findById(id);
		model.addAttribute("student", student);
		model.addAttribute("appointments", appointmentService.findAll());
		model.addAttribute("titulo", "Editar al alumno " + student.getFistName());

		return "student";

	}

//	@GetMapping("/student/info/{id}")
//	public String showStudentInfo(@PathVariable(name = "id") Long id, Model model) {
//		Student student = studentService.findById(id);
//		if (student.getFistName() != null) {
//			model.addAttribute("student", student);
//			model.addAttribute("appointments", student.getAppointments());
//		} else {
//			// Si no se encuentra el alumno, manejar el error
//			model.addAttribute("error", "Alumno no encontrado");
//			return "error";
//		}
//		System.out.println(student.getStudentAppointments());
//		return "studentInfo"; // El nombre de la vista Thymeleaf
//	}

	@GetMapping("/student/info/{id}")
	public String showStudentInfo(@PathVariable(name = "id") Long id, Model model) {
		Student student = studentService.findById(id);

		if (student.getFistName() != null) {
			// Obtener la fecha y hora actual
			LocalDateTime now = LocalDateTime.now();

			// Dividir las citas en pasadas y próximas
			List<Appointment> appointments = student.getAppointments();
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

			List<Appointment> pastAppointments = appointments.stream().filter(appointment -> {
				LocalDate appointmentDate = appointment.getAppointmentDate().toLocalDate();
				LocalTime appointmentTime = LocalTime.parse(appointment.getAppointmentTime(), timeFormatter);
				LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, appointmentTime);
				return appointmentDateTime.isBefore(now);
			}).collect(Collectors.toList());

			List<Appointment> upcomingAppointments = appointments.stream().filter(appointment -> {
				LocalDate appointmentDate = appointment.getAppointmentDate().toLocalDate();
				LocalTime appointmentTime = LocalTime.parse(appointment.getAppointmentTime(), timeFormatter);
				LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, appointmentTime);
				return appointmentDateTime.isAfter(now);
			}).sorted(Comparator.comparing(appointment -> {
				LocalDate appointmentDate = appointment.getAppointmentDate().toLocalDate();
				LocalTime appointmentTime = LocalTime.parse(appointment.getAppointmentTime(), timeFormatter);
				return LocalDateTime.of(appointmentDate, appointmentTime);
			})).collect(Collectors.toList());

			// Añadir las listas al modelo
			model.addAttribute("student", student);
			model.addAttribute("pastAppointments", pastAppointments);
			model.addAttribute("upcomingAppointments", upcomingAppointments);
		} else {
			// Manejar el error si no se encuentra el alumno
			model.addAttribute("error", "Alumno no encontrado");
			return "error";
		}
		
		return "studentInfo"; // El nombre de la vista Thymeleaf
	}

	@GetMapping("/student/graduate/{id}")
	public String graduateStudent(@PathVariable(name = "id") Long id, Model model, RedirectAttributes redirectAttributes) {
		Student student = studentService.findById(id);

		student.setStudentGraduate(true);
		studentService.save(student);
		
		redirectAttributes.addFlashAttribute("msj", "Estudiante graduado exitosamente.");
		redirectAttributes.addFlashAttribute("tipoMsj", "success");
		return "redirect:/home";

	}

	@PostMapping("/student")
	public String saveStudent(@Valid Student student, BindingResult br, Model model, RedirectAttributes redirectAttributes) {

//		System.out.println("pdf ="+ student.getStudentPdf());
//		Long cambio;
//		cambio = student.getStudentNonAtten();
		if (student.getStudentAge() == null) {
			student.setStudentAge((long) 18);
		}

		if (student.getStudentNonAtten() == null) {
			student.setStudentNonAtten((long) 0);
//			System.out.println(student.getStudentNonAtten());
		}
		if (student.getStudentEstacionar() == null) {
			student.setStudentEstacionar(false);
//			System.out.println(student.getStudentEstacionar());
		}

		if (student.getStudentGiroU() == null) {
			student.setStudentGiroU(false);
		}
		if (student.getStudentPdf() == null) {
			student.setStudentPdf(false);
		}

		if (student.getStudentZigZag() == null) {
			student.setStudentZigZag(false);
		}
		if (student.getStudentVideoPista() == null) {
			student.setStudentVideoPista(false);
		}
		if (student.getStudentGraduate() == null) {
			student.setStudentGraduate(false);
		}

//		System.out.println(cambio);
		if (br.hasErrors()) {
			System.out.println(br);
			model.addAttribute("student", student);
			model.addAttribute("appointments", appointmentService.findAll());
			model.addAttribute("titulo", "Errores encontrados, Vuelva a intentar");
			return "student";
		}
		try {
			// Lógica para guardar el estudiante
			studentService.save(student);
			redirectAttributes.addFlashAttribute("msj", "Estudiante guardado exitosamente.");
			redirectAttributes.addFlashAttribute("tipoMsj", "success");
			return "redirect:/student/info/" + student.getId();
		} catch (DataIntegrityViolationException e) {
			// Capturamos la excepción de violación de integridad de datos, como el campo
			// 'dni' no puede ser null
			model.addAttribute("msj", "El campo DNI no puede ser duplicado, por favor vuelva a intentar");
			model.addAttribute("tipoMsj", "danger");
			model.addAttribute("titulo", "El campo 'DNI' el campo dni no puede ser duplicado");
			model.addAttribute("student", student);
			model.addAttribute("appointments", appointmentService.findAll());
			return "student"; // Retornamos al formulario con el mensaje de error
		}

	}
}
