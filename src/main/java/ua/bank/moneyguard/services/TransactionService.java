package ua.bank.moneyguard.services;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.bank.moneyguard.models.Client;
import ua.bank.moneyguard.models.Transaction;
import ua.bank.moneyguard.repositories.ClientRepo;
import ua.bank.moneyguard.repositories.TransactionRepo;

import java.util.List;

@AllArgsConstructor
@Service
public class TransactionService implements ServiceExample<Transaction> {

    private final TransactionRepo transactionRepo;
    private final ClientRepo clientRepo;

    @Transactional(readOnly = true)
    @Override
    public Transaction findById(Long id) {
        return transactionRepo.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Page<Transaction> findAll(int page, int size, Client client) {
        Sort sort = Sort.by(Sort.Direction.DESC, "transactionsId");
        Pageable pageable = PageRequest.of(page, size, sort);

        return transactionRepo.findTransactionByClient(client, pageable);
    }

    @Transactional
    @Override
    public Client save(Transaction transaction) {
        Client client = transaction.getClient();
        clientRepo.save(client);
        transactionRepo.save(transaction);
        return null;
    }

    @Transactional
    @Override
    public void saveAll(List<Transaction> list) {
        transactionRepo.saveAll(list);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        transactionRepo.deleteById(id);
    }

    @Transactional
    @Override
    public void update(Long id, Transaction transaction) {
        transaction.setTransactionsId(id);
        transactionRepo.save(transaction);
    }
}
