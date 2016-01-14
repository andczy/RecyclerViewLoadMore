package com.example.czy.recyclerviewloadmore.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.czy.recyclerviewloadmore.R;
import com.example.czy.recyclerviewloadmore.model.IntModel;

/**
 * Created by czy on 2016/1/14.
 */
public class IntAdapter extends LoadMoreAdapter<RecyclerView.ViewHolder , IntModel> {
    @Override
    public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());
        int pad = parent.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        textView.setPadding(pad , pad , pad , pad );
        return new MyHolder(textView);
    }

    @Override
    public void onBindHolder(RecyclerView.ViewHolder holder, int position) {
        IntModel model = getItem(position);
        TextView textView = (TextView) holder.itemView;
        textView.setText("this is int adapter = " + model.id );
    }

    @Override
    public int getViewType(int position) {
        return 0;
    }
}
