package com.resha.fless.evaluation

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.resha.fless.databinding.ActivityObjectiveBinding
import com.resha.fless.model.Answer
import com.resha.fless.model.Material
import com.resha.fless.model.Objective
import com.resha.fless.model.UserPreference
import com.resha.fless.ui.ViewModelFactory
import java.util.Collections.shuffle

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
            binding.rvListObjective.layoutManager = LinearLayoutManager(this)

            shuffle(data)

            val listObjectiveAdapter = ListObjectiveAdapter(data)
            binding.rvListObjective.adapter = listObjectiveAdapter

            val getAnswer = ArrayList<Answer>()

            for (id in data){
                val numberId = id.objectiveId?.toInt()
                getAnswer.add(Answer("number$numberId", "answer"))
            }

            listObjectiveAdapter.setOnButtonChange(object: ListObjectiveAdapter.OnButtonCallback{
                override fun onButtonChange(answer: Answer, id: Int) {
                    getAnswer[id-1] = answer
                }
            })

            binding.finishButton.setOnClickListener(){
                objectiveViewModel.sendAnswer(material, getAnswer.size, getAnswer)
                objectiveViewModel.isLoading.observe(this){
                    if(it){
                        binding.svObjective.visibility = View.INVISIBLE
                    }else{
                        finish()
                    }
                }
            }
        }else{
            binding.rvListObjective.visibility = View.INVISIBLE
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if(isLoading){
            binding.loading.visibility = View.VISIBLE
            binding.svObjective.visibility = View.INVISIBLE
        }else{
            binding.loading.visibility = View.GONE
            binding.svObjective.visibility = View.VISIBLE
        }
    }
}