package ua.bank.moneyguard.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.bank.moneyguard.dtos.mainDTOs.CardDataDTO;
import ua.bank.moneyguard.exceptions.AccountNotFound;
import ua.bank.moneyguard.exceptions.CardNotFoundOrNotValid;
import ua.bank.moneyguard.exceptions.IncorrectIBAN;
import ua.bank.moneyguard.exceptions.NotEnoughMoney;
import ua.bank.moneyguard.models.Account;
import ua.bank.moneyguard.models.Client;
import ua.bank.moneyguard.models.MainCard;
import ua.bank.moneyguard.repositories.AccountRepo;
import ua.bank.moneyguard.repositories.ClientRepo;
import ua.bank.moneyguard.repositories.MainCardRepo;
import ua.bank.moneyguard.utils.GenerateAndFilterBasedOnFormulaLuna;
import ua.bank.moneyguard.validators.TransferValidator;

import javax.naming.directory.InvalidAttributesException;
import java.util.List;

@AllArgsConstructor
@Service
public class MainCardService implements ServiceExample<MainCard> {
    private final AccountRepo accountRepo;
    private final MainCardRepo mainCardRepo;
    private final ClientRepo clientRepo;
    private final ExchangeRateService exchangeRateService;

    @Transactional(readOnly = true)
    @Override
    public MainCard findById(Long id) {
        return mainCardRepo.findById(id).orElse(null);
    }

    @Transactional
    @Override
    public Client save(MainCard mainCard) {
        mainCardRepo.save(mainCard);
        return null;
    }

    @Transactional
    @Override
    public void saveAll(List<MainCard> list) {
        mainCardRepo.saveAll(list);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        mainCardRepo.deleteById(id);
    }

    @Transactional
    @Override
    public void update(Long id, MainCard mainCard) {
        mainCard.setCardId(id);
        mainCardRepo.save(mainCard);
    }

    @Transactional(readOnly = true)
    public String generateUniqueCardNumber() {
        String newCardNum = GenerateAndFilterBasedOnFormulaLuna.generateMasterCardNumber();
        MainCard mainCard = mainCardRepo.findMainCardByCardNumberLike(newCardNum).orElse(null);
        if (mainCard == null) {
            return newCardNum;
        } else {
            return generateUniqueCardNumber();
        }
    }

    @Transactional
    public MainCard createMainCard(Account account) {
        MainCard mainCard = new MainCard(account.getClient().getFirstName() + " " +
                account.getClient().getSecondName(),
                account, generateUniqueCardNumber());
        account.getCardIds().add(mainCard);
        mainCardRepo.save(mainCard);
        accountRepo.save(account);
        return mainCard;
    }

    @Transactional
    public Account sendMoneyToCard(Client client, String currencyName, Double transferAmount, String toCardNumber) throws AccountNotFound, CardNotFoundOrNotValid, NotEnoughMoney, InvalidAttributesException {
        Account accountFrom = TransferValidator.checkToValidTransferData(client, currencyName, transferAmount);

        if (GenerateAndFilterBasedOnFormulaLuna.existCardFilteredByMoonFormula(toCardNumber)) {
            MainCard mainCard = mainCardRepo.findMainCardByCardNumberLike(toCardNumber).orElseThrow(CardNotFoundOrNotValid::new);
            Account accountTo = mainCard.getAccount();
            transferMoneyToAccountsOfDifferentCurrencies(transferAmount, accountFrom, accountTo);
            return accountTo;
        } else {
            throw new CardNotFoundOrNotValid();
        }
    }

    @Transactional
    public Account sendMoneyToIBAN(Client client, String currencyName, Double transferAmount, String toIBAN) throws AccountNotFound, CardNotFoundOrNotValid, NotEnoughMoney, IncorrectIBAN, InvalidAttributesException {
        Account accountFrom = TransferValidator.checkToValidTransferData(client, currencyName, transferAmount);
        Client clientTo = clientRepo.findClientByIBANLike(toIBAN).orElseThrow(IncorrectIBAN::new);

        Account accountTo = clientTo.getAccounts().stream()
                .filter(a -> a.getCurrencyName() == currencyName).findAny().orElse(null);
        if (accountTo == null) {
            accountTo = clientTo.getAccounts().stream().findAny().orElseThrow(AccountNotFound::new);
        }
        transferMoneyToAccountsOfDifferentCurrencies(transferAmount, accountFrom, accountTo);
        return accountTo;
    }

    @Transactional
    public void transferMoneyToAccountsOfDifferentCurrencies(Double transferAmount, Account accountFrom, Account accountTo) throws InvalidAttributesException {
        if (accountFrom.getCurrencyName().equals(accountTo.getCurrencyName())) {
            accountFrom.setAmountOfMoney(accountFrom.getAmountOfMoney() - transferAmount);
            accountTo.setAmountOfMoney(accountTo.getAmountOfMoney() + transferAmount);
        } else {
            Double resultOfExchange = exchangeRateService.currencyExchangeForDifferentAccounts(transferAmount, accountFrom, accountTo);
            accountFrom.setAmountOfMoney(accountFrom.getAmountOfMoney() - transferAmount);
            accountTo.setAmountOfMoney(accountTo.getAmountOfMoney() + resultOfExchange);
        }

        accountRepo.saveAll(List.of(accountTo, accountFrom));
    }

    public CardDataDTO getCardDataFromClient(Client client, String currencyName) throws AccountNotFound {
        Account account = client.getAccounts().stream()
                .filter(a -> a.getCurrencyName().equals(currencyName))
                .findAny().orElseThrow(AccountNotFound::new);
        MainCard mainCard = account.getCardIds().get(0);
        String beautifulCardNumber = mainCard.getCardNumber().replaceAll("(.{4})", "$1 ");
        beautifulCardNumber = beautifulCardNumber.trim();
        Double amountOfMoney = account.getAmountOfMoney();
        String beautifulAmountOfMoney = amountOfMoney.toString().replaceAll("(\\d+\\.\\d{2})(\\d+)?", "$1");
        amountOfMoney = Double.parseDouble(beautifulAmountOfMoney);
        return new CardDataDTO(beautifulCardNumber,
                mainCard.getValidity(), mainCard.getCvv(),
                amountOfMoney, mainCard.getOwnerName());
    }
}
