package ua.bank.moneyguard.controllers.rest;

import lombok.Data;
import org.springframework.web.bind.annotation.*;
import ua.bank.moneyguard.dtos.moneyTransferDTOs.CurrencyExchangeDTO;
import ua.bank.moneyguard.dtos.moneyTransferDTOs.MoneyTransferCardRequestDTO;
import ua.bank.moneyguard.dtos.moneyTransferDTOs.MoneyTransferIBANRequestDTO;
import ua.bank.moneyguard.models.Account;
import ua.bank.moneyguard.models.Client;
import ua.bank.moneyguard.models.Transaction;
import ua.bank.moneyguard.services.*;
import ua.bank.moneyguard.utils.ApiResponse;
import ua.bank.moneyguard.utils.enums.TransactionName;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@RestController
@RequestMapping("/moneyTransfer")
public class MoneyTransferRestController {
    private final MainCardService mainCardService;
    private final AccountService accountService;
    private final ClientService clientService;
    private final TransactionService transactionService;
    private final ExchangeRateService exchangeRateService;


    public Client findNowClient() {
        return clientService.findNowClient();
    }

    @GetMapping("/getMineCurrency")
    public ApiResponse getMineCurrency() {
        Client client = findNowClient();
        try {
            Set<String> currencySet = accountService.getMineCurrency(client);
            return new ApiResponse(true, "", currencySet);
        } catch (Exception e) {
            return new ApiResponse(false, e.getMessage());
        }
    }

    @PostMapping("/cardTransfer")
    public ApiResponse sendMoneyToCard(@RequestBody MoneyTransferCardRequestDTO moneyTransferCardRequestDTO) {
        Client client = findNowClient();
        try {
            Account accountTo = mainCardService.sendMoneyToCard(client,
                    moneyTransferCardRequestDTO.getCurrencyName(),
                    moneyTransferCardRequestDTO.getTransferAmount(),
                    moneyTransferCardRequestDTO.getToCardNumber());
            Transaction transaction = new Transaction(LocalDateTime.now(),
                    TransactionName.CARD_TRANSFER.name(),
                    client.getFirstName(),
                    accountTo.getClient().getFirstName(),
                    moneyTransferCardRequestDTO.getTransferAmount(), client);
            transactionService.save(transaction);
            clientService.save(accountTo.getClient());
            return new ApiResponse(true, "Transaction successful");
        } catch (Exception e) {
            return new ApiResponse(false, e.getMessage());
        }
    }

    @PostMapping("/IBANTransfer")
    public ApiResponse sendMoneyToIBAN(@RequestBody MoneyTransferIBANRequestDTO moneyTransferIBANRequestDTO) {
        Client client = findNowClient();
        try {
            Account accountTo = mainCardService.sendMoneyToIBAN(client,
                    moneyTransferIBANRequestDTO.getCurrencyName(),
                    moneyTransferIBANRequestDTO.getTransferAmount(),
                    moneyTransferIBANRequestDTO.getToIBAN());
            Transaction transaction = new Transaction(LocalDateTime.now(),
                    TransactionName.IBAN_TRANSFER.name(),
                    client.getFirstName(),
                    accountTo.getClient().getFirstName(),
                    moneyTransferIBANRequestDTO.getTransferAmount(), client);
            transactionService.save(transaction);
            clientService.save(accountTo.getClient());
            return new ApiResponse(true, "Transaction successful");
        } catch (Exception e) {
            return new ApiResponse(false, e.getMessage());
        }
    }

    @PostMapping("/currencyExchange")
    public ApiResponse currencyExchange(@RequestBody CurrencyExchangeDTO currencyExchangeDTO) {
        Client client = findNowClient();
        try {
            exchangeRateService.currencyExchangeOnOneAccount(client, currencyExchangeDTO.getCurrencyNameFrom(),
                    currencyExchangeDTO.getCurrencyNameTo(), currencyExchangeDTO.getTransferAmount());

            Transaction transaction = new Transaction(LocalDateTime.now(),
                    TransactionName.CURRENCY_EXCHANGE.name(),
                    client.getFirstName(),
                    client.getFirstName(),
                    currencyExchangeDTO.getTransferAmount(), client);
            transactionService.save(transaction);
            return new ApiResponse(true, "Transaction successful");
        } catch (Exception e) {
            return new ApiResponse(false, e.getMessage());
        }
    }

}
