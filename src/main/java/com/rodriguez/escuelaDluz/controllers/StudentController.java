package com.rodriguez.escuelaDluz.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.rodriguez.escuelaDluz.dao.IUserRepository;
import com.rodriguez.escuelaDluz.entities.Appointment;
import com.rodriguez.escuelaDluz.entities.Rol;
import com.rodriguez.escuelaDluz.entities.Student;
import com.rodriguez.escuelaDluz.entities.User;
import com.rodriguez.escuelaDluz.services.IAppointmentService;
import com.rodriguez.escuelaDluz.services.IEmployeeService;
import com.rodriguez.escuelaDluz.services.IStudentService;

import jakarta.validation.Valid;

@Controller
public class StudentController {
	@Autowired
	private PasswordEncoder passwordEncoder;

	private IStudentService studentService;
	private IAppointmentService appointmentService;
	private IEmployeeService employeeService;
	private IUserRepository userRepository;

	public StudentController(IStudentService studentService, IAppointmentService appointmentService,
			IEmployeeService employeeService, IUserRepository userRepository) {
		this.studentService = studentService;
		this.appointmentService = appointmentService;
		this.employeeService = employeeService;
		this.userRepository = userRepository;
	}

	private static final String UPLOAD_DIR = "imagenes";

	@GetMapping("/student")
	public String createStudent(Model model) {
		Student student = new Student();
		model.addAttribute("student", student);
		model.addAttribute("appointments", appointmentService.findAll());
		model.addAttribute("recepcionistas", employeeService.findRecepcionistas());
		model.addAttribute("titulo", "Crear un nuevo alumno.");

		return "student";
	}

	@GetMapping("/student/{id}")
	public String editStudent(@PathVariable(name = "id") Long id, Model model) {
		Student student = studentService.findById(id);
		model.addAttribute("student", student);
		model.addAttribute("appointments", appointmentService.findAll());
		model.addAttribute("recepcionistas", employeeService.findRecepcionistas());
		model.addAttribute("titulo", "Editar al alumno " + student.getFirstName());

		return "student";

	}

	@GetMapping("/inactivate/{id}")
	public String inactivateStudent(@PathVariable(name = "id") Long id, Model model,
			RedirectAttributes redirectAttributes) {
		Student student = studentService.findById(id);

		LocalDate localDate = LocalDate.now();
		Date sqlDate = Date.valueOf(localDate);

		student.setStudentAlertNonActive(true);
		student.setStudentInactive(true);
		student.setStudentInactiveDate(sqlDate);
		studentService.save(student);

		redirectAttributes.addFlashAttribute("msj", "Estudiante desactivado exitosamente.");
		redirectAttributes.addFlashAttribute("tipoMsj", "success");
		return "redirect:/home";
	}

	@GetMapping("/ignore/{id}")
	public String ignoreStudent(@PathVariable(name = "id") Long id, Model model,
			RedirectAttributes redirectAttributes) {
		Student student = studentService.findById(id);

		student.setStudentAlertNonActive(true);
		studentService.save(student);

		redirectAttributes.addFlashAttribute("msj", "Alerta escondida exitosamente.");
		redirectAttributes.addFlashAttribute("tipoMsj", "success");
		return "redirect:/home";
	}

	@GetMapping("/student/info/{id}")
	public String showStudentInfo(@PathVariable(name = "id") Long id, Model model) {
		Student student = studentService.findById(id);

		if (student.getFirstName() != null) {
			// Obtener la fecha y hora actual
			LocalDateTime now = LocalDateTime.now();

			// Formateador de tiempo para comparar la hora
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

			// Obtener lista de turnos del estudiante
			List<Appointment> appointments = student.getAppointments();

			// Filtrar turnos pasados o completados/cancelados
			List<Appointment> pastAppointments = appointments.stream().filter(appointment -> {
				LocalDate appointmentDate = appointment.getAppointmentDate().toLocalDate();
				LocalTime appointmentTime = LocalTime.parse(appointment.getAppointmentTime(), timeFormatter);
				LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, appointmentTime);

				// Incluir si el turno es pasado o si el estado no es "Turno Pendiente."
				return appointmentDateTime.isBefore(now) || (appointment.getAppointmentComplete() != null
						&& !appointment.getAppointmentComplete().equals("Turno Pendiente."));
			}).collect(Collectors.toList());

			// Filtrar turnos próximos
			List<Appointment> upcomingAppointments = appointments.stream().filter(appointment -> {
				LocalDate appointmentDate = appointment.getAppointmentDate().toLocalDate();
				LocalTime appointmentTime = LocalTime.parse(appointment.getAppointmentTime(), timeFormatter);
				LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, appointmentTime);

				return appointmentDateTime.isAfter(now) && (appointment.getAppointmentComplete() == null
						|| appointment.getAppointmentComplete().equals("Turno Pendiente."));
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

		appointmentService.actualizarClasesDelStudent(student);
		return "studentInfo"; // El nombre de la vista Thymeleaf
	}

	@GetMapping("/student/graduate/{id}")
	public String graduateStudent(@PathVariable(name = "id") Long id, Model model,
			RedirectAttributes redirectAttributes) {
		Student student = studentService.findById(id);

		LocalDate localDate = LocalDate.now();
		Date sqlDate = Date.valueOf(localDate);

		student.setStudentInactiveDate(sqlDate);

		student.setStudentGraduate(true);
		studentService.save(student);

		redirectAttributes.addFlashAttribute("msj", "Estudiante graduado exitosamente.");
		redirectAttributes.addFlashAttribute("tipoMsj", "success");
		return "redirect:/home";

	}

	@GetMapping("student/info/student/graduate/{id}")
	public String graduateStudentView(@PathVariable(name = "id") Long id, Model model,
			RedirectAttributes redirectAttributes) {
		Student student = studentService.findById(id);
		LocalDate localDate = LocalDate.now();
		Date sqlDate = Date.valueOf(localDate);

		student.setStudentInactiveDate(sqlDate);
		student.setStudentGraduate(true);
		studentService.save(student);

		redirectAttributes.addFlashAttribute("msj", "Estudiante graduado exitosamente.");
		redirectAttributes.addFlashAttribute("tipoMsj", "success");
		return "redirect:/student/info/" + id;

	}

	@GetMapping("/student/generate-users")
	public String generateStudentUsers() {
		List<Student> students = studentService.findAll();

        for (Student student : students) {
            // Si el estudiante ya tiene usuario, saltarlo
            if (student.getUser() != null) {
                continue;
            }

            // Crear el usuario basado en los datos del estudiante
            User user = new User();
            user.setUsername(String.valueOf(student.getStudentDNI())); // Conversión de Long a String

            // Crear la contraseña (Primera letra de FirstName en mayúscula + studentDNI)
            String password = student.getFirstName().substring(0, 1).toUpperCase() + student.getStudentDNI();
            user.setPassword(passwordEncoder.encode(password));

            user.setRol(Rol.ALUMNO);

            // Vincular usuario con estudiante
            user.setStudent(student);
            student.setUser(user);

            // Guardar usuario primero para evitar problemas de persistencia
            userRepository.save(user);
            studentService.save(student);
        }
        return "redirect:/home";
	}
	
	@PostMapping("/student")
	public String saveStudent(@Valid Student student, BindingResult br, Model model,
			@RequestParam("image") MultipartFile file, RedirectAttributes redirectAttributes) {

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

		if (student.getStudentClass() == null) {
			student.setStudentClass((long) 0);
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

		if (student.getStudentClass() == null) {
			student.setStudentClass((long) 0);
		}

		if (student.getStudent45degree() == null) {
			student.setStudent45degree(false);
		}

		if (student.getStudentInactive() == null) {
			student.setStudentInactive(false);
		}

		if (student.getStudentAdminSellCon() == null) {
			student.setStudentAdminSellCon(false);
		}

		if (student.getStudentIncline() == null) {
			student.setStudentIncline(false);
		}

		if (student.getStudentMotorTalk() == null) {
			student.setStudentMotorTalk(false);
		}

		if (student.getStudentGrade() == null) {
			student.setStudentGrade((long) 0);
		}

//		System.out.println(cambio);
		if (br.hasErrors()) {
//			System.out.println(br);
			model.addAttribute("student", student);
			model.addAttribute("appointments", appointmentService.findAll());
			model.addAttribute("recepcionistas", employeeService.findRecepcionistas());
			model.addAttribute("titulo", "Errores encontrados, Vuelva a intentar");
			return "student";
		}
		try {

			// Lógica para guardar el estudiante
			if (student.getId() != null) {
				Student student2 = studentService.findById(student.getId());
//				System.out.println(student2.getStudentAppointments());A
				student.setAppointments(student2.getStudentAppointments());
				student.setStudentExams(student2.getStudentExams());
				student.setStudentPayments(student2.getStudentPayments());
			} else {
				Student existingStudent = studentService.searchByDNI(student.getStudentDNI());
				if (existingStudent != null
						&& (student.getId() == null || !existingStudent.getId().equals(student.getId()))) {
					model.addAttribute("msj", "Este DNI ya está registrado, por favor use otro.");
					model.addAttribute("tipoMsj", "danger");
					model.addAttribute("titulo", "Error: DNI duplicado");
					model.addAttribute("student", student);
					model.addAttribute("appointments", appointmentService.findAll());
					return "student"; // Retornar al formulario con mensaje de error
				}
			}

//			studentService.save(student);
			if (!file.isEmpty()) {
				if (student.getStudentImage() != null) {
					deleteImage(student.getStudentImage()); // Eliminar la imagen previa
				}
				String imageUrl = saveImage(student.getId(), file);
				student.setStudentImage(imageUrl);
			}

			if (student.getId() != null) {
				Student student2 = studentService.findById(student.getId());
//				System.out.println(student.getStudentImage());
//				System.out.println(student2.getStudentImage());
//				System.out.println(student2.getFirstName());
				if (student2.getStudentImage() != null && !student2.getStudentImage().isEmpty()) {
					if (student.getStudentImage() == null || student.getStudentImage().isEmpty()) {
//						System.out.println(student2.getStudentImage());
						student.setStudentImage(student2.getStudentImage());
//						System.out.println(student.getStudentImage());
					}

				}
			}

			User user = new User();
			user.setUsername(String.valueOf(student.getStudentDNI()));

			// Crear la contraseña (Primera letra de FirstName en mayúscula + studentDNI)
			String password = student.getFirstName().substring(0, 1).toUpperCase() + student.getStudentDNI();
			user.setPassword(passwordEncoder.encode(password));

			user.setRol(Rol.ALUMNO);

			// Vincular usuario con estudiante
			user.setStudent(student);
			student.setUser(user);
			studentService.save(student);

			redirectAttributes.addFlashAttribute("msj", "Estudiante guardado exitosamente.");
			redirectAttributes.addFlashAttribute("tipoMsj", "success");

			return "redirect:/student/info/" + student.getId();
		} catch (DataIntegrityViolationException e) {
			// Capturamos la excepción de violación de integridad de datos, como el campo
			// 'dni' no puede ser null
			model.addAttribute("msj", "El campo DNI no puede ser duplicado, por favor vuelva a intentar");
			model.addAttribute("tipoMsj", "danger");
			model.addAttribute("titulo", "El campo 'DNI' el campo no puede ser duplicado");
			model.addAttribute("student", student);
			model.addAttribute("appointments", appointmentService.findAll());
			return "student"; // Retornamos al formulario con el mensaje de error
		}

	}

	private String saveImage(Long studentId, MultipartFile file) {
		try {
			File directory = new File(UPLOAD_DIR);
			if (!directory.exists()) {
				directory.mkdirs();
			}

			String fileName = studentId + ".jpg"; // Generar nombre único basado en el ID del estudiante
			Path filePath = Paths.get(UPLOAD_DIR, fileName);

			Files.write(filePath, file.getBytes());

			return "/imagenes/" + fileName; // Retornar la URL relativa para la imagen
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void deleteImage(String imagePath) {
		if (imagePath != null && !imagePath.isEmpty()) {
			Path filePath = Paths.get(imagePath.replace("/", File.separator));
			File file = filePath.toFile();
			if (file.exists()) {
				file.delete();
			}
		}
	}
}
