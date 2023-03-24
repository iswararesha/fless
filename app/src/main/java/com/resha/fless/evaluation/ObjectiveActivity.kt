package com.resha.fless.evaluation

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.resha.fless.databinding.ActivityObjectiveBinding
import com.resha.fless.model.*
import com.resha.fless.ui.ViewModelFactory
import com.resha.fless.ui.material.MaterialActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class ObjectiveActivity : AppCompatActivity() {

    private lateinit var binding: ActivityObjectiveBinding
    private lateinit var objectiveViewModel: ObjectiveViewModel
    private lateinit var material: Material

    companion object{
        const val MATERIAL_DETAIL = "data_detail"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityObjectiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        material = intent.getParcelableExtra<Material>(MATERIAL_DETAIL) as Material

        setupViewModel()
    }

    private fun setupViewModel() {
        objectiveViewModel = ViewModelProvider(this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[ObjectiveViewModel::class.java]

        objectiveViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        if(material.subModulId != null){
            objectiveViewModel.getObjective(material)
        }

        objectiveViewModel.objectiveData.observe(this) {
            setObjectiveData(it)
        }
    }

    private fun setObjectiveData(data: List<Objective>){
        if(data.isNotEmpty()){
            binding.rvListObjective.visibility = View.VISIBLE

            if(applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
                binding.rvListObjective.layoutManager = GridLayoutManager(this, 2)
            }else{
                binding.rvListObjective.layoutManager = LinearLayoutManager(this)
            }

            val listObjectiveAdapter = ListObjectiveAdapter(data)
            binding.rvListObjective.adapter = listObjectiveAdapter

            val getAnswer = ArrayList<Answer>()
            for (id in 1 .. data.size){
                getAnswer.add(Answer("number$id", "answer"))
            }

            listObjectiveAdapter.setOnButtonChange(object: ListObjectiveAdapter.OnButtonCallback{
                override fun onButtonChange(answer: Answer, id: Int) {
                    getAnswer[id-1] = answer
                }
            })

            binding.finishButton.setOnClickListener(){
                binding.rvListObjective.visibility = View.INVISIBLE
                objectiveViewModel.sendAnswer(material, getAnswer.size, getAnswer)

                val intent = Intent(this, MaterialActivity::class.java)
                intent.putExtra(MaterialActivity.MATERIAL_DETAIL, material)
                startActivity(intent)
                finish()
            }
        }else{
            binding.rvListObjective.visibility = View.INVISIBLE
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