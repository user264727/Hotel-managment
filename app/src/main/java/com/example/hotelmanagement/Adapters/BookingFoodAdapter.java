package com.example.hotelmanagement.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmanagement.BookingActivity;
import com.example.hotelmanagement.Models.Cart;
import com.example.hotelmanagement.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class BookingFoodAdapter extends RecyclerView.Adapter<BookingFoodAdapter.BookingFoodViewHolder> {
    private Context mContext;
    ArrayList<Cart> mData;

    public BookingFoodAdapter(Context mContext, ArrayList<Cart> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    public ArrayList<Cart> getmData() {
        return mData;
    }

    public void setmData(ArrayList<Cart> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public BookingFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.booking_fooditem, parent, false);
        return new BookingFoodViewHolder(view, mContext, mData);
    }

    @Override
    public void onBindViewHolder(@NonNull final BookingFoodViewHolder ViewHolder, final int position) {

        ViewHolder.food.setText(mData.get(position).getName().replace("\n",""));
        System.out.println(mData.get(position).getName());
        ViewHolder.quantity.setText(String.valueOf(mData.get(position).getQuantity()));

        NumberFormat formatter = new DecimalFormat("#,###");
        String total_str = formatter.format(mData.get(position).getPrice());
        ViewHolder.price.setText(total_str);
        ViewHolder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mData.remove(position);
                notifyItemRemoved(position);
                ((BookingActivity)mContext).calculte();
            }
        });

    }
    @Override
    public int getItemCount() {

        return mData.size();
    }

    public static class BookingFoodViewHolder extends RecyclerView.ViewHolder{

        TextView food,quantity,price;
        ImageView remove;
        public BookingFoodViewHolder(@NonNull View itemView, final Context context, final List<Cart> item) {
            super(itemView);

            food = itemView.findViewById(R.id.food);
            quantity = itemView.findViewById(R.id.quantity);
            price = itemView.findViewById(R.id.price);
            remove = itemView.findViewById(R.id.remove);
        }
    }
}
