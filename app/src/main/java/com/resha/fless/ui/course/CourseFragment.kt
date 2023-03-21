package com.resha.fless.ui.course

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

        return root
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

            if(context?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE){
                binding.rvListCourse.layoutManager = GridLayoutManager(context, 2)
            }else{
                binding.rvListCourse.layoutManager = LinearLayoutManager(context)
            }

            val listCourseAdapter = ListCourseAdapter(data)
            binding.rvListCourse.adapter = listCourseAdapter

            listCourseAdapter.setOnItemClickCallback(object: ListCourseAdapter.OnItemClickCallback{
                override fun onItemClicked(course: Course) {
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