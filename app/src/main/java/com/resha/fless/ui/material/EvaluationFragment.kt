package com.resha.fless.ui.material

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.resha.fless.R
import com.resha.fless.model.Material
import com.resha.fless.model.UserPreference
import com.resha.fless.ui.ViewModelFactory
import com.resha.fless.databinding.FragmentEvaluationBinding
import com.resha.fless.evaluation.EssayActivity
import com.resha.fless.evaluation.ObjectiveActivity

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

        val transaction = childFragmentManager.beginTransaction()
    }

    private fun setupAction(){
        binding.attempButton.setOnClickListener(){

            val bundle = arguments
            val material = bundle!!.getParcelable<Material>("material")

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