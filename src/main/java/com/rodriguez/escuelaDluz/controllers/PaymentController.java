package com.rodriguez.escuelaDluz.controllers;

import java.sql.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.rodriguez.escuelaDluz.entities.Payment;
import com.rodriguez.escuelaDluz.entities.Student;
import com.rodriguez.escuelaDluz.services.IPaymentService;
import com.rodriguez.escuelaDluz.services.IStudentService;

@Controller
public class PaymentController {
	private IStudentService studentService;
	private IPaymentService paymentService;

	public PaymentController(IStudentService studentService, IPaymentService paymentService) {
		this.paymentService = paymentService;
		this.studentService = studentService;
	}

	@GetMapping("/payment/{id}")
	public String createPayment(@PathVariable(name = "id") Long id, Model model) {
		Student student = studentService.findById(id);

		model.addAttribute("student", student);
		return "payment";
	}

	@PostMapping("/payment/save")
	public String savePayment(@RequestParam(name = "paymentDate") Date Date,
			@RequestParam(required = false) Long paymentId,
			@RequestParam(name = "paymentType") String paymentType,
			@RequestParam(name = "paymentTime") String paymentTime,
			@RequestParam(name = "paymentQuantity") Long paymentQuantity, @RequestParam(name = "id") Long id) {

		Payment payment;
		Student student = studentService.findById(id);
		
		if(paymentId != null) {
			payment = paymentService.findById(paymentId);
		} else {
			payment = new Payment();
		}
		
		 List<Payment> payments = student.getStudentPayments();

	        if (payments == null || payments.isEmpty()) {
	            // Si la lista está vacía, asigna 1
	            payment.setPaymentNumber((long) 1);
	        } else {
	            // Si no está vacía, cuenta los pagos y asigna el siguiente número
	            payment.setPaymentNumber((long) payments.size() + 1);
	        }
		payment.setStudent(student);
		payment.setPaymentDate(Date);
		payment.setPaymentQuantity(paymentQuantity);
		payment.setPaymentTime(paymentTime);
		payment.setPaymentType(paymentType);
		
		paymentService.save(payment);
		return "redirect:/home";
	}

	@GetMapping("/payment/edit/{id}")
	public String editPayment(@PathVariable(name = "id") Long id, Model model) {
	    Payment payment = paymentService.findById(id);
	    if (payment == null) {
	        return "redirect:/home"; // Redirige si el pago no existe
	    }

	    model.addAttribute("payment", payment);
	    model.addAttribute("student", payment.getStudent());
	    return "payment_edit"; // Nombre de la vista de edición
	}

	@PostMapping("/payment/update")
	public String updatePayment(
	        @RequestParam(name = "paymentId") Long paymentId,
	        @RequestParam(name = "paymentDate") Date paymentDate,
	        @RequestParam(name = "paymentType") String paymentType,
	        @RequestParam(name = "paymentTime") String paymentTime,
	        @RequestParam(name = "paymentQuantity") Long paymentQuantity,
	        @RequestParam(name = "id") Long studentId) {

	    Payment payment = paymentService.findById(paymentId);
	    if (payment == null) {
	        return "redirect:/home";
	    }

	    Student student = studentService.findById(studentId);
	    payment.setStudent(student);
	    payment.setPaymentDate(paymentDate);
	    payment.setPaymentQuantity(paymentQuantity);
	    payment.setPaymentTime(paymentTime);
	    payment.setPaymentType(paymentType);

	    paymentService.save(payment);
	    return "redirect:/home";
	}
}
