package ua.bank.moneyguard.controllers.view;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.bank.moneyguard.services.AccountService;
import ua.bank.moneyguard.services.ClientService;
import ua.bank.moneyguard.services.ExchangeRateService;

import java.util.Set;

@AllArgsConstructor
@Controller
@RequestMapping("/main")
public class MainController {

    private final AccountService accountService;
    private final ClientService clientService;
    private final ExchangeRateService exchangeRateService;



    @GetMapping("/")
    public String getAccountPage(Model model){
        Set<String> currencySet = (exchangeRateService.getAbbreviatedNameOfCurrency());
        model.addAttribute("currencySet", currencySet);
        return "html/main";
    }

    @GetMapping("/moneyTransfer/card")
    public String getMoneyTransferCardPage(Model model){
        Set<String> currencySet = accountService.getMineCurrency(clientService.findNowClient());
        model.addAttribute("currencySet", currencySet);
        return "html/moneyTransferCard";
    }
    @GetMapping("/moneyTransfer/IBAN")
    public String getMoneyTransferIBANPage(Model model){
        Set<String> currencySet = accountService.getMineCurrency(clientService.findNowClient());
        model.addAttribute("currencySet", currencySet);
        return "html/moneyTransferIBAN";
    }

    @GetMapping("/currencyExchange")
    public String getCurrencyExchangePage(Model model) {
        Set<String> currencySet = accountService.getMineCurrency(clientService.findNowClient());
        model.addAttribute("currencySet", currencySet);
        return "html/exchange";
    }
    @GetMapping("/credit")
    public String getCreditPage(){
        return "html/credit";
    }
    @GetMapping("/deposit")
    public String getDepositPage(){
        return "html/deposit";
    }
    @GetMapping("/savingJar")
    public String getSavingsJarPage(){
        return "html/savingJar";
    }

    @GetMapping("/changeProfile")
    public String getChangeProfilePage(){
        return "html/changeProfile";
    }

}
