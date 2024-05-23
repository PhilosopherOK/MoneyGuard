package ua.bank.moneyguard.validators;

import ua.bank.moneyguard.dtos.authorizationDTOs.PasswordDTO;
import ua.bank.moneyguard.exceptions.FieldIsEmpty;
import ua.bank.moneyguard.exceptions.IncorrectSpecialField;

import java.util.regex.Pattern;

public class PasswordsValidator {
    private static final String PASSWORD_PATTERN =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,20}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    public static boolean validate(String password) throws Exception {
        if (password == null || password.isBlank()) {
            throw new FieldIsEmpty();
        }
        if(!pattern.matcher(password).matches()){
            throw new IncorrectSpecialField("The password must contain at least one uppercase letter, one lowercase letter, one number, one special symbol(!@#$%^&*) and be between 8 and 20 characters long.");
        }else {
            return true;
        }
    }

    public static void checkToValidPasswordDTO(PasswordDTO passwordDTO) throws Exception {
        if (!passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())) {
            throw new IncorrectSpecialField("Passwords not equals !");
        }
        if(passwordDTO.getNewPassword().isBlank() || passwordDTO.getConfirmPassword().isBlank()){
            throw new FieldIsEmpty();
        }
        validate(passwordDTO.getNewPassword());
    }
}
