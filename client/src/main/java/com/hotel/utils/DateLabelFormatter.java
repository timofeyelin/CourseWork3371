package com.hotel.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.JFormattedTextField.AbstractFormatter;


public class DateLabelFormatter extends AbstractFormatter {
    private final String datePattern = "dd.MM.yyyy";
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

    @Override
    public Object stringToValue(String text) throws ParseException {
        return dateFormatter.parseObject(text);
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        if (value != null) {
            java.util.Calendar cal = (java.util.Calendar) value;
            return dateFormatter.format(cal.getTime());
        }
        return "";
    }
}