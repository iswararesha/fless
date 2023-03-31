package com.resha.fless.ui.material

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.system.Os.remove
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.resha.fless.R
import com.resha.fless.model.Material
import com.resha.fless.model.UserPreference
import com.resha.fless.ui.ViewModelFactory
import com.resha.fless.databinding.FragmentEvaluationBinding
import com.resha.fless.evaluation.EssayActivity
import com.resha.fless.evaluation.ObjectiveActivity
import com.resha.fless.model.Attempt
import com.resha.fless.model.Course
import com.resha.fless.ui.course.ListCourseAdapter

private val Context.dataStore by preferencesDataStore("user")
class EvaluationFragment : Fragment() {
    private var _binding: FragmentEvaluationBinding? = null
    private lateinit var materialViewModel: MaterialViewModel
    private lateinit var dataStore : DataStore<Preferences>

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataStore = requireContext().dataStore
        _binding = FragmentEvaluationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupViewModel()
        setupAction()

        return root
    }

    private fun setupViewModel() {
        materialViewModel = ViewModelProvider(this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MaterialViewModel::class.java]

        materialViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        val bundle = arguments
        val material = bundle!!.getParcelable<Material>("material")


        if(material != null) materialViewModel.getUserAttempt(material)

        materialViewModel.attemptData.observe(viewLifecycleOwner){
            setUserAttempt(it)
        }
    }

    private fun setupAction(){
        val bundle = arguments
        val material = bundle!!.getParcelable<Material>("material")

        binding.previousButton.setOnClickListener {
            if (material != null) materialViewModel.getSubModul(material)

            materialViewModel.materialData.observe(viewLifecycleOwner) {
                val prevMaterial = Material(
                    it.prevSubModulId,
                    it.courseParent,
                    it.prevModulParent
                )

                moveData(prevMaterial)
                materialViewModel.materialData.removeObservers(viewLifecycleOwner)
            }
        }

        binding.nextButton.setOnClickListener {
            if(material != null) materialViewModel.getSubModul(material)

            materialViewModel.materialData.observe(viewLifecycleOwner){
                val nextMaterial = Material(
                    it.nextSubModulId,
                    it.courseParent,
                    it.nextModulParent
                )

                moveData(nextMaterial)
                materialViewModel.materialData.removeObservers(viewLifecycleOwner)
            }
        }

        binding.attempButton.setOnClickListener {
            if(material != null) materialViewModel.getSubModul(material)

            materialViewModel.materialData.observe(viewLifecycleOwner){
                if(it.type == "test"){
                    val intent = Intent(context, ObjectiveActivity::class.java)
                    intent.putExtra(ObjectiveActivity.MATERIAL_DETAIL, material)
                    startActivity(intent)
                    activity?.finish()
                }else if(it.type == "practice"){
                    val intent = Intent(context, EssayActivity::class.java)
                    intent.putExtra(EssayActivity.MATERIAL_DETAIL, material)
                    startActivity(intent)
                    activity?.finish()
                }

                materialViewModel.materialData.removeObservers(viewLifecycleOwner)
            }
        }
    }

    private fun setUserAttempt(data: List<Attempt>){
        if(data.isNotEmpty()){
            binding.rvAttempt.visibility = View.VISIBLE
            binding.rvAttempt.layoutManager = LinearLayoutManager(context)

            val evaluationAdapter = EvaluationAdapter(data)
            binding.rvAttempt.adapter = evaluationAdapter

        }else{
            binding.rvAttempt.visibility = View.INVISIBLE
        }
    }

    private fun moveData(material: Material){
        if (material.subModulId == "none") {
            activity?.finish()
        } else {
            val intent = Intent(context, MaterialActivity::class.java)
            intent.putExtra(MaterialActivity.MATERIAL_DETAIL, material)
            startActivity(intent)
            activity?.finish()
        }
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