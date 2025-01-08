package com.clothassistv3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {

    private List<HomeItem> homeItemList;
    private Context context;

    public HomeAdapter(List<HomeItem> homeItemList, Context context) {
        this.homeItemList = homeItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        HomeItem homeItem = homeItemList.get(position);
        holder.titleTextView.setText(homeItem.getTitle());

        // Set background color based on title
        int backgroundColor = getBackgroundColorForTitle(homeItem.getTitle());
        holder.itemView.setBackgroundColor(backgroundColor); // Use setBackgroundColor instead of setBackgroundResource

        // Set onClickListener to show dialog with description
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDescriptionDialog(homeItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return homeItemList.size();
    }

    private int getBackgroundColorForTitle(String title) {
        switch (title) {
            case "Key Features":
                return ContextCompat.getColor(context, R.color.hot_pink);
            case "How It Works":
                return ContextCompat.getColor(context, R.color.orange);
            case "Benefits":
                return ContextCompat.getColor(context, R.color.green);
            case "Getting Started":
                return ContextCompat.getColor(context, R.color.violet);
            default:
                return ContextCompat.getColor(context, android.R.color.transparent);
        }
    }

    private void showDescriptionDialog(HomeItem homeItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(homeItem.getTitle());
        builder.setMessage(homeItem.getDescription());
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static class HomeViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
        }
    }
}
