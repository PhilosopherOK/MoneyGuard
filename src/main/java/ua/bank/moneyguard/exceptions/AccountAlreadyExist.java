package ua.bank.moneyguard.exceptions;

public class AccountAlreadyExist extends Exception {
    @Override
    public String getMessage() {
        return "Account with currency name already exist";
    }
}
