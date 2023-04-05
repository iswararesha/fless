package com.resha.fless.ui.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.resha.fless.databinding.ActivityOnboardingBinding
import com.resha.fless.model.UserPreference
import com.resha.fless.ui.ViewModelFactory
import com.resha.fless.ui.landing.LandingActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
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
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[OnboardingViewModel::class.java]

        setupView()
        setupActions()
    }

    private fun setupActions() {
        binding.btnStart.setOnClickListener {
            onboardingViewModel.editStatus()

            val intent = Intent(this, LandingActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnSkip.setOnClickListener {
            onboardingViewModel.editStatus()

            val intent = Intent(this, LandingActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnNext.setOnClickListener {

        }

        binding.btnBack.setOnClickListener {

        }
    }

    private fun setupView() {
        supportActionBar?.hide()
    }
}