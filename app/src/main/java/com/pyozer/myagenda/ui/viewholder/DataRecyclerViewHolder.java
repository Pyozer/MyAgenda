package com.pyozer.myagenda.ui.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pyozer.myagenda.R;
import com.pyozer.myagenda.model.DataRecycler;

public class DataRecyclerViewHolder extends RecyclerView.ViewHolder {

    public TextView title;
    public ImageView icon;

    public DataRecyclerViewHolder(View view) {
        super(view);
        icon = view.findViewById(R.id.icon);
        title = view.findViewById(R.id.title);
    }

    public void bind(@NonNull DataRecycler dataRecycler) {
        title.setText(dataRecycler.getTitle());
        icon.setImageResource(dataRecycler.getIcon());
    }
}