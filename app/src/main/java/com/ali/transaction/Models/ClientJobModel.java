package com.ali.transaction.Models;

import com.ali.transaction.Classes.DateAndTime;
import com.ali.transaction.Classes.UniqueIdGenerator;

public class ClientJobModel extends Client {
    String date;

    public ClientJobModel() {
    }

    public ClientJobModel(String id, String date, String name, double take, double give) {
        super(id, name, take, give);
        this.date = date;
    }

    public static ClientJobModel getInstance(String name) {
        return new ClientJobModel(UniqueIdGenerator.generateUniqueId(), DateAndTime.getCurrentDateTime(), name, 0, 0);
    }

    public String getDate() {
        return date;
    }
}
