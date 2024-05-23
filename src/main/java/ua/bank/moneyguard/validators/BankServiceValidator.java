package ua.bank.moneyguard.validators;

import ua.bank.moneyguard.dtos.ServicesDTOs.BankServiceFormDTO;
import ua.bank.moneyguard.dtos.ServicesDTOs.BankServicePayDTO;
import ua.bank.moneyguard.exceptions.*;
import ua.bank.moneyguard.mappers.Mapper;
import ua.bank.moneyguard.models.*;

public class BankServiceValidator {
    public static Account checkToValidBankServiceGetService(BankServiceModelExample bankServiceModelExample) throws NoAvailableLimit, AccountNotFound, NotSupportedService, NotEnoughMoney {
        if (bankServiceModelExample.getAmount() <= 0) {
            throw new ArithmeticException("The amount cannot be less than or equal to 0");
        }
        if (bankServiceModelExample.getClass() == Credit.class) {
            if (bankServiceModelExample.getAmount() > bankServiceModelExample.getClient().getTitul().getLimitForService()) {
                throw new NoAvailableLimit(bankServiceModelExample.getClient().getTitul().getLimitForService());
            }
        }
        Account account = bankServiceModelExample.getClient().getAccounts().stream().filter(a -> a.getCurrencyName().equals(bankServiceModelExample.getCurrencyName()))
                .findAny().orElseThrow(AccountNotFound::new);
        if (bankServiceModelExample.getClass() == Deposit.class) {
            if (bankServiceModelExample.getAmount() > account.getAmountOfMoney()) {
                throw new NotEnoughMoney();
            }
        }
        return account;
    }

    public static Account checkToValidBankServicePay(BankServicePayDTO bankServicePayDTO, Client client,
                                                     BankServiceModelExample bankServiceModelExample) throws AccountNotFound, NotEnoughMoney, NotSupportedService {
        if (bankServicePayDTO.getAmount() <= 0) {
            throw new ArithmeticException("The amount cannot be less than or equal to 0");
        }
        Account account = client.getAccounts().stream().filter(a -> a.getCurrencyName()
                .equals(bankServiceModelExample.getCurrencyName())).findAny().orElseThrow(AccountNotFound::new);
        if (bankServicePayDTO.getAmount() > account.getAmountOfMoney()) {
            throw new NotEnoughMoney();
        }
        return account;
    }

    public static BankServiceModelExample checkAndReturnWhatServiceDoYouHaveWithMapping(BankServiceFormDTO bankServiceFormDTO, Client client, String serviceName) throws NotSupportedService {
        BankServiceModelExample bankServiceModelExample;
        if (serviceName.equals("deposit")) {
            bankServiceModelExample = Mapper.convertBankServiceFormDTOToBankService(bankServiceFormDTO, client, new Deposit());
        } else if (serviceName.equals("credit")) {
            bankServiceModelExample = Mapper.convertBankServiceFormDTOToBankService(bankServiceFormDTO, client, new Credit());
        } else if (serviceName.equals("savingjar")) {
            bankServiceModelExample = Mapper.convertBankServiceFormDTOToBankService(bankServiceFormDTO, client, new SavingJar());
        } else {
            throw new NotSupportedService();
        }
        return bankServiceModelExample;
    }

    public static BankServiceModelExample checkAndReturnWhatServiceDoYouHave(String serviceName) throws NotSupportedService {
        BankServiceModelExample bankServiceModelExample;
        if (serviceName.equals("deposit")) {
            bankServiceModelExample = new Deposit();
        } else if (serviceName.equals("credit")) {
            bankServiceModelExample = new Credit();
        } else if (serviceName.equals("savingjar")) {
            bankServiceModelExample = new SavingJar();
        } else {
            throw new NotSupportedService();
        }
        return bankServiceModelExample;
    }

    public static void checkToValidFormDTO(BankServiceFormDTO bankServiceFormDTO) throws FieldIsEmpty {
        if (bankServiceFormDTO.getAmount() == null || bankServiceFormDTO.getServiceName() == null
                || bankServiceFormDTO.getCurrencyName() == null) {
            throw new FieldIsEmpty();
        }
        if (bankServiceFormDTO.getAmount() <= 0) {
            throw new ArithmeticException("amount cannot be less than or equal to zero.");
        }
        if (bankServiceFormDTO.getServiceName().isBlank() || bankServiceFormDTO.getCurrencyName().isBlank()) {
            throw new FieldIsEmpty();
        }
    }

    public static void checkToValidFormDTO(BankServicePayDTO bankServicePayDTO) throws FieldIsEmpty {
        if (bankServicePayDTO.getId() == null || bankServicePayDTO.getAmount() == null) {
            throw new FieldIsEmpty();
        }
        if (bankServicePayDTO.getAmount() <= 0) {
            throw new ArithmeticException("amount cannot be less than or equal to zero.");
        }
    }
}
