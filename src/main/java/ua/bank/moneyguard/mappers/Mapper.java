package ua.bank.moneyguard.mappers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ua.bank.moneyguard.dtos.ExchangeRateDTOFromAPI;
import ua.bank.moneyguard.dtos.ServicesDTOs.BankServiceFormDTO;
import ua.bank.moneyguard.dtos.ServicesDTOs.BankServiceShowDTO;
import ua.bank.moneyguard.dtos.authorizationDTOs.RegistrationDTO;
import ua.bank.moneyguard.dtos.mainDTOs.AccountDTO;
import ua.bank.moneyguard.dtos.mainDTOs.ClientDTO;
import ua.bank.moneyguard.dtos.mainDTOs.MainCardDTO;
import ua.bank.moneyguard.dtos.mainDTOs.TransactionDTO;
import ua.bank.moneyguard.models.*;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class Mapper {

    public static Client registrationDTOtoClients(RegistrationDTO registrationDTO) {
        return new Client(registrationDTO.getFirstName(), registrationDTO.getSecondName(),
                registrationDTO.getDateOfBirth(), registrationDTO.getPhoneNumber(),
                registrationDTO.getEmail(), registrationDTO.getPassword());
    }

    public static ClientDTO clientToClientDTO(Client client) {
        return new ClientDTO(client.getIBAN(), client.getFirstName(),
                client.getSecondName(), client.getDateOfBirth(),
                client.getPhoneNumber(), client.getEmail(),
                client.getCashBackInUSD(), client.getTitul(),
                client.getTitul().getLimitForService());
    }

    public static AccountDTO accountToAccountDTO(Account account) {
        List<MainCardDTO> mainCardDTOS = listMainCardToListMainCardDTO(account.getCardIds());
        AccountDTO accountDTO = new AccountDTO(mainCardDTOS, account.getAmountOfMoney(), account.getCurrencyName());
        return accountDTO;
    }

    public static List<MainCardDTO> listMainCardToListMainCardDTO(List<MainCard> list) {
        return list.stream().map(m ->
                        new MainCardDTO(m.getValidity(), m.getCvv(), m.getOwnerName(), m.getCardNumber()))
                .collect(Collectors.toList());
    }

    public static TransactionDTO transactionToTransactionDTO(Transaction transaction) {
        return new TransactionDTO(transaction.getDateTimeOfTransaction(), transaction.getNameOfTransaction(),
                transaction.getFromWhom(), transaction.getForWhom(), transaction.getHowMuch());
    }

    public static ExchangeRate convertExchangeRateDTOFromAPIToExchangeRate(ExchangeRateDTOFromAPI forSell, ExchangeRateDTOFromAPI forBuy) {
        return new ExchangeRate(forSell.getCc(), forSell.getEnname(), forSell.getRate(), forBuy.getRate());
    }


    public static <T extends BankServiceModelExample> T convertBankServiceFormDTOToBankService(
            BankServiceFormDTO dto, Client client, T emptyBankServiceClass) {
        emptyBankServiceClass.updateFromDTO(dto, client);
        return emptyBankServiceClass;
    }

    public static <T extends BankServiceModelExample> BankServiceShowDTO convertBankServiceToBankServiceShowDTO(T BankServiceClass) {
        Long idForTClass;
        if (BankServiceClass.getClass() == Deposit.class) {
            idForTClass = ((Deposit) BankServiceClass).getDepositsId();
        } else if (BankServiceClass.getClass() == Credit.class) {
            idForTClass = ((Credit) BankServiceClass).getCreditsId();
        } else {
            idForTClass = ((SavingJar) BankServiceClass).getJarId();
        }
        return new BankServiceShowDTO(idForTClass, BankServiceClass.getServiceName(),
                BankServiceClass.getCurrencyName(), BankServiceClass.getAmount(),
                BankServiceClass.getInterestRatePerMonth(), BankServiceClass.getServiceDurationTo(),
                BankServiceClass.getAccumulated());
    }
}
