package ua.bank.moneyguard.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;


@NoArgsConstructor
@Data
@Entity
@Table(name = "main_card")
public class MainCard {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "card_id")
    private Long cardId;
    private LocalDate validity;
    private Integer cvv;
    @Column(name = "owner_name")
    private String ownerName;
    @Column(name = "card_number")
    private String cardNumber;
    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", referencedColumnName = "account_id")
    private Account account;

    public MainCard(String ownerName, Account account,String generateUniqueCardNumber) {
        this.validity = LocalDate.now().plusYears(4);
        this.cvv = 100 + (int)(Math.random() * 900);
        this.ownerName = ownerName;
        this.cardNumber = generateUniqueCardNumber;
        this.account = account;
    }

    @Override
    public String toString() {
        return "MainCard{" +
                "cardId=" + cardId +
                ", validity=" + validity +
                ", cvv=" + cvv +
                ", ownerName='" + ownerName + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", account=" + account.getAccountId() +
                '}';
    }
}
