package ua.bank.moneyguard.dtos.moneyTransferDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoneyTransferCardRequestDTO {
    private String currencyName;
    private String toCardNumber;
    private Double transferAmount;
}
