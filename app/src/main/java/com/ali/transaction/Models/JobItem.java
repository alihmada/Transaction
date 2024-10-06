package com.ali.transaction.Models;

import com.ali.transaction.Classes.UniqueIdGenerator;

public class JobItem {
    private Type type;
    private String id, date, reason;
    private double balance;

    public JobItem() {
    }

    public JobItem(String date, String id, Type type, String reason, double balance) {
        this.date = date;
        this.id = id;
        this.type = type;
        this.reason = reason;
        this.balance = balance;
    }

    public static JobItem getInstance(String date, Type type, String reason, double balance) {
        return new JobItem(date, UniqueIdGenerator.generateUniqueId(), type, reason, balance);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public enum Type {
        TAKE, GIVE
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
