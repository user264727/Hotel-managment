package com.example.hotelmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hotelmanagement.Adapters.CustomerAdapter;
import com.example.hotelmanagement.Adapters.EventAdapter;
import com.example.hotelmanagement.Models.Customer;
import com.example.hotelmanagement.Models.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ViewCustomersActivity extends AppCompatActivity {

    RecyclerView listView;
    ImageView add_customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_customers);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        listView = findViewById(R.id.customers);
        add_customer = findViewById(R.id.add_customer);
        ArrayList<Customer> customers = new ArrayList<>();
        ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Customers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    customers.clear();
                    for(DataSnapshot ds:snapshot.getChildren()){
                        Customer customer = ds.getValue(Customer.class);
                        customer.setId(ds.getKey());
                        customers.add(customer);
                    }
                    CustomerAdapter myAdapter = new CustomerAdapter(ViewCustomersActivity.this,customers);
                    listView.setLayoutManager(new LinearLayoutManager(ViewCustomersActivity.this));
                    listView.setAdapter(myAdapter);
                    pDialog.dismiss();
                }else{
                    pDialog.dismiss();
                    Toast.makeText(ViewCustomersActivity.this,"No customers",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pDialog.dismiss();
                Toast.makeText(ViewCustomersActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

        add_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewCustomersActivity.this,CustomerAddActivity.class));
                finish();
            }
        });
    }
}