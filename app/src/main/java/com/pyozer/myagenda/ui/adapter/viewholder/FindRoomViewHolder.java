package com.pyozer.myagenda.ui.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.pyozer.myagenda.R;

public class FindRoomViewHolder extends RecyclerView.ViewHolder {

    public TextView roomName;
    public TextView roomEndAvailable;

    public FindRoomViewHolder(View view) {
        super(view);

        roomName = view.findViewById(R.id.room_find_row_name);
        roomEndAvailable = view.findViewById(R.id.room_find_row_endtime);
    }
}