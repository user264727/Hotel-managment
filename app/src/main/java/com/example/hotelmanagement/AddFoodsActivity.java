package com.example.hotelmanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hotelmanagement.Models.Food;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddFoodsActivity extends AppCompatActivity {

    EditText food_name ,price ,note;
    Button add_foods;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_foods);

        food_name = findViewById(R.id.food_name);
        price = findViewById(R.id.price);
        note = findViewById(R.id.note);
        add_foods = findViewById(R.id.add_foods);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        add_foods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(AddFoodsActivity.this);
                if(food_name.getText().toString().isEmpty()|| price.getText().toString().isEmpty()|| note.getText().toString().isEmpty()){
                    Toast.makeText(AddFoodsActivity.this,"Fields can't be empty",Toast.LENGTH_SHORT).show();
                }else{
                    addFoods();
                }
            }
        });
    }

    private void addFoods() {
        Food food = new Food();
        food.setName(food_name.getText().toString());
        food.setDescription(note.getText().toString());
        food.setPrice(Double.parseDouble(price.getText().toString()));

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Foods");
        reference.child(reference.push().getKey()).setValue(food);
        food_name.setText("");
        price.setText("");
        note.setText("");
        Toast.makeText(this,"Successfully Added",Toast.LENGTH_SHORT).show();

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}