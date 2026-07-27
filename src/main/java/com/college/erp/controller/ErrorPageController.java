package com.college.erp.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorPageController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        int statusCode = status != null ? Integer.parseInt(status.toString()) : 500;
        model.addAttribute("statusCode", statusCode);

        switch (statusCode) {
            case 403 -> {
                model.addAttribute("errorTitle", "Access Denied");
                model.addAttribute("errorMessage", "You do not have permission to access this page.");
                return "error/403";
            }
            case 404 -> {
                model.addAttribute("errorTitle", "Page Not Found");
                model.addAttribute("errorMessage", "The page you are looking for does not exist.");
            }
            default -> {
                model.addAttribute("errorTitle", "Internal Server Error");
                model.addAttribute("errorMessage", message != null ? message.toString()
                        : "An unexpected error occurred.");
            }
        }
        return "error/error";
    }

    @GetMapping("/error/403")
    public String accessDenied(Model model) {
        model.addAttribute("statusCode", 403);
        model.addAttribute("errorTitle", "Access Denied");
        model.addAttribute("errorMessage", "You do not have permission to access this page.");
        return "error/403";
    }
}
