package ua.bank.moneyguard.dtos.ServicesDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankServicePayDTO {
    private Long id;
    private Double amount;
}
