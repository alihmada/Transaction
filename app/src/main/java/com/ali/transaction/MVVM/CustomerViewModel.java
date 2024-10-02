package com.ali.transaction.MVVM;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ali.transaction.Models.Customer;

import java.util.List;

public class CustomerViewModel extends ViewModel {
    private MutableLiveData<Customer> mutableLiveData;

    public void initialize(String id) {
        if (mutableLiveData != null) {
            return;
        }
        Repository repository = Repository.getInstance();
        mutableLiveData = repository.getCustomer(id);
    }

    public LiveData<Customer> getCustomer() {
        return mutableLiveData;
    }
}
