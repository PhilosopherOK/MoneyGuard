package ua.bank.moneyguard.services;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.bank.moneyguard.exceptions.AccountAlreadyExist;
import ua.bank.moneyguard.models.Account;
import ua.bank.moneyguard.models.Client;
import ua.bank.moneyguard.repositories.AccountRepo;
import ua.bank.moneyguard.repositories.ClientRepo;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class AccountService implements ServiceExample<Account> {
    private final AccountRepo accountRepo;
    private final ClientRepo clientRepo;

    @Transactional(readOnly = true)
    @Override
    public Account findById(Long id) {
        return accountRepo.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Page<Account> findAccountByClient(int page, int size, Client client) {
        Sort sort = Sort.by(Sort.Direction.ASC, "accountId");
        Pageable pageable = PageRequest.of(page, size, sort);
        return accountRepo.findAccountByClient(client, pageable);
    }

    @Transactional
    @Override
    public Client save(Account account) {
        accountRepo.save(account);
        return null;
    }

    @Transactional
    @Override
    public void saveAll(List<Account> list) {
        accountRepo.saveAll(list);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        accountRepo.deleteById(id);
    }

    @Transactional
    @Override
    public void update(Long id, Account account) {
        account.setAccountId(id);
        accountRepo.save(account);
    }

    @Transactional
    public Account createAccountWithCurrencyName(String currencyName, Client client) throws AccountAlreadyExist {
        List<Account> accounts = accountRepo.findAccountByClient(client);
        if (accounts.stream().filter(a -> a.getCurrencyName().equals(currencyName)).findAny().orElse(null) != null) {
            throw new AccountAlreadyExist();
        }
        Account account = new Account(currencyName, client);
        Account saved = accountRepo.save(account);

        client.getAccounts().add(saved);
        clientRepo.save(client);
        return account;
    }

    @Transactional(readOnly = true)
    public Set<String> getMineCurrency(Client client) {
        List<Account> accountByClient = accountRepo.findAccountByClient(client);
        Set<String> stringSet = accountByClient.stream().map(a -> a.getCurrencyName()).collect(Collectors.toSet());
        return stringSet;
    }
}
