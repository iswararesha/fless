package com.resha.fless.ui.material

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.resha.fless.databinding.ActivityMaterialBinding
import com.resha.fless.model.Content
import com.resha.fless.model.Modul
import com.resha.fless.model.SubModul
import com.resha.fless.model.UserPreference
import com.resha.fless.ui.ViewModelFactory
import com.resha.fless.ui.course.CourseDetailViewModel
import com.resha.fless.ui.course.ListModulAdapter

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class MaterialActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMaterialBinding
    private lateinit var materialViewModel: MaterialViewModel
    private lateinit var subModul: SubModul

    companion object{
        const val MATERIAL_DETAIL = "data_detail"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaterialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        subModul = intent.getParcelableExtra<SubModul>(MATERIAL_DETAIL) as SubModul

        setupViewModel()
    }

    private fun setupViewModel() {
        materialViewModel = ViewModelProvider(this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MaterialViewModel::class.java]

        materialViewModel.isLoading.observe(this) {
            showLoading(it)
        }
        if(subModul.subModulId != null){
            materialViewModel.getContent(
                subModul.courseParent.toString(),
                subModul.modulParent.toString(),
                subModul.subModulId.toString()
            )
        }

        materialViewModel.materialData.observe(this) {
            setMaterialData(it)
        }
    }

    private fun setMaterialData(data: List<Content>){
        if(data.isNotEmpty()){
            binding.rvMaterial.visibility = View.VISIBLE

            if(applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
                binding.rvMaterial.layoutManager = GridLayoutManager(this, 2)
            }else{
                binding.rvMaterial.layoutManager = LinearLayoutManager(this)
            }

            val materialAdapter = MaterialAdapter(data)
            binding.rvMaterial.adapter = materialAdapter

        }else{
            binding.rvMaterial.visibility = View.INVISIBLE
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if(isLoading){
            binding.loading.visibility = View.VISIBLE
        }else{
            binding.loading.visibility = View.GONE
        }
    }
}