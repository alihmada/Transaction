package com.ali.transaction.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
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
import com.ali.transaction.Models.Item;
import com.ali.transaction.R;

import java.util.ArrayList;
import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> implements Filterable {
    static List<Item> filteredItems;
    private final ViewOnClickListener viewOnClickListener;
    private final List<Item> items;


    public ItemsAdapter(List<Item> items, ViewOnClickListener viewOnClickListener) {
        this.viewOnClickListener = viewOnClickListener;
        filteredItems = new ArrayList<>(items);
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
        return new ViewHolder(view, viewOnClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.reason.setText(filteredItems.get(position).getReason());
        holder.date.setText(DateAndTime.getDate(holder.itemView.getContext(), filteredItems.get(position).getDate()));
        holder.balance.setText(String.format("%s ج.م", Calculation.formatNumberWithCommas(filteredItems.get(position).getBalance())));
        if (filteredItems.get(position).getType() == Item.Type.TAKE) {
            holder.imageView.setImageResource(R.drawable.arrow_downward);
            holder.balance.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
        } else {
            holder.imageView.setImageResource(R.drawable.arrow_upward);
            holder.balance.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
        }
        Animation.startAnimation(holder.itemView);
    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString().toLowerCase();

                List<Item> filteredList = new ArrayList<>();

                for (Item item : items) {
                    if (item.getReason().toLowerCase().contains(query)
                            || String.valueOf(item.getBalance()).toLowerCase().contains(query)
                            || item.getDate().toLowerCase().contains(query)) {
                        filteredList.add(item);
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
                    if (!resultList.isEmpty() && resultList.get(0) instanceof Item) {
                        @SuppressWarnings("unchecked")
                        List<Item> filteredList = (List<Item>) resultList;

                        // Calculate the differences between the previous and new filtered lists
                        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(filteredItems, filteredList));

                        // Update the filtered customers list
                        filteredItems.clear();
                        filteredItems.addAll(filteredList);

                        // Dispatch the specific change events to the adapter
                        diffResult.dispatchUpdatesTo(ItemsAdapter.this);
                    }
                }
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView date, reason, balance;

        public ViewHolder(@NonNull View itemView, ViewOnClickListener viewOnClickListener) {
            super(itemView);

            imageView = itemView.findViewById(R.id.type);
            date = itemView.findViewById(R.id.date);
            reason = itemView.findViewById(R.id.reason);
            balance = itemView.findViewById(R.id.balance);

            itemView.setOnClickListener(v -> viewOnClickListener.onClickListener(filteredItems.get(getBindingAdapterPosition()).getId()));
        }
    }
}