package ua.bank.moneyguard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.bank.moneyguard.models.Client;
import ua.bank.moneyguard.models.Credit;

import java.util.List;
import java.util.Optional;

public interface CreditRepo extends JpaRepository<Credit, Long> {
    List<Credit> findAllByClientLike(Client client);
    Optional<Credit> findByCreditsIdAndClient(Long id, Client client);

}
