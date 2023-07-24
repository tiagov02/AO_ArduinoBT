package com.example.ao_arduinobt

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Predicate
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_ENABLE_BT = 1
    }
    private val scanResults = mutableListOf<ScanResult>()

    private fun <T> removeItems(list: MutableList<T>, predicate: Predicate<T>) {
        val newList: MutableList<T> = ArrayList()
        list.filter { predicate.test(it) }.forEach { newList.add(it) }
        list.removeAll(newList)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.getAdapter()
        if (bluetoothAdapter == null) {
            //super.onDestroy();
            println("NULL" +
                    "")
        }

        if (bluetoothAdapter?.isEnabled == false) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.BLUETOOTH,Manifest.permission.BLUETOOTH_SCAN,Manifest.permission.BLUETOOTH_CONNECT),
                    REQUEST_ENABLE_BT
                )
            }
        }
        findViewById<Button>(R.id.seachDevices).setOnClickListener {
            val btDevices = findViewById<TextView>(R.id.btDevices)
            btDevices.text = ""
            val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
            //verifica e pesquisa dispositivos
            pairedDevices?.forEach { device ->
                val deviceName = device.name
                val deviceHardwareAddress = device.address
                println("Entrei aqui")

                btDevices.text = btDevices.text.toString() + "\n" + deviceName + " || " + deviceHardwareAddress
            }

             val scanCallback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult?) {
                    super.onScanResult(callbackType, result)
                    if (result != null && ContextCompat.checkSelfPermission(
                            applicationContext,
                            Manifest.permission.BLUETOOTH
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        val indexQuery =
                            scanResults.indexOfFirst { it.device.address == result.device.address }
                        if (indexQuery != -1) {
                            scanResults[indexQuery] = result
                        } else {
                            scanResults.add(result)
                            scanResults.sortByDescending { it.rssi }
                            val predicate = Predicate { x: ScanResult -> x.device.name == null }
                            removeItems(scanResults, predicate)
                        }
                    }
                }

                override fun onScanFailed(errorCode: Int) {
                    super.onScanFailed(errorCode)
                    Log.e("ScanCallback", "onScanFailed: code $errorCode")
                }
            }
            scanResults.forEach { result ->
                btDevices.text = btDevices.text.toString() + "\n" + result.device.name + " || " + result.device.address
            }
            findViewById<Button>(R.id.connectToDevice).setOnClickListener {

            }
        }
    }
}