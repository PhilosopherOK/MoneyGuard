package ua.bank.moneyguard.scheduledActions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ua.bank.moneyguard.dtos.ExchangeRateDTOFromAPI;
import ua.bank.moneyguard.mappers.Mapper;
import ua.bank.moneyguard.models.ExchangeRate;
import ua.bank.moneyguard.services.EmailSenderService;
import ua.bank.moneyguard.services.ExchangeRateService;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Component
public class RefreshExchangeRates {
    private final ExchangeRateService exchangeRateService;
    private final EmailSenderService emailSenderService;

    @Scheduled(fixedRate = 10_800_000)  //every 180 minutes
    public void updateExchangeRates() {
        List<ExchangeRateDTOFromAPI> exchangeRatesFromAnyApi = getExchangeRatesFromAnyApi();
        if(exchangeRatesFromAnyApi != null){
            List<ExchangeRate> resultList = exchangeRateDTOFromAPIToExchangeRate(exchangeRatesFromAnyApi);
            exchangeRateService.updateAll(resultList);
        }
    }

    public List<ExchangeRate> exchangeRateDTOFromAPIToExchangeRate(List<ExchangeRateDTOFromAPI> list){
        List<ExchangeRate> exchangeRateList = new ArrayList<>();
        for (int i = 0; i < list.size(); i+=2) {
            ExchangeRateDTOFromAPI e1 = list.get(i);
            ExchangeRateDTOFromAPI e2 = list.get(i+1);
            if(e1.getRate() < e2.getRate()){
                ExchangeRateDTOFromAPI temp = e1;
                e1 = e2;
                e2 = temp;
            }
            exchangeRateList.add(Mapper.convertExchangeRateDTOFromAPIToExchangeRate(e1,e2));
        }
        return exchangeRateList;
    }



    public List<ExchangeRateDTOFromAPI> getExchangeRatesFromAnyApi() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate1 = yesterday.format(formatter);
        String formattedDate2 = today.format(formatter);
        StringBuilder sb = new StringBuilder("https://bank.gov.ua/NBU_Exchange/exchange_site?");

        sb.append("start=").append(formattedDate1)
                .append("&end=").append("%20").append(formattedDate2)
                .append("&sort=").append("rate")
                .append("&order=").append("desc")
                .append("&json");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("dd.MM.yyyy"));
        List<ExchangeRateDTOFromAPI> exchangeRateDTOFromAPIList = null;
        try {
            URL obj = new URL(sb.toString());
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            try (InputStream is = conn.getInputStream()) {
                exchangeRateDTOFromAPIList = objectMapper.readValue(conn.getInputStream(), new TypeReference<List<ExchangeRateDTOFromAPI>>() {
                });
            }
        } catch (IOException e) {
            emailSenderService.sendEmail(
                    "hnatiukrost@gmail.com", "bank",e.getMessage());
        }
        return exchangeRateDTOFromAPIList;
    }
}
//https://bank.gov.ua/NBU_Exchange/exchange_site?"start=20240424&end=%2020240425&sort=rate&order=desc&json