package com.example.hotelmanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hotelmanagement.Models.Food;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateFoodActivity extends AppCompatActivity {

    TextView title;
    EditText food_name ,price ,note;
    String id;
    Button update,delete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_food);

        food_name = findViewById(R.id.food_name);
        price = findViewById(R.id.price);
        note = findViewById(R.id.note);
        update = findViewById(R.id.update);
        delete = findViewById(R.id.delete);
        title = findViewById(R.id.title);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        id = getIntent().getStringExtra("id");
        food_name.setText(getIntent().getStringExtra("name"));
        note.setText(getIntent().getStringExtra("description"));
        price.setText(getIntent().getStringExtra("price"));

        title.setText("Update "+getIntent().getStringExtra("name"));
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(UpdateFoodActivity.this);
                updateFood();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData();
            }
        });
    }

    private void deleteData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Foods");
        reference.child(id).removeValue();
        Toast.makeText(this,"Successfully Deleted",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(UpdateFoodActivity.this,FoodsViewActivity.class));
        finish();
    }

    private void updateFood() {
        if(food_name.getText().toString().isEmpty()|| price.getText().toString().isEmpty()|| note.getText().toString().isEmpty()){
            Toast.makeText(UpdateFoodActivity.this,"Fields can't be empty",Toast.LENGTH_SHORT).show();
        }else{
            Food food = new Food();
            food.setName(food_name.getText().toString());
            food.setDescription(note.getText().toString());
            food.setPrice(Double.parseDouble(price.getText().toString()));

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Foods");
            reference.child(id).setValue(food);
            Toast.makeText(this,"Successfully Updated",Toast.LENGTH_SHORT).show();
        }
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