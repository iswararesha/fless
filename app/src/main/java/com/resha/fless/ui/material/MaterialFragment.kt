package com.resha.fless.ui.material

import android.content.Context
import android.content.Intent
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
import com.resha.fless.databinding.FragmentMaterialBinding
import com.resha.fless.model.Content
import com.resha.fless.model.Material

private val Context.dataStore by preferencesDataStore("user")
class MaterialFragment : Fragment() {
    private var _binding: FragmentMaterialBinding? = null
    private val materialViewModel: MaterialViewModel by activityViewModels()
    private lateinit var dataStore : DataStore<Preferences>

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataStore = requireContext().dataStore
        _binding = FragmentMaterialBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupViewModel()

        return root
    }

    private fun setupViewModel() {
        materialViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        materialViewModel.materialData.observe(viewLifecycleOwner){
            val material = Material(
                it.subModulId,
                it.courseParent,
                it.modulParent
            )

            materialViewModel.getContent(material)
        }

        materialViewModel.contentData.observe(viewLifecycleOwner) {
            setMaterialData(it)
        }
    }

    private fun setMaterialData(data: List<Content>){
        if(data.isNotEmpty()){
            binding.rvMaterial.visibility = View.VISIBLE
            binding.rvMaterial.layoutManager = LinearLayoutManager(context)

            val materialAdapter = MaterialAdapter(data)
            binding.rvMaterial.adapter = materialAdapter

            materialAdapter.setOnItemClickCallback(object: MaterialAdapter.OnItemClickCallback{
                override fun onItemClicked(string: String) {
                    showImageDetail(string)
                }
            })

        }else{
            binding.rvMaterial.visibility = View.INVISIBLE
        }
    }

    private fun showImageDetail(string: String) {
        if(string != null){
            val intent = Intent(context, DetailImageActivity::class.java)
            intent.putExtra(DetailImageActivity.IMAGE, string)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoading(isLoading: Boolean) {
        if(isLoading){
            binding.loading.visibility = View.VISIBLE
        }else{
            binding.loading.visibility = View.GONE
        }
    }
}