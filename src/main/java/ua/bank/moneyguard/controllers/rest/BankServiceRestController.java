package ua.bank.moneyguard.controllers.rest;

import lombok.Data;
import org.springframework.web.bind.annotation.*;
import ua.bank.moneyguard.dtos.ServicesDTOs.BankServiceFormDTO;
import ua.bank.moneyguard.dtos.ServicesDTOs.BankServicePayDTO;
import ua.bank.moneyguard.dtos.ServicesDTOs.BankServiceShowDTO;
import ua.bank.moneyguard.exceptions.FieldIsEmpty;
import ua.bank.moneyguard.mappers.Mapper;
import ua.bank.moneyguard.models.*;
import ua.bank.moneyguard.services.ClientService;
import ua.bank.moneyguard.services.BankServiceService;
import ua.bank.moneyguard.services.TransactionService;
import ua.bank.moneyguard.utils.ApiResponse;
import ua.bank.moneyguard.utils.enums.CreditPropositions;
import ua.bank.moneyguard.utils.enums.DepositPropositions;
import ua.bank.moneyguard.utils.enums.SavingJarPropositions;
import ua.bank.moneyguard.utils.enums.TransactionName;
import ua.bank.moneyguard.validators.BankServiceValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Data
@RequestMapping("/service")
public class BankServiceRestController {
    private final TransactionService transactionService;
    private final BankServiceService bankServiceService;
    private final ClientService clientService;

    public Client findNowClient() {
        return clientService.findNowClient();
    }

    @GetMapping("/getVariant/{service}")
    public ApiResponse getVariantOfService(@PathVariable("service") String serviceName) {
        if (serviceName.equals("deposit")) {
            return new ApiResponse(true, "", List.of(
                    List.of(DepositPropositions.MODEST_TREASURE.getServiceName(),
                            DepositPropositions.MODEST_TREASURE.getInterestRatePerMonth(),
                            DepositPropositions.MODEST_TREASURE.getNumberOfMonths()),
                    List.of(DepositPropositions.LUXURIOUS_TREASURE.getServiceName(),
                            DepositPropositions.LUXURIOUS_TREASURE.getInterestRatePerMonth(),
                            DepositPropositions.LUXURIOUS_TREASURE.getNumberOfMonths()),
                    List.of(DepositPropositions.DIVINE_TREASURE.getServiceName(),
                            DepositPropositions.DIVINE_TREASURE.getInterestRatePerMonth(),
                            DepositPropositions.DIVINE_TREASURE.getNumberOfMonths())));
        } else if (serviceName.equals("credit")) {
            return new ApiResponse(true, "", List.of(
                    List.of(CreditPropositions.MINI_CREDIT.getServiceName(),
                            CreditPropositions.MINI_CREDIT.getInterestRatePerMonth(),
                            CreditPropositions.MINI_CREDIT.getNumberOfMonths()),
                    List.of(CreditPropositions.NOBLE_CREDIT.getServiceName(),
                            CreditPropositions.NOBLE_CREDIT.getInterestRatePerMonth(),
                            CreditPropositions.NOBLE_CREDIT.getNumberOfMonths()),
                    List.of(CreditPropositions.DIVINE_CREDIT.getServiceName(),
                            CreditPropositions.DIVINE_CREDIT.getInterestRatePerMonth(),
                            CreditPropositions.DIVINE_CREDIT.getNumberOfMonths())));
        } else if (serviceName.equals("savingjar")) {
            return new ApiResponse(true, "", List.of(
                    List.of(SavingJarPropositions.JAR_PROPOSITION_ON_3_MONTH.getServiceName(),
                            SavingJarPropositions.JAR_PROPOSITION_ON_3_MONTH.getInterestRatePerMonth(),
                            SavingJarPropositions.JAR_PROPOSITION_ON_3_MONTH.getNumberOfMonths()),
                    List.of(SavingJarPropositions.JAR_PROPOSITION_ON_6_MONTH.getServiceName(),
                            SavingJarPropositions.JAR_PROPOSITION_ON_6_MONTH.getInterestRatePerMonth(),
                            SavingJarPropositions.JAR_PROPOSITION_ON_6_MONTH.getNumberOfMonths())));
        } else return new ApiResponse(false, "Non-existent service");
    }

    @PostMapping("/getService/{service}")
    public ApiResponse getService(@PathVariable("service") String serviceName,
                                  @RequestBody BankServiceFormDTO bankServiceFormDTO) throws FieldIsEmpty {
        try {
            BankServiceValidator.checkToValidFormDTO(bankServiceFormDTO);
            Client client = findNowClient();
            BankServiceModelExample bankServiceModelExample = BankServiceValidator.checkAndReturnWhatServiceDoYouHaveWithMapping(bankServiceFormDTO, client, serviceName);
            Account account = BankServiceValidator.checkToValidBankServiceGetService(bankServiceModelExample);
            bankServiceService.getService(client, account, bankServiceModelExample);

            Transaction transaction = new Transaction(LocalDateTime.now(),
                    TransactionName.GET_SERVICE.name(),
                    bankServiceModelExample.getClass() == Credit.class ? "Money Guard" : client.getFirstName(),
                    bankServiceModelExample.getClass() == Credit.class ? client.getFirstName() : "Money Guard",
                    bankServiceFormDTO.getAmount(), client);
            transactionService.save(transaction);
            return new ApiResponse(true, "You used an additional service");
        } catch (Exception e) {
            return new ApiResponse(false, e.getMessage());
        }
    }


    @GetMapping("/showActiveService/{service}")
    public ApiResponse showActiveCredits(@PathVariable("service") String serviceName) {
        Client client = findNowClient();
        try {
            BankServiceModelExample emptyBankServiceModel = BankServiceValidator.checkAndReturnWhatServiceDoYouHave(serviceName);

            List<BankServiceModelExample> bankServiceModelExample = bankServiceService.findAll(client, emptyBankServiceModel);
            if (bankServiceModelExample.size() == 0) {
                throw new Exception("You don't have an active services");
            }
            List<BankServiceShowDTO> bankServiceShowDTOS = bankServiceModelExample.stream()
                    .map(c -> Mapper.convertBankServiceToBankServiceShowDTO(c)).collect(Collectors.toList());
            return new ApiResponse(true, "", bankServiceShowDTOS);
        } catch (Exception e) {
            return new ApiResponse(false, e.getMessage());
        }
    }


    @PostMapping("/pay/{service}")
    public ApiResponse pay(@PathVariable("service") String serviceName,
                           @RequestBody BankServicePayDTO bankServicePayDTO) {
        try {
            BankServiceValidator.checkToValidFormDTO(bankServicePayDTO);
            Client client = findNowClient();
            BankServiceModelExample emptyBankServiceModel = BankServiceValidator.checkAndReturnWhatServiceDoYouHave(serviceName);

            BankServiceModelExample bankServiceModelExample = bankServiceService.findById(bankServicePayDTO.getId(), emptyBankServiceModel);
            Account account = BankServiceValidator.checkToValidBankServicePay(bankServicePayDTO, client, bankServiceModelExample);

            bankServiceService.pay(bankServiceModelExample, account, bankServicePayDTO.getAmount());

            String resultOfTransaction = "Transaction successful !";
            if (bankServiceModelExample.getClass() == Credit.class) {
                resultOfTransaction = bankServiceService.checkToCreditFinished((Credit) bankServiceModelExample);
            }

            Transaction transaction = new Transaction(LocalDateTime.now(),
                    TransactionName.SERVICE_REPLENISHMENT.name(),
                    client.getFirstName(),
                    "Money Guard",
                    bankServicePayDTO.getAmount(), client);
            transactionService.save(transaction);
            return new ApiResponse(true, resultOfTransaction);
        } catch (Exception e) {
            return new ApiResponse(false, e.getMessage());
        }
    }


    @PostMapping("/withdraw/{service}/{id}")
    public ApiResponse withdrawDepositOrJar(@PathVariable("service") String serviceName, @PathVariable("id") Long id) {
        Client client = findNowClient();
        try {
            BankServiceModelExample emptyBankServiceModel = BankServiceValidator.checkAndReturnWhatServiceDoYouHave(serviceName);

            Double savingsForService = bankServiceService.withdrawService(client, emptyBankServiceModel, id);

            Transaction transaction = new Transaction(LocalDateTime.now(),
                    TransactionName.WITHDRAWAL.name(),
                    "Money Guard",
                    client.getFirstName(),
                    savingsForService, client);
            transactionService.save(transaction);
            return new ApiResponse(true, "Withdrawal processed successfully.");
        } catch (Exception e) {
            return new ApiResponse(false, e.getMessage());
        }
    }
}
