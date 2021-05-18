package com.example.hotelmanagement.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmanagement.BookingActivity;
import com.example.hotelmanagement.EventEditActivity;
import com.example.hotelmanagement.Models.Booking;
import com.example.hotelmanagement.Models.Cart;
import com.example.hotelmanagement.Models.Food;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.UpdateBookingActivity;
import com.example.hotelmanagement.ViewEventActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends FirebaseRecyclerAdapter<
        Booking, BookingAdapter.BookingViewholder> {

    ProgressDialog progressDialog;
    Context mContext;
    public BookingAdapter(@NonNull FirebaseRecyclerOptions<Booking> options, Context mContext) {
        super(options);
        this.mContext = mContext;
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);
    }

    @NonNull
    @Override
    public BookingAdapter.BookingViewholder
    onCreateViewHolder(@NonNull ViewGroup parent,
                       int viewType) {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.booking_item, parent, false);
        return new BookingAdapter.BookingViewholder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull BookingViewholder holder, int position, @NonNull Booking model) {

        if(model.getCarts() == null || model.getCarts().isEmpty()){
            holder.foodLayout.setVisibility(View.GONE);
        }else {
            for (Cart cart : model.getCarts()) {
                View child = ((Activity) mContext).getLayoutInflater().inflate(R.layout.booking_fooditem, null);
                TextView food, quantity, price;
                ImageView remove;
                food = child.findViewById(R.id.food);
                quantity = child.findViewById(R.id.quantity);
                price = child.findViewById(R.id.price);
                remove = child.findViewById(R.id.remove);
                food.setText(cart.getName().replace("\n", ""));
                NumberFormat formatter = new DecimalFormat("#,###");
                String total_str = formatter.format(cart.getPrice());
                price.setText(total_str);
                quantity.setText(String.valueOf(cart.getQuantity()));
                remove.setVisibility(View.GONE);
                holder.foods.addView(child);
            }
        }
        holder.customer.setText(model.getCustomerName());
        holder.room.setText(model.getRoomNo());
        holder.date.setText(model.getDate());
        holder.time.setText(model.getTime());
        NumberFormat formatter = new DecimalFormat("#,###");
        String total_str = formatter.format(model.getTotal());

        holder.amount.setText("Rs "+total_str);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(mContext);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setContentView(R.layout.delete_layout);
                Button yes = dialog.findViewById(R.id.yes);
                Button no = dialog.findViewById(R.id.no);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressDialog.setMessage("Deleting...");
                        progressDialog.show();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Bookings").child(model.getId());
                        databaseReference.removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        Toast.makeText(mContext,"Booking record deleted successfully",Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(mContext,e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UpdateBookingActivity.class);
                Gson gson = new Gson();
                String booking_record = gson.toJson(model);
                intent.putExtra("booking_record",booking_record);
                mContext.startActivity(intent);
            }
        });
    }


    class BookingViewholder
            extends RecyclerView.ViewHolder {
        LinearLayout foods;
        TextView customer,room,date,time,amount;
        LinearLayout foodLayout;
        Button delete,update;
        public BookingViewholder(@NonNull View itemView) {
            super(itemView);
            foods = itemView.findViewById(R.id.food_list);
            customer = itemView.findViewById(R.id.name);
            room = itemView.findViewById(R.id.room);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            amount = itemView.findViewById(R.id.amount);
            delete = itemView.findViewById(R.id.delete);
            update = itemView.findViewById(R.id.update);
            foodLayout = itemView.findViewById(R.id.foodLayout);

        }
    }
}
