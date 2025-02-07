package com.rodriguez.escuelaDluz.services;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.rodriguez.escuelaDluz.dao.IAppointmentRepository;
import com.rodriguez.escuelaDluz.dao.IExamRepository;
import com.rodriguez.escuelaDluz.dao.IPaymentRepository;
import com.rodriguez.escuelaDluz.dao.IStudentRepository;
import com.rodriguez.escuelaDluz.entities.Student;

import jakarta.transaction.Transactional;

@Service
public class StudentService implements IStudentService {

	@Autowired
	private IStudentRepository studentRepository;
	
	@Autowired
	private IExamRepository examRepository;
	
	@Autowired
	private IPaymentRepository paymentRepository;
	
	@Autowired
	private IAppointmentRepository appointmentRepository;

	@Override
	@Transactional
	public void save(Student student) {
		studentRepository.save(student);
	}

	@Override
	public Student findById(Long id) {
		return studentRepository.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		studentRepository.deleteById(id);

	}

	@Override
	public List<Student> findAll() {
		return (List<Student>) studentRepository.findAll();
	}

	@Override
	public Page<Student> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return studentRepository.findAll(pageable);
	}

	@Override
	public List<Student> setInactiveStudents() {
		LocalDate twentyDaysAgo = LocalDate.now().minusDays(20);

		// Obtener todos los estudiantes
		List<Student> allStudents = studentRepository.findAll();

		// Filtrar los estudiantes que cumplen las condiciones
		List<Student> inactiveStudents = allStudents.stream().filter(
				student -> (student.getStudentAlertNonActive() == null || !student.getStudentAlertNonActive()) && // Ignorar
																													// alertNonActive
																													// ==
																													// true
						(student.getStudentInactive() == null || !student.getStudentInactive()) && // Ignorar
																									// studentInactive
																									// == true
						((student.getStudentAppointments().isEmpty()
								&& student.getStudentCourseBegg().toLocalDate().isBefore(twentyDaysAgo)) 
								|| student.getStudentAppointments().stream()
						        .map(appointment -> appointment.getAppointmentDate().toLocalDate()) // Convertir a LocalDate
						        .max(LocalDate::compareTo) // Obtener la última fecha de appointment
						        .map(lastAppointmentDate -> lastAppointmentDate.isBefore(twentyDaysAgo)) // Ver si fue hace más de 20 días
						        .orElse(false) // Si no hay fechas, retorna falso
 // Si no hay fechas, retorna falso
						)).collect(Collectors.toList());
			
		return inactiveStudents;
	}
	
	@Override
	@Transactional
	public void deleteInactiveStudents() {
	    // Calcular la fecha de hace 6 meses
	    LocalDate sixMonthsAgoLocalDate = LocalDate.now().minus(6, ChronoUnit.MONTHS);
	    Date sixMonthsAgoSqlDate = Date.valueOf(sixMonthsAgoLocalDate);

	    // Buscar estudiantes inactivos
	    List<Student> studentsToDelete = studentRepository.findInactiveStudentsOlderThan(sixMonthsAgoSqlDate);

	    for (Student student : studentsToDelete) {
	        // Eliminar entidades relacionadas manualmente
	        examRepository.deleteAllByStudent(student);
	        appointmentRepository.deleteAllByStudent(student);
	        paymentRepository.deleteAllByStudent(student);

	        // Eliminar el estudiante
	        studentRepository.delete(student);
	    }
	}
}
