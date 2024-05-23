package ua.bank.moneyguard.exceptions;

public class EmailNotFound extends Exception{
    @Override
    public String getMessage() {
        return "This email is not found.";
    }
}
