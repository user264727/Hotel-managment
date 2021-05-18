package com.example.hotelmanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    LinearLayout food,customer,event,booking,rooms;
    ImageView logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        food = findViewById(R.id.foods_card);
        customer = findViewById(R.id.customer_card);
        event = findViewById(R.id.event_card);
        booking = findViewById(R.id.booking_card);
        rooms = findViewById(R.id.rooms_card);
        logout = findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
             @Override
            public void onClick(View v) {
                 FirebaseAuth auth = FirebaseAuth.getInstance();
                 auth.signOut();
                 startActivity(new Intent(HomeActivity.this,LoginActivity.class));
            }
        });
        rooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,RoomsViewActivity.class));
            }
        });
        event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,ViewEventActivity.class));
            }
        });
        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,FoodsViewActivity.class));
            }
        });

        booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,BookingViewActivity.class));
            }
        });
        customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,CustomerAddActivity.class));
            }
        });
    }
}