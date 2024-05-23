package ua.bank.moneyguard.dtos.moneyTransferDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoneyTransferIBANRequestDTO {
    private String currencyName;
    private String toIBAN;
    private Double transferAmount;
}
