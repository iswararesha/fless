package com.resha.fless.ui.material

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.resha.fless.databinding.FragmentMaterialBinding
import com.resha.fless.model.Content
import com.resha.fless.model.Material
import com.resha.fless.model.SubModul
import com.resha.fless.model.UserPreference
import com.resha.fless.ui.ViewModelFactory

private val Context.dataStore by preferencesDataStore("user")
class MaterialFragment : Fragment() {
    private var _binding: FragmentMaterialBinding? = null
    private lateinit var materialViewModel: MaterialViewModel
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
        setupAction()

        return root
    }

    private fun setupViewModel() {
        materialViewModel = ViewModelProvider(this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MaterialViewModel::class.java]

        materialViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        val bundle = arguments
        val material = bundle!!.getParcelable<Material>("material")

        if(material != null) materialViewModel.getContent(material)

        materialViewModel.contentData.observe(viewLifecycleOwner) {
            setMaterialData(it)
        }
    }

    private fun setMaterialData(data: List<Content>){
        if(data.isNotEmpty()){
            binding.rvMaterial.visibility = View.VISIBLE

            if(context?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE){
                binding.rvMaterial.layoutManager = GridLayoutManager(context, 2)
            }else{
                binding.rvMaterial.layoutManager = LinearLayoutManager(context)
            }

            val materialAdapter = MaterialAdapter(data)
            binding.rvMaterial.adapter = materialAdapter

        }else{
            binding.rvMaterial.visibility = View.INVISIBLE
        }
    }

    private fun setupAction(){
        binding.nextButton.setOnClickListener(){

            val bundle = arguments
            val material = bundle!!.getParcelable<Material>("material")

            if(material != null) materialViewModel.getSubModul(material)

            materialViewModel.materialData.observe(viewLifecycleOwner){
                val nextMaterial = Material(
                    it.nextSubModulId,
                    it.courseParent,
                    it.nextModulParent
                )

                val intent = Intent(context, MaterialActivity::class.java)
                intent.putExtra(MaterialActivity.MATERIAL_DETAIL, nextMaterial)
                startActivity(intent)

                materialViewModel.materialData.removeObservers(viewLifecycleOwner)
            }
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