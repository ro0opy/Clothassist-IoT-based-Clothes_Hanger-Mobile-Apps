#include <WiFi.h>
#include <ESP32Servo.h>
#include <time.h>
#include <FirebaseESP32.h>
#include <addons/TokenHelper.h>
#include <addons/RTDBHelper.h>
#include <WebServer.h>

// Firebase credentials
#define FIREBASE_URL " "
#define FIREBASE_API " "
#define USER_EMAIL " "
#define USER_PASSWORD " "

// Wi-Fi credentials
const char* ssid = " ";
const char* password = " ";

#define RAIN_SENSOR_PIN        25
#define ANALOG_RAIN_SENSOR_PIN 34
#define SERVO_PIN              26
#define RAIN_THRESHOLD         2

// HTTP server
WebServer server(80);

Servo servo;
FirebaseData firebaseData;
FirebaseConfig config;
FirebaseAuth auth;

int rain_state = HIGH;
bool rain_detected = false;
unsigned long lastCheckTime = 0;
const unsigned long checkInterval = 30000; // 30 seconds

void setup() {
  Serial.begin(9600);
  pinMode(RAIN_SENSOR_PIN, INPUT);
  pinMode(ANALOG_RAIN_SENSOR_PIN, INPUT);
  servo.attach(SERVO_PIN);
  rain_state = digitalRead(RAIN_SENSOR_PIN);

  connectToWifi();
  configTime(28800, 0, "pool.ntp.org", "time.nist.gov");
  syncTime();

  config.api_key = FIREBASE_API;
  config.database_url = FIREBASE_URL;
  auth.user.email = USER_EMAIL;
  auth.user.password = USER_PASSWORD;
  config.token_status_callback = tokenStatusCallback;

  Firebase.begin(&config, &auth);

  server.on("/", handleRoot);
  server.on("/connect", handleConnect);
  server.on("/L", handleMoveServo);
  server.begin();
  Serial.println("HTTP server started");
}

void loop() {
  unsigned long currentTime = millis();

  if (currentTime - lastCheckTime >= checkInterval) {
    checkRainStatus();
    lastCheckTime = currentTime;
  }

  handleSerialCommands();
  server.handleClient();
}

void connectToWifi() {
  WiFi.begin(ssid, password);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("Connected!");
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
}

void syncTime() {
  struct tm timeinfo;
  int retryCount = 0;
  while (!getLocalTime(&timeinfo) && retryCount < 10) {
    Serial.println("Failed to obtain time, retrying...");
    delay(2000);
    retryCount++;
  }
  if (retryCount == 10) {
    Serial.println("Failed to obtain time after multiple attempts");
  } else {
    Serial.println("Time obtained successfully");
  }
}

void checkRainStatus() {
  int current_rain_state = digitalRead(RAIN_SENSOR_PIN);
  Serial.print("Rain status's result: ");

  if (current_rain_state == LOW && rain_state == HIGH) {
    Serial.println("Rain detected!");
    handleRainDetected();
  } else if (current_rain_state == HIGH && rain_state == LOW) {
    Serial.println("No rain detected!");
    handleNoRainDetected();
  } else {
    handleDefaultCondition(current_rain_state, rain_state);
  }

  rain_state = current_rain_state;
  delay(1000);
}

void handleRainDetected() {
  moveServo(180);
  rain_detected = true;
  printCurrentTime();
  float rain_intensity_percentage = calculateRainIntensity();
  Serial.print("Rain intensity: ");
  Serial.print(rain_intensity_percentage);
  Serial.println(" %");

  if (rain_intensity_percentage >= 2 && rain_intensity_percentage <= 17) {
    Serial.println("Light rain");
  } else if (rain_intensity_percentage >= 18 && rain_intensity_percentage <= 51) {
    Serial.println("Moderate rain");
  } else if (rain_intensity_percentage >= 52 && rain_intensity_percentage <= 100) {
    Serial.println("Heavy rain");
  }

  // Update Firebase with rain data
  updateFirebaseRainData(rain_intensity_percentage);
}

void handleNoRainDetected() {
  rain_detected = false;
  printCurrentTime();
  Serial.println("Rain intensity: 0%");

  // Update Firebase with no rain data
  updateFirebaseRainData(0);
}

void handleDefaultCondition(int current_rain_state, int rain_state) {
  if (current_rain_state == LOW && rain_state == LOW) {
    Serial.println("Rain detected");
    rain_detected = true;
    printCurrentTime();
    float rain_intensity_percentage = calculateRainIntensity();
    Serial.print("Rain intensity: ");
    Serial.print(rain_intensity_percentage);
    Serial.println(" %");

    if (rain_intensity_percentage >= 2 && rain_intensity_percentage <= 17) {
      Serial.println("Light rain");
    } else if (rain_intensity_percentage >= 18 && rain_intensity_percentage <= 51) {
      Serial.println("Moderate rain");
    } else if (rain_intensity_percentage >= 52 && rain_intensity_percentage <= 100) {
      Serial.println("Heavy rain");
    }

    // Update Firebase with rain data
    updateFirebaseRainData(rain_intensity_percentage);
  } else {
    Serial.println("No rain detected");
    rain_detected = false;
    printCurrentTime();
    Serial.println("Rain intensity: 0%");

    // Update Firebase with no rain data
    updateFirebaseRainData(0);
  }
}

void handleSerialCommands() {
  if (Serial.available() > 0) {
    char command = Serial.read();
    if (command == 'L' || command == 'l') {
      Serial.println("Move anti-clockwise.");
      moveServo(0);
      servo.detach();
    } else {
      Serial.println("Unrecognized command.");
    }
  }
}

float calculateRainIntensity() {
  int sensor_value = analogRead(ANALOG_RAIN_SENSOR_PIN);
  float intensity_percentage = map(sensor_value, 4095, 0, 0, 100);

  if (sensor_value < RAIN_THRESHOLD) {
    intensity_percentage = 0;
  }

  return intensity_percentage;
}

void moveServo(int position) {
  servo.attach(SERVO_PIN);
  servo.write(position);
  delay(500);
  servo.detach();
}

void printCurrentTime() {
  struct tm timeinfo;
  if (getLocalTime(&timeinfo)) {
    char dateString[12];
    char timeString[10];
    strftime(dateString, sizeof(dateString), "%Y-%m-%d", &timeinfo);
    strftime(timeString, sizeof(timeString), "%H:%M:%S", &timeinfo);
    Serial.print("Date: ");
    Serial.println(dateString);
    Serial.print("Time: ");
    Serial.println(timeString);
  }
}

void updateFirebaseRainData(float intensity) {
  struct tm timeinfo;
  if (getLocalTime(&timeinfo)) {
    char dateString[12];
    char timeString[10];
    strftime(dateString, sizeof(dateString), "%Y-%m-%d", &timeinfo);
    strftime(timeString, sizeof(timeString), "%H:%M:%S", &timeinfo);
    
    // Generate a unique key based on timestamp (or use Firebase.push() to generate one)
    String timestamp = String(timeinfo.tm_year + 1900) + "-" +
                       String(timeinfo.tm_mon + 1) + "-" +
                       String(timeinfo.tm_mday) + "_" +
                       String(timeinfo.tm_hour) + "-" +
                       String(timeinfo.tm_min) + "-" +
                       String(timeinfo.tm_sec);

    // Construct the path for the new entry
    String path = "/rain_data/" + timestamp;

    // Update Firebase with rain data
    if (Firebase.setInt(firebaseData, path + "/rain_intensity", intensity) &&
        Firebase.setString(firebaseData, path + "/date", dateString) &&
        Firebase.setString(firebaseData, path + "/time", timeString)) {
      Serial.println("Firebase update successful!");
    } else {
      Serial.println("Firebase update failed.");
      Serial.println("Reason: " + firebaseData.errorReason());
    }
  }
}

void handleRoot() {
  server.send(200, "text/plain", "Hello, this is your Arduino speaking!");
}

void handleConnect() {
  server.send(200, "text/plain", "Connected");
}

void handleMoveServo() {
  moveServo(0);  // Move the servo anti-clockwise
  servo.detach();
  server.send(200, "text/plain", "Servo moved to position 180");
}
