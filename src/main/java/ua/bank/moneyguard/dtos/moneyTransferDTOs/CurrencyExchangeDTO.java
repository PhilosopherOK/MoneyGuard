package ua.bank.moneyguard.dtos.moneyTransferDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyExchangeDTO {
    private String currencyNameFrom;
    private String currencyNameTo;
    private Double transferAmount;
}
