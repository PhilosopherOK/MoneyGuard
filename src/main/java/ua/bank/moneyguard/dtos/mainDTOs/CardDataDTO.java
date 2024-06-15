package ua.bank.moneyguard.dtos.mainDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CardDataDTO {
    private String cardNumber;
    private LocalDate validity;
    private Integer cvv;
    private Double amountOfMoney;
    private String ownerName;
}
