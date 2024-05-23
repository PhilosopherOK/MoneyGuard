package ua.bank.moneyguard.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.bank.moneyguard.models.Client;
import ua.bank.moneyguard.models.Transaction;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    public Page<Transaction> findTransactionByClient(Client client, Pageable pageable);
}
