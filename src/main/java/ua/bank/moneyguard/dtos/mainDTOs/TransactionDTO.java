package ua.bank.moneyguard.dtos.mainDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private LocalDateTime dateTimeOfTransaction;
    private String nameOfTransaction;
    private String fromCardNumber;
    private String toCardNumber;
    private Double howMuch;
}
