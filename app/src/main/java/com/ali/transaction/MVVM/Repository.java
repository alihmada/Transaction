package com.ali.transaction.MVVM;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.ali.transaction.Classes.Common;
import com.ali.transaction.Database.Firebase;
import com.ali.transaction.Models.Customer;
import com.ali.transaction.Models.Item;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Repository {

    private static Repository instance;

    public static Repository getInstance() {
        if (instance == null) instance = new Repository();
        return instance;
    }

    public MutableLiveData<List<Customer>> getCustomers() {
        MutableLiveData<List<Customer>> mutableLiveData = new MutableLiveData<>();

        // Query to get the list of Persons from Firebase
        Firebase.getPersons().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Customer> persons = new ArrayList<>();

                // Check if data exists
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        try {
                            Customer person = snapshot.getValue(Customer.class);
                            if (person != null) {
                                persons.add(person);
                            }
                        } catch (Exception e) {
                            // Log error if there is an issue parsing the Customer object
                            Log.e("FirebaseError", "Error parsing Customer", e);
                        }
                    }
                }

                // Even if no data, return an empty list instead of null
                mutableLiveData.setValue(persons);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log the database error
                Log.e("FirebaseError", "Firebase operation cancelled: " + databaseError.getMessage());
            }
        });

        return mutableLiveData;
    }

    public MutableLiveData<Customer> getCustomer(String id) {
        MutableLiveData<Customer> mutableLiveData = new MutableLiveData<>();
        fetchCustomerById(id, new CustomerCallback() {
            @Override
            public void onCustomerFetched(DatabaseReference reference, Customer customer) {
                mutableLiveData.setValue(customer);
            }

            @Override
            public void onError(DatabaseError error) {
                mutableLiveData.setValue(null);
            }
        });
        return mutableLiveData;
    }

    private void fetchCustomerById(String id, CustomerCallback callback) {
        Query personQuery = Firebase.getPersons().orderByChild(Common.ID).equalTo(id);
        personQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Customer customer = dataSnapshot.getChildren().iterator().next().getValue(Customer.class);
                    callback.onCustomerFetched(personQuery.getRef(), customer);
                } else {
                    callback.onCustomerFetched(null, null);  // Customer not found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError);  // Handle error case
            }
        });
    }

    public MutableLiveData<Pair<Customer, List<Item>>> getCustomersWithItems(String id) {
        MutableLiveData<Pair<Customer, List<Item>>> mutableLiveData = new MutableLiveData<>();

        // Query to fetch the Customer data by ID
        Query personQuery = Firebase.getPersons().orderByChild(Common.ID).equalTo(id);
        personQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if data exists and is valid
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    DataSnapshot personSnapshot = dataSnapshot.getChildren().iterator().next();
                    Customer person = personSnapshot.getValue(Customer.class);

                    if (person != null) {
                        // After retrieving Customer, query for the associated items
                        fetchPersonItems(personSnapshot.getRef(), person, mutableLiveData);
                    } else {
                        mutableLiveData.setValue(null);  // Handle null person case
                    }
                } else {
                    mutableLiveData.setValue(null);  // No data found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mutableLiveData.setValue(null);  // Handle error case
            }
        });

        return mutableLiveData;
    }

    private void fetchPersonItems(DatabaseReference personRef, Customer person, MutableLiveData<Pair<Customer, List<Item>>> mutableLiveData) {
        // Query to fetch the items associated with the person
        personRef.child(Common.DATABASE_NAME).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot itemSnapshot) {
                List<Item> items = new ArrayList<>();

                // Retrieve all items associated with the person
                for (DataSnapshot snapshot : itemSnapshot.getChildren()) {
                    Item item = snapshot.getValue(Item.class);
                    if (item != null) {
                        items.add(item);
                    }
                }

                // Set the Pair<Customer, List<Item>> value to LiveData
                mutableLiveData.setValue(new Pair<>(person, items));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mutableLiveData.setValue(null);  // Handle error case
            }
        });
    }

    public MutableLiveData<Item> getItem(String parentID, String childID) {
        MutableLiveData<Item> mutableLiveData = new MutableLiveData<>();

        Query personQuery = Firebase.getPersons().orderByChild(Common.ID).equalTo(parentID);
        personQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if data exists and is valid
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    DataSnapshot personSnapshot = dataSnapshot.getChildren().iterator().next();

                    // After retrieving Customer, query for the associated items
                    personSnapshot.getRef().child(Common.DATABASE_NAME).orderByChild(Common.ID).equalTo(childID)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot itemSnapshot) {
                                    Item item = null;

                                    if (itemSnapshot.exists() && itemSnapshot.getChildrenCount() > 0) {
                                        // Get the first matching person and update the specified field
                                        item = itemSnapshot.getChildren().iterator().next().getValue(Item.class);
                                    }

                                    mutableLiveData.setValue(item);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    mutableLiveData.setValue(null);  // Handle error case
                                }
                            });
                } else {
                    mutableLiveData.setValue(null);  // Handle null person case
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mutableLiveData.setValue(null);  // Handle error case
            }
        });

        return mutableLiveData;
    }

    interface CustomerCallback {
        void onCustomerFetched(DatabaseReference reference, Customer customer);

        void onError(DatabaseError error);
    }
}
