package com.resha.fless.ui.material

import android.content.Context
import android.content.Intent
import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.resha.fless.R
import com.resha.fless.databinding.FragmentEvaluationBinding
import com.resha.fless.evaluation.EssayActivity
import com.resha.fless.evaluation.ObjectiveActivity
import com.resha.fless.model.Attempt
import com.resha.fless.model.Material

private val Context.dataStore by preferencesDataStore("user")
class EvaluationFragment : Fragment() {
    private var _binding: FragmentEvaluationBinding? = null
    private val materialViewModel: MaterialViewModel by activityViewModels()
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
        materialViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.tvDesciptionEvaluation.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
        }

        materialViewModel.materialData.observe(viewLifecycleOwner){
            val material = Material(
                it.subModulId,
                it.courseParent,
                it.modulParent
            )

            materialViewModel.getUserAttempt(material)

            binding.tvTittleEvaluation.text = it.name
            if(it.type == "test"){
                binding.tvDesciptionEvaluation.text = (getString(R.string.testDesctiption))
            }else{
                binding.tvDesciptionEvaluation.text = (getString(R.string.practiceDesctiption))
            }
        }

        materialViewModel.attemptData.observe(viewLifecycleOwner){
            setUserAttempt(it)
        }
    }

    private fun setupAction(){
        binding.attempButton.setOnClickListener {
            materialViewModel.materialData.observe(viewLifecycleOwner){
                val material = Material(
                    it.subModulId,
                    it.courseParent,
                    it.modulParent
                )

                if(it.type == "test"){
                    val intent = Intent(context, ObjectiveActivity::class.java)
                    intent.putExtra(ObjectiveActivity.MATERIAL_DETAIL, material)
                    startActivity(intent)
                }else if(it.type == "practice"){
                    val intent = Intent(context, EssayActivity::class.java)
                    intent.putExtra(EssayActivity.MATERIAL_DETAIL, material)
                    startActivity(intent)
                }

                materialViewModel.materialData.removeObservers(viewLifecycleOwner)
            }
        }
    }

    private fun setUserAttempt(data: List<Attempt>){
        if(data.isNotEmpty()){
            for(id in data){
                if(id.score!!.toFloat() > 80){
                    (activity as MaterialActivity).setButtonOn()
                }
            }

            binding.rvAttempt.visibility = View.VISIBLE
            binding.rvAttempt.layoutManager = LinearLayoutManager(context)

            val evaluationAdapter = EvaluationAdapter(data)
            binding.rvAttempt.adapter = evaluationAdapter

        }else{
            binding.rvAttempt.visibility = View.INVISIBLE
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