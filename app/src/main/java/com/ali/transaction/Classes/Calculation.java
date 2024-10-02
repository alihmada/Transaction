package com.ali.transaction.Classes;

import android.util.Pair;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Calculation {
    private static final int NUMBER_OF_BAGS_ON_TON = 20;

    public static String evaluateExpression(String expression) {
        Expression exp = new ExpressionBuilder(expression).build();
        double result = exp.evaluate();
        if (result == (int) result) {
            return String.valueOf((int) result);
        } else {
            return String.valueOf(result);
        }
    }

    public static Pair<String, Boolean> getBalance(double take, double give) {
        return new Pair<>(Calculation.formatNumberWithCommas(Math.abs(take - give)), take > give);
    }

    public static String getNumber(double number) {
        if (number == (int) number) {
            return String.valueOf((int) number);
        } else {
            return String.valueOf(number);
        }
    }

    public static String formatNumberWithCommas(double number) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat formatter = new DecimalFormat("#,###.###", symbols);
        return formatter.format(number);
    }
}
