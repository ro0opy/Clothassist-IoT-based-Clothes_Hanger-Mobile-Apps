package com.clothassistv3;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RainInfoFragment extends Fragment {

    private LineChart lineChart;
    private List<Entry> entries;
    private RainDataAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rain_info, container, false);

        // Initialize the LineChart
        lineChart = view.findViewById(R.id.lineChart);
        // Declare ListView
        ListView listView = view.findViewById(R.id.listView); // Initialize ListView
        entries = new ArrayList<>();
        adapter = new RainDataAdapter(getContext(), entries);
        listView.setAdapter(adapter);

        setupLineChart();
        fetchDataFromFirebase();

        return view;
    }

    private void setupLineChart() {
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return formatElapsedTime(value);
            }
        });

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setGranularity(10f);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setTextColor(android.graphics.Color.WHITE);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setValueFormatter(new PercentFormatter());

        lineChart.getAxisRight().setEnabled(false);
        lineChart.invalidate();
    }

    private String formatElapsedTime(float hours) {
        int totalMinutes = (int) (hours * 60);
        int days = totalMinutes / (24 * 60);
        int remainingMinutes = totalMinutes % (24 * 60);
        int hoursPart = remainingMinutes / 60;
        int minutesPart = remainingMinutes % 60;
        return String.format(Locale.getDefault(), "Day %d\n%02d:%02d", days, hoursPart, minutesPart);
    }

    private void fetchDataFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("rain_data");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                entries.clear();
                adapter.clearDates(); // Clear previous dates

                long baseTimestamp = -1; // Initialize a base timestamp

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String date = snapshot.child("date").getValue(String.class);
                    String time = snapshot.child("time").getValue(String.class);
                    Float rainIntensity = snapshot.child("rain_intensity").getValue(Float.class);

                    // Combine date and time to a single timestamp
                    long timestamp = convertDateTimeToTimestamp(date, time);

                    // Set the base timestamp if not already set
                    if (baseTimestamp == -1) {
                        baseTimestamp = timestamp;
                    }

                    // Calculate elapsed time in hours since the base timestamp
                    float timeValue = (timestamp - baseTimestamp) / 3600000f; // Convert milliseconds to hours

                    if (timestamp != -1 && rainIntensity != null) {
                        entries.add(new Entry(timeValue, rainIntensity));
                        adapter.addDate(date + " " + time); // Add date and time to adapter
                    }
                }
                updateLineChart();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("RainInfoFragment", "Database error: " + databaseError.getMessage());
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Failed to load data. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateLineChart() {
        LineDataSet dataSet = new LineDataSet(entries, "Rain Intensity");
        dataSet.setColor(android.graphics.Color.parseColor("#39FF14"));
        dataSet.setValueTextColor(android.graphics.Color.WHITE);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate(); // refresh
    }

    private long convertDateTimeToTimestamp(String date, String time) {
        // Assuming date format is "yyyy-MM-dd" and time format is "HH:mm:ss"
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date parsedDate = dateFormat.parse(date + " " + time);
            return parsedDate != null ? parsedDate.getTime() : -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}