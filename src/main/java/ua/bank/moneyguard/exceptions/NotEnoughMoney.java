package ua.bank.moneyguard.exceptions;

public class NotEnoughMoney extends Exception{
    @Override
    public String getMessage() {
        return "Not enough money !";
    }
}
