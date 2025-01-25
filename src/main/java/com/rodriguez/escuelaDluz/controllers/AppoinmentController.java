package com.rodriguez.escuelaDluz.controllers;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.rodriguez.escuelaDluz.entities.Appointment;
import com.rodriguez.escuelaDluz.entities.Student;
import com.rodriguez.escuelaDluz.services.IAppointmentService;
import com.rodriguez.escuelaDluz.services.IStudentService;

import jakarta.validation.Valid;

@Controller
public class AppoinmentController {

	private IStudentService studentService;
	private IAppointmentService appointmentService;

	public AppoinmentController(IStudentService studentService, IAppointmentService appointmentService) {
		this.studentService = studentService;
		this.appointmentService = appointmentService;
	}

	@GetMapping("/appointment/{id}")
	public String createAppointment(@PathVariable(name = "id") Long id, Model model) {

		Student student = studentService.findById(id);

		List<Map<String, Object>> fechasDisponibles = appointmentService.generarFechasDisponibles();

		System.out.println(fechasDisponibles);

		if (student == null) {
			return "redirect:/home"; // Redirige si no se encuentra el alumno
		}
		model.addAttribute("fechasDisponibles", fechasDisponibles);
		model.addAttribute("student", student);

		return "appointment";
	}

	@GetMapping("/appointment/edit/{id}/{appointmentId}")
	public String editAppointment(@PathVariable(name = "id") Long id,
			@PathVariable(name = "appointmentId") Long appointmentId, Model model) {
		Student student = studentService.findById(id);
		
		List<Map<String, Object>> fechasDisponibles = appointmentService.generarFechasDisponibles();
		
		model.addAttribute("fechasDisponibles", fechasDisponibles);
		model.addAttribute("student", student);
		model.addAttribute("appointment", appointmentService.findById(appointmentId));
		model.addAttribute("titulo", "Editar al alumno " + student.getFistName());

		return "appointment2";

	}

	@PostMapping("/appointment/")
	public String createTime(@RequestParam(name = "id") Long id, @RequestParam(name = "appointmentDate") Date date,
			Model model) {


		List<String> horarios = Arrays.asList("09:00", "09:45", "10:30", "11:15", "12:00", "12:45", "13:30", "14:15",
				"15:00", "15:45", "16:30", "17:15", "18:00");

		// Agregar la lista de horarios al modelo
		model.addAttribute("appointmentDate", date);
		model.addAttribute("horarios", horarios);
		model.addAttribute("id", id);
		model.addAttribute("titulo", "Elija un horario.");
		System.out.println(id);
		return "appointment2";
	}

	@PostMapping("/save/appointment")
	public String saveOrUpdateAppointment(@RequestParam(required = false) Long appointmentId,
			@RequestParam("appointmentDate") Date appointmentDate,
			@RequestParam("appointmentTime") String appointmentTime, @RequestParam(required = false) Long id,
			@RequestParam(required = false) String appointmentComplete, RedirectAttributes redirectAttributes) {
		try {
			Appointment appointment;
			System.out.println(appointmentId);
// Verificar si es una actualización
			if (appointmentId != null) {
			    appointment = appointmentService.findById(appointmentId);
			    if (appointment == null) {
			        throw new IllegalArgumentException("No se encontró el Appointment con ID: " + appointmentId);
			    }
			} else {
			    appointment = new Appointment();
			}

			// Configurar los campos
			appointment.setAppointmentDate(appointmentDate);
			appointment.setAppointmentTime(appointmentTime);
			appointment.setAppointmentComplete(appointmentComplete != null ? appointmentComplete :"Turno Pendiente.");

			// Asociar el estudiante si corresponde
			if (id != null) {
			    Student student = studentService.findById(id);
			    if (student == null) {
			        throw new IllegalArgumentException("No se encontró el Student con ID: " + id);
			    }
			    appointment.setStudent(student);
			}
// Guardar en la base de datos
			appointmentService.save(appointment);

// Mensaje de éxito
			redirectAttributes.addFlashAttribute("msj", "Turno guardado exitosamente.");
			redirectAttributes.addFlashAttribute("tipoMsj", "success");
		} catch (Exception e) {
// Mensaje de error
			redirectAttributes.addFlashAttribute("msj", "Error al guardar el Appointment: " + e.getMessage());
		}

// Redirigir a una página de lista o formulario
		
		return "redirect:/home";
	}

//	@PostMapping("/appointment2")
//	public String saveAppointment(@RequestParam(name = "appointmentDate") Date date, @RequestParam(name = "id") Long id,
//			@RequestParam(name = "time") String time, Model model) {
//
//		Student student = studentService.findById(id);
//		System.out.println(studentService.findById(id));
//		Appointment appointment = new Appointment();
//
//		System.out.println(appointment.getStudent());
//		appointment.setStudent(student);
//		appointment.setAppointmentDate(date);
//		appointment.setAppointmentTime(time);
//
//		System.out.println(appointment.getStudent());
//		appointmentService.save(appointment);
//
//		return "redirect:home";
//
//	}
}
