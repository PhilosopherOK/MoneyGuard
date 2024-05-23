package ua.bank.moneyguard.exceptions;

public class NotSupportedService extends Exception{
    @Override
    public String getMessage() {
        return "Not supported or not found service.";
    }
}
