package ua.bank.moneyguard.validators;

import ua.bank.moneyguard.exceptions.FieldIsEmpty;
import ua.bank.moneyguard.exceptions.IncorrectSpecialField;

import java.util.regex.Pattern;
public class EmailValidator {
    private static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    public static boolean validate(String email) throws FieldIsEmpty, IncorrectSpecialField {
        if(email == null || email.isBlank()){
            throw new FieldIsEmpty();
        }
        if(!pattern.matcher(email).matches()){
            throw new IncorrectSpecialField("Email is not valid.");
        }
        return true;
    }
}