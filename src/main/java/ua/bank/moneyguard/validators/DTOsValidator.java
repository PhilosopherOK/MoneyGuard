package ua.bank.moneyguard.validators;

import ua.bank.moneyguard.dtos.authorizationDTOs.RegistrationDTO;
import ua.bank.moneyguard.exceptions.FieldIsEmpty;
import ua.bank.moneyguard.exceptions.IncorrectSpecialField;

import java.time.LocalDate;

public class DTOsValidator {

    public static void checkToRegistrationDTOIsValidWithOutPass(RegistrationDTO registrationDTO) throws Exception {
        if (registrationDTO == null) {
            throw new Exception("Bad request !");
        }
        EmailValidator.validate(registrationDTO.getEmail());
        PhoneValidator.validate(registrationDTO.getPhoneNumber());
        if (registrationDTO.getFirstName().isBlank() || registrationDTO.getFirstName() == null
                || registrationDTO.getSecondName().isBlank() || registrationDTO.getSecondName() == null
                || registrationDTO.getDateOfBirth() == null) {
            throw new FieldIsEmpty();
        }
        if (registrationDTO.getDateOfBirth().isAfter(LocalDate.now())) {
            throw new IncorrectSpecialField("The date must be earlier than the current date.");
        }
        if (registrationDTO.getFirstName().length() < 3 || registrationDTO.getFirstName().length() > 30
                || registrationDTO.getSecondName().length() < 3 || registrationDTO.getSecondName().length() > 30){
            throw new IncorrectSpecialField("Names must be more than 3 characters and less than 30 characters.");
        }
    }
}
