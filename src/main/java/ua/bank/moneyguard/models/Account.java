package ua.bank.moneyguard.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@Data
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "account_id")
    private Long accountId;

    @JsonManagedReference
    @OneToMany(mappedBy = "account", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<MainCard> cardIds;

    @Column(name = "currency_name")
    private String currencyName;

    @Column(name = "amount_of_money")
    private Double amountOfMoney;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    @JsonBackReference
    private Client client;


    public Account(String currencyName, Client client) {
        this.cardIds = new ArrayList<>();
        this.currencyName = currencyName;
        this.client = client;
        amountOfMoney = 0.0;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", cardIds=" + cardIds +
                ", currencyName='" + currencyName + '\'' +
                ", amountOfMoney=" + amountOfMoney +
                ", client=" + client.getClientId() +
                '}';
    }
}
