package ua.bank.moneyguard.beforeRun;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.bank.moneyguard.models.*;
import ua.bank.moneyguard.services.*;
import ua.bank.moneyguard.utils.IBANGenerated;
import ua.bank.moneyguard.utils.enums.*;
import ua.bank.moneyguard.utils.role.UserRole;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AppConfig extends GlobalMethodSecurityConfiguration {

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.pass}")
    private String adminPass;

    @Bean
    public CommandLineRunner demo(final ClientService clientService,
                                  final AccountService accountService,
                                  final MainCardService mainCardService,
                                  final BankServiceService bankServiceService,
                                  final TransactionService transactionService,
                                  final PasswordEncoder encoder) {
        return args -> {
            if (clientService.findAdmin() == null) {
                Client client = new Client("ADMIN", "VSEIA Bankov", LocalDate.now().minusYears(20),
                        "380966839230", adminEmail, encoder.encode(adminPass));
                client.setUserRole(UserRole.ADMIN);
                client.setTitul(Tituls.IMMORTAL);
                client.setEnabled(true);
                Client clientWithId = clientService.saveAndReturnObj(client);
                clientWithId.setIBAN(IBANGenerated.generator(clientWithId.getClientId().toString()));
                clientService.save(clientWithId);

                Account account = accountService.createAccountWithCurrencyName("UAH", clientWithId);
                account.setAmountOfMoney(10000000.0);
                mainCardService.createMainCard(account);
                Account account2 = accountService.createAccountWithCurrencyName("USD", clientWithId);
                account2.setAmountOfMoney(10000.0);
                mainCardService.createMainCard(account2);

                bankServiceService.getService(client, account, new Credit(
                        CreditPropositions.DIVINE_CREDIT.getServiceName(), "UAH", 3000.0, +
                        CreditPropositions.DIVINE_CREDIT.getInterestRatePerMonth(), LocalDateTime.now().plusYears(1), client));
                bankServiceService.getService(client, account, new Deposit(
                        DepositPropositions.DIVINE_TREASURE.getServiceName(), "UAH", 3000.0, +
                        DepositPropositions.DIVINE_TREASURE.getInterestRatePerMonth(), LocalDateTime.now().plusYears(1), client));
                bankServiceService.getService(client, account, new SavingJar(
                        SavingJarPropositions.JAR_PROPOSITION_ON_6_MONTH.getServiceName(), "UAH", 3000.0, +
                        SavingJarPropositions.JAR_PROPOSITION_ON_6_MONTH.getInterestRatePerMonth(), LocalDateTime.now().plusMonths(6), client));

                transactionService.save(new Transaction(LocalDateTime.now(),
                        TransactionName.GET_SERVICE.name(),
                        "Money Guard",
                        client.getFirstName(),
                        3000.0, client));
                transactionService.save(new Transaction(LocalDateTime.now(),
                        TransactionName.GET_SERVICE.name(),
                        client.getFirstName(),
                        "Money Guard",
                        3000.0, client));
                transactionService.save(new Transaction(LocalDateTime.now(),
                        TransactionName.GET_SERVICE.name(),
                        client.getFirstName(),
                        "Money Guard",
                        3000.0, client));


                for (int i = 0; i < 13; i++) {
                    Client client2 = new Client("someClientName" + i, "someClientSecondName" + i,
                            LocalDate.now().minusYears(20),
                            "380966839230", "mail"+ i+"@gmail.com", encoder.encode("123"));
                    client2.setUserRole(UserRole.USER);
                    client2.setTitul(Tituls.GUARDIAN);
                    client2.setEnabled(true);
                    Client clientWithId2 = clientService.saveAndReturnObj(client2);
                    clientWithId2.setIBAN(IBANGenerated.generator(clientWithId2.getClientId().toString()));
                    clientService.save(clientWithId2);

                    Account account3 = accountService.createAccountWithCurrencyName("UAH", clientWithId2);
                    account3.setAmountOfMoney(0.0);
                    mainCardService.createMainCard(account3);

                }
            }
        };
    }


}
