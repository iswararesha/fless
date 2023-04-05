package com.resha.fless.ui.course

import android.content.Context
import android.graphics.text.LineBreaker
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.MediaController
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

        setupView()
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

    private fun setupView(){
        supportActionBar?.hide()
    }

    private fun setModulData(data: List<Modul>){
        if(data.isNotEmpty()){
            binding.tvCourseName.text = course.courseName
            binding.tvCourseId.text = course.courseId
            binding.tvCourseDescription.text = course.courseDescription

            val uri = Uri.parse(course.videoLink)
            binding.vvCourseIntro.setVideoURI(uri)

            val mediaController = MediaController(this)
            mediaController.setAnchorView(binding.vvCourseIntro)
            mediaController.setMediaPlayer(binding.vvCourseIntro)
            binding.vvCourseIntro.setMediaController(mediaController)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                binding.tvCourseDescription.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
            }

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

    override fun onPause() {
        super.onPause()
        courseDetailViewModel.modulData.removeObservers(this)
    }

    override fun onResume() {
        super.onResume()
        courseDetailViewModel.modulData.observe(this) {
            setModulData(it)
        }
    }
}