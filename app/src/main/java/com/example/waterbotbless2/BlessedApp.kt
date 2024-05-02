package com.example.waterbotbless2

import android.app.Application

class BlessedApp: Application() {
    override fun onCreate() {
        super.onCreate()
        BluetoothHandler.initialize(this.applicationContext)
    }
}