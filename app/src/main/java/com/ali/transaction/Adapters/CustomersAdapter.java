package com.ali.transaction.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.ali.transaction.Classes.Animation;
import com.ali.transaction.Classes.DiffCallback;
import com.ali.transaction.Models.Customer;
import com.ali.transaction.R;
import com.ali.transaction.Interfaces.ViewOnClickListener;

import java.util.ArrayList;
import java.util.List;

public class CustomersAdapter extends RecyclerView.Adapter<CustomersAdapter.ViewHolder> implements Filterable {
    static List<Customer> filteredCustomers;
    private final ViewOnClickListener viewOnClickListener;
    private final List<Customer> Customers;


    public CustomersAdapter(List<Customer> customers, ViewOnClickListener viewOnClickListener) {
        this.viewOnClickListener = viewOnClickListener;
        filteredCustomers = new ArrayList<>(customers);
        this.Customers = customers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_row, parent, false);
        return new ViewHolder(view, viewOnClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.character.setText(String.valueOf(filteredCustomers.get(position).getName().charAt(0)));
        holder.name.setText(filteredCustomers.get(position).getName());

        Animation.startAnimation(holder.itemView);
    }

    @Override
    public int getItemCount() {
        return filteredCustomers.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString().toLowerCase();

                List<Customer> filteredList = new ArrayList<>();

                for (Customer person : Customers) {
                    if (person.getName().toLowerCase().contains(query)) {
                        filteredList.add(person);
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.values instanceof List) {
                    List<?> resultList = (List<?>) results.values;
                    if (!resultList.isEmpty() && resultList.get(0) instanceof Customer) {
                        @SuppressWarnings("unchecked")
                        List<Customer> filteredList = (List<Customer>) resultList;

                        // Calculate the differences between the previous and new filtered lists
                        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(filteredCustomers, filteredList));

                        // Update the filtered customers list
                        filteredCustomers.clear();
                        filteredCustomers.addAll(filteredList);

                        // Dispatch the specific change events to the adapter
                        diffResult.dispatchUpdatesTo(CustomersAdapter.this);
                    }
                }
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView character, name;

        public ViewHolder(@NonNull View itemView, ViewOnClickListener viewOnClickListener) {
            super(itemView);

            character = itemView.findViewById(R.id.character);
            name = itemView.findViewById(R.id.name);

            itemView.setOnClickListener(v -> viewOnClickListener.onClickListener(filteredCustomers.get(getBindingAdapterPosition()).getId()));
        }
    }
}