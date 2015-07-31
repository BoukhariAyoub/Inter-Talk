package com.aboukhari.intertalking.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by aboukhari on 31/07/2015.
 */
public class MyLayoutManager extends LinearLayoutManager {


    public MyLayoutManager(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return null;
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return true;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);

    }
}
