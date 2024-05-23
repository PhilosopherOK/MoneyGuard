package ua.bank.moneyguard.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.bank.moneyguard.dtos.ServicesDTOs.BankServiceFormDTO;
import ua.bank.moneyguard.utils.enums.ServicePropositions;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@MappedSuperclass
public abstract class BankServiceModelExample {
    @Column(name = "service_name")
    private String serviceName;
    private String currencyName;
    private Double amount;
    @Column(name = "interest_rate_per_month")
    private Double interestRatePerMonth;
    @Column(name = "service_duration_to", columnDefinition = "TIMESTAMP")
    private LocalDateTime serviceDurationTo;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", referencedColumnName = "client_id")
    private Client client;
    private Double accumulated;

    public BankServiceModelExample(String serviceName, String currencyName, Double amount, Double interestRatePerMonth, LocalDateTime serviceDurationTo, Client client) {
        this.serviceName = serviceName;
        this.currencyName = currencyName;
        this.amount = amount;
        this.interestRatePerMonth = interestRatePerMonth;
        this.serviceDurationTo = serviceDurationTo;
        this.client = client;
    }

    public abstract void updateFromDTO(BankServiceFormDTO dto, Client client);

    protected void setCommonFields(ServicePropositions props, BankServiceFormDTO dto, Client client) {
        setServiceName(props.getServiceName());
        setCurrencyName(dto.getCurrencyName());
        setAmount(dto.getAmount());
        setInterestRatePerMonth(props.getInterestRatePerMonth());
        setServiceDurationTo(LocalDateTime.now().plusMonths(props.getNumberOfMonths()));
        setClient(client);
    }

    @Override
    public String toString() {
        return "BankServiceModelExample{" +
                "serviceName='" + serviceName + '\'' +
                ", currencyName='" + currencyName + '\'' +
                ", amount=" + amount +
                ", interestRatePerMonth=" + interestRatePerMonth +
                ", serviceDurationTo=" + serviceDurationTo +
                ", client=" + client.getClientId() +
                ", accumulated=" + accumulated +
                '}';
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
