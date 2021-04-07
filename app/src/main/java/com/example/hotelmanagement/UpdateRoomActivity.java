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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.hotelmanagement.Models.Room;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;

public class UpdateRoomActivity extends AppCompatActivity {

    Room room;
    EditText room_number,floor,price,note;
    Button update;
    Spinner type;
    ProgressDialog pDialog;
    ArrayList<String> types = new ArrayList<>(Arrays.asList("Single", "Double", "Triple", "Quad", "Queen","King"));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_room);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        room = (Room) getIntent().getSerializableExtra("room");
        type = findViewById(R.id.type);
        room_number = findViewById(R.id.room_number);
        floor = findViewById(R.id.floor);
        price = findViewById(R.id.price);
        note = findViewById(R.id.note);
        update = findViewById(R.id.update_room);
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(adapter);

        room_number.setText(room.getRoomNo());
        floor.setText(room.getFloor());
        price.setText(String.valueOf(room.getPrice()));
        note.setText(room.getNote());
        type.setSelection(types.indexOf(room.getType()));
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
                if(validate()){
                    updateRoom();
                }else{
                    Toast.makeText(UpdateRoomActivity.this,"Please fill all data",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateRoom() {
        pDialog.setMessage("Please wait...");
        pDialog.show();
        String type_txt = type.getSelectedItem().toString();
        String room_number_txt = room_number.getText().toString();
        String floor_txt = floor.getText().toString();
        String price_txt = price.getText().toString();
        String note_txt = note.getText().toString();
        room.setRoomNo(room_number_txt);
        room.setFloor(floor_txt);
        room.setPrice(Double.parseDouble(price_txt));
        room.setNote(note_txt);
        room.setType(type_txt);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Rooms");
        ref.child(room.getId()).setValue(room)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pDialog.dismiss();
                        Toast.makeText(UpdateRoomActivity.this,"Data removed",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pDialog.dismiss();
                        Toast.makeText(UpdateRoomActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
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