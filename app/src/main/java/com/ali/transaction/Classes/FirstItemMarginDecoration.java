package com.ali.transaction.Classes;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FirstItemMarginDecoration extends RecyclerView.ItemDecoration {
    private final int marginTop;

    public FirstItemMarginDecoration(int marginTop) {
        this.marginTop = marginTop;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = marginTop;
        }
    }
}
