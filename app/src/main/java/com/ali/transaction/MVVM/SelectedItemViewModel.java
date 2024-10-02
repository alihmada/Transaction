package com.ali.transaction.MVVM;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ali.transaction.Models.Item;

public class SelectedItemViewModel extends ViewModel {
    private MutableLiveData<Item> mutableLiveData;

    public void initialize(String parentID, String childID) {
        if (mutableLiveData != null) {
            return;
        }
        Repository repository = Repository.getInstance();
        mutableLiveData = repository.getItem(parentID, childID);
    }

    public LiveData<Item> getItem() {
        return mutableLiveData;
    }
}
