package com.resha.fless.ui.home

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.resha.fless.databinding.ActivityMaterialBinding
import com.resha.fless.databinding.ActivityTutorialBinding
import com.resha.fless.ui.landing.LandingActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class TutorialActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTutorialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupActions()
    }

    private fun setupActions() {
        binding.btnBack.setOnClickListener{
            finish()
        }
    }

    private fun setupView() {
        supportActionBar?.hide()
    }
}