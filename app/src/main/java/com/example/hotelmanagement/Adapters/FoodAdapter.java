package com.example.hotelmanagement.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmanagement.Models.Food;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.UpdateFoodActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    private Context mContext;
    private List<Food> mData;

    public FoodAdapter(Context mContext, ArrayList<Food> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.food_item, parent, false);
        return new FoodViewHolder(view, mContext, mData);
    }

    @Override
    public void onBindViewHolder(@NonNull final FoodViewHolder ViewHolder, final int position) {

        ViewHolder.food_name.setText(mData.get(position).getName());

        ViewHolder.description.setText(mData.get(position).getDescription());
        NumberFormat formatter = new DecimalFormat("#,###");
        String price = formatter.format(mData.get(position).getPrice());
        ViewHolder.price.setText("Rs. "+price);

        ViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UpdateFoodActivity.class);

                intent.putExtra("name",mData.get(position).getName());
                intent.putExtra("description",mData.get(position).getDescription());
                intent.putExtra("price",String.valueOf(mData.get(position).getPrice()));
                intent.putExtra("id",mData.get(position).getId());

                mContext.startActivity(intent);
            }
        });

    }
    @Override
    public int getItemCount() {

        return mData.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder{

        TextView food_name, description, price;
        public FoodViewHolder(@NonNull View itemView, final Context context, final List<Food> item) {
            super(itemView);

            food_name = itemView.findViewById(R.id.food_name);
            description = itemView.findViewById(R.id.description);
            price = itemView.findViewById(R.id.price);
        }
    }
}
