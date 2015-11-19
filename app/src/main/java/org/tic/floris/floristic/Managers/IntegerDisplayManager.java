package org.tic.floris.floristic.Managers;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

public class IntegerDisplayManager {

    public static String getDecimalFormat(int value, char separator) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(separator);
        return formatter.format(value);
    }

    public static String getDecimalFormat(Integer value, char separator) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(separator);
        return formatter.format(value);
    }
}
