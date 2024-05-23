package ua.bank.moneyguard.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ua.bank.moneyguard.repositories.ClientRepo;

@Component
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ClientRepo clientRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        return clientRepo.findClientByEmailLike(email).orElseThrow
                (() -> new UsernameNotFoundException("User with this email not found: " + email));
    }
}
