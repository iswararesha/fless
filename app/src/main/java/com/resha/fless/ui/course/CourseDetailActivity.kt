package com.resha.fless.ui.course

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.resha.fless.databinding.ActivityCourseDetailBinding
import com.resha.fless.model.Course
import com.resha.fless.model.Modul
import com.resha.fless.model.UserPreference
import com.resha.fless.ui.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class CourseDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCourseDetailBinding
    private lateinit var courseDetailViewModel: CourseDetailViewModel
    private lateinit var course : Course

    companion object{
        const val COURSE_DETAIL = "data_detail"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        course = intent.getParcelableExtra<Modul>(COURSE_DETAIL) as Course

        setupViewModel()
    }

    private fun setupViewModel() {
        courseDetailViewModel = ViewModelProvider(this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[CourseDetailViewModel::class.java]

        courseDetailViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        if(course.courseId != null){
            courseDetailViewModel.getModul(course.courseId.toString())
        }

        courseDetailViewModel.modulData.observe(this) {
            setModulData(it)
        }
    }

    private fun setModulData(data: List<Modul>){
        if(data.isNotEmpty()){
            binding.rvListModul.visibility = View.VISIBLE
            binding.rvListModul.layoutManager = LinearLayoutManager(this)

            val listModulAdapter = ListModulAdapter(data)
            binding.rvListModul.adapter = listModulAdapter

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

    override fun onResume() {
        super.onResume()
        courseDetailViewModel.modulData.removeObservers(this)
        courseDetailViewModel.modulData.observe(this) {
            setModulData(it)
        }
    }
}