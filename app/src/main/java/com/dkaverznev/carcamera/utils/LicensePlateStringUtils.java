package com.dkaverznev.carcamera.utils;

import java.util.Map;
import java.util.Set; // Убедитесь, что этот импорт есть
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.Map.entry; // Этот импорт используется для CYRILLIC_TO_LATIN_MAP

public class LicensePlateStringUtils {

    private static final Map<Character, Character> CYRILLIC_TO_LATIN_MAP = Map.ofEntries(
            entry('А', 'A'),
            entry('В', 'B'),
            entry('Е', 'E'),
            entry('К', 'K'),
            entry('М', 'M'),
            entry('Н', 'H'),
            entry('О', 'O'),
            entry('Р', 'P'),
            entry('С', 'C'),
            entry('Т', 'T'),
            entry('У', 'Y'),
            entry('Х', 'X')
    );

    public static String convert(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        StringBuilder resultBuilder = new StringBuilder(input.length());

        for (char c : input.toCharArray()) {
            char upperCaseChar = Character.toUpperCase(c);

            Character convertedChar = CYRILLIC_TO_LATIN_MAP.get(upperCaseChar);
            if (convertedChar != null) {
                upperCaseChar = convertedChar;
            }

            if ((upperCaseChar >= 'A' && upperCaseChar <= 'Z') || (upperCaseChar >= '0' && upperCaseChar <= '9')) {
                resultBuilder.append(upperCaseChar);
            }
        }

        return resultBuilder.toString();
    }

    private static final Pattern STRICT_LICENSE_PLATE_PATTERN =
            Pattern.compile("[A-Z][0-9]{3}[A-Z]{2}([0-9]{2,3})");

    // Используем Set.of() для инициализации неизменяемого Set
    private static final Set<String> VALID_REGION_CODES = Set.of(
            "01", "101", "02", "102", "702", "03", "103", "04", "05", "06",
            "07", "08", "09", "109", "10", "11", "111", "12", "13", "113",
            "14", "15", "16", "116", "716", "17", "18", "118", "19", "20",
            "95", "21", "121", "22", "122", "23", "93", "123", "193", "323",
            "24", "84", "88", "124", "25", "125", "725", "26", "126", "27",
            "28", "29", "30", "130", "31", "32", "33", "34", "134", "35",
            "36", "136", "37", "38", "138", "39", "91", "139", "40", "41",
            "42", "142", "43", "44", "45", "46", "47", "147", "48", "49",
            "50", "90", "150", "190", "250", "550", "750", "790", "51", "52",
            "152", "252", "53", "54", "154", "754", "55", "155", "56", "156",
            "57", "58", "158", "59", "159", "60", "61", "161", "761", "62",
            "63", "163", "763", "64", "164", "65", "66", "96", "196", "67",
            "68", "69", "169", "70", "71", "72", "172", "73", "173", "74",
            "174", "774", "75", "76", "77", "97", "99", "177", "197", "199",
            "777", "797", "799", "977", "78", "98", "178", "198", "79", "80",
            "180", "81", "181", "82", "182", "83", "184", "85", "185",
            "86", "186", "87", "89", "92", "192", "188", "94"
    );

    private static boolean isValidRegionCode(String regionCode) {
        return VALID_REGION_CODES.contains(regionCode);
    }

    public static String findLicensePlate(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        String processedInput = convert(input);

        Matcher matcher = STRICT_LICENSE_PLATE_PATTERN.matcher(processedInput);
        if (matcher.find()) {
            String licensePlate = matcher.group();
            String regionCode = matcher.group(1);

            if (isValidRegionCode(regionCode)) {
                return licensePlate;
            }
        }
        return null;
    }

    /**
     * Вспомогательный класс для хранения основной части номера и кода региона.
     */
    public static class LicensePlateParts {
        public final String numberPart;
        public final String regionPart;

        public LicensePlateParts(String numberPart, String regionPart) {
            this.numberPart = numberPart;
            this.regionPart = regionPart;
        }
    }

    public static LicensePlateParts splitLicensePlate(String fullLicensePlate) {
        if (fullLicensePlate == null || fullLicensePlate.isEmpty()) {
            return new LicensePlateParts("", null);
        }

        Pattern regionPattern = Pattern.compile("(\\d{3}|\\d{2})$");
        Matcher matcher = regionPattern.matcher(fullLicensePlate);

        if (matcher.find()) {
            String region = matcher.group(1);
            assert region != null;
            String number = fullLicensePlate.substring(0, fullLicensePlate.length() - region.length());
            return new LicensePlateParts(number, region);
        } else {
            return new LicensePlateParts(fullLicensePlate, null);
        }
    }
}