package com.clothassistv3;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AboutUsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Set up WebView
        WebView aboutUsWebView = findViewById(R.id.aboutUsWebView);

        String aboutUsContent = "<html>" +
                "<head>" +
                "<style type=\"text/css\">" +
                "body {" +
                "    font-family: 'Roboto-Regular';" +
                "    text-align: justify;" +
                "    padding: 16px;" + // Adjust padding as needed
                "    margin: 0;" + // Remove default margins
                "}" +
                "h2, h3 {" +
                "    text-align: center;" +
                "    margin-top: 16px;" + // Adjust margin top for headings
                "    margin-bottom: 8px;" + // Adjust margin bottom for headings
                "}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<h2><b>ABOUT US</b></h2>" +
                "<p>Welcome to Clothassist, the innovative solution to your laundry drying needs. Our smart clothes hanger application is designed to make managing your laundry easier, more efficient, and stress-free.</p>" +
                "<p>At ShahTech Sdn Bhd, we are committed to leveraging cutting-edge technology to enhance everyday household tasks. Our smart clothes hanger system integrates seamlessly with our mobile app, providing you with real-time updates and controls at your fingertips. Whether it's monitoring weather conditions, adjusting hanger settings, or receiving notifications when your clothes are dry, our app ensures your laundry is taken care of, no matter where you are.</p>" +
                "<p>Join us in transforming the way you handle laundry, bringing convenience and smart living into your home.</p>" +
                "<h3><b>MISSION</b></h3>" +
                "<p>To simplify and modernize household chores with intelligent solutions, saving you time and effort while ensuring your clothes are always in perfect condition.</p>" +
                "<h3><b>VISION</b></h3>" +
                "<p>To become the leading provider of smart home solutions, known for our innovation, reliability, and user-friendly designs.</p>" + "<br>" +
                "<p>In conclusion, we thank you for choosing Clothassist. We hope to help you achieve a smarter and more comfortable lifestyle.</p>" +
                "</body>" +
                "</html>";

        aboutUsWebView.loadDataWithBaseURL(null, aboutUsContent, "text/html", "UTF-8", null);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Handle the Up button action
        return true;
    }
}
