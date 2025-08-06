package com.example.yogaadminmvvm

import android.app.Application // Import the base Application class
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application : Application() { // Add " : Application()" to extend the base class
}
