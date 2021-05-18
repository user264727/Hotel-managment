package com.example.hotelmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hotelmanagement.Models.Customer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ImageUpdateActivity extends AppCompatActivity {

    ImageView image;
    Button upload;
    String imageUri;
    Uri uri = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_update);

        image = findViewById(R.id.image);
        upload = findViewById(R.id.upload);
        imageUri = getIntent().getStringExtra("image");
        try {
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(imageUri).getContent());
            image.setImageBitmap(bitmap);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uri == null){
                    Toast.makeText(ImageUpdateActivity.this,"Please select new Image",Toast.LENGTH_SHORT).show();
                }else {
                    final ProgressDialog mDialog = new ProgressDialog(ImageUpdateActivity.this);
                    mDialog.setMessage("Uploading...");
                    mDialog.show();

                    final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("customer_images").child(getIntent().getStringExtra("name"));
                    Bitmap bitmap = null;

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                    byte[] data = baos.toByteArray();
                    UploadTask uploadTask = filePath.putBytes(data);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ImageUpdateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    });

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Map userInfo = new HashMap();
                                    userInfo.put("image", uri.toString());
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Customers").child(getIntent().getStringExtra("id"));
                                    reference.updateChildren(userInfo);
                                    Toast.makeText(ImageUpdateActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                    mDialog.dismiss();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(ImageUpdateActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    });
                }
            }
        });
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
}