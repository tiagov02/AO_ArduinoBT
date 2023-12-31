package com.example.ao_arduinobt

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.*
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.ao_arduinobt.RoomDB.History
import com.example.ao_arduinobt.RoomDB.HistoryAplication
import com.example.ao_arduinobt.RoomDB.HistoryViewModel
import com.example.ao_arduinobt.RoomDB.HistoryViewModelFactory
import java.io.IOException
import java.io.InputStream
import java.util.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_ENABLE_BT = 1
        private const val SHOW_ALL_REQUEST_CODE = 2
        private const val SHOW_GRAPHS_REQUEST_CODE = 3
        private const val SHOW_GRAPH_SECONDS = 4
    }

    private lateinit var MY_UUID :UUID
    private var connected = false
    private lateinit var bluetoothSocket: BluetoothSocket
    private lateinit var bluetoothManager: BluetoothManager
    var bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var inputStream: InputStream
    private var targetDevice: BluetoothDevice? = null


    private val historyViewModel: HistoryViewModel by viewModels {
        HistoryViewModelFactory((application as HistoryAplication).repository)
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermissions()

        MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        if (bluetoothAdapter == null) {
            //super.onDestroy();
            //The Bluetooth function do not exists in device
            Toast.makeText(this,"The device doen't have bluetooth", Toast.LENGTH_LONG).show()
            super.onDestroy()
        }

        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
        else{
            searchDevicesAndConnect()
        }

        findViewById<Button>(R.id.ViewData).setOnClickListener {
            val intent = Intent(this@MainActivity,ShowAllActivity::class.java)
            startActivityForResult(intent, SHOW_ALL_REQUEST_CODE)
        }
        findViewById<Button>(R.id.ViewGraphs).setOnClickListener {
            val intent = Intent(this@MainActivity,DashboardActivity::class.java)
            startActivityForResult(intent, SHOW_GRAPHS_REQUEST_CODE)
        }
        findViewById<Button>(R.id.ViewGraphPerSecond).setOnClickListener {
            val intent = Intent(this@MainActivity, DashboardSeconds::class.java)
            startActivityForResult(intent, SHOW_GRAPH_SECONDS)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == Activity.RESULT_OK){
                searchDevicesAndConnect()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun searchDevicesAndConnect(){
        findViewById<Button>(R.id.seachDevices).setOnClickListener {
            val btDevices = findViewById<TextView>(R.id.btDevices)
            val btConn = findViewById<Button>(R.id.connectToDevice)
            btDevices.text = ""
            val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
            //verify devices
            pairedDevices?.forEach { device ->
                val deviceName = device.name
                val deviceHardwareAddress = device.address
                println("Entrei aqui")

                btDevices.text = btDevices.text.toString() + "\n" + deviceName + " || " + deviceHardwareAddress
            }
            btConn.isEnabled = true
            btConn.setOnClickListener {
                connectToHC06()
            }
        }
    }

    fun checkPermissions(){
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
    @SuppressLint("MissingPermission")
    private fun connectToHC06() {
        if (!connected) {
            targetDevice = bluetoothAdapter!!.bondedDevices.find { it.name == "HC-06"}
            Log.d("BT/DEVICE MAC", targetDevice.hashCode().toString())
            if (targetDevice != null) {
                try {
                    bluetoothSocket = targetDevice!!.createRfcommSocketToServiceRecord(MY_UUID)
                    bluetoothSocket.connect()
                    connected = true
                    inputStream = bluetoothSocket.inputStream
                    startReceivingData()
                } catch (e: IOException) {
                    Log.e("BT-ERROR", "Error in connecting Device: ${e.message}")
                    Toast.makeText(this,"Error in connecting Device: ${e.message}",Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("BT-ERROR", "Device HC-06 not founded in paired devices please pair them!")
                Toast.makeText(this,"Device HC-06 not founded in paired devices please pair them!",Toast.LENGTH_SHORT).show()
            }
        } else {
            startReceivingData()
        }
    }

    private fun startReceivingData() {
        //Create the observer and open the socket for bluetooth
        Toast.makeText(this,"Sucessfully connected! Receiving Data!!",Toast.LENGTH_SHORT).show()
        Observable.create<ByteArray> { emitter ->
            val buffer = ByteArray(1024)
            var hasDataStarted = false
            try {
                while (connected) {
                    val bytesRead = inputStream.read(buffer)
                    if (bytesRead == -1) {
                        break
                    }
                    if (!hasDataStarted && buffer[0].toChar() == '@') {
                        hasDataStarted = true
                    }
                    if (hasDataStarted) {
                        val data = buffer.copyOf(bytesRead)
                        emitter.onNext(data)
                    }
                }
                emitter.onComplete()
            } catch (e: IOException) {
                println(e)
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe ({ data ->
                handleData(data)
            },
                {
                    error ->
                    Log.e("ERRO-BT",error.message.toString())
                })
    }

    //This function will pass the data to a Room Database
    fun handleData(data: ByteArray){
        //treat the data
        val receivedString = String(data, charset("UTF-8"))
        Log.d("TAG", "Dados recebidos: $receivedString")

        if (receivedString.contains("@")){
            val sanitizedString = if (receivedString.isNotBlank()) receivedString.substring(1) else ""
            sendDataToDB(sanitizedString)
        }else{
            sendDataToDB(receivedString)
        }
    }

    fun sendDataToDB(data: String){
        //sed the data to a room database
        data.split("\n").forEach{string ->
            if(string.isNotBlank()){
                val spl_individual = string.split(";")

                if(spl_individual.size >= 2){
                    val temperature = spl_individual[0].toFloat()
                    val humidity = spl_individual[1].toFloat()

                    historyViewModel.insert(History(temperature,humidity, LocalDateTime.now()))
                }
            }
        }
    }
}