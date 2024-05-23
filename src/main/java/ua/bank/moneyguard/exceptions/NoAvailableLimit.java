package ua.bank.moneyguard.exceptions;

public class NoAvailableLimit extends Exception {
    public NoAvailableLimit(Long limitForService) {
        this.limitForService = limitForService;
    }

    private Long limitForService;

    @Override
    public String getMessage() {
        return "You cannot perform a transaction that exceeds your current limit like: " + limitForService;
    }
}
