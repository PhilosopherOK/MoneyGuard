package ua.bank.moneyguard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.bank.moneyguard.models.ExchangeRate;


public interface ExchangeRateRepo extends JpaRepository<ExchangeRate, Long> {
    ExchangeRate findExchangeRateByShortNameLike(String shortName);
}
