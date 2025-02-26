//package com.rodriguez.escuelaDluz.controllers;
//
//import org.springframework.boot.web.servlet.error.ErrorController;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//@Controller
//public class CustomErrorController implements ErrorController {
//
//    @GetMapping("/error")
//    public String handleError(Model model, RedirectAttributes redirectAttributes) {
//    	redirectAttributes.addFlashAttribute("msj", "Direccion no encontrada, redireccionando a listado, error: ");
//		redirectAttributes.addFlashAttribute("tipoMsj", "danger");
//		
//		return "redirect:/home";
//    }
//}
