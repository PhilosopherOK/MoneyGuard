package ua.bank.moneyguard.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import ua.bank.moneyguard.dtos.ServicesDTOs.BankServiceFormDTO;
import ua.bank.moneyguard.utils.enums.DepositPropositions;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
public class Deposit extends BankServiceModelExample{
    @Id
    @Column(name = "deposits_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long depositsId;

    public Deposit(String serviceName, String currencyName, Double amount, Double interestRatePerMonth, LocalDateTime serviceDurationTo, Client client) {
        super(serviceName, currencyName, amount, interestRatePerMonth, serviceDurationTo, client);
    }
    @Override
    public void updateFromDTO(BankServiceFormDTO dto, Client client) {
        DepositPropositions props = DepositPropositions.valueOf(dto.getServiceName());
        setCommonFields(props, dto, client);
    }
}