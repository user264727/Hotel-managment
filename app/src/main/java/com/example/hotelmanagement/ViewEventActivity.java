package com.example.hotelmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hotelmanagement.Adapters.EventAdapter;
import com.example.hotelmanagement.Adapters.FoodAdapter;
import com.example.hotelmanagement.Models.Event;
import com.example.hotelmanagement.Models.Food;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ViewEventActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView add;
    LinearLayout loading;
    ArrayList<Event> events = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        recyclerView = findViewById(R.id.events);
        add = findViewById(R.id.add);
        loading = findViewById(R.id.loading);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = db.getReference();
        databaseReference.child("Events").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                events.clear();
                if(snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Event event = dataSnapshot.getValue(Event.class);
                        event.setId(dataSnapshot.getKey());
                        events.add(event);
                    }
                }else{
                    Toast.makeText(ViewEventActivity.this,"No data",Toast.LENGTH_SHORT).show();
                }
                loading.setVisibility(View.GONE);
                setData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewEventActivity.this,EventAddActivity.class));
            }
        });
    }

    private void setData() {
        EventAdapter myAdapter = new EventAdapter(this,events);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);
    }
}