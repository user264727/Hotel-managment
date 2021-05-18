package com.example.hotelmanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.hotelmanagement.Adapters.BookingAdapter;
import com.example.hotelmanagement.Adapters.FoodAdapter;
import com.example.hotelmanagement.Adapters.RoomAdapter;
import com.example.hotelmanagement.Models.Booking;
import com.example.hotelmanagement.Models.Food;
import com.example.hotelmanagement.Models.Room;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class BookingViewActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    BookingAdapter bookingAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_view);

        recyclerView = findViewById(R.id.booking_list);
//        ArrayList<Booking> bookings = new ArrayList<>();
//        bookings.add(new Booking());
//        bookings.add(new Booking());
//        bookings.add(new Booking());

        getData();
    }
//    void setData(ArrayList<Booking> list){
//        BookingAdapter myAdapter = new BookingAdapter(this,list);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(myAdapter);
//    }

    private void getData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Bookings");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<Booking> options
                = new FirebaseRecyclerOptions.Builder<Booking>()
                .setQuery(reference, Booking.class)
                .build();

        bookingAdapter = new BookingAdapter(options,BookingViewActivity.this);
        recyclerView.setAdapter(bookingAdapter);
    }

    @Override protected void onStart()
    {
        super.onStart();
        bookingAdapter.startListening();
    }

    @Override protected void onStop()
    {
        super.onStop();
        bookingAdapter.stopListening();
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.view_bookings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.view_bookings) {
            startActivity(new Intent(BookingViewActivity.this,BookingActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}