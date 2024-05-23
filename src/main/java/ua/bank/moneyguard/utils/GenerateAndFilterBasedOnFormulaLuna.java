package ua.bank.moneyguard.utils;
/*
Формула Луна:
Отбросьте последнюю цифру из числа. Последняя цифра — это то, что мы хотим проверить.
Перевернуть цифры
Умножьте цифры в нечетных позициях (1, 3, 5 и т. д.) на 2 и вычтите 9 из любого результата, превышающего 9.
Сложите все числа вместе
Контрольная цифра (последний номер карты) — это сумма, которую вам нужно прибавить, чтобы получить число, кратное 10 (по модулю 10).
 */

import java.util.Random;

public class GenerateAndFilterBasedOnFormulaLuna {
    public static String generateMasterCardNumber() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        int[] prefixes = {51, 52, 53, 54, 55};
        int prefix = random.nextBoolean()? prefixes[random.nextInt(prefixes.length)] : (222100 + random.nextInt(272099 - 222100));
        sb.append(prefix);
        for (int i = sb.length(); i < 15; i++) {
            sb.append(random.nextInt(10));
        }

        int controlNumber = 10 - (calculateSumFromStrByMoon(sb.toString()) % 10);
        if(controlNumber == 10)
            controlNumber = 0;
        sb.append(controlNumber);
        return sb.toString();
    }

    public static boolean existCardFilteredByMoonFormula(String cardNumber) {
        Integer lastNum = Integer.parseInt(cardNumber.substring(cardNumber.length() - 1));
        StringBuilder sb = new StringBuilder(cardNumber.substring(0, cardNumber.length() - 1));
        sb.reverse();

        String reversedNumber = sb.toString();
        int sum = calculateSumFromStrByMoon(reversedNumber);
        if ((sum + lastNum) % 10 != 0) {
            return false;
        }
        return true;
    }
    private static int calculateSumFromStrByMoon(String reversedNumber){
        int sum = 0;
        for (int i = 0; i < reversedNumber.length(); i++) {
            int digit = Character.getNumericValue(reversedNumber.charAt(i));
            if (i % 2 == 0) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
        }
        return sum;
    }
}
