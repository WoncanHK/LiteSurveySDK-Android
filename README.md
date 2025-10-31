# LiteSurvey SDK Demo for Android

[![Maven Central](https://img.shields.io/maven-central/v/com.woncan.litesurvey/sdk.svg)](https://central.sonatype.com/artifact/com.woncan.litesurvey/sdk/)

[![License: Apache-2.0 (shields.io)](https://img.shields.io/badge/License-Apache--2.0-c02041?logo=apache)](https://www.apache.org/licenses/LICENSE-2.0)

This demo project shows how to use the LiteSurvey SDK to connect to Woncan LiteSurvey devices.

LiteSurvey SDK for Android can connect to LiteSurvey devices and

- Output device data (location, satellite, etc.) to the application
- Configure device settings
- Perform device firmware upgrade
- and more

By integrating this SDK, you can support LiteSurvey device's high-accuracy position in your own app.

This SDK is also used in Woncan's official Android app, LiteGPS.

## Android system requirement

Minimum Android API level 26 (Android 8.0) is required.

## How to use the SDK in your own app

### 1. Adding the library to your project

**Method 1 (Recommended) - Add dependency in build.gradle(.kts)**

This SDK is published to Maven Central. The following link explains how to add it as a dependency.

```
dependencies {
    implementation("com.woncan.litesurvey:sdk:2.0.0")
    // replace "2.0.0" with any available version
}
```

**Method 2 - Add AAR Library**

You can download the AAR library file under "Releases" and add it manually to your project.


### 2. Scanning for devices

For Bluetooth devices, use the `LiteSurveyDeviceScanner.startBluetoothScan`method to start scanning. Android system will launch the companion device pairing dialog and guide the user through the Bluetooth connection process.

For USB devices,  use the `LiteSurveyDeviceScanner.startUsbSerialScan`method instead.

The combined `LiteSurveyDeviceScanner.startScan` method will first scan for USB devices and, if no USB devices are found, scan for Bluetooth devices.

### 3. Receive device data (e.g. location)

You will need a listener implementing LiteSurveyDeviceListener to receive device data. Register the listener as you call any of the scanning methods in LiteSurveyDeviceScanner.

### 4. Configuring devices (optional)

Use the `LiteSurveyDevice` interface to configure the device.

### Code example

The following code snippet is a minimal code example in Kotlin that

1. Scans for and connect to LiteSurvey devices.
2. Prints the location information reported by the device to Logcat.

```kotlin
// Import LiteSurvey packages as needed
import com.woncan.litesurvey.deviceInterface.LiteSurveyDeviceScanner
import com.woncan.litesurvey.listener.LiteSurveyDeviceListener
import com.woncan.litesurvey.data.LiteSurveyLocation

// Start scanning (via USB and Bluetooth)
LiteSurveyDeviceScanner.startScan(context , object : LiteSurveyDeviceListener() {

	// Location information callback
	override fun onLocationChanged(location : LiteSurveyLocation) {
		// Output latitude and longitude to Logcat
		Log.i(TAG,"onLocationChanged: ${location.getLatitude()}  ${location.getLongitude()}")
	}
	// Add other callbacks here as necessary

})
```

## Location information reported by the SDK

Location information is reported as the `LiteSurveyLocation` class. This class inherits from the system Location class [ `android.location.Location`](https://developer.android.com/reference/kotlin/android/location/Location#). Please see `LiteSurveyLocation` class definition for details.

Table: LiteSurvey SDK support for system Location class parameters

|            Parameter            | Supported by LiteSurvey | Notes                         |
| :-----------------------------: | ----------------------- | ----------------------------- |
|            Accuracy             | Yes                     |                               |
|            Altitude             | Yes                     |                               |
|             Bearing             | Yes                     |                               |
|      BearingAccuracyMeters      | No                      |                               |
|    ElapsedRealtimeAgeMillis     | No                      |                               |
| ElapsedRealtimeUncertaintyNanos | No                      |                               |
|             Extras              | No                      |                               |
|            Latitude             | Yes                     |                               |
|            Longitude            | Yes                     |                               |
|    MslAltitudeAccuracyMeters    | Yes                     | Equals VerticalAccuracyMeters |
|        MslAltitudeMeters        | Yes                     |                               |
|            Provider             | Yes                     | Fixed as "LiteSurvey"         |
|              Speed              | Yes                     |                               |
|  SpeedAccuracyMetersPerSecond   | No                      |                               |
|              Time               | Yes                     |                               |
|     VerticalAccuracyMeters      | Yes                     |                               |

## Android system permissions

LiteSurvey SDK declares the following permission(s) in its AndroidManifest.xml file.

```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
```

## Contact email

For technical or business inquiries, please contact support@woncan.com.hk

