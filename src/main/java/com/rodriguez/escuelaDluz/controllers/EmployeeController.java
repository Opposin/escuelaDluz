package com.rodriguez.escuelaDluz.controllers;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.rodriguez.escuelaDluz.entities.Employee;
import com.rodriguez.escuelaDluz.services.IEmployeeService;

import jakarta.validation.Valid;

@Controller
public class EmployeeController {

	private IEmployeeService employeeService;
	
	public EmployeeController(IEmployeeService employeeService) {
		this.employeeService = employeeService;
	}
	
	@GetMapping("/employee")
	public String CreateEmployee(Model model) {
		Employee employee = new Employee();
		
		model.addAttribute("employee", employee);
		model.addAttribute("titulo", "Crear un nuevo empleado.");
		
		return "employee";
	}
	
	@GetMapping("/employee/{id}")
	public String EditEmployee(@PathVariable(name = "id") Long id,Model model) {
		Employee employee = employeeService.findById(id);
		
		model.addAttribute("employee", employee);
		model.addAttribute("titulo", "Editar al empleado " + employee.getEmployeeFirstName());
		return "employee";
	}
	
	@PostMapping("/employee")
	public String SaveEmployee(@Valid Employee employee, BindingResult br, Model model, RedirectAttributes redirectAttributes) {
		
		if (br.hasErrors()) {
			model.addAttribute("employee", employee);
			model.addAttribute("titulo", "Se han encontrado errores, vuelva a intentarlo");
			return "employee";
		}
		
		try {
			employeeService.save(employee);
			redirectAttributes.addFlashAttribute("msj", "Empleado guardado exitosamente.");
			redirectAttributes.addFlashAttribute("tipoMsj", "success");
			return "redirect:/employee/info/" + employee.getId();
			
		} catch (DataIntegrityViolationException e) {
			model.addAttribute("msj", "El campo DNI no puede ser duplicado, por favor vuelva a intentar");
			model.addAttribute("tipoMsj", "danger");
			model.addAttribute("titulo", "El campo 'DNI' el campo dni no puede ser duplicado");
			model.addAttribute("employee", employee);
			
			return "employee";
		}
		
	}
	
	@GetMapping("/employee/info/{id}")
	public String EmployeeInfo(Model model,@PathVariable(name = "id") Long id) {
		Employee employee = employeeService.findById(id);
		model.addAttribute("employee", employee);
		model.addAttribute("mensaje", "Informacion del empleado " + employee.getEmployeeFirstName());
		
		
		return "employeeInfo";
	}
	
	@GetMapping("employee/info/employee/deactivate/{id}")
	public String EmployeeDeactivate(Model model, @PathVariable(name = "id") Long id, RedirectAttributes redirectAttributes) {
		Employee employee = employeeService.findById(id);
		employee.setEmployeeActive(false);
		employeeService.save(employee);
		redirectAttributes.addFlashAttribute("msj", "Empleado marcado como inactivo exitosamente.");
		redirectAttributes.addFlashAttribute("tipoMsj", "success");
		return "redirect:/employee/info/" + id;
	}
	
	@GetMapping("employee/info/employee/activate/{id}")
	public String EmployeeActivate(Model model, @PathVariable(name = "id") Long id, RedirectAttributes redirectAttributes) {
		Employee employee = employeeService.findById(id);
		employee.setEmployeeActive(true);
		employeeService.save(employee);
		redirectAttributes.addFlashAttribute("msj", "Empleado marcado como activo exitosamente.");
		redirectAttributes.addFlashAttribute("tipoMsj", "success");
		return "redirect:/employee/info/" + id;
	}
	
	
	@GetMapping("employee/employee/deactivate/{id}")
	public String EmployeeDeactivateList(Model model, @PathVariable(name = "id") Long id, RedirectAttributes redirectAttributes) {
		Employee employee = employeeService.findById(id);
		employee.setEmployeeActive(false);
		employeeService.save(employee);
		redirectAttributes.addFlashAttribute("msj", "Empleado marcado como inactivo exitosamente.");
		redirectAttributes.addFlashAttribute("tipoMsj", "success");
		return "redirect:/employee/list";
	}
	
	@GetMapping("employee/employee/activate/{id}")
	public String EmployeeActivateList(Model model, @PathVariable(name = "id") Long id, RedirectAttributes redirectAttributes) {
		Employee employee = employeeService.findById(id);
		employee.setEmployeeActive(true);
		employeeService.save(employee);
		redirectAttributes.addFlashAttribute("msj", "Empleado marcado como activo exitosamente.");
		redirectAttributes.addFlashAttribute("tipoMsj", "success");
		return "redirect:/employee/list";
	}
	@GetMapping("/employee/list")
	public String EmployeeList(Model model) {
		model.addAttribute("employees", employeeService.findAll());
		
		return "employeeList";
	}
	
}
