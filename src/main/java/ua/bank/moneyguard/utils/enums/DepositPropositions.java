package ua.bank.moneyguard.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum DepositPropositions implements ServicePropositions{
    MODEST_TREASURE("MODEST_TREASURE", 0.03, 3),
    LUXURIOUS_TREASURE("LUXURIOUS_TREASURE", 0.06, 6),
    DIVINE_TREASURE("DIVINE_TREASURE", 0.12, 12);
    private String serviceName;
    private Double interestRatePerMonth;
    private Integer numberOfMonths;
}
