package com.rodriguez.escuelaDluz.controllers;

import java.sql.Date;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
public class AppointmentController {

	private IStudentService studentService;
	private IAppointmentService appointmentService;

	public AppointmentController(IStudentService studentService, IAppointmentService appointmentService) {
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

	@PostMapping("/appointment/Complete/{id}")
	public String completeAppointment(@PathVariable(name = "id") Long appointmentId, @RequestParam(name = "completedType") String completedType,
			Model model, RedirectAttributes redirectAttributes) {


		// Agregar la lista de horarios al modelo
		Appointment appointment;
		
		appointment = appointmentService.findById(appointmentId);
		Student student = appointment.getStudent();
		
		
		if (completedType.equals("Completado")) {
			appointment.setAppointmentComplete(completedType);
			appointmentService.save(appointment);
		} else if (completedType.equals("Inasistencia")) {
			Long variable = student.getStudentNonAtten();
			System.out.println(variable);
			variable++;
			System.out.println(variable);
			student.setStudentNonAtten(variable);
			appointment.setAppointmentComplete(completedType);
			studentService.save(student);
			appointmentService.save(appointment);
		} else {
			appointment.setAppointmentComplete(completedType);
			appointmentService.save(appointment);
		}
		redirectAttributes.addFlashAttribute("msj", "Turno completado exitosamente.");
		redirectAttributes.addFlashAttribute("tipoMsj", "success");
		System.out.println(appointmentId);
		return "redirect:/home";
	}
	
	@PostMapping("/appointment/Complete/view/{id}")
	public String completeAppointmentsView(@PathVariable(name = "id") Long appointmentId, @RequestParam(name = "completedType") String completedType,
			Model model, RedirectAttributes redirectAttributes) {


		// Agregar la lista de horarios al modelo
		Appointment appointment;
		
		appointment = appointmentService.findById(appointmentId);
		Student student = appointment.getStudent();
		
		
		if (completedType.equals("Completado")) {
			appointment.setAppointmentComplete(completedType);
			appointmentService.save(appointment);
		} else if (completedType.equals("Inasistencia")) {
			Long variable = student.getStudentNonAtten();
			System.out.println(variable);
			variable++;
			System.out.println(variable);
			student.setStudentNonAtten(variable);
			appointment.setAppointmentComplete(completedType);
			studentService.save(student);
			appointmentService.save(appointment);
			appointmentService.updateAppointmentsAfterCancellation(appointment);
		} else {
			appointment.setAppointmentComplete(completedType);
			appointmentService.save(appointment);
			appointmentService.updateAppointmentsAfterCancellation(appointment);
			
		}
		redirectAttributes.addFlashAttribute("msj", "Turno completado exitosamente.");
		redirectAttributes.addFlashAttribute("tipoMsj", "success");
		System.out.println(appointmentId);
		return "redirect:/appointments/view";
	}
	
	@PostMapping("/appointment/Complete/view/{id}/2")
	public String completeAppointmentsView2(@PathVariable(name = "id") Long appointmentId, @RequestParam(name = "completedTypeTable") String completedType,
			Model model, RedirectAttributes redirectAttributes) {


		// Agregar la lista de horarios al modelo
		Appointment appointment;
		
		appointment = appointmentService.findById(appointmentId);
		Student student = appointment.getStudent();
		
		System.out.println(completedType);
		
		if (completedType.equals("Completado")) {
			appointment.setAppointmentComplete(completedType);
			appointmentService.save(appointment);
		} else if (completedType.equals("Inasistencia")) {
			Long variable = student.getStudentNonAtten();
			System.out.println(variable);
			variable++;
			System.out.println(variable);
			student.setStudentNonAtten(variable);
			appointment.setAppointmentComplete(completedType);
			studentService.save(student);
			appointmentService.save(appointment);
			appointmentService.updateAppointmentsAfterCancellation(appointment);
		} else {
			appointment.setAppointmentComplete(completedType);
			appointmentService.save(appointment);
			appointmentService.updateAppointmentsAfterCancellation(appointment);
			
		}
		redirectAttributes.addFlashAttribute("msj", "Turno completado exitosamente.");
		redirectAttributes.addFlashAttribute("tipoMsj", "success");
		System.out.println(appointmentId);
		return "redirect:/appointments/view";
	}
	
	@PostMapping("/appointment/Complete/student/{id}")
	public String completeAppointmentsStudent(@PathVariable(name = "id") Long appointmentId,@RequestParam(name = "studentId") Long id, @RequestParam(name = "completedType") String completedType,
			Model model, RedirectAttributes redirectAttributes) {


		// Agregar la lista de horarios al modelo
		Appointment appointment;
		
		appointment = appointmentService.findById(appointmentId);
		Student student = appointment.getStudent();
		
		
		if (completedType.equals("Completado")) {
			appointment.setAppointmentComplete(completedType);
			appointmentService.save(appointment);
		} else if (completedType.equals("Inasistencia")) {
			Long variable = student.getStudentNonAtten();
			System.out.println(variable);
			variable++;
			System.out.println(variable);
			student.setStudentNonAtten(variable);
			appointment.setAppointmentComplete(completedType);
			studentService.save(student);
			appointmentService.save(appointment);
		} else {
			appointment.setAppointmentComplete(completedType);
			appointmentService.save(appointment);			
		}
		redirectAttributes.addFlashAttribute("msj", "Turno completado exitosamente.");
		redirectAttributes.addFlashAttribute("tipoMsj", "success");
		System.out.println(appointment.getAppointmentComplete());
		return "redirect:/student/info/" + id;
	}

	@PostMapping("/save/appointment")
	public String saveOrUpdateAppointment(@RequestParam(required = false) Long appointmentId,
			@RequestParam(required = false) Date appointmentDate,
			@RequestParam(required = false) String appointmentTime, @RequestParam(required = false) Long id,
			@RequestParam(required = false) String appointmentComplete,
			@RequestParam(required = false) String appointmentType, RedirectAttributes redirectAttributes) {
		
		if(appointmentDate == null ||  appointmentTime == null || appointmentTime.equals("")) {
			redirectAttributes.addFlashAttribute("msj", "Se debe seleccionar una fecha y un horario para el turno.");
			redirectAttributes.addFlashAttribute("tipoMsj", "danger");
			return "redirect:/appointment/" + id;
		}
		
		
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
			appointment.setAppointmentType(appointmentType);
			appointment.setAppointmentComplete(appointmentComplete);

			// Asociar el estudiante si corresponde
			if (id != null) {
				Student student = studentService.findById(id);
				if (student == null) {
					throw new IllegalArgumentException("No se encontró el Student con ID: " + id);
				}
				appointment.setStudent(student);
				
				 List<Appointment> appointments = student.getAppointments();

			        if (appointments == null || appointments.isEmpty()) {
			            // Caso sin appointments asignados
			        	if(student.getStudentClass() == null) {
			        		appointment.setAppointmentClassNumber((long) 1);
			        	} else {
			        		appointment.setAppointmentClassNumber(student.getStudentClass() + 1);
			        	}
			            
			        } else {
			            // Encuentra el appointment con la fecha y hora más lejana
			            Optional<Appointment> lastAppointment = appointments.stream()
			                .max(Comparator.comparing(a -> a.getAppointmentDate().toLocalDate()
			                    .atTime(Integer.parseInt(a.getAppointmentTime().split(":")[0]),
			                            Integer.parseInt(a.getAppointmentTime().split(":")[1]))));
			            
			            if (lastAppointment.isPresent()) {
			            	Appointment lastAppointmentReal = lastAppointment.get();
			            	if (lastAppointmentReal.getAppointmentComplete().equals("Inasistencia") || lastAppointmentReal.getAppointmentComplete().equals("CanceladoB")) {
			            		appointment.setAppointmentClassNumber(lastAppointmentReal.getAppointmentClassNumber());
			            	}
			            	else {
			            		appointment.setAppointmentClassNumber(lastAppointmentReal.getAppointmentClassNumber() + 1);
			            	}
			            }
			        }

			}
// Guardar en la base de datos
			appointmentService.save(appointment);

			redirectAttributes.addFlashAttribute("msj", "Turno guardado exitosamente.");
			redirectAttributes.addFlashAttribute("tipoMsj", "success");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("msj", "Error al guardar el Appointment: " + e.getMessage());
		}


		return "redirect:/home";
	}

}
