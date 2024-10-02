package com.ali.transaction.MVVM;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ali.transaction.Models.Customer;

import java.util.List;

public class CustomersViewModel extends ViewModel {
    private MutableLiveData<List<Customer>> mutableLiveData;

    public void initialize() {
        if (mutableLiveData != null) {
            return;
        }
        Repository repository = Repository.getInstance();
        mutableLiveData = repository.getCustomers();
    }

    public LiveData<List<Customer>> getCustomers() {
        return mutableLiveData;
    }
}
