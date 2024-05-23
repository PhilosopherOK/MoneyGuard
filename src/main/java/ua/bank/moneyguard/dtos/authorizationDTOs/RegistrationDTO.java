package ua.bank.moneyguard.dtos.authorizationDTOs;

import lombok.*;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDTO {
    private String firstName;
    private String secondName;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String email;
    private String password;
}
