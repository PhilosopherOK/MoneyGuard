package ua.bank.moneyguard.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SavingJarPropositions implements ServicePropositions{
    JAR_PROPOSITION_ON_3_MONTH("JAR_PROPOSITION_ON_3_MONTH", 0.0, 3),
    JAR_PROPOSITION_ON_6_MONTH("JAR_PROPOSITION_ON_6_MONTH", 0.0, 6);

    private String serviceName;
    private Double interestRatePerMonth;
    private Integer numberOfMonths;
}
