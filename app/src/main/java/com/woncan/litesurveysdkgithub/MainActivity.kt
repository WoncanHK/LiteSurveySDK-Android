package com.woncan.litesurveysdkgithub

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.woncan.litesurvey.data.BatteryInfo
import com.woncan.litesurvey.data.ImuData
import com.woncan.litesurvey.data.LiteSurveyDeviceInfo
import com.woncan.litesurvey.data.LiteSurveyLocation
import com.woncan.litesurvey.data.NtripAccount
import com.woncan.litesurvey.data.NtripMountpoint
import com.woncan.litesurvey.data.SatelliteInfo
import com.woncan.litesurvey.deviceInterface.LiteSurveyDevice
import com.woncan.litesurvey.deviceInterface.LiteSurveyDeviceScanner
import com.woncan.litesurvey.enumClass.GnssFixStatus
import com.woncan.litesurvey.enumClass.NmeaType
import com.woncan.litesurvey.enumClass.NtripConnectionStatus
import com.woncan.litesurvey.listener.LiteSurveyDeviceListener
import com.woncan.litesurvey.listener.NtripMountpointCallback
import com.woncan.litesurveysdkgithub.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() { companion object {

		const val TAG : String = "TAG_DEVICE_MAIN"
}

		private lateinit var binding : ActivityMainBinding
		override fun onCreate(savedInstanceState : Bundle?) {
				super.onCreate(savedInstanceState)
				enableEdgeToEdge()
				binding = ActivityMainBinding.inflate(layoutInflater)
				setContentView(binding.getRoot())
				ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v , insets ->
						val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
						v.setPadding(systemBars.left , systemBars.top , systemBars.right , systemBars.bottom)
						insets
				}

				/** Device scanning button */
				binding.btnScan.setOnClickListener { _ ->
						scan()
				}

				val adapter = ArrayAdapter<String>(this , R.layout.list_item)
				val tvMountpoint = (binding.tfMountpoint.editText as? MaterialAutoCompleteTextView)?.apply {
						setAdapter(adapter)
				}
				binding.btnRefreshMountpoint.setOnClickListener {
						val address : String = binding.etAddress.text.toString()
						val port = binding.etPort.text.toString().toIntOrNull() ?: return@setOnClickListener


						LiteSurveyDevice.queryNtripMountpoint(address ,
						                                      port ,
						                                      object : NtripMountpointCallback {
								                                      override fun onNtripMountpointFailure(
										                                      errorMessage : String) {
										                                      adapter.clear()
								                                      }

								                                      override fun onNtripMountpointListRetrieved(
										                                      mountpointList : List<NtripMountpoint>) {
										                                      runOnUiThread {
												                                      adapter.clear()
												                                      adapter.addAll(mountpointList.map { it.mountPoint })
												                                      mountpointList.firstOrNull()?.let {
														                                      tvMountpoint?.setText(it.mountPoint ,
														                                                            false)
												                                      }
										                                      }

								                                      }
						                                      })
				}

				// Ntrip
				binding.switchNtrip.setOnCheckedChangeListener { _ , isChecked ->
						if (isChecked) {
								val port = binding.etPort.text.toString().toIntOrNull()
										?: return@setOnCheckedChangeListener
								val address : String = binding.etAddress.text.toString()
								val username : String = binding.etUsername.text.toString()
								val password : String = binding.etPassword.text.toString()
								val mountPoint = binding.tvMountpoint.text.toString()

								val account = NtripAccount(address , port , mountPoint , username , password)
								mDevice?.startNtripConnection(account , transmitNmeaPosition = true)
						} else {
								mDevice?.stopNtripConnection()
						}
				}

				/** NMEA GNGSA message output switch */
				binding.switchGsa.setOnCheckedChangeListener { _ , isChecked ->
						mDevice?.setNmeaOutput(NmeaType.NmeaTypeGSA , isChecked)
				}
				/** NMEA GSV message output switch */
				binding.switchGsv.setOnCheckedChangeListener { _ , isChecked ->
						mDevice?.setNmeaOutput(NmeaType.NmeaTypeGSV , isChecked)
				}
				/** NMEA VTG message output switch */
				binding.switchVtg.setOnCheckedChangeListener { _ , isChecked ->
						mDevice?.setNmeaOutput(NmeaType.NmeaTypeVTG , isChecked)
				}

				/** RTCM output toggle switch */
				binding.switchRtcm.setOnCheckedChangeListener { _ , isChecked ->
						if (isChecked) {
								mDevice?.enableRtcmOutput(1074 , 1084 , 1124)
						} else {
								mDevice?.disableRtcmOutput()
						}
				}
				/** Laser status switch */
				binding.switchLaser.setOnCheckedChangeListener { _ , isChecked ->
						mDevice?.setLaserState(isChecked)
				}
				/** IMU output rate switch */
				binding.switchImu.setOnCheckedChangeListener { _ , isChecked ->
						mDevice?.setImuOutput(if (isChecked) {
								1000
						} else {
								10000
						})
				}

				/** Query firmware upgrade*/
				binding.btnQueryFirmware.setOnClickListener {
						mDevice?.queryFirmwareUpgrade()
				}
		}

		private var mDevice : LiteSurveyDevice? = null

		/** Device scanning logic */
		private fun scan() {
				LiteSurveyDeviceScanner.startScan(this , object : LiteSurveyDeviceListener() {

						/** Satellite Information callback */
						override fun onSatelliteInfo(satelliteInfoList : List<SatelliteInfo>) {
								Log.i(TAG , "onSatelliteInfo: ${satelliteInfoList.size}")
						}

						/** NMEA message callback */
						override fun onNmeaMessage(nmeaMessage : String) {
								Log.i(TAG , "onNmeaMessage: $nmeaMessage")
						}

						/** RTCM message callback */
						override fun onRtcmMessage(rtcmMessage : ByteArray) {
								Log.i(TAG , "onRtcmMessage: ")
						}

						/** Device information callback */
						override fun onDeviceInfo(deviceInfo : LiteSurveyDeviceInfo) {
								Log.i(TAG , deviceInfo.toString())
								runOnUiThread {
										binding.tvDeviceId.text = deviceInfo.deviceId
								}
						}

						/** Battery information callback */
						override fun onBatteryInfo(batteryInfo : BatteryInfo) {
								runOnUiThread {
										binding.tvBatteryPercentage.text = String.format(Locale.getDefault() ,
										                                                 "%d%s" ,
										                                                 batteryInfo.percentageRemaining ,
										                                                 "%")
								}
						}

						/** Device disconnection callback */
						override fun onDisconnect() {
								Log.i(TAG , "=== onDisconnect ===")
						}

						/** Inertial measurement unit (IMU) data callback */
						override fun onImuData(imuData : ImuData) {
								Log.i(TAG , "onImuData: ${imuData.accelerometer.contentToString()}\n")
						}

						/** Laser state changed callback */
						override fun onLaserStateChange(laserState : Boolean) {
								Log.i(TAG , "onLaserStateChange: $laserState")

						}

						/** NTRIP connection error callback */
						override fun onNtripConnectionError(errorMessage : String) {
								Log.i(TAG , "onNtripConnectionError: $errorMessage")
						}

						/** NTRIP connection status change callback */

						/** Firmware upgrade availability query result callback */
						override fun onFirmwareUpgradeAvailability(newFirmwareAvailable : Boolean) {
								runOnUiThread {
										binding.tvFirmwareInfo.text = String.format(Locale.getDefault() ,
										                                            "Firmware upgrade available : %s" ,
										                                            newFirmwareAvailable)
								}
						}

						/** Firmware upgrade progress percentage callback */
						override fun onFirmwareUpgradeProgress(progressPercentage : Int) {
								Log.i(TAG , "onFirmwareUpgradeProgress: $progressPercentage")
						}

						/** Firmware upgrade error callback */
						override fun onFirmwareUpgradeError(errorMessage : String) {
								Log.i(TAG , "onFirmwareUpgradeError: $errorMessage")
						}
						/** Scan failure callback */
						override fun onScanFailure(errorMessage : String) {
								super.onScanFailure(errorMessage)
								Log.i(TAG , "onScanFailure: $errorMessage")
						}
						/** Connect callback */
						override fun onConnect(device : LiteSurveyDevice) {
								super.onConnect(device)
								mDevice = device
						}
						/** Location changed callback */
						override fun onLocationChanged(location : LiteSurveyLocation) {
								runOnUiThread {
										binding.tvStatus.text = when (location.fixStatus) {
												GnssFixStatus.GnssFixUnknown -> "Unknown"
												GnssFixStatus.GnssFixSingle  -> "Single"
												GnssFixStatus.GnssFixDGNSS   -> "DGNSS"
												GnssFixStatus.GnssFixFixed   -> "Fixed"
												GnssFixStatus.GnssFixFloat   -> "Float"
										}
										binding.tvLatitude.text = String.format(Locale.getDefault() ,
										                                        "%.9f° %s" ,
										                                        location.latitude ,
										                                        if (location.latitude > 0) {
												                                        "N"
										                                        } else {
												                                        "S"
										                                        })
										binding.tvLongitude.text = String.format(Locale.getDefault() ,
										                                         "%.9f° %s" ,
										                                         location.longitude ,
										                                         if (location.longitude > 0) {
												                                         "E"
										                                         } else {
												                                         "W"
										                                         })

										binding.tvAltitude.text =
												String.format(Locale.getDefault() , "%.3fm" , location.altitude)

								}

						}
						/** Ntrip connection status changed callback */
						override fun onNtripConnectionStatusChange(status : NtripConnectionStatus) {
								super.onNtripConnectionStatusChange(status)
								Log.i(TAG , "onNtripConnectionStatusChange: $status")
						}
				})
		}

}