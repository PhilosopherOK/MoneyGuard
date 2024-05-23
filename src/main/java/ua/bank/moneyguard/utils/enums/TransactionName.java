package ua.bank.moneyguard.utils.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TransactionName {
    CARD_TRANSFER, IBAN_TRANSFER, CURRENCY_EXCHANGE, GET_SERVICE, SERVICE_REPLENISHMENT, WITHDRAWAL
}
