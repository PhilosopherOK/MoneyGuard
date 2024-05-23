package ua.bank.moneyguard.exceptions;

public class FieldIsEmpty extends Exception{
    @Override
    public String getMessage() {
        return "Field cannot be empty.";
    }
}
