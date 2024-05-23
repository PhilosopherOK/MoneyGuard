package ua.bank.moneyguard.validators;

import ua.bank.moneyguard.exceptions.FieldIsEmpty;
import ua.bank.moneyguard.exceptions.IncorrectSpecialField;

import java.util.regex.Pattern;

public class PhoneValidator {
    private static final String PHONE_PATTERN = "^380(50|66|95|99|63|93|67|96|97|98|68|39|91|92|94|89)[0-9]{7}$";
    private static final Pattern pattern = Pattern.compile(PHONE_PATTERN);

    public static boolean validate(String phoneNumber) throws FieldIsEmpty, IncorrectSpecialField {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new FieldIsEmpty();
        }
        if(!pattern.matcher(phoneNumber).matches()){
            throw new IncorrectSpecialField("Phone number should be starting with 380 + 2 numb operator code + 7 numbers !");
        }
        return true;
    }
}