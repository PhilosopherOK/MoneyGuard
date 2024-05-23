package ua.bank.moneyguard.services.token;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.bank.moneyguard.models.Client;
import ua.bank.moneyguard.models.tokens.EmailToken;
import ua.bank.moneyguard.repositories.token.EmailTokenRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EmailTokenService {

    private final EmailTokenRepo emailTokenRepo;

    @Transactional
    public void deleteById(Long id){
        emailTokenRepo.deleteById(id);
    }
    @Transactional
    public void saveToken(EmailToken token) {
        emailTokenRepo.save(token);
    }

    @Transactional
    public Optional<EmailToken> getToken(String token) {
        return emailTokenRepo.findByToken(token);
    }

    @Transactional(readOnly = true)
    public EmailToken findTokenByClient(Client client) {
        return emailTokenRepo.findByClient(client);
    }

    @Transactional(readOnly = true)
    public List<EmailToken> findByConfirmedAtIsNull() {
        return emailTokenRepo.findByConfirmedAtIsNull();
    }

    @Transactional
    public void setConfirmedAt(String token) {
        EmailToken emailToken = emailTokenRepo.findByTokenIsLike(token);
        emailToken.setConfirmedAt(LocalDateTime.now());
        emailTokenRepo.save(emailToken);
    }
}