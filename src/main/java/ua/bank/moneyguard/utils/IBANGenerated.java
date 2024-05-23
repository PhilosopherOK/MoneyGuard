package ua.bank.moneyguard.utils;

public class IBANGenerated {
    private static String MFO = "305299"; // PrivatBank MFO
    private static String countryCode = "UA";

    public static String generator(String id) {
        for (int i = 0; id.length() != 18; i++) {
            id = "0" + id;
        }
        String tempIBAN = MFO + id + countryCode + "00";
        tempIBAN = convertLettersToDigits(tempIBAN);
        String checksum = calculateMOD97(tempIBAN);

        return countryCode + checksum + MFO + id;
    }

    private static String convertLettersToDigits(String input) {
        StringBuilder numericIBAN = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (Character.isLetter(ch)) {
                int numericValue = Character.getNumericValue(ch);
                numericIBAN.append(numericValue);
            } else {
                numericIBAN.append(ch);
            }
        }
        return numericIBAN.toString();
    }

    private static String calculateMOD97(String iban) {
        java.math.BigInteger ibanNumber = new java.math.BigInteger(iban);
        java.math.BigInteger mod97 = new java.math.BigInteger("97");

        int checksumValue = 98 - ibanNumber.mod(mod97).intValue();

        return String.format("%02d", checksumValue);
    }
}