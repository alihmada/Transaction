package com.ali.transaction.Classes;

public class Matcher {
    public static boolean isUserName(String userName) {
        return !userName.matches("^[a-zA-Zء-ي]?/?\\s?+(([a-zA-Zء-ي]{3,10})(?:\\s|$)){1,6}$");
    }

    public static boolean isPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^0?1[0125][0-9]{8}$");
    }

    public static boolean isNumber(String number) {
        return number.matches("[0-9]+");
    }

    public static boolean isFloatingNumber(String price) {
        return price.matches("^[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?$");
    }

    public static boolean isOperation(String operation) {
        // ^([+\-*/]?\d+\.?\d*)([+\-*/])([+\-*/]?\d+\.?\d*)*([+\-*/])$
        return operation.matches("[0-9]+(?:\\.[0-9]+)?(?:[eE][+-]?[0-9]+)?(?:\\s*[+\\-*/]\\s*[0-9]+(?:\\.[0-9]+)?(?:[eE][+-]?[0-9]+)?)+");
    }
}
