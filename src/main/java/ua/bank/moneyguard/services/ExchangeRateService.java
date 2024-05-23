package ua.bank.moneyguard.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.bank.moneyguard.exceptions.AccountNotFound;
import ua.bank.moneyguard.exceptions.NotEnoughMoney;
import ua.bank.moneyguard.models.Account;
import ua.bank.moneyguard.models.Client;
import ua.bank.moneyguard.models.ExchangeRate;
import ua.bank.moneyguard.repositories.ClientRepo;
import ua.bank.moneyguard.repositories.ExchangeRateRepo;
import ua.bank.moneyguard.utils.role.UserRole;
import ua.bank.moneyguard.validators.TransferValidator;

import javax.naming.directory.InvalidAttributesException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ExchangeRateService implements ServiceExample<ExchangeRate> {
    private final ExchangeRateRepo exchangeRateRepo;
    private final ClientRepo clientRepo;
    @Transactional(readOnly = true)
    @Override
    public ExchangeRate findById(Long id) {
        return exchangeRateRepo.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<ExchangeRate> findAll() {
        return exchangeRateRepo.findAll();
    }

    @Transactional
    @Override
    public Client save(ExchangeRate exchangeRateDTOFromAPI) {
        exchangeRateRepo.save(exchangeRateDTOFromAPI);
        return null;
    }

    @Transactional
    @Override
    public void saveAll(List<ExchangeRate> list) {
        exchangeRateRepo.saveAll(list);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        exchangeRateRepo.deleteById(id);
    }

    @Transactional
    @Override
    public void update(Long id, ExchangeRate exchangeRate) {
        exchangeRate.setRateId(id);
        exchangeRateRepo.save(exchangeRate);
    }

    @Transactional
    public void updateAll(List<ExchangeRate> newRates) {
        List<ExchangeRate> oldRates = findAll();

        for (int i = 0; i < oldRates.size(); i++) {
            newRates.get(i).setRateId(oldRates.get(i).getRateId());
        }
        saveAll(newRates);
    }

    @Transactional(readOnly = true)
    public Set<String> getAbbreviatedNameOfCurrency() {
        return findAll().stream().map(o -> o.getShortName()).collect(Collectors.toSet());
    }


    @Transactional(readOnly = true)
    public List<ExchangeRate> getThreeMainCurrency() {
        List<ExchangeRate> rateList = new ArrayList<>();
        rateList.add(exchangeRateRepo.findExchangeRateByShortNameLike("USD"));
        rateList.add(exchangeRateRepo.findExchangeRateByShortNameLike("EUR"));
        rateList.add(exchangeRateRepo.findExchangeRateByShortNameLike("GBP"));
        return rateList;
    }

    @Transactional
    public ExchangeRate findByShortName(String shortName) {
        return exchangeRateRepo.findExchangeRateByShortNameLike(shortName);
    }

    @Transactional
    public void currencyExchangeOnOneAccount(Client client, String currencyNameFrom, String currencyNameTo, Double transferAmount) throws AccountNotFound, InvalidAttributesException, NotEnoughMoney {
        Account accountFrom = client.getAccounts().stream().filter(a -> a.getCurrencyName().equals(currencyNameFrom))
                .findAny().orElseThrow(AccountNotFound::new);
        Account accountTo = client.getAccounts().stream().filter(a -> a.getCurrencyName().equals(currencyNameTo))
                .findAny().orElseThrow(AccountNotFound::new);

        TransferValidator.checkToValidTransferData(client, currencyNameFrom, transferAmount);
        Double resultOfExchange = currencyExchangeForDifferentAccounts(transferAmount, accountFrom, accountTo);
        accountFrom.setAmountOfMoney(accountFrom.getAmountOfMoney() - transferAmount);
        accountTo.setAmountOfMoney(accountTo.getAmountOfMoney() + resultOfExchange);
    }


    @Transactional(readOnly = true)
    public Client findAdmin(){
        return clientRepo.findClientByUserRoleLike(UserRole.ADMIN).orElse(null);
    }

    @Transactional(readOnly = true)
    public Double currencyExchangeForDifferentAccounts(Double transferAmount, Account accountFrom, Account accountTo) throws InvalidAttributesException {
        if (accountFrom.getCurrencyName().equals(accountTo.getCurrencyName())) {
            throw new InvalidAttributesException("Bad operation.");
        }
        Double bankEarnings = 0.0;
        ExchangeRate rateOnFromAcc = findByShortName(accountFrom.getCurrencyName());
        ExchangeRate rateOnTransAcc = findByShortName(accountTo.getCurrencyName());
        Account adminAccount = findAdmin().getAccounts().stream().filter(a -> a.getCurrencyName().equals("UAH")).findAny().get();
        if (accountFrom.getCurrencyName().equals("UAH")) {
            Double resultOfExchange = transferAmount / rateOnTransAcc.getSellRate();
            bankEarnings += (transferAmount / rateOnTransAcc.getBuyRate()) - resultOfExchange;
            adminAccount.setAmountOfMoney(adminAccount.getAmountOfMoney() + bankEarnings);
            return resultOfExchange;
        } else if (accountTo.getCurrencyName().equals("UAH")) {
            Double resultOfExchange = transferAmount * rateOnFromAcc.getBuyRate();
            bankEarnings += transferAmount * rateOnFromAcc.getSellRate() - resultOfExchange;
            adminAccount.setAmountOfMoney(adminAccount.getAmountOfMoney() + bankEarnings);
            return resultOfExchange;
        } else {
            Double firstConvertationToUAH = transferAmount * rateOnFromAcc.getBuyRate();
            bankEarnings += transferAmount * rateOnFromAcc.getSellRate() - firstConvertationToUAH;
            Double secondConvertationToNeededCur = firstConvertationToUAH / rateOnTransAcc.getSellRate();
            bankEarnings += (firstConvertationToUAH / rateOnTransAcc.getBuyRate() - secondConvertationToNeededCur) * rateOnTransAcc.getSellRate();
//accumulate on admin acc
            adminAccount.setAmountOfMoney(adminAccount.getAmountOfMoney() + bankEarnings);
            return secondConvertationToNeededCur;
        }
    }
}










