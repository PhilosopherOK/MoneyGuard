package ua.bank.moneyguard.scheduledActions;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ua.bank.moneyguard.models.tokens.EmailToken;
import ua.bank.moneyguard.services.ClientService;
import ua.bank.moneyguard.services.token.EmailTokenService;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Component
public class ForgottenEmailTokenCleaner {
    private final EmailTokenService emailTokenService;
    private final ClientService clientService;

    @Scheduled(fixedRate = 3_600_000)
    public void checkValidTokens() {
        List<EmailToken> tokenList = emailTokenService.findByConfirmedAtIsNull();
        if (!tokenList.isEmpty()) {
            for (int i = 0; i < tokenList.size(); i++) {
                EmailToken t = tokenList.get(i);
                if (t.getExpiresAt().isBefore(LocalDateTime.now())) {
                    Long clientId = t.getClient().getClientId();
                    t.setClient(null);

                    emailTokenService.deleteById(t.getId());
                    clientService.deleteById(clientId);
                }
            }
        }
    }
}
