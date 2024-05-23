package ua.bank.moneyguard.controllers.view;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class AuthorizationController {

    @GetMapping("/login")
    public String getLoginPage(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/main/";
        }
        return "html/login";
    }

    @GetMapping("/registration")
    public String getRegisterPage(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/main/";
        }
        return "html/registration";
    }

    @GetMapping("/forgotPass")
    public String getForgotPasswordPage(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/main/";
        }
        return "html/forgotPass";
    }

    @GetMapping(path = "/changePass")
    public String getPageForChangeForgotPassword(){
        return "html/changePass";
    }
}
