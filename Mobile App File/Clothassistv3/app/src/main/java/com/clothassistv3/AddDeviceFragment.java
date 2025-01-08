package com.clothassistv3;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class AddDeviceFragment extends Fragment {

    private String arduinoIp = "192.168.133.144";
    private int arduinoPort = 80;
    private CustomCircularProgressBar customCircularProgressBar;
    private DatabaseReference databaseReference;
    private TextView textRainStatus;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_device, container, false);

        textRainStatus = view.findViewById(R.id.text_rain_status);
        customCircularProgressBar = view.findViewById(R.id.customCircularProgressBar);
        Button adjustButton = view.findViewById(R.id.button_adjust);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("rain_data");

        // Listen for changes in rain data
        databaseReference.orderByKey().limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Float rainIntensity = data.child("rain_intensity").getValue(Float.class);
                    Log.d("FirebaseData", "Rain Intensity: " + rainIntensity);
                    if (rainIntensity != null) {
                        updateProgressBar(rainIntensity);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseData", "Failed to read data", error.toException());
                Toast.makeText(getActivity(), "Failed to read data", Toast.LENGTH_SHORT).show();
            }
        });

        adjustButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AdjustServoTask().execute(arduinoIp, String.valueOf(arduinoPort), "L");
            }
        });

        // Show the pop-up dialog
        showAddDeviceDialog();

        return view;
    }

    @SuppressLint("MissingInflatedId")
    private void showAddDeviceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_device, null);

        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        Button connectNowButton = dialogView.findViewById(R.id.button_connect_now);
        Button cancelButton = dialogView.findViewById(R.id.button_cancel);

        connectNowButton.setOnClickListener(v -> {
            // Handle "Connect Now" button click
            connectToArduino(); // Call the method to connect to Arduino
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

        // Apply the animation
        Animation enterAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.dialog_enter);
        dialogView.startAnimation(enterAnimation);
    }

    private void connectToArduino() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userEmail = auth.getCurrentUser().getEmail();
        auth.getAccessToken(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult().getToken();
                new ConnectToArduinoTask().execute(arduinoIp, String.valueOf(arduinoPort), userEmail, token);
            } else {
                Toast.makeText(getActivity(), "Failed to retrieve token", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProgressBar(float rainIntensityPercentage) {
        customCircularProgressBar.setProgress(rainIntensityPercentage);
    }

    private class ConnectToArduinoTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String ip = params[0];
            int port = Integer.parseInt(params[1]);
            String userEmail = params[2];
            String token = params[3];
            try (Socket socket = new Socket(ip, port)) {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Send connect command along with the user email and token
                out.println("GET /connect?email=" + userEmail + "&token=" + token + " HTTP/1.1\r\n\r\n");
                String response = in.readLine();
                return response != null ? response : "No response from device";
            } catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
            if (result.contains("Connected for user")) {
                // Update the switch state in SettingsActivity
                if (getActivity() instanceof SettingsActivity) {
                    ((SettingsActivity) getActivity()).updateSwitchState(true);
                }
            }
        }
    }

    private class AdjustServoTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String ip = params[0];
            int port = Integer.parseInt(params[1]);
            String command = params[2];
            try (Socket socket = new Socket(ip, port)) {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println("GET /" + command + " HTTP/1.1\r\nHost: " + ip + "\r\n\r\n"); // Send command
                String response = in.readLine(); // Read response from Arduino
                return response != null ? response : "No response from device";
            } catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
        }
    }
}
