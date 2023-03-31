package com.resha.fless.ui.course

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.view.contains
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.resha.fless.databinding.FragmentCourseBinding
import com.resha.fless.model.Course
import com.resha.fless.model.UserPreference
import com.resha.fless.ui.ViewModelFactory

private val Context.dataStore by preferencesDataStore("user")
class CourseFragment : Fragment() {
    private var _binding: FragmentCourseBinding? = null
    private lateinit var courseViewModel: CourseViewModel
    private lateinit var dataStore : DataStore<Preferences>

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataStore = requireContext().dataStore
        _binding = FragmentCourseBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupViewModel()
        searchCourse()

        return root
    }

    private fun searchCourse(){
        val searchView = binding.svCourse

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if(query == ""){
                    courseViewModel.getCourse()
                }

                Log.d("onQueryTextSubmit", query)
                courseViewModel.searchCourse(query)

                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                if(newText == ""){
                    courseViewModel.getCourse()
                }

                Log.d("onQueryTextChange", newText)
                courseViewModel.searchCourse(newText)

                return false
            }
        })
    }

    private fun setupViewModel() {
        courseViewModel = ViewModelProvider(this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[CourseViewModel::class.java]

        courseViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        courseViewModel.getCourse()

        courseViewModel.courseData.observe(viewLifecycleOwner) {
            setCourseData(it)
        }
    }

    private fun setCourseData(data: List<Course>){
        if(data.isNotEmpty()){
            binding.rvListCourse.visibility = View.VISIBLE
            binding.rvListCourse.layoutManager = LinearLayoutManager(context)

            val listCourseAdapter = ListCourseAdapter(data)
            binding.rvListCourse.adapter = listCourseAdapter

            listCourseAdapter.setOnItemClickCallback(object: ListCourseAdapter.OnItemClickCallback{
                override fun onItemClicked(course: Course) {
                    if(!course.isOpen!!){
                        courseViewModel.setStatus(course)
                    }

                    showSelectedCourse(course)
                }
            })
        }else{
            binding.rvListCourse.visibility = View.INVISIBLE
        }
    }

    private fun showSelectedCourse(course: Course) {
        val intent = Intent(context, CourseDetailActivity::class.java)
        intent.putExtra(CourseDetailActivity.COURSE_DETAIL, course)
        startActivity(intent)
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