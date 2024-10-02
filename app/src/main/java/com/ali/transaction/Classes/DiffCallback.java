package com.ali.transaction.Classes;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class DiffCallback extends DiffUtil.Callback {
    private final List<?> oldList;
    private final List<?> newList;

    public DiffCallback(List<?> oldList, List<?> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Object oldString = oldList.get(oldItemPosition);
        Object newString = newList.get(newItemPosition);
        // Compare unique identifiers of the items (e.g., String IDs)
        return oldString.equals(newString);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Object oldString = oldList.get(oldItemPosition);
        Object newString = newList.get(newItemPosition);
        // Compare the content of the items
        return oldString.equals(newString);
    }
}
