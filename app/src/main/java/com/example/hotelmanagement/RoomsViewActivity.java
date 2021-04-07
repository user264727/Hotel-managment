package com.example.hotelmanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.hotelmanagement.Adapters.RoomAdapter;
import com.example.hotelmanagement.Models.Room;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RoomsViewActivity extends AppCompatActivity {

    RecyclerView rooms_list;
    LinearLayout add;
    RoomAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms_view);

        add = findViewById(R.id.add_rooms);
        rooms_list = findViewById(R.id.rooms_list);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RoomsViewActivity.this,AddRoomActivity.class));
            }
        });
        getData();
    }

    private void getData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Rooms");

        rooms_list.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<Room> options
                = new FirebaseRecyclerOptions.Builder<Room>()
                .setQuery(reference, Room.class)
                .build();

        adapter = new RoomAdapter(options,RoomsViewActivity.this);
        rooms_list.setAdapter(adapter);
    }

    @Override protected void onStart()
    {
        super.onStart();
        adapter.startListening();
    }

    @Override protected void onStop()
    {
        super.onStop();
        adapter.stopListening();
    }
}