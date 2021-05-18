package com.example.hotelmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.hotelmanagement.Adapters.FoodAdapter;
import com.example.hotelmanagement.Adapters.RoomAdapter;
import com.example.hotelmanagement.Models.Food;
import com.example.hotelmanagement.Models.Room;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FoodsViewActivity extends AppCompatActivity {
    RecyclerView roomsRecyclerView;
    LinearLayout addFoods;
    ArrayList<Food> foods = new ArrayList<>();
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foods_view);
        addFoods = findViewById(R.id.add_foods);
        roomsRecyclerView = findViewById(R.id.food_list);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Loading...");
        dialog.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Foods");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foods.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    Food food = ds.getValue(Food.class);
                    food.setId(ds.getKey());
                    foods.add(food);
                }
                setData(foods);
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                Toast.makeText(FoodsViewActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        addFoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FoodsViewActivity.this,AddFoodsActivity.class));
            }
        });
    }
    void setData(ArrayList<Food> list){
        FoodAdapter myAdapter = new FoodAdapter(this,list);
        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        roomsRecyclerView.setAdapter(myAdapter);
    }
}