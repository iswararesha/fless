package com.resha.fless.ui.task

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.resha.fless.databinding.FragmentTaskBinding
import com.resha.fless.model.*
import com.resha.fless.ui.ViewModelFactory
import com.resha.fless.ui.course.CourseDetailActivity
import com.resha.fless.ui.course.ListCourseAdapter
import com.resha.fless.ui.material.MaterialActivity
import com.resha.fless.ui.material.MaterialAdapter


private val Context.dataStore by preferencesDataStore("user")
class TaskFragment : Fragment() {
    private var _binding: FragmentTaskBinding? = null
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var dataStore : DataStore<Preferences>

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        dataStore = requireContext().dataStore
        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupViewModel()

        return root
    }

    private fun setupViewModel() {
        taskViewModel = ViewModelProvider(this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[TaskViewModel::class.java]

        taskViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        setTab()
        taskViewModel.getOnGoingTask()

        taskViewModel.taskData.observe(viewLifecycleOwner){
            setTaskData(it)
        }
    }

    private fun setTab(){
        val tabLayout = binding.tabTask
        tabLayout.addTab(tabLayout.newTab().setText("Saat ini"))
        tabLayout.addTab(tabLayout.newTab().setText("Selesai"))

        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tabLayout.selectedTabPosition == 0) {
                    taskViewModel.getOnGoingTask()
                } else if (tabLayout.selectedTabPosition == 1) {
                    taskViewModel.getFinishTask()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setTaskData(data: List<TaskModel>){
        if(data.isNotEmpty()){
            binding.rvListTask.visibility = View.VISIBLE
            binding.rvListTask.layoutManager = LinearLayoutManager(context)

            val listTaskAdapter = ListTaskAdapter(data)
            binding.rvListTask.adapter = listTaskAdapter

            listTaskAdapter.setOnItemClickCallback(object: ListTaskAdapter.OnItemClickCallback{
                override fun onItemClicked(material: Material) {
                    showSelectedTask(material)
                }
            })
        }else{
            binding.rvListTask.visibility = View.INVISIBLE
        }
    }

    private fun showSelectedTask(material: Material) {
        val intent = Intent(context, MaterialActivity::class.java)
        intent.putExtra(MaterialActivity.MATERIAL_DETAIL, material)
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

    override fun onResume() {
        super.onResume()
        taskViewModel.taskData.removeObservers(this)

        val tab = binding.tabTask.getTabAt(0)
        binding.tabTask.selectTab(tab)

        taskViewModel.getOnGoingTask()
        taskViewModel.taskData.observe(this) {
            setTaskData(it)
        }
    }
}