package ua.bank.moneyguard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.bank.moneyguard.models.MainCard;

import java.util.Optional;

public interface MainCardRepo extends JpaRepository<MainCard, Long> {
    public Optional<MainCard> findMainCardByCardNumberLike(String cardNumber);
}
