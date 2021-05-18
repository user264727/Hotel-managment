package com.example.hotelmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hotelmanagement.Models.Customer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class CustomerAddActivity extends AppCompatActivity {

    EditText name,nic,address,pax,contact;
    ImageView image;
    private static final int PERMISSION_REQUEST_CODE = 200;
    Button add;
    Uri uri = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_add);

        name = findViewById(R.id.name);
        nic = findViewById(R.id.nic);
        address = findViewById(R.id.address);
        pax = findViewById(R.id.pax);
        contact = findViewById(R.id.contact);
        image = findViewById(R.id.image);
        add = findViewById(R.id.add);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
            }
        });

        // checking our permissions.
        if (checkPermission()) {
//            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);

                if(name.getText().toString().isEmpty()||nic.getText().toString().isEmpty()||address.getText().toString().isEmpty()||contact.getText().toString().isEmpty()||pax.getText().toString().isEmpty()){
                    Toast.makeText(CustomerAddActivity.this,"Field can't be empty",Toast.LENGTH_SHORT).show();
                }else if(contact.getText().toString().length() != 10){
                    Toast.makeText(CustomerAddActivity.this,"Contact No. should contain 10 digits",Toast.LENGTH_SHORT).show();
                }else if(!((nic.getText().toString().length() == 10 && nic.getText().toString().toLowerCase().contains("v")) || nic.getText().toString().length() == 12)){
                    Toast.makeText(CustomerAddActivity.this,"Please insert valid NIC",Toast.LENGTH_SHORT).show();
                }else if(uri == null) {
                    Toast.makeText(CustomerAddActivity.this,"Please add an image of the Customer",Toast.LENGTH_SHORT).show();
                }else{
                    final ProgressDialog mDialog = new ProgressDialog(CustomerAddActivity.this);
                    mDialog.setCancelable(false);
                    mDialog.setMessage("Please wait...");
                    mDialog.show();
                    String cus_name = name.getText().toString();
                    String cus_nic = nic.getText().toString();
                    String cus_address = address.getText().toString();
                    String cus_pax = pax.getText().toString();
                    String cus_contact = contact.getText().toString();
                    final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("customer_images").child(cus_name);
                    Bitmap bitmap = null;

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), uri);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                    byte[] data = baos.toByteArray();
                    UploadTask uploadTask = filePath.putBytes(data);


                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            finish();
                            return;
                        }
                    });

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Customer customer = new Customer();
                                    customer.setCus_name(cus_name);
                                    customer.setNic(cus_nic);
                                    customer.setAddress(cus_address);
                                    customer.setContact_no(cus_contact);
                                    customer.setPax(cus_pax);
                                    customer.setImage(uri.toString());
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Customers");
                                    reference.child(reference.push().getKey()).setValue(customer);
                                    Toast.makeText(CustomerAddActivity.this, "Data uploaded successfully", Toast.LENGTH_SHORT).show();
                                    mDialog.dismiss();
                                    startActivity(new Intent(CustomerAddActivity.this,ViewCustomersActivity.class));
                                    finish();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(CustomerAddActivity.this, ""+e.getMessage() , Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    });
                }
            }
        });
    }


    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.view_customer) {
            startActivity(new Intent(CustomerAddActivity.this,ViewCustomersActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        System.out.println(requestCode);
        switch(requestCode) {
            case 1:
                if(resultCode == RESULT_OK){
                    uri = imageReturnedIntent.getData();
                    image.setImageURI(uri);
                }
                break;
        }
    }

    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{ READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denined.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

}