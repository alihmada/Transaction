package com.ali.transaction.Database;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.ali.transaction.Classes.Common;
import com.ali.transaction.Classes.UniqueIdGenerator;
import com.ali.transaction.Models.Client;
import com.ali.transaction.Models.ClientJobModel;
import com.ali.transaction.Models.JobItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Firebase {

    public static FirebaseDatabase getInstance() {
        return FirebaseDatabase.getInstance();
    } // End getInstance()

    public static DatabaseReference getClients() {
        return getInstance().getReference(Common.getRoot());
    } // End getRoot()

    public static void getAllClients(ClientsCallback clientCallback) {
        getClients().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Client> clients = new ArrayList<>();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        try {
                            Client client = snapshot.getValue(Client.class);
                            if (client != null) {
                                clients.add(client);
                            }
                        } catch (Exception ignored) {
                        }
                    }
                    clientCallback.onClientFetched(clients);
                } else {
                    clientCallback.onClientFetched(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public static void addClient(Client client) {
        getClients().orderByChild("id").equalTo(client.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) try {
                    getClients().push().setValue(client);
                } catch (Exception ignored) {
                }
                else {
                    client.setId(UniqueIdGenerator.generateUniqueId());
                    addClient(client);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    } // End addClient()

    public static void addJob(String id, ClientJobModel job) {
        getClients().orderByChild(Common.ID).equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                        snapshot.getChildren().iterator().next().getRef().orderByChild(Common.ID).equalTo(job.getId())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (!dataSnapshot.exists()) {
                                            dataSnapshot.getRef().child(Common.JOBS).push().setValue(job);
                                        } else {
                                            job.setId(UniqueIdGenerator.generateUniqueId());
                                            addJob(id, job);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    } // End addItem()

    public static void addItem(String clientID, String jobID, JobItem item) {
        fetchClientByID(clientID, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot clientSnapshot) {
                if (clientSnapshot.exists() && clientSnapshot.getChildrenCount() > 0) {
                    DataSnapshot clientData = clientSnapshot.getChildren().iterator().next();
                    findJobByID(clientData, jobID, new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot jobSnapshot) {
                            if (jobSnapshot.exists() && jobSnapshot.getChildrenCount() > 0) {
                                DataSnapshot jobData = jobSnapshot.getChildren().iterator().next();
                                findJobItemByID(jobData, item, new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot jobItemSnapshot) {
                                        if (!jobItemSnapshot.exists()) {
                                            // Add new item
                                            jobData.getRef().child(Common.JOB_ITEMS).push().setValue(item);
                                        } else {
                                            // Generate a new ID and retry adding the item
                                            item.setId(UniqueIdGenerator.generateUniqueId());
                                            addItem(clientID, jobID, item); // Recursive call with new ID
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Handle error
                                        System.err.println("Error fetching job item: " + error.getMessage());
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle error
                            System.err.println("Error fetching job: " + error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                System.err.println("Error fetching client: " + error.getMessage());
            }
        });
    } // End addItem()

    // Clients

    public static void getClientById(String id, ClientCallback callback) {
        Query clientQuery = Firebase.getClients().orderByChild(Common.ID).equalTo(id);
        clientQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Client Client = dataSnapshot.getChildren().iterator().next().getValue(Client.class);
                    callback.onClientFetched(clientQuery.getRef(), Client);
                } else {
                    callback.onClientFetched(null, null);  // Client not found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError);  // Handle error case
            }
        });
    }

    private static void fetchClientByID(String clientID, ValueEventListener listener) {
        getClients().orderByChild(Common.ID).equalTo(clientID)
                .addListenerForSingleValueEvent(listener);
    } // End findClientByID()

    public static void updateClientName(String id, String name) {
        queryClientById(id, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot clientSnapshot = dataSnapshot.getChildren().iterator().next();
                    clientSnapshot.getRef().child(Common.NAME).setValue(name);
                } else {
                    Log.e("FirebaseError", "Client with ID: " + id + " not found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Error querying customer: " + databaseError.getMessage());
            }
        });
    } // End updateClientName()

    public static void deleteClient(String id) {
        queryClientById(id, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot clientSnapshot = dataSnapshot.getChildren().iterator().next();
                    clientSnapshot.getRef().removeValue();
                } else {
                    Log.e("FirebaseError", "Client with ID: " + id + " not found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Error querying customer: " + databaseError.getMessage());
            }
        });
    } // End deleteClient()

    // Client and job
    public static void getClientWithJobs(String id, ClientJobsCallback callback) {
        Query clientQuery = Firebase.getClients().orderByChild(Common.ID).equalTo(id);
        clientQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if data exists and is valid
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    DataSnapshot clientSnapshot = dataSnapshot.getChildren().iterator().next();
                    Client client = clientSnapshot.getValue(Client.class);

                    if (client != null) {
                        fetchClientJobs(clientSnapshot.getRef(), client, callback);
                    } else {
                        callback.onClientFetched(null);
                    }
                } else {
                    callback.onClientFetched(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onClientFetched(null);
            }
        });
    }

    private static void fetchClientJobs(DatabaseReference clientRef, Client client, ClientJobsCallback callback) {
        clientRef.child(Common.JOBS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot itemSnapshot) {
                List<ClientJobModel> jobs = new ArrayList<>();

                for (DataSnapshot snapshot : itemSnapshot.getChildren()) {
                    ClientJobModel job = snapshot.getValue(ClientJobModel.class);
                    if (job != null) {
                        jobs.add(job);
                    }
                }

                callback.onClientFetched(new Pair<>(client, jobs));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onClientFetched(null);  // Handle error case
            }
        });
    }

    // Job and item
    public static void getJob(String clientID, String jobID, JobCallback callback) {
        fetchClientByID(clientID, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot clientSnapshot) {
                if (clientSnapshot.exists() && clientSnapshot.getChildrenCount() > 0) {
                    DataSnapshot clientData = clientSnapshot.getChildren().iterator().next();
                    fetchJobByID(clientData, jobID, new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot jobSnapshot) {
                            if (jobSnapshot.exists() && jobSnapshot.getChildrenCount() > 0) {
                                ClientJobModel clientJobModel = jobSnapshot.getChildren().iterator().next().getValue(ClientJobModel.class);

                                callback.onJobFetched(clientJobModel);

                            } else {
                                callback.onFailure();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            callback.onFailure();
                        }
                    });
                } else {
                    callback.onFailure();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure();
            }
        });
    } // End getJob()

    public static void updateJobName(String clientID, String jobID, String name) {
        fetchClientByID(clientID, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot clientSnapshot) {
                if (clientSnapshot.exists() && clientSnapshot.getChildrenCount() > 0) {
                    DataSnapshot clientData = clientSnapshot.getChildren().iterator().next();
                    fetchJobByID(clientData, jobID, new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot jobSnapshot) {
                            if (jobSnapshot.exists() && jobSnapshot.getChildrenCount() > 0) {
                                jobSnapshot.getChildren().iterator().next().getRef().child(Common.NAME).setValue(name);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    } // End updateJobName()

    public static void deleteJob(String clientID, String jobID) {
        fetchClientByID(clientID, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot clientSnapshot) {
                if (clientSnapshot.exists() && clientSnapshot.getChildrenCount() > 0) {
                    DataSnapshot clientData = clientSnapshot.getChildren().iterator().next();
                    fetchJobByID(clientData, jobID, new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot jobSnapshot) {
                            if (jobSnapshot.exists() && jobSnapshot.getChildrenCount() > 0) {

                                ClientJobModel item = jobSnapshot.getChildren().iterator().next().getValue(ClientJobModel.class);

                                jobSnapshot.getChildren().iterator().next().getRef().removeValue();

                                if (item != null) {
                                    updateGiveValue(clientID, item.getGive() * -1);
                                    updateTakeValue(clientID, item.getTake() * -1);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    } // End deleteJob()

    public static void getJobWithItems(String clientID, String jobID, JobAndItemsCallback callback) {
        fetchClientByID(clientID, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot clientSnapshot) {
                if (clientSnapshot.exists() && clientSnapshot.getChildrenCount() > 0) {
                    DataSnapshot clientData = clientSnapshot.getChildren().iterator().next();
                    fetchJobByID(clientData, jobID, new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot jobSnapshot) {
                            if (jobSnapshot.exists() && jobSnapshot.getChildrenCount() > 0) {
                                DataSnapshot jobData = jobSnapshot.getChildren().iterator().next();
                                ClientJobModel model = jobData.getValue(ClientJobModel.class);
                                getJobItemsList(jobData, new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot jobItemsSnapshot) {
                                        if (jobItemsSnapshot.exists()) {
                                            List<JobItem> jobItemsList = new ArrayList<>();
                                            for (DataSnapshot itemSnapshot : jobItemsSnapshot.getChildren()) {
                                                JobItem jobItem = itemSnapshot.getValue(JobItem.class);
                                                if (jobItem != null) {
                                                    jobItemsList.add(jobItem);
                                                }
                                            }
                                            callback.onSuccess(new Pair<>(model, jobItemsList));
                                        } else {
                                            callback.onFailure(new Pair<>(model, new ArrayList<>()));
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        callback.onFailure(new Pair<>(model, new ArrayList<>()));
                                    }
                                });
                            } else {
                                callback.onFailure(new Pair<>(null, new ArrayList<>()));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            callback.onFailure(new Pair<>(null, new ArrayList<>()));
                        }
                    });
                } else {
                    callback.onFailure(new Pair<>(null, new ArrayList<>()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(new Pair<>(null, new ArrayList<>()));
            }
        });
    }

    public static void findJobByID(DataSnapshot clientData, String jobID, ValueEventListener listener) {
        clientData.getRef().child(Common.JOBS).orderByChild(Common.ID).equalTo(jobID)
                .addListenerForSingleValueEvent(listener);
    } // End findJobByID()

    public static void fetchJobByID(DataSnapshot clientData, String jobID, ValueEventListener listener) {
        clientData.getRef().child(Common.JOBS).orderByChild(Common.ID).equalTo(jobID)
                .addValueEventListener(listener);
    } // End findJobByID()

    public static void findJobItemByID(DataSnapshot jobData, JobItem item, ValueEventListener listener) {
        jobData.getRef().child(Common.JOB_ITEMS).orderByChild(Common.ID).equalTo(item.getId())
                .addListenerForSingleValueEvent(listener);
    } // End findJobItemByID()

    public static void updateTakeValue(String clientID, String jobID, double value) {
        updateClientValue(clientID, jobID, Common.TAKE, value);
    } // End updateTakeValue()

    public static void updateTakeValue(String clientID, double value) {
        updateClientValue(clientID, Common.TAKE, value);
    } // End updateTakeValue()

    public static void updateGiveValue(String clientID, String jobID, double value) {
        updateClientValue(clientID, jobID, Common.GIVE, value);
    } // End updateGiveValue()

    public static void updateGiveValue(String clientID, double value) {
        updateClientValue(clientID, Common.GIVE, value);
    } // End updateGiveValue()

    public static void updateClientValue(String clientID, String jobID, String field, double value) {
        fetchClientByID(clientID, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    DataSnapshot clientSnapshot = snapshot.getChildren().iterator().next();
                    Double oldValue = clientSnapshot.child(field).getValue(Double.class);

                    findJobByID(clientSnapshot, jobID, new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            DataSnapshot jobSnapshot = snapshot.getChildren().iterator().next();
                            Double oldValue = jobSnapshot.child(field).getValue(Double.class);

                            if (oldValue == null) {
                                oldValue = 0.0;
                            }

                            double newValue = oldValue + value;
                            jobSnapshot.getRef().child(field).setValue(newValue);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    if (oldValue == null) {
                        oldValue = 0.0;
                    }

                    double newValue = oldValue + value;
                    clientSnapshot.getRef().child(field).setValue(newValue);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    } // End updateClientValue()

    public static void updateClientValue(String clientID, String field, double value) {
        fetchClientByID(clientID, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    DataSnapshot clientSnapshot = snapshot.getChildren().iterator().next();
                    Double oldValue = clientSnapshot.child(field).getValue(Double.class);

                    if (oldValue == null) {
                        oldValue = 0.0;
                    }

                    double newValue = oldValue + value;
                    clientSnapshot.getRef().child(field).setValue(newValue);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    } // End updateClientValue()

    // Item
    public static void getItem(String clientID, String jobID, String itemID, ItemCallback callback) {
        fetchClientByID(clientID, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot clientSnapshot) {
                if (clientSnapshot.exists() && clientSnapshot.getChildrenCount() > 0) {
                    DataSnapshot clientData = clientSnapshot.getChildren().iterator().next();
                    findJobByID(clientData, jobID, new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot jobSnapshot) {
                            if (jobSnapshot.exists() && jobSnapshot.getChildrenCount() > 0) {
                                DataSnapshot jobData = jobSnapshot.getChildren().iterator().next();
                                fetchJobItem(jobData, itemID, new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot jobItemsSnapshot) {
                                        if (jobItemsSnapshot.exists()) {
                                            JobItem jobItem = jobItemsSnapshot.getChildren().iterator().next().getValue(JobItem.class);
                                            callback.onItemFetched(jobItem);
                                        } else {
                                            callback.onFailure("No JobItems found");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        callback.onFailure("Error fetching job items: " + error.getMessage());
                                    }
                                });
                            } else {
                                callback.onFailure("Job not found");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            callback.onFailure("Error fetching job: " + error.getMessage());
                        }
                    });
                } else {
                    callback.onFailure("Client not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure("Error fetching client: " + error.getMessage());
            }
        });
    } // End getItem()

    private static void fetchJobItem(DataSnapshot jobData, String itemID, ValueEventListener listener) {
        jobData.getRef().child(Common.JOB_ITEMS).orderByChild(Common.ID).equalTo(itemID)
                .addValueEventListener(listener);
    } // End getJobItem()

    private static void getJobItem(DataSnapshot jobData, String itemID, ValueEventListener listener) {
        jobData.getRef().child(Common.JOB_ITEMS).orderByChild(Common.ID).equalTo(itemID)
                .addListenerForSingleValueEvent(listener);
    } // End getJobItem()

    public static void editItem(String clientID, String jobID, String itemID, JobItem item) {
        fetchClientByID(clientID, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot clientSnapshot) {
                if (clientSnapshot.exists() && clientSnapshot.getChildrenCount() > 0) {
                    DataSnapshot clientData = clientSnapshot.getChildren().iterator().next();
                    findJobByID(clientData, jobID, new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot jobSnapshot) {
                            if (jobSnapshot.exists() && jobSnapshot.getChildrenCount() > 0) {
                                DataSnapshot jobData = jobSnapshot.getChildren().iterator().next();
                                getJobItem(jobData, itemID, new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot jobItemsSnapshot) {
                                        if (jobItemsSnapshot.exists()) {
                                            JobItem jobItem = jobItemsSnapshot.getChildren().iterator().next().getValue(JobItem.class);

                                            if (jobItem != null) {
                                                if (item.getType() == JobItem.Type.GIVE) {
                                                    updateGiveValue(clientID, jobID, item.getBalance() - jobItem.getBalance());
                                                } else if (item.getType() == JobItem.Type.TAKE) {
                                                    updateTakeValue(clientID, jobID, item.getBalance() - jobItem.getBalance());
                                                }

                                                // Update the item in Firebase
                                                jobItemsSnapshot.getChildren().iterator().next().getRef().setValue(item);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Error handling
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Error handling
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Error handling
            }
        });
    } // End editItem()

    public static void deleteItem(String clientID, String jobID, String itemID) {
        fetchClientByID(clientID, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot clientSnapshot) {
                if (clientSnapshot.exists() && clientSnapshot.getChildrenCount() > 0) {
                    DataSnapshot clientData = clientSnapshot.getChildren().iterator().next();
                    findJobByID(clientData, jobID, new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot jobSnapshot) {
                            if (jobSnapshot.exists() && jobSnapshot.getChildrenCount() > 0) {
                                DataSnapshot jobData = jobSnapshot.getChildren().iterator().next();
                                getJobItem(jobData, itemID, new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot jobItemsSnapshot) {
                                        if (jobItemsSnapshot.exists()) {
                                            JobItem item = jobItemsSnapshot.getChildren().iterator().next().getValue(JobItem.class);

                                            // Remove the item from Firebase
                                            jobItemsSnapshot.getChildren().iterator().next().getRef().removeValue();

                                            // Update the balance after deletion
                                            if (item != null) {
                                                if (item.getType() == JobItem.Type.GIVE) {
                                                    updateGiveValue(clientID, jobID, item.getBalance() * -1);
                                                } else if (item.getType() == JobItem.Type.TAKE) {
                                                    updateTakeValue(clientID, jobID, item.getBalance() * -1);
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Error handling
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Error handling
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Error handling
            }
        });
    } // End deleteItem()

    private static void getJobItemsList(DataSnapshot jobData, ValueEventListener listener) {
        jobData.getRef().child(Common.JOB_ITEMS).addListenerForSingleValueEvent(listener);
    } // End getJobItemsList()

    public static void queryClientById(String id, ValueEventListener valueEventListener) {
        Query clientQuery = Firebase.getClients().orderByChild(Common.ID).equalTo(id);
        clientQuery.addListenerForSingleValueEvent(valueEventListener);
    } // End queryClientById()


    public static FirebaseAuth getFirebaseAuth() {
        return FirebaseAuth.getInstance();
    } // End of getFirebaseAuth()

    public static FirebaseUser getCurrentUser() {
        return getFirebaseAuth().getCurrentUser();
    } // End of getCurrentUser()

    public static String getPhoneNumber() {
        return getCurrentUser().getPhoneNumber();
    }


    public interface ClientCallback {
        void onClientFetched(DatabaseReference reference, Client Client);

        void onError(DatabaseError error);
    }

    public interface ClientsCallback {
        void onClientFetched(List<Client> clients);

        void onError(DatabaseError error);
    }

    public interface ClientJobsCallback {
        void onClientFetched(Pair<Client, List<ClientJobModel>> clientJobs);

        void onError(DatabaseError error);
    }

    public interface JobCallback {
        void onJobFetched(ClientJobModel jobModel);

        void onFailure();
    }

    public interface JobAndItemsCallback {
        void onSuccess(Pair<ClientJobModel, List<JobItem>> pair);

        void onFailure(Pair<ClientJobModel, List<JobItem>> pair);
    }

    public interface ItemCallback {
        void onItemFetched(JobItem jobItem);

        void onFailure(String error);
    }

}
