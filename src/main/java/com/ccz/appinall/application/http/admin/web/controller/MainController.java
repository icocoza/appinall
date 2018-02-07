package com.ccz.appinall.application.http.admin.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.unbescape.html.HtmlEscape;

/**
 * Application home page and login.
 */
@Controller
public class MainController {

    @RequestMapping("/")
    public String root(Locale locale) {
        return "redirect:/index";
    }

    /** Home page. */
/*    @RequestMapping("/index")
    public String index(Model model) {
    		model.addAttribute("title", "all-in-all logo");
        return "index";
    }
*/
    /** Simulation of an exception. */
    @RequestMapping("/simulateError.html")
    public void simulateError() {
        throw new RuntimeException("This is a simulated error message");
    }

    /** Error page. */
    /*@RequestMapping("/errorpage")
    public String error(HttpServletRequest request, Model model) {
        model.addAttribute("errorCode", "Error " + request.getAttribute("javax.servlet.error.status_code"));
        Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("<ul>");
        while (throwable != null) {
            errorMessage.append("<li>").append(HtmlEscape.escapeHtml5(throwable.getMessage())).append("</li>");
            throwable = throwable.getCause();
        }
        errorMessage.append("</ul>");
        model.addAttribute("errorMessage", errorMessage.toString());
        return "error.html";
    }*/

    /** Error page. */
    @RequestMapping("/403.html")
    public String forbidden() {
        return "403";
    }


}