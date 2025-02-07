package com.rodriguez.escuelaDluz.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rodriguez.escuelaDluz.services.AppointmentService;

@RestController
public class RestControllers {

	@Autowired
	private AppointmentService turnoService;

//	@GetMapping("/horarios-disponibles")
//	public List<String> obtenerHorariosDisponibles(
//			@RequestParam("fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha) {
//		System.out.println("Fecha recibida: " + fecha); // Para depuración
//		return turnoService.obtenerHorariosDisponiblesPorFecha(fecha);
//	}

	@GetMapping("/horarios-disponibles")
	public List<String> obtenerHorariosDisponibles(
			@RequestParam("fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha,
			@RequestParam("appointmentInstructor") Long instructorId) {

		System.out.println("Fecha recibida: " + fecha + ", Instructor ID: " + instructorId); // Para depuración

		return turnoService.obtenerHorariosDisponiblesPorFecha(fecha, instructorId);
	}
	
	@GetMapping("/horarios-consecutivos-disponibles")
    public List<String> obtenerHorariosConsecutivosDisponibles(
            @RequestParam("fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha,
            @RequestParam("appointmentInstructor") Long instructorId) {

        System.out.println("Fecha recibida: " + fecha + ", Instructor ID: " + instructorId + " (Consecutivos)"); // Para depuración

        return turnoService.obtenerHorariosDisponiblesConsecutivos(fecha, instructorId);
    }
}
