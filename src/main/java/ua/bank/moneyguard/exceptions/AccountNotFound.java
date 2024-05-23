package ua.bank.moneyguard.exceptions;

public class AccountNotFound extends Exception{
    @Override
    public String getMessage() {
        return "Account with this currency is not found";
    }
}
