package com.resha.fless.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.resha.fless.R
import com.resha.fless.databinding.FragmentProfileBinding
import com.resha.fless.preference.UserPreference
import com.resha.fless.ui.ViewModelFactory
import com.resha.fless.ui.landing.LandingActivity

private val Context.dataStore by preferencesDataStore("user")
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var dataStore : DataStore<Preferences>

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataStore = requireContext().dataStore
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root


        setupViewModel()
        setupActions()

        return root
    }

    private fun setupActions() {
        binding.btnChangePassword.setOnClickListener {
            val intent = Intent(context, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        binding.btnContactDev.setOnClickListener {
            val email = Intent(Intent.ACTION_SEND)

            email.type = "message/rfc822"
            email.putExtra(Intent.EXTRA_EMAIL, arrayOf("reshaiswara@gmail.com"))
            email.putExtra(Intent.EXTRA_SUBJECT, "Fless: Butuh bantuan")

            startActivity(Intent.createChooser(email, "Pilih Client Email:"))
        }

        binding.btnCredit.setOnClickListener {
            Toast.makeText(context,"Fless by \nResha Digitha Iswara.", Toast.LENGTH_LONG).show()
            Toast.makeText(context,"Dibuat untuk \nmemenuhi skripsi.", Toast.LENGTH_LONG).show()
        }

        binding.btnLogout.setOnClickListener {
            profileViewModel.logout()

            val intent = Intent(context, LandingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
        }
    }

    private fun setupViewModel() {
        profileViewModel = ViewModelProvider(this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[ProfileViewModel::class.java]

        profileViewModel.getUser()

        profileViewModel.userDetail.observe(viewLifecycleOwner) {
            binding.tvNameUser.text = it?.name
            binding.tvEmailUser.text = it?.email
        }

        profileViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                binding.btnDarkMode.text = getString(R.string.lightMode)
            } else {
                binding.btnDarkMode.text = getString(R.string.darkMode)
            }
        }

        binding.btnDarkMode.setOnClickListener() {
            if (binding.btnDarkMode.text == getString(R.string.darkMode)){
                profileViewModel.saveThemeSetting(true)
            }else{
                profileViewModel.saveThemeSetting(false)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}