package ua.bank.moneyguard.services;

import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.bank.moneyguard.dtos.authorizationDTOs.PasswordDTO;
import ua.bank.moneyguard.dtos.authorizationDTOs.RegistrationDTO;
import ua.bank.moneyguard.exceptions.EmailNotFound;
import ua.bank.moneyguard.mappers.Mapper;
import ua.bank.moneyguard.models.Client;
import ua.bank.moneyguard.models.tokens.EmailToken;
import ua.bank.moneyguard.models.tokens.PassToken;
import ua.bank.moneyguard.repositories.ClientRepo;
import ua.bank.moneyguard.services.token.EmailTokenService;
import ua.bank.moneyguard.services.token.PassTokenService;
import ua.bank.moneyguard.utils.IBANGenerated;
import ua.bank.moneyguard.utils.enums.Tituls;
import ua.bank.moneyguard.utils.role.UserRole;
import ua.bank.moneyguard.validators.DTOsValidator;
import ua.bank.moneyguard.validators.EmailValidator;
import ua.bank.moneyguard.validators.PasswordsValidator;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@Service
public class RegistrationService {
    private final EmailTokenService emailTokenService;
    private final PassTokenService passTokenService;
    private final EmailSenderService emailSender;
    private final ClientRepo clientRepo;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ClientService clientService;

    private String linkToSite = "https://moneyguard-fc72823844dd.herokuapp.com";


    @Transactional
    public Client registration(RegistrationDTO registrationDTO) throws Exception {
        DTOsValidator.checkToRegistrationDTOIsValidWithOutPass(registrationDTO);
        PasswordsValidator.validate(registrationDTO.getPassword());

        Client client = Mapper.registrationDTOtoClients(registrationDTO);
        String token = singUpUser(client);

        String link = linkToSite + "/api/registration/confirm?token=" + token;
        emailSender.sendEmail(client.getEmail(), "Confirm your email",
                "Hello " + client.getFirstName() + " " + client.getSecondName() + ".\n" +
                        "Please activate your email address via the link to continue:" + "\n" +
                        link);

        return client;
    }


    @Transactional
    public String confirmToken(String token) {
        EmailToken emailToken = emailTokenService.getToken(token)
                .orElseThrow(() -> new IllegalStateException("token not found"));

        if (emailToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }
        LocalDateTime expiredAt = emailToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        emailTokenService.setConfirmedAt(token);

        clientService.setEnableByClientEmail(
                emailToken.getClient().getEmail());
        return "Email has been successfully registered (=";
    }

    @Transactional
    public String singUpUser(Client client) {
        if (clientRepo.findClientByEmailLike(client.getEmail()).isPresent()) {
            Client client1 = clientRepo.findClientByEmailLike(client.getEmail()).get();
            if (!client1.isEnabled()) {
                EmailToken emailToken = emailTokenService.findTokenByClient(client1);

                String link = linkToSite + "/api/registration/confirm?token=";
                if (emailToken == null) {
                    String token = createEmailToken(client1);

                    emailSender.sendEmail(client1.getEmail(), "Confirm your email",
                            "Hello " + client1.getFirstName() + client1.getSecondName() + ".\n" +
                                    "Please activate your email address via the link to continue:" + "\n" +
                                    link + token);
                    throw new IllegalStateException("User was not expired. We send new code on your Email address");

                } else if (emailToken.getConfirmedAt() == null) {
                    emailSender.sendEmail(client1.getEmail(), "Confirm your email",
                            "Hello " + client1.getFirstName() + client1.getSecondName() + ".\n" +
                                    "Please reactivate your email address via the link to continue:" + "\n" +
                                    link + emailToken.getToken());
                    throw new IllegalStateException("User was not expired. We re-send your code on your Email address");
                }
            }
            throw new IllegalStateException("User with this email already exist");
        }
        String encodePassword = bCryptPasswordEncoder.encode(client.getPassword());
        client.setPassword(encodePassword);
        client.setUserRole(UserRole.USER);
        client.setTitul(Tituls.HERALD);
        Client savedClient = clientRepo.save(client);
        savedClient.setIBAN(IBANGenerated.generator(savedClient.getClientId().toString()));
        clientRepo.save(savedClient);

        return createEmailToken(savedClient);
    }

    private String createEmailToken(Client client) {
        String token = UUID.randomUUID().toString();
        EmailToken emailToken = new EmailToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                client
        );
        emailTokenService.saveToken(emailToken);
        return token;
    }


    public void forgotPassword(String email) throws Exception {
        EmailValidator.validate(email);
        String link = linkToSite + "/changePass?token=";
        Client client = clientService.findByEmail(email).orElseThrow(EmailNotFound::new);
        String token = createPasswordToken(client);
        emailSender.sendEmail(client.getEmail(), "Confirm your email",
                "Hello " + client.getFirstName() + " " + client.getSecondName() + ".\n" +
                        "Please confirm your password by clicking on the link:" + "\n" +
                        link + token);
    }

    public void changePasswordByToken(String token, PasswordDTO passwordDTO) throws Exception {
        PassToken passToken = passTokenService.findByToken(token);
        PasswordsValidator.checkToValidPasswordDTO(passwordDTO);
        Client client = passToken.getClient();
        String encodePassword = bCryptPasswordEncoder.encode(passwordDTO.getNewPassword());
        client.setPassword(encodePassword);
        clientService.saveAndReturnObj(client);
        passTokenService.deleteByToken(token);
    }

    private String createPasswordToken(Client client) {
        String token = UUID.randomUUID().toString();
        PassToken passToken = new PassToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(5),
                client
        );
        passTokenService.save(passToken);
        return token;
    }
}
