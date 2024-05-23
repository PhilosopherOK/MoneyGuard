package ua.bank.moneyguard.dtos.mainDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private List<MainCardDTO> cardIds;
    private Double amountOfMoney;
    private String currencyName;
}
