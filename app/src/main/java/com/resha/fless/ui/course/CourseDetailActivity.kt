package com.resha.fless.ui.course

import android.content.Context
import android.content.Intent
import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.resha.fless.databinding.ActivityCourseDetailBinding
import com.resha.fless.model.Course
import com.resha.fless.model.Modul
import com.resha.fless.preference.UserPreference
import com.resha.fless.ui.ViewModelFactory
import com.resha.fless.ui.material.VideoActivity
import com.resha.fless.ui.material.VideoActivity.Companion.VIDEO

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
        setupAction()
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

    private fun setupAction(){
        binding.btnPlay.setOnClickListener {
            val intent = Intent(this, VideoActivity::class.java)
            intent.putExtra(VIDEO, course.videoLink)
            startActivity(intent)
        }
    }

    private fun setModulData(data: List<Modul>){
        if(data.isNotEmpty()){
            binding.tvCourseName.text = course.courseName
            binding.tvCourseId.text = course.courseId
            binding.tvCourseDescription.text = course.courseDescription
            binding.tvCourseObjective.text = course.courseObjective

            Glide.with(this)
                .load(course.thumbnail)
                .into(binding.frameVideo)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                binding.tvCourseDescription.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
                binding.tvCourseObjective.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
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
        courseDetailViewModel.getModul(course.courseId.toString())

        courseDetailViewModel.modulData.observe(this) {
            setModulData(it)
        }
    }
}