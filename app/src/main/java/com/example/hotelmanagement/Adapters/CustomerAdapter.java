package com.example.hotelmanagement.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmanagement.CustomerEditActivity;
import com.example.hotelmanagement.ImageUpdateActivity;
import com.example.hotelmanagement.Models.Customer;
import com.example.hotelmanagement.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {
    private Context context;
    private List<Customer> customers;

    DatabaseReference reference;
    public CustomerAdapter(Context context, ArrayList<Customer> customers) {
        this.context = context;
        this.customers = customers;
        reference = FirebaseDatabase.getInstance().getReference().child("Customers");
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.customer_list_item, parent, false);
        return new CustomerViewHolder(view, context, customers);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomerViewHolder ViewHolder, final int position) {

        ViewHolder.name.setText(customers.get(position).getCus_name());
        ViewHolder.nic.setText(customers.get(position).getNic());
        ViewHolder.address.setText(customers.get(position).getAddress());
        ViewHolder.contact.setText(customers.get(position).getContact_no());

        try {
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(customers.get(position).getImage()).getContent());
            ViewHolder.image.setImageBitmap(bitmap);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("Won't be able to undo this deletion!")
                        .setConfirmText("Yes")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                reference.child(customers.get(position).getId()).removeValue();
                                sDialog
                                        .setTitleText("Deleted!")
                                        .setContentText("Your imaginary file has been deleted!")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                        })
                        .setCancelText("No")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        ViewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ImageUpdateActivity.class);
                intent.putExtra("image",customers.get(position).getImage());
                intent.putExtra("id",customers.get(position).getId());
                intent.putExtra("name",customers.get(position).getCus_name());
                context.startActivity(intent);
            }
        });
        
        ViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strEmp = new Gson().toJson(customers.get(position));
                Intent intent = new Intent(context, CustomerEditActivity.class);
                intent.putExtra("customer", strEmp);
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {

        return customers.size();
    }

    public static class CustomerViewHolder extends RecyclerView.ViewHolder{

        ImageView image,delete;
        TextView name,nic,address,contact;
        public CustomerViewHolder(@NonNull View itemView, final Context context, final List<Customer> item) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            nic = itemView.findViewById(R.id.nic);
            address = itemView.findViewById(R.id.address);
            contact = itemView.findViewById(R.id.contact);
            image = itemView.findViewById(R.id.image);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
