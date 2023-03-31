package com.resha.fless.evaluation

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.resha.fless.databinding.ActivityEssayBinding
import com.resha.fless.databinding.ActivityObjectiveBinding
import com.resha.fless.model.*
import com.resha.fless.ui.ViewModelFactory
import com.resha.fless.ui.course.ListCourseAdapter
import com.resha.fless.ui.material.MaterialActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class EssayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEssayBinding
    private lateinit var essayViewModel: EssayViewModel
    private lateinit var material: Material

    companion object{
        const val MATERIAL_DETAIL = "data_detail"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEssayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        material = intent.getParcelableExtra<Material>(MATERIAL_DETAIL) as Material

        setupViewModel()
    }

    private fun setupViewModel() {
        essayViewModel = ViewModelProvider(this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[EssayViewModel::class.java]

        essayViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        if(material.subModulId != null){
            essayViewModel.getEssay(material)
        }

        essayViewModel.essayData.observe(this) {
            setEssayData(it)
        }
    }

    private fun setEssayData(data: Essay){
        binding.tvQuestion.text = data.question

        Glide.with(this)
            .load(data.programImg)
            .into(binding.imgProgram)

        Glide.with(this)
            .load(data.questionImg)
            .into(binding.imgQuestion)

        binding.rvField.visibility = View.VISIBLE
        binding.rvField.layoutManager = LinearLayoutManager(this)

        var getAnswer = ArrayList<Answer>()

        if (data.fieldNumber != null) {
            val list = List(data.fieldNumber) { it + 1 }
            for(id in 1..data.fieldNumber){
                getAnswer.add(Answer("number$id", "answer$id"))
            }

            val listEssayAdapter = ListEssayAdapter(list)
            binding.rvField.adapter = listEssayAdapter

            listEssayAdapter.setOnTextChange(object: ListEssayAdapter.OnTextCallback{
                override fun onTextChange(answer: ArrayList<Answer>) {
                    getAnswer = answer
                }
            })

            binding.finishButton.setOnClickListener(){
                essayViewModel.sendAnswer(material, getAnswer.size, getAnswer)
                essayViewModel.isLoading.observe(this){
                    if(it){
                        binding.svEssay.visibility = View.INVISIBLE
                    }else{
                        finish()
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if(isLoading){
            binding.loading.visibility = View.VISIBLE
            binding.svEssay.visibility = View.INVISIBLE
        }else {
            binding.loading.visibility = View.GONE
            binding.svEssay.visibility = View.VISIBLE
        }
    }
}