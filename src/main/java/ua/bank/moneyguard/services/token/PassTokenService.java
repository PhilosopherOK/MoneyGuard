package ua.bank.moneyguard.services.token;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.bank.moneyguard.exceptions.TokenNotFound;
import ua.bank.moneyguard.models.tokens.PassToken;
import ua.bank.moneyguard.repositories.token.PassTokenRepo;

import java.util.List;


@AllArgsConstructor
@Service
public class PassTokenService {
    private final PassTokenRepo passTokenRepo;

    @Transactional
    public void deleteById(Long id){
        passTokenRepo.deleteById(id);
    }
    @Transactional
    public void save(PassToken passToken){
        passTokenRepo.save(passToken);
    }

    @Transactional(readOnly = true)
    public PassToken findByToken(String token) throws TokenNotFound {
        return passTokenRepo.findByToken(token).orElseThrow(TokenNotFound::new);
    }

    @Transactional
    public void deleteByToken(String token){
        passTokenRepo.deleteByToken(token);
    }

    @Transactional(readOnly = true)
    public List<PassToken> findByConfirmedAtIsNull(){
        return passTokenRepo.findByConfirmedAtIsNull();
    }

}
