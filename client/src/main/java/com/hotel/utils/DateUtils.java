package com.hotel.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtils {
    private static final DateTimeFormatter UI_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter SERVER_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String convertDateToUI(LocalDate date) {
        if (date == null) return "";
        return date.format(UI_FORMATTER);
    }
    
    public static String convertDateToServer(String uiDate) throws DateTimeParseException {
        if (uiDate == null || uiDate.trim().isEmpty()) return null;
        LocalDate date = LocalDate.parse(uiDate, UI_FORMATTER);
        return date.format(SERVER_FORMATTER);
    }

    public static boolean isValidDate(String date) {
        try {
            LocalDate.parse(date, UI_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}