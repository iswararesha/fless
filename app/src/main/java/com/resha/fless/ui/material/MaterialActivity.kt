package com.resha.fless.ui.material

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.resha.fless.R
import com.resha.fless.databinding.ActivityMaterialBinding
import com.resha.fless.model.*
import com.resha.fless.ui.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class MaterialActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMaterialBinding
    private lateinit var materialViewModel: MaterialViewModel
    private lateinit var material: Material

    companion object{
        const val MATERIAL_DETAIL = "data_detail"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaterialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        material = intent.getParcelableExtra<Material>(MATERIAL_DETAIL) as Material

        setupViewModel()
    }

    private fun setupViewModel() {
        materialViewModel = ViewModelProvider(this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MaterialViewModel::class.java]

        materialViewModel.materialData.observe(this){
            setFragment(it)
        }

        materialViewModel.getSubModul(material)
    }

    private fun setFragment(subModul: SubModul){
        when (subModul.type) {
            "material" -> {
                val fragmentManager = supportFragmentManager
                val materialFragment = MaterialFragment()

                val bundle = Bundle()
                bundle.putParcelable("material", material)
                materialFragment.arguments = bundle

                fragmentManager
                    .beginTransaction()
                    .replace(R.id.material_view_fragment, materialFragment, MaterialFragment::class.java.simpleName)
                    .commit()
            }
            "task" -> {
                val fragmentManager = supportFragmentManager
                val taskViewFragment = TaskViewFragment()

                val bundle = Bundle()
                bundle.putParcelable("material", material)
                taskViewFragment.arguments = bundle

                fragmentManager
                    .beginTransaction()
                    .replace(R.id.material_view_fragment, taskViewFragment, TaskViewFragment::class.java.simpleName)
                    .commit()
            }
            else -> {
                val fragmentManager = supportFragmentManager
                val evaluationFragment = EvaluationFragment()

                val bundle = Bundle()
                bundle.putParcelable("material", material)
                evaluationFragment.arguments = bundle

                fragmentManager
                    .beginTransaction()
                    .replace(R.id.material_view_fragment, evaluationFragment, EvaluationFragment::class.java.simpleName)
                    .commit()
            }
        }
    }
}