package com.ali.transaction.MVVM;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ali.transaction.Models.Client;

public class ClientViewModel extends ViewModel {
    private MutableLiveData<Client> mutableLiveData;

    public void initialize(String id) {
        if (mutableLiveData != null) {
            return;
        }
        Repository repository = Repository.getInstance();
        mutableLiveData = repository.getClient(id);
    }

    public LiveData<Client> getClient() {
        return mutableLiveData;
    }
}
