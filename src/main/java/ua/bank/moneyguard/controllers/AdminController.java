package ua.bank.moneyguard.controllers;

import lombok.Data;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.bank.moneyguard.dtos.authorizationDTOs.RegistrationDTO;
import ua.bank.moneyguard.exceptions.IncorrectSpecialField;
import ua.bank.moneyguard.mappers.Mapper;
import ua.bank.moneyguard.models.Client;
import ua.bank.moneyguard.services.ClientService;
import ua.bank.moneyguard.utils.IBANGenerated;
import ua.bank.moneyguard.utils.enums.Tituls;
import ua.bank.moneyguard.utils.role.UserRole;
import ua.bank.moneyguard.validators.DTOsValidator;
import ua.bank.moneyguard.validators.PasswordsValidator;


@Controller
@Data
@RequestMapping("/admin")
public class AdminController {
    private final ClientService clientService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/clients/{page}")
    public String findAllClients(@PathVariable(value = "page", required = false) Integer page, Model model) {
        if (page == null) {
            page = 0;
        }
        model.addAttribute("clients", clientService.findAll(page, 10));
        return "admin/clients";
    }

    @GetMapping("/create")
    public String createClientGET(Model model) {
        model.addAttribute("client", new Client());
        return "admin/create";
    }

    @PostMapping("/create")
    public String createClientPOST(@ModelAttribute RegistrationDTO registrationDTO,
                                   RedirectAttributes redirectAttributes) throws Exception {
        try {
            PasswordsValidator.validate(registrationDTO.getPassword());
            DTOsValidator.checkToRegistrationDTOIsValidWithOutPass(registrationDTO);
            if(clientService.findByEmail(registrationDTO.getEmail()).orElse(null) != null){
                throw new IncorrectSpecialField("this email is already exist.");
            }

            Client client = Mapper.registrationDTOtoClients(registrationDTO);

            client.setPassword(bCryptPasswordEncoder.encode(client.getPassword()));
            client.setUserRole(UserRole.USER);
            client.setTitul(Tituls.HERALD);
            client.setEnabled(true);
            Client savedClient = clientService.save(client);
            savedClient.setIBAN(IBANGenerated.generator(savedClient.getClientId().toString()));
            clientService.save(savedClient);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("redirectUrl", "/admin/create");
            return "redirect:/admin/create";
        }
        return "redirect:/admin/clients/0";
    }

    @PostMapping("/deleteById/{id}")
    public String deleteClient(@PathVariable Long id) {
        if (clientService.findAdmin().getClientId() != id) {
            clientService.deleteById(id);
        }
        return "redirect:/admin/clients/0";
    }

    @PostMapping("/blockClient/{id}")
    public String blockClient(@PathVariable Long id) throws ChangeSetPersister.NotFoundException {
        if (clientService.findAdmin().getClientId() != id) {
            clientService.setUnEnableByClientId(id);
        }
        return "redirect:/admin/clients/0";
    }

    @PostMapping("/unblockClient/{id}")
    public String unblockClient(@PathVariable Long id) throws ChangeSetPersister.NotFoundException {
        if (clientService.findAdmin().getClientId() != id) {
            clientService.setEnableByClientId(id);
        }
        return "redirect:/admin/clients/0";
    }


    @GetMapping("/updateById/{id}")
    public String updateClientGET(@PathVariable Long id, Model model) {
        model.addAttribute("client", clientService.findById(id));
        return "admin/update";
    }

    @PostMapping("/updateById/{id}")
    public String updateClientPOST(@PathVariable Long id, @ModelAttribute RegistrationDTO registrationDTO,
                                   RedirectAttributes redirectAttributes) {
        try {
            clientService.updateClientFromRegistrationDTO(id, registrationDTO);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("redirectUrl", "/admin/updateById/"+ id);
            return "redirect:/admin/updateById/{id}";
        }
        return "redirect:/admin/clients/0";
    }
}
