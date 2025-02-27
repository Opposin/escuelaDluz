package com.rodriguez.escuelaDluz.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.rodriguez.escuelaDluz.entities.ManagementVariable;
import com.rodriguez.escuelaDluz.services.IVariableService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/variables")
public class VariableController {

    @Autowired
    private IVariableService variableService;

    // Muestra el formulario para crear una nueva variable
    @GetMapping("/form")
    public String showForm(Model model) {
        model.addAttribute("variable", new ManagementVariable());
        return "variable-form";
    }

    // Guarda la variable en la base de datos
    @PostMapping("/save")
    public String saveVariable(@Valid @ModelAttribute("variable") ManagementVariable variable, 
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "variable-form";
        }
        variableService.save(variable);
        return "redirect:/employees/list";
    }

    // Lista todas las variables
    @GetMapping("/list")
    public String listVariables(Model model) {
        model.addAttribute("variables", variableService.findAll());
        return "variable-list";
    }

    // Elimina una variable por ID
    @GetMapping("/delete/{id}")
    public String deleteVariable(@PathVariable Long id) {
        variableService.delete(id);
        return "redirect:/employees/list";
    }
}
