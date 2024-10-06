package com.ali.transaction.MVVM;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ali.transaction.Models.Client;
import com.ali.transaction.Models.ClientJobModel;
import com.ali.transaction.Models.JobItem;

import java.util.List;

public class ClientWithJobsViewModel extends ViewModel {
    private MutableLiveData<Pair<Client, List<ClientJobModel>>> mutableLiveData;

    public void initialize(String id) {
        if (mutableLiveData != null) {
            return;
        }
        Repository repository = Repository.getInstance();
        mutableLiveData = repository.getClientsWithJobs(id);
    }

    public LiveData<Pair<Client, List<ClientJobModel>>> getClientsWithJobs() {
        return mutableLiveData;
    }
}
