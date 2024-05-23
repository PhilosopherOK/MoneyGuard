package ua.bank.moneyguard.controllers.rest;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ua.bank.moneyguard.dtos.authorizationDTOs.RegistrationDTO;
import ua.bank.moneyguard.dtos.mainDTOs.AccountDTO;
import ua.bank.moneyguard.dtos.mainDTOs.CardDataDTO;
import ua.bank.moneyguard.dtos.mainDTOs.CurrencyNameDTO;
import ua.bank.moneyguard.dtos.mainDTOs.TransactionDTO;
import ua.bank.moneyguard.mappers.Mapper;
import ua.bank.moneyguard.models.Account;
import ua.bank.moneyguard.models.Client;
import ua.bank.moneyguard.models.ExchangeRate;
import ua.bank.moneyguard.models.Transaction;
import ua.bank.moneyguard.services.*;
import ua.bank.moneyguard.utils.ApiResponse;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/main/api")
public class MainRestController {

    private final ExchangeRateService exchangeRateService;
    private final AccountService accountService;
    private final ClientService clientService;
    private final MainCardService mainCardService;
    private final TransactionService transactionService;


    public Client findNowClient() {
        return clientService.findNowClient();
    }

    @GetMapping("/currency")
    public ApiResponse getAbbreviatedNameOfCurrency() {
        return new ApiResponse(true, "", exchangeRateService.getAbbreviatedNameOfCurrency());
    }

    @GetMapping("/exchangeRates")
    public ApiResponse getThreeMainCurrency() {
        return new ApiResponse(true, "Retrieved top three currency rates",
                exchangeRateService.getThreeMainCurrency());
    }


    @PostMapping("/createAccount")
    public ApiResponse createAccount(@RequestBody CurrencyNameDTO currencyNameDTO) {
        Client client = findNowClient();
        try {
            Account account = accountService.createAccountWithCurrencyName(currencyNameDTO.getCurrencyName(), client);
            mainCardService.createMainCard(account);
            return new ApiResponse(true, "Account created successfully");
        } catch (Exception e) {
            return new ApiResponse(false, e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ApiResponse getMineProfile() {
        Client client = findNowClient();
        return new ApiResponse(true, "", Mapper.clientToClientDTO(client));
    }

    @GetMapping("/account/{page}")
    public ApiResponse getAccount(@PathVariable(required = false) Integer page) {
        Client client = findNowClient();
        if (page == null) {
            page = 0;
        }
        Page<Account> accountPage = accountService.findAccountByClient(page, 3, client);
        Page<AccountDTO> dtoAPage = accountPage.map(Mapper::accountToAccountDTO);
        return new ApiResponse(true, "", dtoAPage);
    }

    @GetMapping("/transaction/{page}")
    public ApiResponse getTransaction(@PathVariable int page) {
        Client client = findNowClient();
        Page<Transaction> transactionsPage = transactionService.findAll(page, 10, client);
        Page<TransactionDTO> dtoTPage = transactionsPage.map(Mapper::transactionToTransactionDTO);
        return new ApiResponse(true, "", dtoTPage);
    }

    @PostMapping("/cheatingAccountReplenishment")
    public ApiResponse cheatingAccountReplenishment(@RequestBody CurrencyNameDTO currencyNameDTO) {
        Client client = findNowClient();
        Account adminAccount = clientService.findAdmin().getAccounts().stream().filter(a -> a.getCurrencyName().equals("UAH")).findAny().get();
        try {
            Account accountFrom = client.getAccounts().stream().filter(a -> a.getCurrencyName().equals(currencyNameDTO.getCurrencyName())).findAny().get();

            Double resultSumForPayment = 1000.0;
            if (!accountFrom.getCurrencyName().equals("UAH")) {
                ExchangeRate rate = exchangeRateService.findByShortName(accountFrom.getCurrencyName());
                resultSumForPayment = 1000 / rate.getBuyRate();
            }
            adminAccount.setAmountOfMoney(adminAccount.getAmountOfMoney() - 1000.0);
            accountFrom.setAmountOfMoney(accountFrom.getAmountOfMoney() + resultSumForPayment);

            accountService.saveAll(List.of(accountFrom, adminAccount));
            return new ApiResponse(true, "Account successfully replenished on 1000 UAH");
        } catch (Exception e) {
            return new ApiResponse(false, e.getMessage());
        }
    }


    @PostMapping("/cardData")
    public ApiResponse getCardDataByCurrencyName(@RequestBody CurrencyNameDTO currencyNameDTO) {
        Client client = findNowClient();
        try {
            CardDataDTO cardDataDTO = mainCardService.getCardDataFromClient(client, currencyNameDTO.getCurrencyName());
            return new ApiResponse(true, "", cardDataDTO);
        } catch (Exception e) {
            return new ApiResponse(false, e.getMessage());
        }
    }

    @PostMapping("/changeProfile")
    public ApiResponse updateProfile(@RequestBody RegistrationDTO registrationDTO) {
        Client client = findNowClient();
        try {
            clientService.updateClientFromRegistrationDTO(client.getClientId(), registrationDTO);
            return new ApiResponse(true, "Profile successfully update.");
        } catch (Exception e) {
            return new ApiResponse(false, e.getMessage());
        }
    }
}