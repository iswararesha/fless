package com.resha.fless.ui.material

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.resha.fless.databinding.FragmentDrawerBinding
import com.resha.fless.model.Modul

private val Context.dataStore by preferencesDataStore("user")
class DrawerFragment : Fragment() {
    private var _binding: FragmentDrawerBinding? = null
    private val materialViewModel: MaterialViewModel by activityViewModels()
    private lateinit var dataStore : DataStore<Preferences>

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataStore = requireContext().dataStore
        _binding = FragmentDrawerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupViewModel()

        return root
    }

    private fun setupViewModel() {
        materialViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        materialViewModel.materialData.observe(viewLifecycleOwner){
            materialViewModel.getModul(it.courseParent!!)
        }

        materialViewModel.modulData.observe(viewLifecycleOwner) {
            setModulData(it)
        }
    }

    private fun setModulData(data: List<Modul>){
        if(data.isNotEmpty()){
            binding.rvListModul.visibility = View.VISIBLE
            binding.rvListModul.layoutManager = LinearLayoutManager(context)

            val drawerModulAdapter = DrawerModulAdapter(data)
            binding.rvListModul.adapter = drawerModulAdapter

        }else{
            binding.rvListModul.visibility = View.INVISIBLE
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if(isLoading){
            binding.loading.visibility = View.VISIBLE
        }else{
            binding.loading.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}