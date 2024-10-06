package com.ali.transaction.Models;

import com.ali.transaction.Classes.UniqueIdGenerator;

public class Client {
    private String id;
    private String name;
    private double take;
    private double give;

    public Client() {
    }

    public Client(String id, String name, double take, double give) {
        this.id = id;
        this.name = name;
        this.take = take;
        this.give = give;
    }

    public static Client getInstance(String name) {
        return new Client(UniqueIdGenerator.generateUniqueId(), name, 0, 0);
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
