package com.example.ao_arduinobt

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanResult
import android.content.Context
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

class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_ENABLE_BT = 1
    }

    private val MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var connected = false
    private lateinit var bluetoothSocket: BluetoothSocket
    private lateinit var bluetoothManager: BluetoothManager
    var bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var inputStream: InputStream


    private val historyViewModel: HistoryViewModel by viewModels {
        HistoryViewModelFactory((application as HistoryAplication).repository)
    }


    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermissions()

        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        if (bluetoothAdapter == null) {
            //super.onDestroy();
            println("NULL" +
                    "")
        }

        if (bluetoothAdapter?.isEnabled == false) {
           checkPermissions()
        }
        else{
            findViewById<Button>(R.id.seachDevices).setOnClickListener {
                val btDevices = findViewById<TextView>(R.id.btDevices)
                val btConn = findViewById<Button>(R.id.connectToDevice)
                btDevices.text = ""
                val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
                //verifica e pesquisa dispositivos
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
            val targetDevice = bluetoothAdapter!!.bondedDevices.find { it.name == "HC-06" }
            if (targetDevice != null) {
                try {
                    bluetoothSocket = targetDevice.createRfcommSocketToServiceRecord(MY_UUID)
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
            // If Connected Receive Data
            startReceivingData()
        }
    }

    private fun startReceivingData() {
        Observable.create<ByteArray> { emitter ->
            val buffer = ByteArray(1024)
            try {
                while (connected) {
                    val bytesRead = inputStream.read(buffer)
                    if (bytesRead == -1) {
                        break
                    }
                    val data = buffer.copyOf(bytesRead)
                    emitter.onNext(data)
                }
                emitter.onComplete()
            } catch (e: IOException) {
                emitter.onError(e)
            }
        }
            .observeOn(Schedulers.io())
            .subscribe { data ->
                handleData(data)
            }
    }

    //This function will pass the data to a Room Database
    fun handleData(data: ByteArray){
        val receivedString = String(data, charset("UTF-8"))
        Log.d("TAG", "Dados recebidos: $receivedString")
        val spl = receivedString.split(";")

        val temperature = spl[0]
        val humidity = spl[1]


        //TODO: Ver DateTime
        historyViewModel.insert(History(temperature.toFloat(),humidity.toFloat()))
    }
}