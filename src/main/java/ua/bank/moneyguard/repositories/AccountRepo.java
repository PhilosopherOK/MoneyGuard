package ua.bank.moneyguard.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.bank.moneyguard.models.Account;
import ua.bank.moneyguard.models.Client;

import java.util.List;
import java.util.Optional;


public interface AccountRepo extends JpaRepository<Account, Long> {
    public Optional<Account> findAccountByCurrencyNameLike(String currencyName);
    public List<Account> findAccountByClient(Client client);
    public Page<Account> findAccountByClient(Client client, Pageable pageable);


}
