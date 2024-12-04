# SmartHome

This is a SmartHome application developed for Android using Kotlin. The app simulates various sensors, including temperature, humidity, ultrasonic distance, and a keypad for PIN code entry. It includes features like sound notifications, vibrations, and visual feedback to control and monitor the smart home environment.

## Features

- **PIN Code Entry**: A keypad for entering a 4-digit PIN to access the control system.
- **Temperature Control**: Displays and adjusts the temperature, with automatic fan speed adjustment.
- **Distance Sensor**: Simulates an ultrasonic sensor for measuring proximity, with alarm notifications when distance is below a threshold.
- **Alarm Systems**: Alerts for high temperature (fire alarm) and low distance (theft alarm) with sound notifications and vibrations.
- **User Interface**: A clean and simple UI built with Jetpack Compose.

## Technologies Used

- **Kotlin**: The programming language used for app development.
- **Jetpack Compose**: The UI toolkit for building modern Android UIs.
- **MediaPlayer & ToneGenerator**: For sound notifications.
- **Vibrator**: For vibration feedback.
  

## App Flow

1. **Home Page**: The user is greeted with a welcome screen with an image and a button to start the control system.
2. **Control Page**: 
   - Enter a 4-digit PIN using the on-screen keypad.
   - View and control temperature, and distance sensors.
   - Adjust the temperature to control the fan speed (0, 1, 2,3).
   - Receive alarm notifications based on sensor readings (fire alarm for high temperature, theft alarm for low distance).

## Sensors and Alarms

- **Temperature Alarm**: If the temperature exceeds 30°C, an alert sound will play. If it exceeds 50°C, a fire alarm will sound.
- **Theft Alarm**: If the distance sensor detects an object closer than 50 cm, a theft alarm will be triggered.

## Future Enhancements

- Integrate actual hardware sensors for temperature, and ultrasonic distance.
- Implement additional smart home features like lighting control, smoke detection, and energy monitoring.
- Enhance the UI design and add animations for a smoother user experience.
