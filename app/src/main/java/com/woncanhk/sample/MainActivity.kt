package com.woncanhk.sample

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.woncan.device.Device
import com.woncan.device.NMEA
import com.woncan.device.ScanManager
import com.woncan.device.bean.DeviceInfo
import com.woncan.device.bean.DeviceNtripAccount
import com.woncan.device.bean.WLocation
import com.woncan.device.device.DeviceInterval
import com.woncan.device.listener.DeviceStatesListener
import com.woncan.device.listener.WLocationListener
import com.woncanhk.sample.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {

		companion object {

				val TAG : String = "TAG_DEVICE_MAIN"
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

				val adapter = DeviceAdapter()

				binding.tvLog.movementMethod = ScrollingMovementMethod.getInstance()
				binding.btnSearch.setOnClickListener { v ->
						//搜索所有设备
						if (! requestPermission()) {
								Toast.makeText(this , "need permission" , Toast.LENGTH_SHORT).show()
								return@setOnClickListener
						}
						adapter.setNewInstance(null)
						adapter.addData(ScanManager.getPhoneDevice())
						ScanManager.scanDevice(this) { device : Device ->

								if (! adapter.data.contains(device)) {
										adapter.addData(device)
								}
						}
				}

				binding.recyclerView.layoutManager = LinearLayoutManager(this)
				binding.recyclerView.adapter = adapter
				adapter.setOnItemClickListener { adapter1 , view , position ->
						ScanManager.stopScan(this@MainActivity)
						connect(adapter.getItem(position))
				}
		}

		private fun connect(device : Device) {
				device.registerSatesListener(object : DeviceStatesListener() {
						override fun onConnectionStateChange(isConnect : Boolean) {
								binding.tvLog.append(if (isConnect) "Connected\n" else "Disconnect\n")
						}

						override fun onDeviceAccountChange(account : DeviceNtripAccount) {
								super.onDeviceAccountChange(account)
						}

						override fun onDeviceInfoChange(deviceInfo : DeviceInfo) {
								binding.tvDeviceInfo.text = String.format(Locale.CHINA ,
								                                          "model：%s\ndeviceID：%s\nproductName：%s" ,
								                                          deviceInfo.model ,
								                                          deviceInfo.deviceID ,
								                                          deviceInfo.productNameEN)
						}

						override fun onLaserStateChange(isOpen : Boolean) {
						}
				})

				device.registerLocationListener(object : WLocationListener {
						override fun onReceiveLocation(location : WLocation) {
								Log.i(TAG , "onReceiveLocation: wLocation")
								binding.tvLocation.text = String.format(Locale.CHINA ,
								                                        "latitude：%.8f\nlongitude：%.8f\naltitude：%.3f\nfixStatus：%d" ,
								                                        location.latitude ,
								                                        location.longitude ,
								                                        location.altitude ,
								                                        location.fixStatus)
						}

						override fun onError(errCode : Int , message : String) {
								binding.tvLog.append(String.format(Locale.CHINA ,
								                                   "onError:%d  %s\n" ,
								                                   errCode ,
								                                   message))
						}
				})
				device.setNMEAListener { s : String ->
						Log.i(TAG , "onReceiveNMEA: $s")
				}

				//        device.openRTCM(new RTCM[]{RTCM.RTCM1074}, RTCMInterval.SECOND_3);
//        device.registerRTCMAListener(new RTCMListener() {
//            @Override
//            public void onReceiveRTCM(int[] ints, byte[] bytes) {
//
//            }
//
//            @Override
//            public void onReceiveSFR(byte[] bytes) {
//
//            }
//        });
//        device.registerSatelliteListener(new SatelliteListener() {
//            @Override
//            public void onReceiveSatellite(List<SatelliteInfo> list) {
//
//            }
//        });

//        device.closeRTCM();
//        device.setLaserState(true);
//        int port=1;
				device.setNMEAEnable(NMEA.GGA , true)
				device.setNMEAEnable(NMEA.GSV , true)
				device.setNMEAEnable(NMEA.GSA , true)
				device.setNMEAEnable(NMEA.GLL , true)
				device.setNMEAEnable(NMEA.GMC , true)
				device.setNMEAEnable(NMEA.VTG , true)

				device.setNMEAListener { }
				device.connect(this)
				Thread {
						try {
								Thread.sleep(2000)
						} catch (e : InterruptedException) {
								throw RuntimeException(e)
						}
						device.setInterval(DeviceInterval.HZ_5)
				}.start()

//        device.setAccount("",0,"","","");
		}

		private fun requestPermission() : Boolean {
				val list : MutableList<String> = ArrayList()
				if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
						list.add(Manifest.permission.ACCESS_COARSE_LOCATION)
				}
				if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
						list.add(Manifest.permission.ACCESS_FINE_LOCATION)
				}
				if (checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
						list.add(Manifest.permission.BLUETOOTH)
				}
				if (checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
						list.add(Manifest.permission.BLUETOOTH_ADMIN)
				}
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
						if (checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
								list.add(Manifest.permission.BLUETOOTH_SCAN)
						}
				}
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
						if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
								list.add(Manifest.permission.BLUETOOTH_CONNECT)
						}
				}

				resultLauncher.launch(list.toTypedArray<String>())
				return list.isEmpty()
		}

		private val resultLauncher =
				registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { _ : Map<String , Boolean> -> }
}