package ua.bank.moneyguard.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;



@AllArgsConstructor
@Getter
public enum CreditPropositions implements ServicePropositions{
    MINI_CREDIT("MINI_CREDIT", 0.36, 3),
    NOBLE_CREDIT("NOBLE_CREDIT", 0.30, 6),
    DIVINE_CREDIT("DIVINE_CREDIT", 0.24, 12);

    private String serviceName;
    private Double interestRatePerMonth;
    private Integer numberOfMonths;
}
