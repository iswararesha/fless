package com.resha.fless.ui.onboarding

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.resha.fless.databinding.ActivityOnboardingBinding
import com.resha.fless.model.AppPreference
import com.resha.fless.ui.AppViewModelFactory
import com.resha.fless.ui.main.MainActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class OnboardingActivity : AppCompatActivity() {
    private lateinit var onboardingViewModel: OnboardingViewModel
    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
    }

    private fun setupViewModel() {
        onboardingViewModel = ViewModelProvider(this,
            AppViewModelFactory(AppPreference.getInstance(dataStore))
        )[OnboardingViewModel::class.java]

        setupView()
        setupActions()
    }

    private fun setupActions() {
        binding.btnStart.setOnClickListener {
            onboardingViewModel.editStatus()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupView() {
        supportActionBar?.hide()
    }
}