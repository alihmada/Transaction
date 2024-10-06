package com.ali.transaction.MVVM;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ali.transaction.Models.Client;
import com.ali.transaction.Models.ClientJobModel;

public class JobViewModel extends ViewModel {
    private MutableLiveData<ClientJobModel> mutableLiveData;

    public void initialize(String clientID, String jobID) {
        if (mutableLiveData != null) {
            return;
        }
        Repository repository = Repository.getInstance();
        mutableLiveData = repository.getJob(clientID, jobID);
    }

    public LiveData<ClientJobModel> getJob() {
        return mutableLiveData;
    }
}
