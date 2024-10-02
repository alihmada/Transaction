package com.ali.transaction.Classes;

public class Common {
    public static final String PHONE_NUMBER = "phone";
    public static final String DATABASE_NAME = "Database";
    public static final String FIREBASE_USERS = "Users";
    public static final String USER_DATA = "user_data";
    public static final String ACCOUNTS = "accounts";

    public static final String IMAGE = "image";
    public static final String VALUE = "value";
    public static final String REGEX = "regex";
    public static final String DECIMAL_REGEX = "\\d*\\.?\\d+$";
    public static final String NOT_EMPTY_REGEX = "^\\s*\\S.*$";

    public static final String ID = "id";
    public static final String TAKE = "take";
    public static final String GIVE = "give";
    public static final String PARENT_ID = "parent";
    public static final String ITEM_ID = "child";
    public static final String NAME = "name";
    public static final String MESSAGE = "message";

    private static final String ROOT = "My Transactions";

    public static String getROOT() {
        return ROOT;
    }

}
