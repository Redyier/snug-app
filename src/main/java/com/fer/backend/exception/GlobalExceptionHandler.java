package com.fer.backend.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFoundException(EntityNotFoundException ex, Model model){
        model.addAttribute("poruka", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(ValidationException.class)
    public String handleValidation(ValidationException ex, Model model) {
        model.addAttribute("poruka", ex.getMessage());
        return "error/500";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneral(Exception ex, Model model) {
        model.addAttribute("poruka", ex.getMessage());
        return "error/500";
    }
}
