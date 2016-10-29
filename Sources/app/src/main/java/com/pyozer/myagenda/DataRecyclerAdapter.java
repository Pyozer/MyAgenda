package com.pyozer.myagenda;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class DataRecyclerAdapter extends RecyclerView.Adapter<DataRecyclerAdapter.MyViewHolder> {

    private List<DataRecycler> dataList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView icon;

        public MyViewHolder(View view) {
            super(view);
            icon = (ImageView) view.findViewById(R.id.icon);
            title = (TextView) view.findViewById(R.id.title);
        }
    }


    public DataRecyclerAdapter(List<DataRecycler> moviesList) {
        this.dataList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DataRecycler data = dataList.get(position);
        holder.title.setText(data.getTitle());
        holder.icon.setImageResource(data.getIcon());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}