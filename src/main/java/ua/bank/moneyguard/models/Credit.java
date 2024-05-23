package ua.bank.moneyguard.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import ua.bank.moneyguard.dtos.ServicesDTOs.BankServiceFormDTO;
import ua.bank.moneyguard.utils.enums.CreditPropositions;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@Entity
public class Credit extends BankServiceModelExample{
    @Id
    @Column(name = "credits_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long creditsId;

    private Boolean isStitched = false;
    public Credit(String serviceName, String currencyName, Double amount, Double interestRatePerMonth, LocalDateTime serviceDurationTo, Client client) {
        super(serviceName, currencyName, amount, interestRatePerMonth, serviceDurationTo, client);
    }

    @Override
    public void updateFromDTO(BankServiceFormDTO dto, Client client) {
        CreditPropositions props = CreditPropositions.valueOf(dto.getServiceName());
        setCommonFields(props, dto, client);
    }
}


