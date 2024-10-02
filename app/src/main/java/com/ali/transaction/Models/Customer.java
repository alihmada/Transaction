package com.ali.transaction.Models;

public class Customer {
    private String id;
    private String name;
    private double take;
    private double give;

    public Customer() {
    }

    public Customer(String id, String name, double take, double give) {
        this.id = id;
        this.name = name;
        this.take = take;
        this.give = give;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public double getTake() {
        return take;
    }

    public double getGive() {
        return give;
    }
}
