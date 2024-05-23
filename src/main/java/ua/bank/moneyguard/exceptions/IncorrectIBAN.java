package ua.bank.moneyguard.exceptions;

public class IncorrectIBAN extends Exception{
    @Override
    public String getMessage() {
        return "Incorrect IBAN";
    }
}
