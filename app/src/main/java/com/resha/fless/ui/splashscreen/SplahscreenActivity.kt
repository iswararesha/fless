package com.resha.fless.ui.splashscreen

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.resha.fless.R
import com.resha.fless.model.AppPreference
import com.resha.fless.ui.AppViewModelFactory
import com.resha.fless.ui.main.MainActivity
import com.resha.fless.ui.onboarding.OnboardingActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
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
            AppViewModelFactory(AppPreference.getInstance(dataStore))
        )[SplashViewModel::class.java]

        splashViewModel.getStatus().observe(this) { firstOpenStatus ->
            if (firstOpenStatus) {
                redirectOnBoarding()
            } else {
                redirectMain()
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

    private fun setupView() {
        supportActionBar?.hide()
    }
}