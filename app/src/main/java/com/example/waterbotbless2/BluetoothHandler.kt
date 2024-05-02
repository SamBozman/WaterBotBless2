package com.example.waterbotbless2

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.welie.blessed.*
import com.welie.blessed.WriteType.WITH_RESPONSE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.nio.ByteOrder
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("StaticFieldLeak")
object BluetoothHandler {
    private lateinit var context: Context
    lateinit var centralManager: BluetoothCentralManager //Blessed Library
    private val handler = Handler(Looper.getMainLooper())
    private val measurementFlow_ = MutableStateFlow("Waiting for measurement")
    val measurementFlow = measurementFlow_.asStateFlow()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // UUIDs for the WaterBot
    private val WaterBot_UUID: UUID = UUID.fromString("19b10000-e8f2-537e-4f6c-d104768a1214")
    private val WaterBot_CHARACTERISTIC_UUID: UUID = UUID.fromString("19b10000-e8f2-537e-4f6c-d104768a1214")

    private val bluetoothPeripheralCallback = object : BluetoothPeripheralCallback() {
        override fun onServicesDiscovered(peripheral: BluetoothPeripheral) {
            peripheral.requestConnectionPriority(ConnectionPriority.HIGH)

            //startNotify and stopNotify turn notifications/indications on or off.
            peripheral.startNotify(WaterBot_UUID, WaterBot_CHARACTERISTIC_UUID)
        }

        override fun onNotificationStateUpdate(peripheral: BluetoothPeripheral, characteristic: BluetoothGattCharacteristic, status: GattStatus) {
            if (status == GattStatus.SUCCESS) {
                val isNotifying = peripheral.isNotifying(characteristic)
                Timber.i("SUCCESS: Notify set to '%s' for %s", isNotifying, characteristic.uuid)

            } else {
                Timber.e("ERROR: Changing notification state failed for %s (%s)", characteristic.uuid, status)
            }
        }

        // onCharacteristicUpdate
        override fun onCharacteristicUpdate(peripheral: BluetoothPeripheral, value: ByteArray, characteristic: BluetoothGattCharacteristic, status: GattStatus) {
            when (characteristic.uuid) {

                WaterBot_CHARACTERISTIC_UUID -> {

                    sendMeasurement(value.toString())
                }

            }
        }

        fun sendMeasurement(value: String) {
            scope.launch {
                Timber.i(value)
                measurementFlow_.emit(value)
            }
        }

    }

    private val bluetoothCentralManagerCallback = object : BluetoothCentralManagerCallback() {
        override fun onDiscovered(peripheral: BluetoothPeripheral, scanResult: ScanResult) {
            Timber.i("Found peripheral '${peripheral.name}' with RSSI ${scanResult.rssi}")
            centralManager.stopScan()

            if (peripheral.needsBonding() && peripheral.bondState == BondState.NONE) {
                // Create a bond immediately to avoid double pairing popups
                centralManager.createBond(peripheral, bluetoothPeripheralCallback)
            } else {
                centralManager.connect(peripheral, bluetoothPeripheralCallback)
            }
        }

        override fun onConnected(peripheral: BluetoothPeripheral) {
            Timber.i("connected to '${peripheral.name}'")
            Toast.makeText(context, "Connected to ${peripheral.name}", LENGTH_SHORT).show()
        }

        override fun onDisconnected(peripheral: BluetoothPeripheral, status: HciStatus) {
            Timber.i("disconnected '${peripheral.name}'")
            Toast.makeText(context, "Disconnected ${peripheral.name}", LENGTH_SHORT).show()
            handler.postDelayed(
                { centralManager.autoConnect(peripheral, bluetoothPeripheralCallback) },
                15000
            )
        }

        override fun onConnectionFailed(peripheral: BluetoothPeripheral, status: HciStatus) {
            Timber.e("failed to connect to '${peripheral.name}'")
            Toast.makeText(context, "Connection failed to ${peripheral.name} !", LENGTH_SHORT).show()
        }

        override fun onBluetoothAdapterStateChanged(state: Int) {
            Timber.i("bluetooth adapter changed state to %d", state)
            if (state == BluetoothAdapter.STATE_ON) {
                // Bluetooth is on now, start scanning again
                // Scan for peripherals with a certain service UUIDs
                centralManager.startPairingPopupHack()
                startScanning()
            }
        }

        //User disconnect from a peripheral if necessary.  TODO Button??
        fun cancelConnection(peripheral: BluetoothPeripheral){
            Toast.makeText(context, "Connection to ${peripheral.name} has been cancelled!", LENGTH_SHORT).show()
        }
    }


    fun startScanning() {
        if(centralManager.isNotScanning) {
            centralManager.scanForPeripheralsWithNames(
                setOf(
                    "WaterBotESP32"
                )
            )
        }
    } // END fun startScanning()



    fun initialize(context: Context) {
        Timber.plant(Timber.DebugTree())
        Timber.i("initializing BluetoothHandler")
        this.context = context.applicationContext
        this.centralManager = BluetoothCentralManager(this.context, bluetoothCentralManagerCallback, handler)
    }


}

// Peripheral extension to check if the peripheral needs to be bonded first
// This is application specific of course
fun BluetoothPeripheral.needsBonding(): Boolean {
    return name.startsWith("WaterBot")
}

