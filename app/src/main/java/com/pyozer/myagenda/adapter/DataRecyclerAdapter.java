package com.pyozer.myagenda.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pyozer.myagenda.R;
import com.pyozer.myagenda.model.DataRecycler;
import com.pyozer.myagenda.ui.viewholder.DataRecyclerViewHolder;

import java.util.List;

public class DataRecyclerAdapter extends RecyclerView.Adapter<DataRecyclerViewHolder> {

    private List<DataRecycler> mDataList;

    public DataRecyclerAdapter(List<DataRecycler> moviesList) {
        this.mDataList = moviesList;
    }

    @Override
    public DataRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_row, parent, false);

        return new DataRecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DataRecyclerViewHolder holder, int position) {
        holder.bind(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }
}