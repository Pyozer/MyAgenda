package com.pyozer.myagenda.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pyozer.myagenda.R;
import com.pyozer.myagenda.model.RoomAvailable;
import com.pyozer.myagenda.ui.viewholder.FindRoomViewHolder;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class FindRoomAdapter extends RecyclerView.Adapter<FindRoomViewHolder> {

    private List<RoomAvailable> mDataList;
    private Context mContext;

    public FindRoomAdapter(Context context, List<RoomAvailable> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }

    @Override
    public FindRoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_find_row, parent, false);

        return new FindRoomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FindRoomViewHolder holder, int position) {
        RoomAvailable room = mDataList.get(position);

        holder.roomName.setText(room.getRoomName());

        SimpleDateFormat format4RoomEnd = new SimpleDateFormat("HH'h'mm", Locale.getDefault());

        String endAvailable = mContext.getString(R.string.available_until) + " " + format4RoomEnd.format(room.getDateEnd());

        holder.roomEndAvailable.setText(endAvailable);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }
}