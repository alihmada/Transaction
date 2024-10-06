package com.ali.transaction.MVVM;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ali.transaction.Models.JobItem;

public class SelectedItemViewModel extends ViewModel {
    private MutableLiveData<JobItem> mutableLiveData;

    public void initialize(String clientID, String jobID, String itemID) {
        if (mutableLiveData != null) {
            return;
        }
        Repository repository = Repository.getInstance();
        mutableLiveData = repository.getItem(clientID, jobID, itemID);
    }

    public LiveData<JobItem> getItem() {
        return mutableLiveData;
    }
}
