package ua.bank.moneyguard.validators;

import ua.bank.moneyguard.exceptions.AccountNotFound;
import ua.bank.moneyguard.exceptions.NotEnoughMoney;
import ua.bank.moneyguard.models.Account;
import ua.bank.moneyguard.models.Client;

public class TransferValidator {
    public static Account checkToValidTransferData(Client client, String currencyName, Double transferAmount) throws NotEnoughMoney, AccountNotFound {
        if (transferAmount <= 0) {
            throw new ArithmeticException("amount cannot be less than or equal to zero.");
        }
        Account accountFrom = client.getAccounts().stream().filter(a -> a.getCurrencyName().equals(currencyName)).findAny().orElseThrow(AccountNotFound::new);
        if (accountFrom.getAmountOfMoney() < transferAmount) {
            throw new NotEnoughMoney();
        }
        return accountFrom;
    }
}
