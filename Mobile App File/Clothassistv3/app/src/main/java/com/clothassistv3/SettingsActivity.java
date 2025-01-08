package com.clothassistv3;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private SwitchMaterial switchConnectIoT;
    private String arduinoIp = "192.168.133.144";
    private int arduinoPort = 80;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Initialize the switch
        switchConnectIoT = findViewById(R.id.switch_connect_iot);

        // Set listener for switch
        switchConnectIoT.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Handle connection
                String userEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
                new ConnectToArduinoTask(SettingsActivity.this).execute(arduinoIp, String.valueOf(arduinoPort), userEmail);
            } else {
                // Handle disconnection
                new DisconnectFromArduinoTask(SettingsActivity.this).execute(arduinoIp, String.valueOf(arduinoPort));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Handle the Up button action
        return true;
    }

    public void updateSwitchState(boolean state) {
        runOnUiThread(() -> switchConnectIoT.setChecked(state));
    }

    private static class ConnectToArduinoTask extends AsyncTask<String, Void, String> {
        @SuppressLint("StaticFieldLeak")
        private SettingsActivity activity;

        ConnectToArduinoTask(SettingsActivity activity) {
            this.activity = activity;
        }

        @Override
        protected String doInBackground(String... params) {
            String ip = params[0];
            int port = Integer.parseInt(params[1]);
            String userEmail = params[2]; // Get the user email from params
            String token = params[3];
            try (Socket socket = new Socket(ip, port)) {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Send connect command along with the user email
                out.println("GET /connect?email=" + userEmail + "&token=" + token + " HTTP/1.1\r\n\r\n");
                String response = in.readLine();
                return response != null ? response : "No response from device";
            } catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
            if (result.contains("Connected for user")) {
                // Update the switch state in SettingsActivity
                activity.updateSwitchState(true);
            }
        }
    }

    private static class DisconnectFromArduinoTask extends AsyncTask<String, Void, String> {
        @SuppressLint("StaticFieldLeak")
        private SettingsActivity activity;

        DisconnectFromArduinoTask(SettingsActivity activity) {
            this.activity = activity;
        }

        @Override
        protected String doInBackground(String... params) {
            String ip = params[0];
            int port = Integer.parseInt(params[1]);
            try (Socket socket = new Socket(ip, port)) {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println("GET /disconnect HTTP/1.1\r\n\r\n"); // Send disconnect command
                String response = in.readLine(); // Read response from Arduino
                return response != null ? response : "No response from device";
            } catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
            if (result.contains("Disconnected")) {
                activity.updateSwitchState(false);
            } else {
                activity.updateSwitchState(true);
            }
        }
    }
}
