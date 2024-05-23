package ua.bank.moneyguard.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import ua.bank.moneyguard.dtos.ServicesDTOs.BankServiceFormDTO;
import ua.bank.moneyguard.utils.enums.SavingJarPropositions;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@Entity
@Table(name = "saving_jar")
public class SavingJar extends BankServiceModelExample {
    @Id
    @Column(name = "jar_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long jarId;

    public SavingJar(String serviceName, String currencyName, Double amount, Double interestRatePerMonth, LocalDateTime serviceDurationTo, Client client) {
        super(serviceName, currencyName, amount, interestRatePerMonth, serviceDurationTo, client);
    }
    @Override
    public void updateFromDTO(BankServiceFormDTO dto, Client client) {
        SavingJarPropositions props = SavingJarPropositions.valueOf(dto.getServiceName());
        setCommonFields(props, dto, client);
    }
}