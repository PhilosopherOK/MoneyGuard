package ua.bank.moneyguard.exceptions.exceptionHandler;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        String redirectUrl = (String) redirectAttributes.getFlashAttributes().get("redirectUrl");
        return "redirect:" + (redirectUrl != null ? redirectUrl : "/admin/clients/0");
    }
}
