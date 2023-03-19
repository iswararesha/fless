package com.resha.fless.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.resha.fless.R
import com.resha.fless.databinding.FragmentProfileBinding
import com.resha.fless.model.UserPreference
import com.resha.fless.ui.ViewModelFactory

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
        binding.btnLogout.setOnClickListener {
            profileViewModel.logout()
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}