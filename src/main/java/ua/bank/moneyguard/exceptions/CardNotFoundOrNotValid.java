package ua.bank.moneyguard.exceptions;

public class CardNotFoundOrNotValid extends Exception{
    @Override
    public String getMessage() {
        return "Card Not Found Or Not Valid!";
    }
}
