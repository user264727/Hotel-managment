package com.example.hotelmanagement.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmanagement.EventEditActivity;
import com.example.hotelmanagement.Models.Event;
import com.example.hotelmanagement.Models.Food;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.UpdateRoomActivity;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private Context mContext;
    private List<Event> arrayList;

    public EventAdapter(Context mContext, ArrayList<Event> data) {
        this.mContext = mContext;
        this.arrayList = data;

    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.event, parent, false);
        return new EventViewHolder(view, mContext, arrayList);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventViewHolder ViewHolder, final int i) {

        ViewHolder.eventName.setText(arrayList.get(i).getEventName());
        ViewHolder.dateTime.setText(arrayList.get(i).getEventDate()+"\t\t"+arrayList.get(i).getStartTime()+" to "+arrayList.get(i).getEndTime());
        ViewHolder.note.setText(arrayList.get(i).getNote());
        ViewHolder.location.setText("at "+arrayList.get(i).getLocation());

        ViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EventEditActivity.class);
                intent.putExtra("eventObject", arrayList.get(i));
                mContext.startActivity(intent);
            }
        });

    }
    @Override
    public int getItemCount() {

        return arrayList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder{

        TextView eventName,dateTime,note,location;
        public EventViewHolder(@NonNull View itemView, final Context context, final List<Event> item) {
            super(itemView);

            eventName = itemView.findViewById(R.id.eventName);
            dateTime = itemView.findViewById(R.id.dateTime);
            note = itemView.findViewById(R.id.note);
            location = itemView.findViewById(R.id.location);
        }
    }
}
