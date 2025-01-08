package com.clothassistv3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

public class RainDataAdapter extends BaseAdapter {

    private final Context context;
    private final List<Entry> entries;
    private final List<String> dates; // List to store dates

    public RainDataAdapter(Context context, List<Entry> entries) {
        this.context = context;
        this.entries = entries;
        this.dates = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return entries.size();
    }

    @Override
    public Object getItem(int position) {
        return entries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_rain_data, parent, false);
            holder = new ViewHolder();
            holder.dateTextView = convertView.findViewById(R.id.dateTextView);
            holder.timeTextView = convertView.findViewById(R.id.timeTextView);
            holder.intensityTextView = convertView.findViewById(R.id.intensityTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Entry entry = entries.get(position);
        holder.dateTextView.setText(dates.get(position).split(" ")[0]); // Display date part
        holder.timeTextView.setText(dates.get(position).split(" ")[1]); // Display time part
        holder.intensityTextView.setText(String.format("%.2f", entry.getY())); // Display intensity

        return convertView;
    }

    public void addDate(String date) {
        dates.add(date);
    }

    public String getDate(int position) {
        return dates.get(position);
    }

    public void clearDates() {
        dates.clear();
    }

    private static class ViewHolder {
        TextView dateTextView;
        TextView timeTextView;
        TextView intensityTextView;
    }
}
