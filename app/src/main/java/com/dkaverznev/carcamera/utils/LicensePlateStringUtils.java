package com.dkaverznev.carcamera.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LicensePlateStringUtils {

    private static final Map<Character, Character> CYRILLIC_TO_LATIN_MAP = createCyrillicToLatinMap();

    /**
     * @return Возвращаем HashMap с соответствием русских -> английский букв
     */
    private static Map<Character, Character> createCyrillicToLatinMap() {
        Map<Character, Character> map = new HashMap<>();
        map.put('А', 'A');
        map.put('В', 'B');
        map.put('Е', 'E');
        map.put('К', 'K');
        map.put('М', 'M');
        map.put('Н', 'H');
        map.put('О', 'O');
        map.put('Р', 'P');
        map.put('С', 'C');
        map.put('Т', 'T');
        map.put('У', 'Y');
        map.put('Х', 'X');
        return map;
    }

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

    /**
     * Паттерн для номеров РФ
     */
    private static final Pattern STRICT_LICENSE_PLATE_PATTERN =
            Pattern.compile("[A-Z][0-9]{3}[A-Z]{2}([0-9]{3}|[0-9]{2})");


    public static String findLicensePlate(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        String processedInput = convert(input);

        // Поиск по паттерну
        Matcher matcher = STRICT_LICENSE_PLATE_PATTERN.matcher(processedInput);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}