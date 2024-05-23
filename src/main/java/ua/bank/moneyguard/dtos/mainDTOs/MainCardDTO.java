package ua.bank.moneyguard.dtos.mainDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MainCardDTO {
    private LocalDate validity;
    private Integer cvv;
    private String ownerName;
    private String cardNumber;
}
