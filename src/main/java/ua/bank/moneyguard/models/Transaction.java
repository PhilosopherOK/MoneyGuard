package ua.bank.moneyguard.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;


@NoArgsConstructor
@Data
@Entity
public class Transaction {
    @Id
    @Column(name = "transactions_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long transactionsId;
    private LocalDateTime dateTimeOfTransaction;
    @Column(name = "name_of_transaction")
    private String nameOfTransaction;
    private String fromWhom;
    @Column(name = "to_card_number")
    private String forWhom;
    @Column(name = "how_much")
    private Double howMuch;
    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", referencedColumnName = "client_id")
    private Client client;

    public Transaction(LocalDateTime dateTimeOfTransaction, String nameOfTransaction, String fromWhom, String forWhom, Double howMuch, Client client) {
        this.dateTimeOfTransaction = dateTimeOfTransaction;
        this.nameOfTransaction = nameOfTransaction;
        this.fromWhom = fromWhom;
        this.forWhom = forWhom;
        this.howMuch = howMuch;
        this.client = client;
    }


    @Override
    public String toString() {
        return "Transaction{" +
                "transactionsId=" + transactionsId +
                ", dateTimeOfTransaction=" + dateTimeOfTransaction +
                ", nameOfTransaction='" + nameOfTransaction + '\'' +
                ", forWhom='" + forWhom + '\'' +
                ", howMuch=" + howMuch +
                ", client=" + client.getClientId() +
                '}';
    }
}