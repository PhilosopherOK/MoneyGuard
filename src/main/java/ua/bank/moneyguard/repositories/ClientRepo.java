package ua.bank.moneyguard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.bank.moneyguard.models.Client;
import ua.bank.moneyguard.utils.role.UserRole;

import java.util.Optional;

public interface ClientRepo extends JpaRepository<Client, Long> {
    public Optional<Client> findClientByEmailLike(String email);
    public Optional<Client> findClientByIBANLike(String IBAN);
    public Optional<Client> findClientByUserRoleLike(UserRole userRole);
}
