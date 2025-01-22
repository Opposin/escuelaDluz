package com.rodriguez.escuelaDluz.controllers;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
//		Appointment appointment = new Appointment();
//		model.addAttribute("appointment", appointment);
//		model.addAttribute("student", studentService.findById(id));
//		model.addAttribute("titulo", "Elija una fecha.");
		Student student = studentService.findById(id);

		if (student == null) {
			return "redirect:/home"; // Redirige si no se encuentra el alumno
		}
		model.addAttribute("student", student);

		return "appointment";
	}

	@PostMapping("/appointment/")
	public String createTime(@RequestParam(name = "id") Long id,
			@RequestParam(name = "appointmentDate") Date date, Model model) {

//		if (br.hasErrors()) {
//
//			List<String> horarios = Arrays.asList("9:00", "9:45", "10:30", "11:15", "12:00", "12:45", "13:30", "14:15",
//					"15:00", "15:45", "16:30", "17:15", "18:00");
//
//			// Agregar la lista de horarios al modelo
//			model.addAttribute("appointmentDate", date);
//			model.addAttribute("horarios", horarios);
//			model.addAttribute("student", id);
//			model.addAttribute("titulo", "Elija un horario.");
//
//			return "appointment";
//		}

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

	@PostMapping("/appointment2")
	public String saveAppointment(@RequestParam(name = "appointmentDate") Date date,
			@RequestParam(name = "id") Long id,@RequestParam(name = "time") String time, Model model) {

//		if(br.hasErrors()) {
//			List<String> horarios = Arrays.asList("9:00", "9:45", "10:30", "11:15", "12:00", "12:45", "13:30", "14:15",
//					"15:00", "15:45", "16:30", "17:15", "18:00");
//
//			// Agregar la lista de horarios al modelo
//			model.addAttribute("appointmentDate", date);
//			model.addAttribute("horarios", horarios);
//			model.addAttribute("appointment", appointment);
//			model.addAttribute("student", studentService.findById(id));
//			model.addAttribute("titulo", "Se han encontrado errores, vuelva a intentarlo.");
//
//
//			return "appointment2";
//		}

//		System.out.println(appointment.getAppointmentTime());
//
//		System.out.println(appointment.getId());
//
//		System.out.println(appointment.getAppointmentDate());
//
//		System.out.println(appointment.getStudent());
//		
//		
//		appointment.setStudent(studentService.findById(id));
		Student student = studentService.findById(id);
		System.out.println(studentService.findById(id));
		Appointment appointment = new Appointment();
		
		System.out.println(appointment.getStudent());
		appointment.setStudent(student);
		appointment.setAppointmentDate(date);
		appointment.setAppointmentTime(time);
		
		System.out.println(appointment.getStudent());
		appointmentService.save(appointment);

		return "redirect:home";

	}
}
