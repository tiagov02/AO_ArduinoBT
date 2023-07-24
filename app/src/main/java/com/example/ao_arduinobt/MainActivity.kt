package com.example.ao_arduinobt

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.lang.UProperty.NAME
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
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_ENABLE_BT = 1
    }
    private val scanResults = mutableListOf<ScanResult>()
    private val MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var connected = false
    private lateinit var bluetoothSocket: BluetoothSocket
    val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
    val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.getAdapter()
    private lateinit var inputStream: InputStream



    private fun <T> removeItems(list: MutableList<T>, predicate: Predicate<T>) {
        val newList: MutableList<T> = ArrayList()
        list.filter { predicate.test(it) }.forEach { newList.add(it) }
        list.removeAll(newList)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                    Log.e("BT-ERROR", "Erro ao conectar ao dispositivo: ${e.message}")
                }
            } else {
                Log.e("BT-ERROR", "Dispositivo HC-06 não encontrado nos dispositivos emparelhados.")
            }
        } else {
            // Se já estiver conectado, inicie a recepção de dados
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
    }
}