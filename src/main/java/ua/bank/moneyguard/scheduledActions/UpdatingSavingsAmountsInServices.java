package ua.bank.moneyguard.scheduledActions;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import ua.bank.moneyguard.exceptions.NotSupportedService;
import ua.bank.moneyguard.models.*;
import ua.bank.moneyguard.services.AccountService;
import ua.bank.moneyguard.services.BankServiceService;
import ua.bank.moneyguard.services.ClientService;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
public class UpdatingSavingsAmountsInServices {
    private final BankServiceService bankServiceService;
    private final AccountService accountService;
    private final ClientService clientService;
    private final Double penaltyInterestForLateCredit = 0.64;
    private final int entriesPerTransaction = 10;


    @Scheduled(fixedRate = 86_400_000)  //every 24 hours
    public void updateInterestInCredits() throws NotSupportedService {
        Account adminAccount = clientService.findAdmin().getAccounts().stream().filter(a -> a.getCurrencyName().equals("UAH")).findAny().get();

        for (int i = 0; true; i++) {
            List<Credit> credits = bankServiceService.findAll(new Credit(), PageRequest.of(i, entriesPerTransaction, Sort.Direction.ASC, "creditsId"));
            if (credits.size() == 0) {
                break;
            }
            for (int j = 0; j < credits.size(); j++) {
                if (!credits.get(j).getIsStitched()) {
                    Credit credit = credits.get(j);
                    Double prosentPerDay = credit.getAccumulated() * (credit.getInterestRatePerMonth() / 30);
                    credit.setAccumulated(credit.getAccumulated() + prosentPerDay);
                }
            }
            bankServiceService.saveAll(credits);
        }
    }

    @Scheduled(fixedRate = 86_400_000)
    public void checkingForCreditPaymentOnTime() throws NotSupportedService {
        for (int i = 0; true; i++) {
            List<Credit> credits = bankServiceService.findAll(new Credit(), PageRequest.of(i, entriesPerTransaction, Sort.Direction.ASC, "creditsId"));
            if (credits.size() == 0) {
                break;
            }
            for (int j = 0; j < credits.size(); j++) {
                if (!credits.get(j).getIsStitched()) {
                    Credit credit = credits.get(j);
                    if (credit.getServiceDurationTo().isBefore(LocalDateTime.now())) {
                        credit.setAccumulated(credit.getAccumulated() + (credit.getAmount() * penaltyInterestForLateCredit));
                        credit.setIsStitched(true);
                    }
                }
            }
            bankServiceService.saveAll(credits);
        }
    }

    @Scheduled(fixedRate = 86_400_000)
    public void updatingSavingsInDeposits() throws NotSupportedService {
        Account adminAccount = clientService.findAdmin().getAccounts().stream().filter(a -> a.getCurrencyName().equals("UAH")).findAny().get();

        for (int i = 0; true; i++) {
            List<Deposit> deposits = bankServiceService.findAll(new Deposit(), PageRequest.of(i, entriesPerTransaction, Sort.Direction.ASC, "deposits_id"));
            if (deposits.size() == 0) {
                break;
            }
            for (int j = 0; j < deposits.size(); j++) {
                Deposit deposit = deposits.get(j);
                Double prosentPerDay = deposit.getAccumulated() * (deposit.getInterestRatePerMonth() / 30);
                deposit.setAccumulated(deposit.getAccumulated() + prosentPerDay);
                adminAccount.setAmountOfMoney(adminAccount.getAmountOfMoney() - prosentPerDay);
            }
            bankServiceService.saveAll(deposits);
        }
        accountService.save(adminAccount);

    }

    @Scheduled(fixedRate = 86_400_000)
    public void checkingForDepositDateEnding() throws NotSupportedService {
        Account adminAccount = clientService.findAdmin().getAccounts().stream().filter(a -> a.getCurrencyName().equals("UAH")).findAny().get();

        for (int i = 0; true; i++) {
            List<Deposit> deposits = bankServiceService.findAll(new Deposit(), PageRequest.of(i, entriesPerTransaction, Sort.Direction.ASC, "deposits_id"));
            if (deposits.size() == 0) {
                break;
            }
            for (int j = 0; j < deposits.size(); j++) {
                if (deposits.get(j).getServiceDurationTo().isBefore(LocalDateTime.now())) {
                    Deposit deposit = deposits.get(j);
                    Double sumForDeposit = deposit.getAccumulated() + deposit.getAmount();
                    Client client = deposit.getClient();

                    Account account = client.getAccounts().stream().filter(a -> a.getCurrencyName().equals(deposit.getCurrencyName())).findAny().get();
                    account.setAmountOfMoney(account.getAmountOfMoney() + sumForDeposit);
                    adminAccount.setAmountOfMoney(adminAccount.getAmountOfMoney() - sumForDeposit);
                    accountService.save(account);
                    accountService.save(adminAccount);

                    bankServiceService.deleteById(deposit.getDepositsId(), deposit);

                    client.getDeposits().remove(deposit);
                    clientService.saveAndReturnObj(client);
                }
            }
        }
        accountService.save(adminAccount);
    }


    @Scheduled(fixedRate = 86_400_000)
    public void checkingForSavingJarDateEnding() throws NotSupportedService {
        Account adminAccount = clientService.findAdmin().getAccounts().stream().filter(a -> a.getCurrencyName().equals("UAH")).findAny().get();

        for (int i = 0; true; i++) {
            List<SavingJar> savingJars = bankServiceService.findAll(new SavingJar(), PageRequest.of(i, entriesPerTransaction, Sort.Direction.ASC, "deposits_id"));
            if (savingJars.size() == 0) {
                break;
            }
            for (int j = 0; j < savingJars.size(); j++) {
                if (savingJars.get(j).getServiceDurationTo().isBefore(LocalDateTime.now())) {
                    SavingJar savingJar = savingJars.get(j);
                    Double sumForJar = savingJar.getAccumulated();
                    Client client = savingJar.getClient();

                    Account account = client.getAccounts().stream().filter(a -> a.getCurrencyName().equals(savingJar.getCurrencyName())).findAny().get();
                    account.setAmountOfMoney(account.getAmountOfMoney() + sumForJar);
                    adminAccount.setAmountOfMoney(adminAccount.getAmountOfMoney() - sumForJar);
                    accountService.save(account);

                    bankServiceService.deleteById(savingJar.getJarId(), savingJar);

                    client.getDeposits().remove(savingJar);
                    clientService.saveAndReturnObj(client);
                }
            }

        }
        accountService.save(adminAccount);
    }

}









