package com.example.hotelmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.hotelmanagement.Models.Food;
import com.example.hotelmanagement.Models.Room;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.Arrays;

public class AddRoomActivity extends AppCompatActivity{
    Spinner type;
    EditText room_number,floor,price,note;
    Button add_room;
    ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);

        type = findViewById(R.id.type);
        room_number = findViewById(R.id.room_number);
        floor = findViewById(R.id.floor);
        price = findViewById(R.id.price);
        note = findViewById(R.id.note);
        add_room = findViewById(R.id.add_room);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, Arrays.asList("Single", "Double", "Triple", "Quad", "Queen","King"));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(adapter);
        add_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);

                if(validate()){
                    addRoom();
                }else{
                    Toast.makeText(AddRoomActivity.this,"Please fill all data",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addRoom() {
        pDialog.setMessage("Please wait...");
        pDialog.show();
        String type_txt = type.getSelectedItem().toString();
        String room_number_txt = room_number.getText().toString();
        String floor_txt = floor.getText().toString();
        String price_txt = price.getText().toString();
        String note_txt = note.getText().toString();
        Room room = new Room(room_number_txt,floor_txt,Double.parseDouble(price_txt),note_txt,type_txt);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Rooms");
        room.setId(ref.push().getKey());
        ref.child(room.getId()).setValue(room)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pDialog.dismiss();
                        startActivity(new Intent(AddRoomActivity.this,RoomsViewActivity.class));
                        Toast.makeText(AddRoomActivity.this,"data added",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pDialog.dismiss();
                        Toast.makeText(AddRoomActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validate() {
        if( note.getText().toString().isEmpty()|| room_number.getText().toString().isEmpty()|| floor.getText().toString().isEmpty()|| price.getText().toString().isEmpty()){
            return false;
        }else {
            return true;
        }
    }

}