package ua.bank.moneyguard.exceptions;

public class TokenNotFound extends Exception{
    @Override
    public String getMessage() {
        return "Invalid token";
    }
}
