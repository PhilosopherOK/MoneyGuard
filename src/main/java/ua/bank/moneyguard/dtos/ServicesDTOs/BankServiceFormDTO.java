package ua.bank.moneyguard.dtos.ServicesDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankServiceFormDTO {
    private String serviceName;
    private String currencyName;
    private Double amount;
}
