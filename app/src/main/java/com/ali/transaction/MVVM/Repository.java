package com.ali.transaction.MVVM;

import android.util.Pair;

import androidx.lifecycle.MutableLiveData;

import com.ali.transaction.Database.Firebase;
import com.ali.transaction.Models.Client;
import com.ali.transaction.Models.ClientJobModel;
import com.ali.transaction.Models.JobItem;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class Repository {

    private static Repository instance;

    public static Repository getInstance() {
        if (instance == null) instance = new Repository();
        return instance;
    }

    public MutableLiveData<List<Client>> getClients() {
        MutableLiveData<List<Client>> mutableLiveData = new MutableLiveData<>();

        Firebase.getAllClients(new Firebase.ClientsCallback() {
            @Override
            public void onClientFetched(List<Client> clients) {
                mutableLiveData.setValue(clients);
            }

            @Override
            public void onError(DatabaseError error) {
                mutableLiveData.setValue(null);
            }
        });

        return mutableLiveData;
    }

    public MutableLiveData<Client> getClient(String clientID) {
        MutableLiveData<Client> mutableLiveData = new MutableLiveData<>();
        Firebase.getClientById(clientID, new Firebase.ClientCallback() {
            @Override
            public void onClientFetched(DatabaseReference reference, Client Client) {
                mutableLiveData.setValue(Client);
            }

            @Override
            public void onError(DatabaseError error) {
                mutableLiveData.setValue(null);
            }
        });
        return mutableLiveData;
    }

    public MutableLiveData<ClientJobModel> getJob(String clientID, String jobID) {
        MutableLiveData<ClientJobModel> mutableLiveData = new MutableLiveData<>();
        Firebase.getJob(clientID, jobID, new Firebase.JobCallback() {
            @Override
            public void onJobFetched(ClientJobModel jobModel) {
                mutableLiveData.setValue(jobModel);
            }

            @Override
            public void onFailure() {
                mutableLiveData.setValue(null);
            }
        });

        return mutableLiveData;
    }

    public MutableLiveData<Pair<Client, List<ClientJobModel>>> getClientsWithJobs(String id) {
        MutableLiveData<Pair<Client, List<ClientJobModel>>> mutableLiveData = new MutableLiveData<>();

        Firebase.getClientWithJobs(id, new Firebase.ClientJobsCallback() {
            @Override
            public void onClientFetched(Pair<Client, List<ClientJobModel>> clientJobs) {
                mutableLiveData.setValue(clientJobs);
            }

            @Override
            public void onError(DatabaseError error) {
                mutableLiveData.setValue(null);
            }
        });
        return mutableLiveData;
    }

    public MutableLiveData<Pair<ClientJobModel, List<JobItem>>> getJobWithItems(String clientID, String jobID) {
        MutableLiveData<Pair<ClientJobModel, List<JobItem>>> mutableLiveData = new MutableLiveData<>();

        Firebase.getJobWithItems(clientID, jobID, new Firebase.JobAndItemsCallback() {
            @Override
            public void onSuccess(Pair<ClientJobModel, List<JobItem>> pair) {
                mutableLiveData.setValue(pair);
            }

            @Override
            public void onFailure(Pair<ClientJobModel, List<JobItem>> pair) {
                mutableLiveData.setValue(pair);
            }
        });

        return mutableLiveData;
    }

    public MutableLiveData<JobItem> getItem(String clientID, String jobID, String itemID) {
        MutableLiveData<JobItem> mutableLiveData = new MutableLiveData<>();

        Firebase.getItem(clientID, jobID, itemID, new Firebase.ItemCallback() {
            @Override
            public void onItemFetched(JobItem jobItem) {
                mutableLiveData.setValue(jobItem);
            }

            @Override
            public void onFailure(String error) {
                mutableLiveData.setValue(null);
            }
        });

        return mutableLiveData;
    }
}
