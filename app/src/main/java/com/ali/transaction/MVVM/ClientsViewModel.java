package com.ali.transaction.MVVM;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ali.transaction.Models.Client;

import java.util.List;

public class ClientsViewModel extends ViewModel {
    private MutableLiveData<List<Client>> mutableLiveData;

    public void initialize() {
        if (mutableLiveData != null) {
            return;
        }
        Repository repository = Repository.getInstance();
        mutableLiveData = repository.getClients();
    }

    public LiveData<List<Client>> getClients() {
        return mutableLiveData;
    }
}
