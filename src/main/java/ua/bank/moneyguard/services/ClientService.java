package ua.bank.moneyguard.services;

import lombok.AllArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.bank.moneyguard.dtos.authorizationDTOs.RegistrationDTO;
import ua.bank.moneyguard.exceptions.IncorrectSpecialField;
import ua.bank.moneyguard.models.Client;
import ua.bank.moneyguard.repositories.ClientRepo;
import ua.bank.moneyguard.utils.role.UserRole;
import ua.bank.moneyguard.validators.DTOsValidator;
import ua.bank.moneyguard.validators.PasswordsValidator;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ClientService implements ServiceExample<Client> {
    private final ClientRepo clientRepo;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public Client findAdmin(){
        return clientRepo.findClientByUserRoleLike(UserRole.ADMIN).orElse(null);
    }

    @Transactional
    public Client findNowClient() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Client client = (Client) authentication.getPrincipal();
        client = findById(client.getClientId());
        return client;
    }


    @Transactional
    public Client saveAndReturnObj(Client client) {
        return clientRepo.save(client);
    }

    @Override
    @Transactional
    public void saveAll(List<Client> list) {
        clientRepo.saveAll(list);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        clientRepo.deleteById(id);
    }

    @Transactional
    @Override
    public void update(Long id, Client client) {
        client.setClientId(id);
        clientRepo.save(client);
    }

    @Transactional
    public Client findById(Long id) {
        return clientRepo.findById(id).orElse(null);
    }

    @Transactional
    @Override
    public Client save(Client client) {
        clientRepo.save(client);
        return client;
    }
    @Transactional(readOnly = true)
    public Page<Client> findAll(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "clientId");
        Pageable pageable = PageRequest.of(page, size, sort);
        return clientRepo.findAll(pageable);
    }
    @Transactional(readOnly = true)
    public Optional<Client> findByEmail(String email)  {
        return clientRepo.findClientByEmailLike(email);
    }

    @Transactional
    public void setEnableByClientEmail(String email) {
        Client client = clientRepo.findClientByEmailLike(email).orElse(null);
        client.setEnabled(true);
        clientRepo.save(client);
    }

    @Transactional
    public void setEnableByClientId(Long id) throws ChangeSetPersister.NotFoundException {
        Client client = clientRepo.findById(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        client.setEnabled(true);
        clientRepo.save(client);
    }
    @Transactional
    public void setUnEnableByClientId(Long id) throws ChangeSetPersister.NotFoundException {
        Client client = clientRepo.findById(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        client.setEnabled(false);
        clientRepo.save(client);
    }

    @Transactional
    public void updateClientFromRegistrationDTO(Long id, RegistrationDTO registrationDTO) throws Exception {
        DTOsValidator.checkToRegistrationDTOIsValidWithOutPass(registrationDTO);
        Client client = findById(id);
        if(findByEmail(registrationDTO.getEmail()).orElse(null) != null && !client.getEmail().equals(registrationDTO.getEmail())){
            throw new IncorrectSpecialField("this email is already exist.");
        }

        Client oldClient = findById(id);
        if(registrationDTO.getPassword() == null || registrationDTO.getPassword().isBlank() ){
            registrationDTO.setPassword(oldClient.getPassword());
        }else {
            PasswordsValidator.validate(registrationDTO.getPassword());
            oldClient.setPassword(bCryptPasswordEncoder.encode(registrationDTO.getPassword()));
        }

        oldClient.setFirstName(registrationDTO.getFirstName());
        oldClient.setSecondName(registrationDTO.getSecondName());
        oldClient.setDateOfBirth(registrationDTO.getDateOfBirth());
        oldClient.setPhoneNumber(registrationDTO.getPhoneNumber());
        oldClient.setEmail(registrationDTO.getEmail());
        update(id, oldClient);
    }

}
