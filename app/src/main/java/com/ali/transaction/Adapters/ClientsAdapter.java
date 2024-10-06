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
import com.ali.transaction.Models.Client;
import com.ali.transaction.R;
import com.ali.transaction.Interfaces.ViewOnClickListener;

import java.util.ArrayList;
import java.util.List;

public class ClientsAdapter extends RecyclerView.Adapter<ClientsAdapter.ViewHolder> implements Filterable {
    static List<Client> filteredClients;
    private final ViewOnClickListener viewOnClickListener;
    private final List<Client> clients;


    public ClientsAdapter(List<Client> clients, ViewOnClickListener viewOnClickListener) {
        this.viewOnClickListener = viewOnClickListener;
        filteredClients = new ArrayList<>(clients);
        this.clients = clients;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.client_row, parent, false);
        return new ViewHolder(view, viewOnClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.character.setText(filteredClients.get(position).getName().substring(0, 1));
        holder.name.setText(filteredClients.get(position).getName());

        Animation.startAnimation(holder.itemView);
    }

    @Override
    public int getItemCount() {
        return filteredClients.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString().toLowerCase();

                List<Client> filteredList = new ArrayList<>();

                for (Client client : clients) {
                    if (client.getName().toLowerCase().contains(query)) {
                        filteredList.add(client);
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
                    if (!resultList.isEmpty() && resultList.get(0) instanceof Client) {
                        @SuppressWarnings("unchecked")
                        List<Client> filteredList = (List<Client>) resultList;

                        // Calculate the differences between the previous and new filtered lists
                        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(filteredClients, filteredList));

                        // Update the filtered clients list
                        filteredClients.clear();
                        filteredClients.addAll(filteredList);

                        // Dispatch the specific change events to the adapter
                        diffResult.dispatchUpdatesTo(ClientsAdapter.this);
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

            itemView.setOnClickListener(v -> viewOnClickListener.onClickListener(filteredClients.get(getBindingAdapterPosition()).getId()));
        }
    }
}