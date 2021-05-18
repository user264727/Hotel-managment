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
import android.view.Menu;
import android.view.MenuItem;
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BookingActivity extends AppCompatActivity {
    Spinner customers,rooms,foods;
    RecyclerView recyclerView;
    HashMap<String, Customer> cusMap = new HashMap<>();
    HashMap<String, Room> roomsMap = new HashMap<>();
    HashMap<String, Food> foodMap = new HashMap<>();
    ArrayList<String> carts = new ArrayList<>();
    EditText quantity;
    ImageView add_food;
    BookingFoodAdapter adapter;
    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;
    TextView date,time,total_txt;
    Button add;
    double total = 0;
    double roomPrice = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        rooms = findViewById(R.id.rooms);
        customers = findViewById(R.id.customers);
        foods = findViewById(R.id.foods);
        quantity = findViewById(R.id.quantity);
        recyclerView = findViewById(R.id.food_list);
        add_food = findViewById(R.id.add_food);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        add = findViewById(R.id.add);
        total_txt = findViewById(R.id.total);
        Calendar cal = Calendar.getInstance();

        setCustomers();
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
                        Toast.makeText(BookingActivity.this,"Please insert a quantity",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(BookingActivity.this,"Please select a food",Toast.LENGTH_SHORT).show();
                }
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog = new TimePickerDialog(BookingActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                datePickerDialog = new DatePickerDialog(BookingActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date.setText( dayOfMonth + "/" + (month+1) + "/" + year);
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setTitle("Select Date");
                datePickerDialog.show();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rooms.getSelectedItemPosition() == 0 || customers.getSelectedItemPosition() == 0 || date.getText().toString().isEmpty() || time.getText().toString().isEmpty()){
                    Toast.makeText(BookingActivity.this,"Please fill all",Toast.LENGTH_SHORT).show();
                }else{
                    ProgressDialog dialog = new ProgressDialog(BookingActivity.this);
                    dialog.setCancelable(false);
                    dialog.setMessage("Please wait...");
                    dialog.show();
                    Booking booking = new Booking();
                    booking.setCustomerId(cusMap.get(customers.getSelectedItem()).getId());
                    booking.setCustomerName(customers.getSelectedItem().toString());
                    booking.setRoomId(roomsMap.get(rooms.getSelectedItem()).getId());
                    booking.setRoomNo(rooms.getSelectedItem().toString());
                    booking.setCarts(adapter.getmData());
                    booking.setDate(date.getText().toString());
                    booking.setTime(time.getText().toString());
                    booking.setTotal(total);

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Bookings");
                    booking.setId(ref.push().getKey());
                    ref.child(booking.getId()).setValue(booking)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dialog.dismiss();
                                    onBackPressed();
                                    Toast.makeText(BookingActivity.this,"Successfully booking added",Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog.dismiss();
                                    Toast.makeText(BookingActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

    }

    private void setupRecycler() {
        ArrayList<Cart> cartList = new ArrayList<>();

        adapter = new BookingFoodAdapter(this,cartList);
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
                    ArrayAdapter foodsAdapter = new ArrayAdapter(BookingActivity.this,android.R.layout.simple_spinner_item, foodList);
                    foodsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    foods.setAdapter(foodsAdapter);
                }else{
                    Toast.makeText(BookingActivity.this,"No Foods",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setCustomers() {
        DatabaseReference cus_reference  = FirebaseDatabase.getInstance().getReference("Customers");
        cus_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> customerList = new ArrayList<>();
                customerList.add("Select the customer");
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Customer customer = dataSnapshot.getValue(Customer.class);
                        cusMap.put(customer.getCus_name(),customer);
                        customerList.add(customer.getCus_name());
                    }
                    ArrayAdapter cusAdapter = new ArrayAdapter(BookingActivity.this,android.R.layout.simple_spinner_item, customerList);
                    cusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    customers.setAdapter(cusAdapter);
                }else{
                    Toast.makeText(BookingActivity.this,"No Customers",Toast.LENGTH_SHORT).show();
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
                roomList.add("Select the room");
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Room room = dataSnapshot.getValue(Room.class);
                        roomsMap.put(room.getRoomNo(),room);
                        roomList.add(room.getRoomNo());
                    }
                    ArrayAdapter roomAdapter = new ArrayAdapter(BookingActivity.this,android.R.layout.simple_spinner_item, roomList);
                    roomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    rooms.setAdapter(roomAdapter);
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
                    Toast.makeText(BookingActivity.this,"No Rooms",Toast.LENGTH_SHORT).show();
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