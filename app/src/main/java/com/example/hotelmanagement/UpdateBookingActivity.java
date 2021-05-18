package com.example.hotelmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.hotelmanagement.Adapters.BookingFoodAdapter;
import com.example.hotelmanagement.Models.Booking;
import com.example.hotelmanagement.Models.Cart;
import com.example.hotelmanagement.Models.Customer;
import com.example.hotelmanagement.Models.Food;
import com.example.hotelmanagement.Models.Room;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UpdateBookingActivity extends AppCompatActivity {

    Spinner rooms,foods;
    RecyclerView recyclerView;
    HashMap<String, Room> roomsMap = new HashMap<>();
    HashMap<String, Food> foodMap = new HashMap<>();
    ArrayList<String> carts = new ArrayList<>();
    EditText quantity;
    ImageView add_food;
    BookingFoodAdapter adapter;
    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;
    TextView date,time,total_txt,customer;
    Button update;
    double total = 0;
    double roomPrice = 0;
    Booking booking;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_booking);

        Gson gson = new Gson();
        Type type = new TypeToken<Booking>() {}.getType();
        booking = gson.fromJson(getIntent().getStringExtra("booking_record"), type);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        rooms = findViewById(R.id.rooms);
        customer = findViewById(R.id.customers);
        foods = findViewById(R.id.foods);
        quantity = findViewById(R.id.quantity);
        recyclerView = findViewById(R.id.food_list);
        add_food = findViewById(R.id.add_food);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        update = findViewById(R.id.update);
        total_txt = findViewById(R.id.total);
        Calendar cal = Calendar.getInstance();
        customer.setText(booking.getCustomerName());
        date.setText(booking.getDate());
        time.setText(booking.getTime());
        setRooms();
        setFoods();
        setupRecycler();

        add_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!foods.getSelectedItem().equals("Select the food")){
                    if(!quantity.getText().toString().isEmpty()) {
                        if (!carts.contains(foods.getSelectedItem())) {
                            Cart cart = new Cart();
                            int qnt = Integer.parseInt(quantity.getText().toString());
                            Food food = foodMap.get(foods.getSelectedItem());
                            cart.setId(food.getId());
                            cart.setName(food.getName());
                            cart.setQuantity(qnt);
                            cart.setPrice(food.getPrice() * qnt);
                            adapter.getmData().add(cart);
                            carts.add(foods.getSelectedItem().toString());
                            adapter.notifyItemInserted(adapter.getmData().size() - 1);
                        } else {
                            int index = carts.indexOf(foods.getSelectedItem().toString());
                            int qnt = Integer.parseInt(quantity.getText().toString());
                            Food food = foodMap.get(foods.getSelectedItem());
                            adapter.getmData().get(index).setQuantity(qnt + adapter.getmData().get(index).getQuantity());
                            adapter.getmData().get(index).setPrice(food.getPrice() * adapter.getmData().get(index).getQuantity());
                            adapter.notifyItemChanged(index);
                        }
                        foods.setSelection(0);
                        quantity.setText("");
                        calculte();
                    }else{
                        Toast.makeText(UpdateBookingActivity.this,"Please insert a quantity",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(UpdateBookingActivity.this,"Please select a food",Toast.LENGTH_SHORT).show();
                }
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog = new TimePickerDialog(UpdateBookingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        int hours = hour;
                        int minutes = minute;
                        String timeSet = "";
                        if (hours > 12) {
                            hours -= 12;
                            timeSet = "PM";
                        } else if (hours == 0) {
                            hours += 12;
                            timeSet = "AM";
                        } else if (hours == 12){
                            timeSet = "PM";
                        }else{
                            timeSet = "AM";
                        }

                        String min = "";
                        if (minutes < 10)
                            min = "0" + minutes ;
                        else
                            min = String.valueOf(minutes);

                        // Append in a StringBuilder
                        String aTime = new StringBuilder().append(hours).append(':')
                                .append(min ).append(" ").append(timeSet).toString();
                        time.setText(aTime);
                    }
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(UpdateBookingActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date.setText( dayOfMonth + "/" + (month+1) + "/" + year);
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setTitle("Select Date");
                datePickerDialog.show();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rooms.getSelectedItemPosition() == 0 || date.getText().toString().isEmpty() || time.getText().toString().isEmpty()){
                    Toast.makeText(UpdateBookingActivity.this,"Please fill all",Toast.LENGTH_SHORT).show();
                }else{
                    ProgressDialog dialog = new ProgressDialog(UpdateBookingActivity.this);
                    dialog.setCancelable(false);
                    dialog.setMessage("Please wait...");
                    dialog.show();

                    Map bookingMap = new HashMap();
                    bookingMap.put("roomNo", rooms.getSelectedItem().toString());
                    bookingMap.put("roomId", roomsMap.get(rooms.getSelectedItem()).getId());
                    bookingMap.put("date", date.getText().toString());
                    bookingMap.put("time", time.getText().toString());
                    bookingMap.put("total", total);
                    bookingMap.put("carts", adapter.getmData());

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Bookings");
                    ref.child(booking.getId()).updateChildren(bookingMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dialog.dismiss();
                                    startActivity(new Intent(UpdateBookingActivity.this,BookingViewActivity.class));
                                    Toast.makeText(UpdateBookingActivity.this,"Successfully booking updated",Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog.dismiss();
                                    Toast.makeText(UpdateBookingActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    private void setupRecycler() {
        if(booking.getCarts() == null){
            ArrayList<Cart> cartList = new ArrayList<>();
            adapter = new BookingFoodAdapter(this,cartList);
        }else {
            for (int i = 0; i < booking.getCarts().size(); i++) {
                carts.add(booking.getCarts().get(i).getName());
            }
            adapter = new BookingFoodAdapter(this,booking.getCarts());
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setFoods() {
        DatabaseReference cus_reference  = FirebaseDatabase.getInstance().getReference("Foods");
        cus_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> foodList = new ArrayList<>();
                foodList.add("Select the food");
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Food food = dataSnapshot.getValue(Food.class);
                        foodMap.put(food.getName(),food);
                        foodList.add(food.getName());
                    }
                    ArrayAdapter foodsAdapter = new ArrayAdapter(UpdateBookingActivity.this,android.R.layout.simple_spinner_item, foodList);
                    foodsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    foods.setAdapter(foodsAdapter);
                }else{
                    Toast.makeText(UpdateBookingActivity.this,"No Foods",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void setRooms() {
        DatabaseReference cus_reference  = FirebaseDatabase.getInstance().getReference("Rooms");
        cus_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> roomList = new ArrayList<>();
                int i = 0;
                int index = 0;
                roomList.add("Select the room");
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Room room = dataSnapshot.getValue(Room.class);
                        roomsMap.put(room.getRoomNo(),room);
                        roomList.add(room.getRoomNo());
                        i++;
                        if(room.getRoomNo().equals(booking.getRoomNo())){
                            index = i;
                        }
                    }
                    ArrayAdapter roomAdapter = new ArrayAdapter(UpdateBookingActivity.this,android.R.layout.simple_spinner_item, roomList);
                    roomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    rooms.setAdapter(roomAdapter);
                    rooms.setSelection(index);
                    rooms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            System.out.println(rooms.getSelectedItem());
                            if(rooms.getSelectedItemPosition() != 0) {
                                System.out.println(roomsMap.get(rooms.getSelectedItem()).getPrice());
                                roomPrice = roomsMap.get(rooms.getSelectedItem()).getPrice();
                            }else{
                                roomPrice = 0;
                            }
                            calculte();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }else{
                    Toast.makeText(UpdateBookingActivity.this,"No Rooms",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void calculte() {
        total = 0;
        total += roomPrice;
        for(int i = 0;i < adapter.getmData().size(); i++){
            Cart cart = adapter.getmData().get(i);
            total += cart.getPrice();
        }
        NumberFormat formatter = new DecimalFormat("#,###");
        String total_str = formatter.format(total);
        total_txt.setText(total_str);
    }
}