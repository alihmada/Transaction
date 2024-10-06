package com.ali.transaction.Adapters;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.ali.transaction.Classes.Animation;
import com.ali.transaction.Classes.Calculation;
import com.ali.transaction.Classes.DateAndTime;
import com.ali.transaction.Classes.DiffCallback;
import com.ali.transaction.Interfaces.ViewOnClickListener;
import com.ali.transaction.Models.ClientJobModel;
import com.ali.transaction.R;

import java.util.ArrayList;
import java.util.List;

public class JobsAdapter extends RecyclerView.Adapter<JobsAdapter.ViewHolder> implements Filterable {
    static List<ClientJobModel> filteredJobs;
    private final ViewOnClickListener viewOnClickListener;
    private final List<ClientJobModel> jobs;


    public JobsAdapter(List<ClientJobModel> jobs, ViewOnClickListener viewOnClickListener) {
        this.viewOnClickListener = viewOnClickListener;
        filteredJobs = new ArrayList<>(jobs);
        this.jobs = jobs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_row, parent, false);
        return new ViewHolder(view, viewOnClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.character.setText(filteredJobs.get(position).getName().substring(0, 1));
        holder.name.setText(filteredJobs.get(position).getName());
        holder.date.setText(DateAndTime.getDate(holder.itemView.getContext(), filteredJobs.get(position).getDate()));
        Pair<String, Boolean> balance = Calculation.getBalance(filteredJobs.get(position).getTake(), filteredJobs.get(position).getGive());
        holder.balance.setText(String.format("%s ج.م", balance.first));
        if (balance.second)
            holder.balance.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
        else
            holder.balance.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));

        Animation.startAnimation(holder.itemView);
    }

    @Override
    public int getItemCount() {
        return filteredJobs.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString().toLowerCase();

                List<ClientJobModel> filteredList = new ArrayList<>();

                for (ClientJobModel job : jobs) {
                    if (job.getName().toLowerCase().contains(query)
                            || job.getDate().toLowerCase().contains(query)
                            || Calculation.getBalance(job.getTake(), job.getGive()).first.toLowerCase().contains(query)) {
                        filteredList.add(job);
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
                    if (!resultList.isEmpty() && resultList.get(0) instanceof ClientJobModel) {
                        @SuppressWarnings("unchecked") List<ClientJobModel> filteredList = (List<ClientJobModel>) resultList;

                        // Calculate the differences between the previous and new filtered lists
                        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(filteredJobs, filteredList));

                        // Update the filtered jobs list
                        filteredJobs.clear();
                        filteredJobs.addAll(filteredList);

                        // Dispatch the specific change events to the adapter
                        diffResult.dispatchUpdatesTo(JobsAdapter.this);
                    }
                }
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView character, date, name, balance;

        public ViewHolder(@NonNull View itemView, ViewOnClickListener viewOnClickListener) {
            super(itemView);

            character = itemView.findViewById(R.id.character);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            balance = itemView.findViewById(R.id.balance);

            itemView.setOnClickListener(v -> viewOnClickListener.onClickListener(filteredJobs.get(getBindingAdapterPosition()).getId()));
        }
    }
}