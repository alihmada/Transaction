package com.ali.transaction.MVVM;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ali.transaction.Models.Client;
import com.ali.transaction.Models.ClientJobModel;
import com.ali.transaction.Models.JobItem;

import java.util.List;

public class ClientWithItemsViewModel extends ViewModel {
    private MutableLiveData<Pair<ClientJobModel, List<JobItem>>> mutableLiveData;

    public void initialize(String clientID, String jobID) {
        if (mutableLiveData != null) {
            return;
        }
        Repository repository = Repository.getInstance();
        mutableLiveData = repository.getJobWithItems(clientID, jobID);
    }

    public LiveData<Pair<ClientJobModel, List<JobItem>>> getJobWithItems() {
        return mutableLiveData;
    }
}
