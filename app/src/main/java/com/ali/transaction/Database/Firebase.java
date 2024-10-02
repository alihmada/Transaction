package com.ali.transaction.Database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.ali.transaction.Classes.Common;
import com.ali.transaction.Classes.UniqueIdGenerator;
import com.ali.transaction.Models.Customer;
import com.ali.transaction.Models.Item;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Firebase {

    public static FirebaseDatabase getInstance() {
        return FirebaseDatabase.getInstance();
    } // End getInstance()

    public static DatabaseReference getPersons() {
        return getInstance().getReference(Common.getROOT());
    } // End getRoot()

    public static void addPerson(Customer person) {
        getPersons().orderByChild("id").equalTo(person.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) try {
                    getPersons().push().setValue(person);
                } catch (Exception ignored) {
                }
                else {
                    person.setId(UniqueIdGenerator.generateUniqueId());
                    addPerson(person);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    } // End addPerson()

    public static void addItem(String id, Item item) {
        getPersons().orderByChild(Common.ID).equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                        snapshot.getChildren().iterator().next().getRef().orderByChild(Common.ID).equalTo(item.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    dataSnapshot.getRef().child(Common.DATABASE_NAME).push().setValue(item);
                                } else {
                                    item.setId(UniqueIdGenerator.generateUniqueId());
                                    addItem(id, item);
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

    public static void updateTakeValue(String id, double value) {
        updatePersonValue(id, Common.TAKE, value);
    } // End updateTakeValue()

    public static void updateGiveValue(String id, double value) {
        updatePersonValue(id, Common.GIVE, value);
    } // End updateGiveValue()

    public static void updatePersonValue(String id, String field, double value) {
        getPersons().orderByChild(Common.ID).equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    // Get the first matching person and update the specified field
                    DataSnapshot personSnapshot = snapshot.getChildren().iterator().next();
                    Double oldValue = personSnapshot.child(field).getValue(Double.class);

                    // Safely handle the case where oldValue is null
                    if (oldValue == null) {
                        oldValue = 0.0; // Default value if field doesn't exist
                    }

                    // Perform the update (e.g., add value to oldValue, or just set value)
                    double newValue = oldValue + value; // Example: adding the new value to the old one
                    personSnapshot.getRef().child(field).setValue(newValue);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Log the error message for debugging
                Log.e("FirebaseError", "Error updating " + field + ": " + error.getMessage());
            }
        });
    } // End updatePersonValue()

    public static void queryPersonAndItem(String parentID, String childID, ValueEventListener valueEventListener) {
        // Query to get the parent person by ID
        Query personQuery = Firebase.getPersons().orderByChild(Common.ID).equalTo(parentID);
        personQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    DataSnapshot personSnapshot = dataSnapshot.getChildren().iterator().next();

                    // Query the associated item using childID
                    Query itemQuery = personSnapshot.getRef().child(Common.DATABASE_NAME).orderByChild(Common.ID).equalTo(childID);
                    itemQuery.addListenerForSingleValueEvent(valueEventListener);
                } else {
                    Log.e("FirebaseError", "Parent with ID: " + parentID + " not found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Error querying person: " + databaseError.getMessage());
            }
        });
    } // End queryPersonAndItem()

    public static void editItem(String parentID, String childID, Item item) {
        queryPersonAndItem(parentID, childID, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot itemSnapshot) {
                if (itemSnapshot.exists() && itemSnapshot.getChildrenCount() > 0) {
                    // Get the item
                    Item oldItem = itemSnapshot.getChildren().iterator().next().getValue(Item.class);

                    if (oldItem != null && item.getType() == Item.Type.GIVE) {
                        updateGiveValue(parentID, item.getBalance() - oldItem.getBalance());
                    } else if (oldItem != null && item.getType() == Item.Type.TAKE) {
                        updateTakeValue(parentID, item.getBalance() - oldItem.getBalance());
                    }

                    // Set the new value for the item
                    itemSnapshot.getChildren().iterator().next().getRef().setValue(item);
                } else {
                    Log.e("FirebaseError", "Item with ID: " + childID + " not found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Error editing item: " + databaseError.getMessage());
            }
        });
    } // End editItem()

    public static void deleteItem(String parentID, String childID) {
        queryPersonAndItem(parentID, childID, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot itemSnapshot) {
                if (itemSnapshot.exists() && itemSnapshot.getChildrenCount() > 0) {
                    // Get the item
                    Item item = itemSnapshot.getChildren().iterator().next().getValue(Item.class);

                    // Remove the item
                    itemSnapshot.getChildren().iterator().next().getRef().removeValue();

                    // Update the balance after deletion if item is not null
                    if (item != null) {
                        if (item.getType() == Item.Type.GIVE) {
                            updateGiveValue(parentID, item.getBalance() * -1);  // Revert GIVE balance
                        } else {
                            updateTakeValue(parentID, item.getBalance() * -1);  // Revert TAKE balance
                        }
                    }
                } else {
                    Log.e("FirebaseError", "Item with ID: " + childID + " not found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Error deleting item: " + databaseError.getMessage());
            }
        });
    } // End deleteItem()

    public static void queryCustomerById(String id, ValueEventListener valueEventListener) {
        Query personQuery = Firebase.getPersons().orderByChild(Common.ID).equalTo(id);
        personQuery.addListenerForSingleValueEvent(valueEventListener);
    } // End queryCustomerById()

    public static void updateCustomerName(String id, String name) {
        queryCustomerById(id, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot personSnapshot = dataSnapshot.getChildren().iterator().next();
                    personSnapshot.getRef().child(Common.NAME).setValue(name);
                } else {
                    Log.e("FirebaseError", "Customer with ID: " + id + " not found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Error querying customer: " + databaseError.getMessage());
            }
        });
    } // End updateCustomerName()

    public static void deleteCustomer(String id) {
        queryCustomerById(id, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot personSnapshot = dataSnapshot.getChildren().iterator().next();
                    personSnapshot.getRef().removeValue();
                } else {
                    Log.e("FirebaseError", "Customer with ID: " + id + " not found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Error querying customer: " + databaseError.getMessage());
            }
        });
    } // End deleteCustomer()




    public static FirebaseAuth getFirebaseAuth() {
        return FirebaseAuth.getInstance();
    } // End of getFirebaseAuth()

    public static FirebaseUser getCurrentUser() {
        return getFirebaseAuth().getCurrentUser();
    } // End of getCurrentUser()

    public static String getPhoneNumber() {
        return getCurrentUser().getPhoneNumber();
    }
}
