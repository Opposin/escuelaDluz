package com.rodriguez.escuelaDluz.controllers;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.rodriguez.escuelaDluz.entities.Payment;
import com.rodriguez.escuelaDluz.services.IAppointmentService;
import com.rodriguez.escuelaDluz.services.IPaymentService;
import com.rodriguez.escuelaDluz.services.IStudentService;

@Controller
public class PaymentListController {

	private IStudentService studentService;
	private IAppointmentService appointmentService;
	private IPaymentService paymentService;

	public PaymentListController(IStudentService studentService, IAppointmentService appointmentService,
			IPaymentService paymentService) {
		this.studentService = studentService;
		this.appointmentService = appointmentService;
		this.paymentService = paymentService;
	}

	@GetMapping("/payments/view")
	public String listPayments(@RequestParam(name = "pagina", required = false, defaultValue = "0") Integer pagina,
			@RequestParam(name = "size", required = false, defaultValue = "1") Integer size, Model model) {

		// Obtén la lista de pagos desde el servicio y ordénalos
		List<Payment> sortedPayments = paymentService.findAllSortedByProximity();

		// Configurar la paginación
		Pageable pageable = PageRequest.of(pagina, size);
		int start = Math.min((int) pageable.getOffset(), sortedPayments.size());
		int end = Math.min(start + pageable.getPageSize(), sortedPayments.size());
		List<Payment> paginatedPayments = sortedPayments.subList(start, end);
		Page<Payment> paymentsPage = new PageImpl<>(paginatedPayments, pageable, sortedPayments.size());

		// Agregar datos al modelo
		model.addAttribute("payments", paymentsPage.getContent());
		model.addAttribute("actual", pagina + 1); // Página actual (1-indexed)
		int totalPages = paymentsPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> paginas = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("paginas", paginas);
		}

		return "paymentsView"; // Vista donde se renderizan los pagos
	}

	@GetMapping({ "/payments/view/filter"})
	public String filterPayments(Model model,
			@RequestParam(name = "pagina", required = false, defaultValue = "0") Integer pagina,
			@RequestParam(name = "size", required = false, defaultValue = "1") Integer size,
			@RequestParam(name = "filter", required = false) String param,
			@RequestParam(name = "filterType", required = false) String filterType,
			@RequestParam(name = "orderType", required = false, defaultValue = "paymentDate") String orderType) {

		// Obtener todos los pagos ordenados por proximidad a la fecha actual
		List<Payment> payments = paymentService.findAllSortedByProximity();

		// Filtrado
		if (param != null && !param.isEmpty() && filterType != null) {
			switch (filterType.toLowerCase()) {
			case "paymentdate" -> {
				try {
					LocalDate filterDate = LocalDate.parse(param);
					payments = payments.stream().filter(p -> p.getPaymentDate().toLocalDate().isEqual(filterDate))
							.collect(Collectors.toList());
				} catch (Exception e) {
					model.addAttribute("error", "Formato de fecha inválido. Use YYYY-MM-DD.");
				}
			}
			case "paymentnumber" -> {
				try {
					long number = Long.parseLong(param);
					payments = payments.stream()
							.filter(p -> p.getPaymentNumber() != null && p.getPaymentNumber() >= number)
							.collect(Collectors.toList());
				} catch (NumberFormatException e) {
					model.addAttribute("error", "El número de pago debe ser un número válido.");
				}
			}
			case "paymentnumberreverse" -> {
				try {
					long number = Long.parseLong(param);
					payments = payments.stream()
							.filter(p -> p.getPaymentNumber() != null && p.getPaymentNumber() <= number)
							.collect(Collectors.toList());
				} catch (NumberFormatException e) {
					model.addAttribute("error", "El número de pago debe ser un número válido.");
				}
			}
			case "paymentquantity" -> {
				try {
					long quantity = Long.parseLong(param);
					payments = payments.stream()
							.filter(p -> p.getPaymentQuantity() != null && p.getPaymentQuantity().equals(quantity))
							.collect(Collectors.toList());
				} catch (NumberFormatException e) {
					model.addAttribute("error", "La cantidad de pago debe ser un número válido.");
				}
			}
			case "studentdni" -> {
				try {
					long dni = Long.parseLong(param);
					payments = payments.stream().filter(p -> p.getStudent() != null
							&& p.getStudent().getStudentDNI() != null && p.getStudent().getStudentDNI().equals(dni))
							.collect(Collectors.toList());
				} catch (NumberFormatException e) {
					model.addAttribute("error", "El DNI del estudiante debe ser un número válido.");
				}
			}
			case "name" -> {
				payments = payments.stream()
						.filter(p -> p.getStudent() != null && ((p.getStudent().getFistName() != null
								&& p.getStudent().getFistName().toLowerCase().contains(param.toLowerCase()))
								|| (p.getStudent().getLastName() != null && p.getStudent().getLastName()
										.toLowerCase().contains(param.toLowerCase()))))
						.collect(Collectors.toList());
			}
			case "paymenttype" -> {
				payments = payments.stream()
						.filter(p -> p.getPaymentType() != null
								&& p.getPaymentType().toLowerCase().contains(param.toLowerCase()))
						.collect(Collectors.toList());
			}
			}
		}

		LocalDateTime now = LocalDateTime.now();

		// Ordenamiento por proximidad a la fecha actual usando calculateProximity()
		Comparator<Payment> proximityComparator = Comparator.comparingLong(p -> calculateProximity(p, now).toMillis());

		// Aplicar ordenamiento basado en orderType
		if ("date".equalsIgnoreCase(orderType)) {
			payments.sort(proximityComparator); // Ordena por los más cercanos primero
		} else if ("datereverse".equalsIgnoreCase(orderType)) {
			payments.sort(proximityComparator.reversed()); // Ordena por los más lejanos primero
		} else {
			// Ordenamiento manual para otros casos
			Comparator<Payment> comparator = switch (orderType.toLowerCase()) {
			case "paymentnumber" ->
				Comparator.comparingLong(p -> p.getPaymentNumber() != null ? p.getPaymentNumber() : 0L);
			case "paymentnumberreverse" -> Comparator
					.comparingLong((Payment p) -> p.getPaymentNumber() != null ? p.getPaymentNumber() : 0L).reversed();
			case "paymentquantity" ->
				Comparator.comparingLong(p -> p.getPaymentQuantity() != null ? p.getPaymentQuantity() : 0L);
			case "paymentquantityreverse" ->
				Comparator.comparingLong((Payment p) -> p.getPaymentQuantity() != null ? p.getPaymentQuantity() : 0L)
						.reversed();
			default -> Comparator.comparing(Payment::getPaymentDate, Comparator.nullsLast(Comparator.naturalOrder()));
			};

			payments.sort(comparator);
		}
		// Paginación
		int totalPayments = payments.size();
		int totalPages = (int) Math.ceil((double) totalPayments / size);
		int fromIndex = Math.min(pagina * size, totalPayments);
		int toIndex = Math.min((pagina + 1) * size, totalPayments);
		List<Payment> paymentsPage = payments.subList(fromIndex, toIndex);

		// Crear un objeto Page
		Pageable pageable = PageRequest.of(pagina, size);
		Page<Payment> filteredPage = new PageImpl<>(paymentsPage, pageable, totalPayments);

		// Agregar atributos al modelo
		model.addAttribute("payments", filteredPage);
		model.addAttribute("actual", pagina + 1);
		model.addAttribute("titulo", "Listado de Pagos Filtrado");

		// Lista de páginas disponibles para la paginación
		if (totalPages > 0) {
			List<Integer> paginas = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("paginas", paginas);
		}

		// Pasar los parámetros de filtro a la vista
		model.addAttribute("filter", param);
		model.addAttribute("filterType", filterType);
		model.addAttribute("orderType", orderType);

		return "paymentsViewFilter";
	}

	private Duration calculateProximity(Payment p, LocalDateTime now) {
		LocalDate paymentDate = p.getPaymentDate().toLocalDate();
		LocalTime paymentTime = LocalTime.parse(p.getPaymentTime()); // Asegúrate del formato correcto hh:mm
		LocalDateTime paymentDateTime = LocalDateTime.of(paymentDate, paymentTime);
		return Duration.between(now, paymentDateTime).abs();

	}

}
