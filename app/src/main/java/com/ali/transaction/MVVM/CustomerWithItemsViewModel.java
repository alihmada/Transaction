package com.ali.transaction.MVVM;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ali.transaction.Models.Customer;
import com.ali.transaction.Models.Item;

import java.util.List;

public class CustomerWithItemsViewModel extends ViewModel {
    private MutableLiveData<Pair<Customer, List<Item>>> mutableLiveData;

    public void initialize(String id) {
        if (mutableLiveData != null) {
            return;
        }
        Repository repository = Repository.getInstance();
        mutableLiveData = repository.getCustomersWithItems(id);
    }

    public LiveData<Pair<Customer, List<Item>>> getCustomersWithItems() {
        return mutableLiveData;
    }
}
