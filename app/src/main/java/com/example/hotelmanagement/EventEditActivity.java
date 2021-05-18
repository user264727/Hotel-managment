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

public class EventEditActivity extends AppCompatActivity {

    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;
    EditText name,note,location;
    TextView date,startTime,endTime;
    Button update,delete;
    Event event;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);

        name = findViewById(R.id.name);
        note = findViewById(R.id.note);
        date = findViewById(R.id.date);
        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);
        update = findViewById(R.id.updateEvent);
        location = findViewById(R.id.location);
        delete = findViewById(R.id.deleteEvent);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Events");

        event = (Event) getIntent().getSerializableExtra("eventObject");
        name.setText(event.getEventName());
        note.setText(event.getNote());
        date.setText(event.getEventDate());
        startTime.setText(event.getStartTime());
        endTime.setText(event.getEndTime());
        location.setText(event.getLocation());

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(EventEditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date.setText( dayOfMonth + "/" + (month+1) + "/" + year);
                    }
                }, Integer.parseInt(date.getText().toString().split("/")[2]), Integer.parseInt(date.getText().toString().split("/")[1]),Integer.parseInt(date.getText().toString().split("/")[0]));
                datePickerDialog.setTitle("Select Date");
                datePickerDialog.show();
            }
        });
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = startTime.getText().toString();
                int hours = Integer.parseInt(time.split(":")[0]);
                if(time.contains("PM")){
                    hours += 12;
                }
                timePickerDialog = new TimePickerDialog(EventEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                }, hours, Integer.parseInt(time.split(":")[1].substring(0,2)), true);
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        });
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = endTime.getText().toString();
                int hour = Integer.parseInt(time.split(":")[0]);
                if(time.contains("PM")){
                    hour += 12;
                }
                timePickerDialog = new TimePickerDialog(EventEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                }, hour, Integer.parseInt(time.split(":")[1].substring(0,2)), true);
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    updateData();
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(event.getId()).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(EventEditActivity.this,ViewEventActivity.class));
                                Toast.makeText(EventEditActivity.this,"Event deleted successfully",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EventEditActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void updateData() {
        event.setEventName(name.getText().toString());
        event.setEventDate(date.getText().toString());
        event.setStartTime(startTime.getText().toString());
        event.setEndTime(endTime.getText().toString());
        event.setNote(note.getText().toString());
        event.setLocation(location.getText().toString());
        databaseReference.child(event.getId()).setValue(event)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(EventEditActivity.this,ViewEventActivity.class));
                        Toast.makeText(EventEditActivity.this,"Event updated successfully",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EventEditActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
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
        Toast.makeText(EventEditActivity.this,text,Toast.LENGTH_SHORT).show();
    }
}