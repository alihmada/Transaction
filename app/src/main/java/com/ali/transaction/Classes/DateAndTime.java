package com.ali.transaction.Classes;

import android.content.Context;

import com.ali.transaction.R;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateAndTime {
    public static LocalDateTime getLocalTime() {
        return LocalDateTime.now();
    }

    public static String timeFormatter(LocalDateTime time) {
        return time.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a"));
    }

    public static String getCurrentDateTime() {
        return DateAndTime.timeFormatter(DateAndTime.getLocalTime());
    }

    public static String getYear() {
        return Integer.toString(LocalDate.now().getYear());
    }

    public static int getCurrentMonth() {
        return Calendar.getInstance().get(Calendar.MONTH);
    }

    public static String getArabicNameOfMonth() {
        return new DateFormatSymbols(new Locale("ar")).getMonths()[getCurrentMonth()];
    }

    public static String getArabicNameOfMonth(String month) {
        return new DateFormatSymbols(new Locale("ar")).getMonths()[Integer.parseInt(month) - 1];
    }

    public static String getPastDays(String firstDate, String secondDate) {
        try {
            // Parse the dates
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
            Date date1 = formatter.parse(handleAMPM(firstDate));
            Date date2 = formatter.parse(handleAMPM(secondDate));

            // Ignore the time component by setting the time to midnight
            date1 = resetTime(date1);
            date2 = resetTime(date2);

            // Calculate the difference in milliseconds
            long diffInMillies = Math.abs(date2.getTime() - date1.getTime());

            // Convert milliseconds to days
            long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            // Output the difference in days
            return String.valueOf(diffInDays);
        } catch (ParseException ignored) {
        }
        return null;
    }

    private static Date resetTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static String handleAMPM(String date) {
        if ((date.contains("م") || date.contains("ص")) && Locale.getDefault().getLanguage().equals("en")) {
            date = date.replace("م", "PM").replace("ص", "AM");
        } else if ((date.contains("PM") || date.contains("AM")) && Locale.getDefault().getLanguage().equals("ar")) {
            date = date.replace("PM", "م").replace("AM", "ص");
        }
        return date;
    }

    public static String extractDateOnly(String dateTime) {

        dateTime = handleAMPM(dateTime);

        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");

        try {
            Date date = inputFormat.parse(dateTime);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return "";
        }
    }

    public static String getDate(Context context, String date) {
        date = handleAMPM(date);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");

        try {
            LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
            LocalDate inputDate = dateTime.toLocalDate();
            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);

            String dayString;
            if (inputDate.equals(today)) {
                dayString = context.getString(R.string.today);
            } else if (inputDate.equals(yesterday)) {
                dayString = context.getString(R.string.yesterday);
            } else {
                dayString = dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            }

            return formatAndSetDate(context, new String[]{
                    dayString,
                    dateTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
            });
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String formatAndSetDate(Context context, String[] dateAndTime) {
        String formattedDate;
        if (dateAndTime[0].equals(context.getString(R.string.today)) || dateAndTime[0].equals(context.getString(R.string.yesterday))) {
            formattedDate = String.format("%s %s %s",
                    dateAndTime[0],
                    context.getString(R.string.hour),
                    dateAndTime[1].replace("AM", "ص").replace("PM", "م"));

        } else {
            formattedDate = String.format("%s %s %s %s",
                    context.getString(R.string.day), dateAndTime[0],
                    context.getString(R.string.hour),
                    dateAndTime[1].replace("AM", "ص").replace("PM", "م"));
        }
        return formattedDate;
    }
}
