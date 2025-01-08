# Clothassist

Clothassist is an IoT-based clothes hanger system designed to simplify the process of drying clothes. It automates the movement of the hanger in response to weather conditions, stores rainfall data, and displays it in the mobile app for enhanced user insights.

## Features
- **Rain Detection**: Automatically detects rain using a rain sensor and moves the hanger to a protected area.
- **Rainfall Data Storage**: Records rain intensity and stores the data in Firebase for easy access and tracking.
- **Real-Time Monitoring**: Displays rainfall data and system status in the Android mobile app.
- **Remote Control**: Control the hanger's movement via an Android mobile application.
- **Smart Automation**: Adjusts the hanger's position based on weather conditions.
- **Energy Efficient**: Designed with low-power hardware components.

## Important Notes ⚠️
1. IP Address Configuration:
   You will need to find and configure your own ESP32's IP address. Ensure your ESP32 is connected to the same network as your mobile device.
   - Update the IP address in both the Arduino code and the mobile application configuration.

2. Firebase Configuration:
   Firebase is required to store rainfall data. You must set up your own Firebase account and database.
   - Replace placeholders for the API key, database URL, and project credentials in both the Arduino code and the mobile app source code.

## Hardware Components
- **ESP32**: The microcontroller for handling IoT functionalities and communication.
- **Rain Sensor**: Detects rain and triggers automation.
- **Servo Motor**: Moves the hanger to the desired position.
- **Power Supply**: Provides power to the components.

## Software Components
- **Firebase**: Used to store and retrieve rainfall data, including rain intensity.
- **Android Mobile Application**: Displays real-time rainfall data and allows remote control of the hanger.
- **Arduino IDE**: Used for programming the ESP32 and other hardware components.

## Getting Started
### Prerequisites
- ESP32 microcontroller
- Rain sensor and servo motor
- Firebase account and database configuration
- Arduino IDE installed on your computer
- Android device running Android 6.0 or later for the mobile application

### Installation
1. Clone the repository:
```
   git clone https://github.com/your-username/Clothassist.git
```
2. Open the Arduino sketch in Arduino IDE and configure:
   - Firebase: Add your API key and database URL.
   - IP Address: Specify the IP address of your ESP32.
        
3. Upload the code to the ESP32.
4. Assemble the hardware components as per the provided circuit diagram (included in the repository).
5. Install the APK for the Android application on a compatible device and update the IP address and Firebase configuration.

## How to Use
1. Place the clothes on the hanger and power on the system.

2. The rain sensor detects rain and automatically moves the hanger to a sheltered area using the servo motor.
   
3. Rainfall data, including rain intensity, is sent to Firebase and displayed in the mobile app.
   
4. Use the Android app to manually control the hanger's position or monitor the system's status and rainfall data in real time.
