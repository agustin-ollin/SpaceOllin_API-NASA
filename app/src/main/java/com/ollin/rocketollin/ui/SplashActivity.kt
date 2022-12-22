package com.ollin.rocketollin.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ollin.rocketollin.MainActivity
import com.ollin.rocketollin.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val screenSplash = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        screenSplash.setKeepOnScreenCondition{ true }
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}