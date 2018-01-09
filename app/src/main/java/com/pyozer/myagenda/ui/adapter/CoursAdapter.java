package com.pyozer.myagenda.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pyozer.myagenda.R;
import com.pyozer.myagenda.model.Cours;
import com.pyozer.myagenda.ui.adapter.viewholder.CoursViewHolder;

import java.util.List;

public class CoursAdapter extends RecyclerView.Adapter<CoursViewHolder> {

    private List<Cours> mDataList;

    public CoursAdapter(List<Cours> dataList) {
        this.mDataList = dataList;
    }

    @Override
    public CoursViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cours_row, parent, false);

        return new CoursViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CoursViewHolder holder, int position) {
        holder.bind(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }
}