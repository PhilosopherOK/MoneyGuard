package ua.bank.moneyguard.repositories.token;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.bank.moneyguard.models.Client;
import ua.bank.moneyguard.models.tokens.EmailToken;

import java.util.List;
import java.util.Optional;

public interface EmailTokenRepo extends JpaRepository<EmailToken, Long> {
    EmailToken findByClient(Client client);
    Optional<EmailToken> findByToken(String token);
    List<EmailToken> findByConfirmedAtIsNull();
    void deleteByToken(String token);
    EmailToken findByTokenIsLike(String token);
}
