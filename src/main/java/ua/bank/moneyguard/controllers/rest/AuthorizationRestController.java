package ua.bank.moneyguard.controllers.rest;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ua.bank.moneyguard.dtos.mainDTOs.ClientDTO;
import ua.bank.moneyguard.dtos.authorizationDTOs.EmailDTO;
import ua.bank.moneyguard.dtos.authorizationDTOs.PasswordDTO;
import ua.bank.moneyguard.dtos.authorizationDTOs.RegistrationDTO;
import ua.bank.moneyguard.mappers.Mapper;
import ua.bank.moneyguard.models.Account;
import ua.bank.moneyguard.models.Client;
import ua.bank.moneyguard.services.AccountService;
import ua.bank.moneyguard.services.MainCardService;
import ua.bank.moneyguard.services.RegistrationService;
import ua.bank.moneyguard.utils.ApiResponse;

@AllArgsConstructor
@RestController
@RequestMapping("/api/registration")
public class AuthorizationRestController {

    private final RegistrationService registrationService;
    private final AccountService accountService;
    private final MainCardService mainCardService;

    @PostMapping
    public ApiResponse register(@RequestBody RegistrationDTO registrationDTO) {
        try {
            Client client = registrationService.registration(registrationDTO);
            ClientDTO clientDTO = Mapper.clientToClientDTO(client);
            Account account = accountService.createAccountWithCurrencyName("UAH", client);
            mainCardService.createMainCard(account);
            return new ApiResponse(true, "Registration successful, please follow the instructions sent to you by email.", clientDTO);
        } catch (Exception e) {
            return new ApiResponse(false, "Registration failed: " + e.getMessage());
        }
    }

    @GetMapping(path = "/confirm")
    public String confirm(@RequestParam("token") String token) {
        try {
            return registrationService.confirmToken(token);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @PostMapping("/forgotPass")
    public ApiResponse forgotPassword(@RequestBody EmailDTO emailDTO) throws Exception {
        try {
            registrationService.forgotPassword(emailDTO.getEmail());
            return new ApiResponse(true, "Please follow the instructions sent to you by email.");
        } catch (Exception e) {
            return new ApiResponse(false, e.getMessage());
        }
    }

    @PostMapping("/changePass")
    public ApiResponse changePassword(@RequestBody PasswordDTO passwordDTO,
                                      @RequestParam("token") String token) {
        try {
            registrationService.changePasswordByToken(token, passwordDTO);
            return new ApiResponse(true, "Password successfully changed.");
        } catch (Exception e) {
            return new ApiResponse(false, e.getMessage());
        }
    }
}


