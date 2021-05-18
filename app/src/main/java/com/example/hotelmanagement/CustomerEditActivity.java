package com.example.hotelmanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hotelmanagement.Models.Customer;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CustomerEditActivity extends AppCompatActivity {
    EditText name,nic,address,pax,contact;
    Button update;
    Customer customer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_edit);

        name = findViewById(R.id.name);
        nic = findViewById(R.id.nic);
        address = findViewById(R.id.address);
        pax = findViewById(R.id.pax);
        contact = findViewById(R.id.contact);
        update = findViewById(R.id.update);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Gson gson = new Gson();
        Type type = new TypeToken<Customer>() {}.getType();
        customer = gson.fromJson(getIntent().getStringExtra("customer"), type);
        name.setText(customer.getCus_name());
        nic.setText(customer.getNic());
        address.setText(customer.getAddress());
        pax.setText(customer.getPax());
        contact.setText(customer.getContact_no());

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);

                if(name.getText().toString().isEmpty()||nic.getText().toString().isEmpty()||address.getText().toString().isEmpty()||contact.getText().toString().isEmpty()||pax.getText().toString().isEmpty()){
                    Toast.makeText(CustomerEditActivity.this,"Field can't be empty",Toast.LENGTH_SHORT).show();
                }else if(contact.getText().toString().length() != 10){
                    Toast.makeText(CustomerEditActivity.this,"Contact No. should contain 10 digits",Toast.LENGTH_SHORT).show();
                }else if(!((nic.getText().toString().length() == 10 && nic.getText().toString().toLowerCase().contains("v")) || nic.getText().toString().length() == 12)){
                    Toast.makeText(CustomerEditActivity.this,"Please insert valid NIC",Toast.LENGTH_SHORT).show();
                }else{
                    final ProgressDialog mDialog = new ProgressDialog(CustomerEditActivity.this);
                    mDialog.setCancelable(false);
                    mDialog.setMessage("Updating...");
                    mDialog.show();
                    String cus_name = name.getText().toString();
                    String cus_nic = nic.getText().toString();
                    String cus_address = address.getText().toString();
                    String cus_pax = pax.getText().toString();
                    String cus_contact = contact.getText().toString();
                    Map userInfo = new HashMap();
                    userInfo.put("cus_name", cus_name);
                    userInfo.put("nic", cus_nic);
                    userInfo.put("address", cus_address);
                    userInfo.put("pax", cus_pax);
                    userInfo.put("contact_no", cus_contact);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Customers").child(customer.getId());
                    reference.updateChildren(userInfo);
                    startActivity(new Intent(CustomerEditActivity.this,ViewCustomersActivity.class));
                    finish();
                }
            }
        });
    }
}