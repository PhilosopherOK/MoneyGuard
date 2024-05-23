package ua.bank.moneyguard.scheduledActions;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ua.bank.moneyguard.models.tokens.PassToken;
import ua.bank.moneyguard.services.token.PassTokenService;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Component
public class ForgottenPasswordTokenCleaner {
    private final PassTokenService passTokenService;

    @Scheduled(fixedRate = 3_600_000)
    public void checkValidTokens() {
        List<PassToken> tokenList = passTokenService.findByConfirmedAtIsNull();
        if (!tokenList.isEmpty()) {
            for (int i = 0; i < tokenList.size(); i++) {
                PassToken t = tokenList.get(i);
                if (t.getExpiresAt().isBefore(LocalDateTime.now())) {
                    passTokenService.deleteById(t.getId());
                }
            }
        }
    }
}
