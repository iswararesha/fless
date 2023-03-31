package com.resha.fless.ui.material

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.resha.fless.databinding.FragmentMaterialBinding
import com.resha.fless.databinding.FragmentTaskViewBinding
import com.resha.fless.model.Content
import com.resha.fless.model.Material
import com.resha.fless.model.SubModul
import com.resha.fless.model.UserPreference
import com.resha.fless.ui.ViewModelFactory
import java.io.File
import java.io.FileInputStream

private val Context.dataStore by preferencesDataStore("user")
class TaskViewFragment : Fragment() {
    private var _binding: FragmentTaskViewBinding? = null
    private lateinit var materialViewModel: MaterialViewModel
    private lateinit var dataStore : DataStore<Preferences>

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataStore = requireContext().dataStore
        _binding = FragmentTaskViewBinding.inflate(inflater, container, false)
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

        if(material != null) {
            materialViewModel.getContent(material)
            materialViewModel.getUserTask(material)
        }

        materialViewModel.contentData.observe(viewLifecycleOwner) {
            setMaterialData(it)
        }



        materialViewModel.taskStatus.observe(viewLifecycleOwner) {
            setStatus(it)
        }
    }

    private fun setStatus(status: Boolean?) {
        if(status == true){
            binding.imgStatus.visibility = View.VISIBLE
            binding.tvChosenFile.visibility = View.GONE
            binding.chooseFileButton.visibility = View.GONE
            binding.uploadButton.visibility = View.GONE
        }else{
            binding.imgStatus.visibility = View.INVISIBLE
            binding.tvChosenFile.visibility = View.VISIBLE
            binding.chooseFileButton.visibility = View.VISIBLE
            binding.uploadButton.visibility = View.VISIBLE
        }
    }

    private fun setMaterialData(data: List<Content>){
        if(data.isNotEmpty()){
            binding.rvMaterial.visibility = View.VISIBLE
            binding.rvMaterial.layoutManager = LinearLayoutManager(context)

            val materialAdapter = MaterialAdapter(data)
            binding.rvMaterial.adapter = materialAdapter

        }else{
            binding.rvMaterial.visibility = View.INVISIBLE
        }
    }

    private fun setupAction(){
        val bundle = arguments
        val material = bundle!!.getParcelable<Material>("material")

        binding.chooseFileButton.setOnClickListener(){
            startDocument()
        }

        binding.uploadButton.setOnClickListener(){
            if(material != null) {
                if(getFile != null){
                    materialViewModel.uploadTask(material, getFile!!, getFileName!!)
                }
            }
        }

        binding.previousButton.setOnClickListener() {
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

        binding.nextButton.setOnClickListener(){
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
    }

    private fun startDocument() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "*/*"
        val chooser = Intent.createChooser(intent, "Choose a File")
        launcherIntentDocument.launch(chooser)
    }

    private var getFile: Uri? = null
    private var getFileName: String? = null
    private val launcherIntentDocument = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedFile: Uri = result.data?.data as Uri
            val selectedFileName = getFileName(context!!, selectedFile)

            getFile = selectedFile
            getFileName = selectedFileName

            binding.tvChosenFile.text = getFileName
        }
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (cursor != null) {
                    if(cursor.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }
        }
        return uri.path?.lastIndexOf('/')?.let { uri.path?.substring(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    private fun showLoading(isLoading: Boolean) {
        if(isLoading){
            binding.loading.visibility = View.VISIBLE
        }else{
            binding.loading.visibility = View.GONE
        }
    }
}