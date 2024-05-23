package ua.bank.moneyguard.dtos.mainDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.bank.moneyguard.utils.enums.Tituls;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {
    private String IBAN;
    private String firstName;
    private String secondName;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String email;
    private Long cashBackInUSD;
    private Tituls titul;
    private Long limitForService;
}
