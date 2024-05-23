package ua.bank.moneyguard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.bank.moneyguard.models.Client;
import ua.bank.moneyguard.models.SavingJar;

import java.util.List;
import java.util.Optional;

public interface SavingsJarRepo extends JpaRepository<SavingJar, Long> {
    List<SavingJar> findAllByClientLike(Client client);
    Optional<SavingJar> findByJarIdAndClient(Long id, Client client);

}
