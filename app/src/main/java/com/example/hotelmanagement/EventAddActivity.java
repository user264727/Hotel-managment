package com.example.hotelmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.hotelmanagement.Models.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Objects;

public class EventAddActivity extends AppCompatActivity {

    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;
    EditText name,note,location;
    TextView date,startTime,endTime;
    Calendar cal = Calendar.getInstance();
    Button add_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_add);

        name = findViewById(R.id.name);
        note = findViewById(R.id.note);
        date = findViewById(R.id.date);
        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);
        add_btn = findViewById(R.id.add_event);
        location = findViewById(R.id.location);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(EventAddActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date.setText( dayOfMonth + "/" + (month+1) + "/" + year);
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setTitle("Select Date");
                datePickerDialog.show();
            }
        });
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog = new TimePickerDialog(EventAddActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                        startTime.setText(aTime);
                    }
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        });
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog = new TimePickerDialog(EventAddActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                        endTime.setText(aTime);
                    }
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    addEvent();
                }
            }
        });

    }

    private void addEvent() {
        Event event = new Event();
        event.setEventName(name.getText().toString());
        event.setEventDate(date.getText().toString());
        event.setStartTime(startTime.getText().toString());
        event.setEndTime(endTime.getText().toString());
        event.setNote(note.getText().toString());
        event.setLocation(location.getText().toString());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Events");
        String id = Objects.requireNonNull(databaseReference.push().getKey());
        databaseReference.child(id).setValue(event)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        onBackPressed();
                        Toast.makeText(EventAddActivity.this,"Event added successfully",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EventAddActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validate() {
        if(name.getText().toString().isEmpty()){
            displayAlert("Event can't be empty");
            return false;
        }else if(date.getText().toString().isEmpty()){
            displayAlert("Date can't be empty");
            return false;
        }else if(startTime.getText().toString().isEmpty()){
            displayAlert("Start Time can't be empty");
            return false;
        }else if(endTime.getText().toString().isEmpty()){
            displayAlert("End Time can't be empty");
            return false;
        }else if(note.getText().toString().isEmpty()){
            displayAlert("Event Note can't be empty");
            return false;
        }else if(location.getText().toString().isEmpty()){
            displayAlert("Location can't be empty");
            return false;
        }else{
            return true;
        }
    }

    void displayAlert(String text){
        Toast.makeText(EventAddActivity.this,text,Toast.LENGTH_SHORT).show();
    }
}