package com.resha.fless.ui.splashscreen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.resha.fless.R
import com.resha.fless.preference.UserPreference
import com.resha.fless.ui.ViewModelFactory
import com.resha.fless.ui.landing.LandingActivity
import com.resha.fless.ui.main.MainActivity
import com.resha.fless.ui.onboarding.OnboardingActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class SplahscreenActivity : AppCompatActivity() {
    private lateinit var splashViewModel: SplashViewModel

    companion object{
        const val MILIS_TIME : Long = 2000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splahscreen)

        setupViewModel()
        setupView()
    }

    private fun setupViewModel() {
        splashViewModel = ViewModelProvider(this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[SplashViewModel::class.java]

        splashViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        splashViewModel.getStatus().observe(this) { firstOpenStatus ->
            if (firstOpenStatus) {
                redirectOnBoarding()
            }else{
                val user = Firebase.auth.currentUser
                if (user != null) {
                    redirectMain()
                } else {
                    redirectLanding()
                }
            }
        }
    }

    private fun redirectOnBoarding() {
        val handler = Handler(Looper.getMainLooper())

        handler.postDelayed({
            val intent = Intent(this, OnboardingActivity::class.java)
            startActivity(intent)
            finish()
        }, MILIS_TIME)
    }

    private fun redirectMain() {
        val handler = Handler(Looper.getMainLooper())

        handler.postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, MILIS_TIME)
    }

    private fun redirectLanding() {
        val handler = Handler(Looper.getMainLooper())

        handler.postDelayed({
            val intent = Intent(this, LandingActivity::class.java)
            startActivity(intent)
            finish()
        }, MILIS_TIME)
    }

    private fun setupView() {
        supportActionBar?.hide()
    }
}