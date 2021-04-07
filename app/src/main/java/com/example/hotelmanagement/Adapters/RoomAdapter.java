package com.example.hotelmanagement.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmanagement.AddRoomActivity;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.Models.Room;
import com.example.hotelmanagement.UpdateRoomActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class RoomAdapter extends FirebaseRecyclerAdapter<
        Room, RoomAdapter.roomViewholder> {

    Context mContext;
    public RoomAdapter(@NonNull FirebaseRecyclerOptions<Room> options,Context mContext) {
        super(options);
        this.mContext = mContext;
    }

    @Override
    protected void
    onBindViewHolder(@NonNull roomViewholder holder,
                     int position, @NonNull Room model) {

        holder.room_number.setText(model.getRoomNo());
        holder.type.setText(model.getType());
        holder.floor.setText("Floor "+model.getFloor());
        holder.note.setText(model.getNote());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Rooms").child(model.getId());
                reference.removeValue();
                Toast.makeText(mContext,"Successfully data added",Toast.LENGTH_SHORT).show();
            }
        });
        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, UpdateRoomActivity.class);
                i.putExtra("room", model);
                mContext.startActivity(i);
            }
        });
    }

    @NonNull
    @Override
    public roomViewholder
    onCreateViewHolder(@NonNull ViewGroup parent,
                       int viewType) {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.room_item, parent, false);
        return new RoomAdapter.roomViewholder(view);
    }


    class roomViewholder
            extends RecyclerView.ViewHolder {
        TextView room_number, type, floor,note;
        ImageView delete,update;
        public roomViewholder(@NonNull View itemView) {
            super(itemView);

            room_number = itemView.findViewById(R.id.room_number);
            type = itemView.findViewById(R.id.type);
            floor = itemView.findViewById(R.id.floor);
            note = itemView.findViewById(R.id.note);
            delete = itemView.findViewById(R.id.delete);
            update = itemView.findViewById(R.id.update);
        }
    }
}
