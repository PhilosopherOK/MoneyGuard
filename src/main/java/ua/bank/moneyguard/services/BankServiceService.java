package ua.bank.moneyguard.services;

import lombok.Data;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.bank.moneyguard.exceptions.NotSupportedService;
import ua.bank.moneyguard.models.*;
import ua.bank.moneyguard.repositories.*;
import ua.bank.moneyguard.utils.enums.Tituls;
import ua.bank.moneyguard.utils.role.UserRole;

import java.util.Arrays;
import java.util.List;

@Service
@Data
public class BankServiceService {
    private final DepositRepo depositRepo;
    private final CreditRepo creditRepo;
    private final SavingsJarRepo savingsJarRepo;
    private final AccountRepo accountRepo;
    private final ClientRepo clientRepo;

    @Transactional
    public Client findAdmin() {
        return clientRepo.findClientByUserRoleLike(UserRole.ADMIN).get();
    }

    @Transactional(readOnly = true)
    public BankServiceModelExample findById(Long id, BankServiceModelExample emptyBankServiceModel) throws NotSupportedService {
        if (emptyBankServiceModel.getClass() == Deposit.class) {
            return depositRepo.findById(id).orElseThrow(NotSupportedService::new);
        } else if (emptyBankServiceModel.getClass() == Credit.class) {
            return creditRepo.findById(id).orElseThrow(NotSupportedService::new);
        } else if (emptyBankServiceModel.getClass() == SavingJar.class) {
            return savingsJarRepo.findById(id).orElseThrow(NotSupportedService::new);
        } else {
            throw new NotSupportedService();
        }
    }

    @Transactional(readOnly = true)
    public BankServiceModelExample findByIdAndClient(Long id, BankServiceModelExample emptyBankServiceModel, Client client) throws NotSupportedService {
        if (emptyBankServiceModel.getClass() == Deposit.class) {
            return depositRepo.findByDepositsIdAndClient(id, client).orElseThrow(NotSupportedService::new);
        } else if (emptyBankServiceModel.getClass() == Credit.class) {
            return creditRepo.findByCreditsIdAndClient(id, client).orElseThrow(NotSupportedService::new);
        } else if (emptyBankServiceModel.getClass() == SavingJar.class) {
            return savingsJarRepo.findByJarIdAndClient(id, client).orElseThrow(NotSupportedService::new);
        } else {
            throw new NotSupportedService();
        }
    }


    @Transactional
    public <T extends BankServiceModelExample> void saveAll(List<T> list) throws NotSupportedService {
        BankServiceModelExample emptyBankServiceModel = list.get(0);
        if (emptyBankServiceModel.getClass() == Deposit.class) {
            depositRepo.saveAll((List<Deposit>) list);
        } else if (emptyBankServiceModel.getClass() == Credit.class) {
            creditRepo.saveAll((List<Credit>) list);
        } else if (emptyBankServiceModel.getClass() == SavingJar.class) {
            savingsJarRepo.saveAll((List<SavingJar>) list);
        } else {
            throw new NotSupportedService();
        }

    }

    @Transactional
    public <T extends BankServiceModelExample> void deleteById(Long id, T emptyBankServiceModel) throws NotSupportedService {
        if (emptyBankServiceModel.getClass() == Deposit.class) {
            depositRepo.deleteById(id);
        } else if (emptyBankServiceModel.getClass() == Credit.class) {
            creditRepo.deleteById(id);
        } else if (emptyBankServiceModel.getClass() == SavingJar.class) {
            savingsJarRepo.deleteById(id);
        } else {
            throw new NotSupportedService();
        }
    }


    @Transactional
    public <T extends BankServiceModelExample> void getService(Client client, Account account, T bankServiceModelExample) throws NotSupportedService {
        Account adminAccount = findAdmin().getAccounts().stream().filter(a -> a.getCurrencyName().equals("UAH")).findAny().get();
        if (bankServiceModelExample.getClass() == Deposit.class) {
            account.setAmountOfMoney(account.getAmountOfMoney() - bankServiceModelExample.getAmount());
            adminAccount.setAmountOfMoney(adminAccount.getAmountOfMoney() + bankServiceModelExample.getAmount());

            bankServiceModelExample.setAccumulated(0.0);
            client.getDeposits().add((Deposit) bankServiceModelExample);

            depositRepo.save((Deposit) bankServiceModelExample);
            clientRepo.save(client);
        } else if (bankServiceModelExample.getClass() == Credit.class) {
            account.setAmountOfMoney(account.getAmountOfMoney() + bankServiceModelExample.getAmount());
            adminAccount.setAmountOfMoney(adminAccount.getAmountOfMoney() - bankServiceModelExample.getAmount());

            bankServiceModelExample.setAccumulated(bankServiceModelExample.getAmount());
            client.getCredits().add((Credit) bankServiceModelExample);

            creditRepo.save((Credit) bankServiceModelExample);
            clientRepo.save(client);
        } else if (bankServiceModelExample.getClass() == SavingJar.class) {
            bankServiceModelExample.setAccumulated(0.0);
            client.getSavingJars().add((SavingJar) bankServiceModelExample);

            savingsJarRepo.save((SavingJar) bankServiceModelExample);
            clientRepo.save(client);
        } else {
            throw new NotSupportedService();
        }
        accountRepo.save(adminAccount);
    }

    @Transactional(readOnly = true)
    public <T extends BankServiceModelExample> List<T> findAll(T emptyBankServiceModel, Pageable pageable) throws NotSupportedService {
        if (emptyBankServiceModel.getClass() == Deposit.class) {
            return (List<T>) depositRepo.findAll(pageable);
        } else if (emptyBankServiceModel.getClass() == Credit.class) {
            return (List<T>) creditRepo.findAll();
        } else if (emptyBankServiceModel.getClass() == SavingJar.class) {
            return (List<T>) savingsJarRepo.findAll();
        } else {
            throw new NotSupportedService();
        }
    }

    @Transactional(readOnly = true)
    public <T extends BankServiceModelExample> List<T> findAll(Client client, T emptyBankServiceModel) throws NotSupportedService {
        if (emptyBankServiceModel.getClass() == Deposit.class) {
            return (List<T>) depositRepo.findAllByClientLike(client);
        } else if (emptyBankServiceModel.getClass() == Credit.class) {
            return (List<T>) creditRepo.findAllByClientLike(client);
        } else if (emptyBankServiceModel.getClass() == SavingJar.class) {
            return (List<T>) savingsJarRepo.findAllByClientLike(client);
        } else {
            throw new NotSupportedService();
        }
    }

    @Transactional
    public <T extends BankServiceModelExample> void pay(T bankServiceModelExample, Account account, Double amount) throws NotSupportedService {
        Account adminAccount = findAdmin().getAccounts().stream().filter(a -> a.getCurrencyName().equals("UAH")).findAny().get();
        if (bankServiceModelExample.getClass() == Deposit.class) {
            account.setAmountOfMoney(account.getAmountOfMoney() - amount);
            adminAccount.setAmountOfMoney(adminAccount.getAmountOfMoney() + amount);
            bankServiceModelExample.setAmount(bankServiceModelExample.getAmount() + amount);
            accountRepo.save(account);
            depositRepo.save((Deposit) bankServiceModelExample);
        } else if (bankServiceModelExample.getClass() == Credit.class) {
            if (bankServiceModelExample.getAccumulated() <= amount) {
                amount = bankServiceModelExample.getAccumulated();
            }
            account.setAmountOfMoney(account.getAmountOfMoney() - amount);
            adminAccount.setAmountOfMoney(adminAccount.getAmountOfMoney() + amount);
            bankServiceModelExample.setAccumulated(bankServiceModelExample.getAccumulated() - amount);
            accountRepo.save(account);
            creditRepo.save((Credit) bankServiceModelExample);
        } else if (bankServiceModelExample.getClass() == SavingJar.class) {
            account.setAmountOfMoney(account.getAmountOfMoney() - amount);
            bankServiceModelExample.setAccumulated(bankServiceModelExample.getAccumulated() + amount);
            adminAccount.setAmountOfMoney(adminAccount.getAmountOfMoney() + amount);
            accountRepo.save(account);
            savingsJarRepo.save((SavingJar) bankServiceModelExample);
        } else {
            throw new NotSupportedService();
        }
        accountRepo.save(adminAccount);
    }

    @Transactional
    public Double withdrawService(Client client, BankServiceModelExample emptyBankServiceModel, Long id) throws NotSupportedService {
        Account adminAccount = findAdmin().getAccounts().stream().filter(a -> a.getCurrencyName().equals("UAH")).findAny().get();
        BankServiceModelExample bankServiceModelExample = findByIdAndClient(id, emptyBankServiceModel, client);
        Account account = client.getAccounts().stream().filter(a -> a.getCurrencyName()
                .equals(bankServiceModelExample.getCurrencyName())).findAny().get();
        Double savingsForService;
        if (emptyBankServiceModel.getClass() == Deposit.class) {
            savingsForService = bankServiceModelExample.getAmount() + bankServiceModelExample.getAccumulated();
            account.setAmountOfMoney(account.getAmountOfMoney() + savingsForService);
            adminAccount.setAmountOfMoney(adminAccount.getAmountOfMoney() - savingsForService);
        } else if (emptyBankServiceModel.getClass() == SavingJar.class) {
            savingsForService = bankServiceModelExample.getAccumulated();
            account.setAmountOfMoney(account.getAmountOfMoney() + savingsForService);
            adminAccount.setAmountOfMoney(adminAccount.getAmountOfMoney() - savingsForService);
        } else {
            throw new NotSupportedService();
        }
        accountRepo.save(account);
        deleteById(id, emptyBankServiceModel);
        accountRepo.save(adminAccount);
        return savingsForService;
    }

    @Transactional
    public String  checkToCreditFinished(Credit credit) {
        if (credit.getAccumulated() <= 0) {
            Client client = credit.getClient();
                client.getCredits().remove(credit);
            creditRepo.deleteById(credit.getCreditsId());
            String promotion = "Your title remains the same, to increase it you will need to go above 70% of your credit limit.";

            if (credit.getAmount() > client.getTitul().getLimitForService() * 0.7){
                client.setTitul(Arrays.stream(Tituls.values())
                        .filter(t -> t.getLimitForService() > client.getTitul().getLimitForService()).findFirst().orElse(Tituls.IMMORTAL));
                clientRepo.save(client);
                promotion = "You have been given a new title:"+ client.getTitul().getName() + "\n"
                        + "And your credit limit is now:"+ client.getTitul().getLimitForService();
            }

            return "Credit defeated !!! " + "\n" + promotion;
        }
        return "The blow on credit was struck";
    }

}
