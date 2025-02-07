package com.rodriguez.escuelaDluz.controllers;

import java.sql.Date;
import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.rodriguez.escuelaDluz.entities.Exam;
import com.rodriguez.escuelaDluz.entities.Student;
import com.rodriguez.escuelaDluz.services.IExamService;
import com.rodriguez.escuelaDluz.services.IStudentService;

@Controller
public class ExamController {

	private IExamService examService;
	private IStudentService studentService;

	public ExamController(IExamService examService, IStudentService studentService) {
		this.examService = examService;
		this.studentService = studentService;
	}

	@GetMapping("/exam/{id}")
	public String createExam(Model model, @PathVariable(name = "id") Long id) {
		Student student = studentService.findById(id);

		model.addAttribute("student", student);
		return "exam";
	}
	
	@GetMapping("/exam/deactivateAlert/{id}")
	public String deactivateExamAlarm(Model model, @PathVariable(name = "id") Long id, RedirectAttributes redirectAttributes) {
		Exam exam = examService.findById(id);
		exam.setExamAlert(false);
		examService.save(exam);
		
		redirectAttributes.addFlashAttribute("msj", "Alerta ocultada.");
		redirectAttributes.addFlashAttribute("tipoMsj", "success");
		return "redirect:/home";
	}

	@PostMapping("/exam/save")
	public String saveExam(Model model, @RequestParam Long id, @RequestParam(required = false) Date examDate,
			@RequestParam(required = false) Boolean examAproved, @RequestParam(required = false) String examTry,
			 @RequestParam(required = false) String examType, RedirectAttributes redirectAttributes) {
		
		Student student = studentService.findById(id);
		if(examAproved == null || examDate == null || examTry == null || examType == null) {
			redirectAttributes.addFlashAttribute("msj", "Estudiante desactivado exitosamente.");
			redirectAttributes.addFlashAttribute("tipoMsj", "success");
			return "redirect;/exam/" + id;
		}
		
		Exam exam = new Exam();
		
		exam.setStudent(student);
		exam.setExamAproved(examAproved);
		exam.setExamDate(examDate);
		exam.setExamTry(examTry);
		exam.setExamType(examType);
		exam.setExamAlert(true);
		examService.save(exam);
		
		if (examType.equals("practico") && examAproved == true) {
			LocalDate localDate = LocalDate.now();
			Date sqlDate = Date.valueOf(localDate);
			student.setStudentInactiveDate(sqlDate);
			student.setStudentGraduate(true);
			studentService.save(student);
		}
				
		return "redirect:/student/info/" + id;
	}
}
