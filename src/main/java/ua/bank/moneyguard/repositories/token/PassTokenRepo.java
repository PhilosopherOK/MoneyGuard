package ua.bank.moneyguard.repositories.token;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.bank.moneyguard.models.tokens.PassToken;

import java.util.List;
import java.util.Optional;

public interface PassTokenRepo extends JpaRepository<PassToken, Long> {
    Optional<PassToken> findByToken(String token);

    void deleteByToken(String token);

    List<PassToken> findByConfirmedAtIsNull();
}
