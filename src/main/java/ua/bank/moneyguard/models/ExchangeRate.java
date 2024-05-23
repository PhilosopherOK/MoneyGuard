package ua.bank.moneyguard.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Data
@Entity
@Table(name = "exchange_rate")
public class ExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rate_id")
    private Long rateId;
    @Column(name= "short_name")
    private String shortName;
    @Column(name= "full_name")
    private String fullName;
    @Column(name= "sell_rate")
    private Double sellRate;
    @Column(name= "buy_rate")
    private Double buyRate;

    public ExchangeRate(String shortName, String fullName, Double sellRate, Double buyRate) {
        this.shortName = shortName;
        this.fullName = fullName;
        this.sellRate = sellRate;
        this.buyRate = buyRate;
    }
}
