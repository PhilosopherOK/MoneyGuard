package ua.bank.moneyguard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.bank.moneyguard.models.Client;
import ua.bank.moneyguard.models.Deposit;

import java.util.List;
import java.util.Optional;

public interface DepositRepo extends JpaRepository<Deposit, Long> {
    List<Deposit> findAllByClientLike(Client client);

    Optional<Deposit> findByDepositsIdAndClient(Long id, Client client);
}
