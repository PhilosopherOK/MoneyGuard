package ua.bank.moneyguard.dtos.ServicesDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankServiceShowDTO {
    private Long serviceId;
    private String serviceName;
    private String currencyName;
    private Double amount;
    private Double interestRatePerMonth;
    private LocalDateTime serviceDurationTo;
    private Double accumulated;
}
