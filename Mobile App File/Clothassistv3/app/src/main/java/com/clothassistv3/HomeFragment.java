package com.clothassistv3;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize the RecyclerView with GridLayoutManager for 2 columns
        RecyclerView recyclerView = view.findViewById(R.id.homeRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Create the data for the RecyclerView
        List<HomeItem> homeItemList = new ArrayList<>();
        homeItemList.add(new HomeItem("Key Features",
                "Smart Monitoring: " +
                        "\nAutomatically detect rain and receive notifications to ensure your clothes are always dry and protected." +
                        "\n\nReal-time Updates: " + "\nStay informed with live data on rain detection and timestamps, " +
                        "helping you make timely decisions about your laundry." +
                        "\n\nUser-friendly Interface: " +
                        "\nNavigate easily between different sections such as real-time data and settings."));
        homeItemList.add(new HomeItem("How It Works",
                "Rain Detection: " +
                        "\nThe smart hanger continuously monitors weather conditions for rain. " +
                        "If rain is detected, the system alerts you instantly via the app." +
                        "\n\nTimestamp Records: " +
                        "\nKeep track of when rain is detected with precise timestamps, " +
                        "providing a detailed history of weather conditions." +
                        "\n\nAutomation: " +
                        "\nEnjoy the convenience of automatic adjustments based on weather conditions, " +
                        "ensuring optimal drying times for your clothes."));
        homeItemList.add(new HomeItem("Benefits",
                "Efficiency: " +
                        "\nAutomate your laundry process, saving time and effort." +
                        "\n\nProtection: " +
                        "\nPrevent your clothes from getting wet during unexpected rain showers." +
                        "\n\nConvenience: " +
                        "\nManage your clothes drying process from anywhere using your mobile device."));
        homeItemList.add(new HomeItem("Getting Started",
                "Connect Your Device: " +
                        "\nFollow the easy setup instructions to connect your smart clothes hanger to the app." +
                        "\n\nCustomize Settings: " +
                        "\nAdjust your preferences in the settings page to tailor the app to your needs." +
                        "\n\nMonitor & Relax: " +
                        "\nLet Clothassist handle the monitoring while you relax, knowing your laundry is in good hands."));

        // Set the adapter
        HomeAdapter adapter = new HomeAdapter(homeItemList, getContext());
        recyclerView.setAdapter(adapter);

        // Greet users based on current time
        TextView greetingTextView = view.findViewById(R.id.greetingTextView);
        String greeting = getGreeting();
        greetingTextView.setText(greeting);

        return view;
    }

    // Method to get appropriate greeting based on current time
    private String getGreeting() {
        Calendar calendar = Calendar.getInstance();
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 0 && timeOfDay < 12) {
            return "Good Morning";
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            return "Good Afternoon";
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            return "Good Evening";
        } else {
            return "Good Night";
        }
    }
}