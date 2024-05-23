package ua.bank.moneyguard.utils.role;

public enum UserRole {
    USER, ADMIN;

    @Override
    public String toString() {
        return "ROLE_" + name();
    }
}
