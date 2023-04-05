package com.resha.fless.ui.material

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.resha.fless.databinding.FragmentTaskViewBinding
import com.resha.fless.model.Content
import com.resha.fless.model.Material

private val Context.dataStore by preferencesDataStore("user")
class TaskViewFragment : Fragment() {
    private var _binding: FragmentTaskViewBinding? = null
    private val materialViewModel: MaterialViewModel by activityViewModels()
    private lateinit var dataStore : DataStore<Preferences>

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        dataStore = requireContext().dataStore
        _binding = FragmentTaskViewBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupViewModel()
        setupAction()

        return root
    }

    private fun setupViewModel() {
        materialViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        materialViewModel.materialData.observe(viewLifecycleOwner){
            val material = Material(
                it.subModulId,
                it.courseParent,
                it.modulParent
            )

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
            binding.tvTaskStatus.visibility = View.VISIBLE
            binding.tvChosenFile.visibility = View.GONE
            binding.uploadButton.visibility = View.GONE

            (activity as MaterialActivity).setButtonOn()
        }else{
            binding.imgStatus.visibility = View.INVISIBLE
            binding.tvTaskStatus.visibility = View.INVISIBLE
            binding.tvChosenFile.visibility = View.VISIBLE
            binding.uploadButton.visibility = View.VISIBLE
        }
    }

    private fun setMaterialData(data: List<Content>){
        if(data.isNotEmpty()){
            binding.rvMaterial.visibility = View.VISIBLE
            binding.rvMaterial.layoutManager = LinearLayoutManager(context)

            val materialAdapter = MaterialAdapter(data)
            binding.rvMaterial.adapter = materialAdapter

            materialAdapter.setOnItemClickCallback(object: MaterialAdapter.OnItemClickCallback{
                override fun onItemClicked(string: String) {
                    showImageDetail(string)
                }
            })

        }else{
            binding.rvMaterial.visibility = View.INVISIBLE
        }
    }

    private fun setupAction(){
        binding.tvChosenFile.setOnClickListener(){
            startDocument()
        }

        binding.uploadButton.setOnClickListener(){
            materialViewModel.materialData.observe(viewLifecycleOwner) {
                val material = Material(
                    it.subModulId,
                    it.courseParent,
                    it.modulParent
                )

                if (getFile != null) {
                    materialViewModel.uploadTask(material, getFile!!, getFileName!!)
                }
            }
        }
    }

    private fun showImageDetail(string: String) {
        if(string != null){
            val intent = Intent(context, DetailImageActivity::class.java)
            intent.putExtra(DetailImageActivity.IMAGE, string)
            startActivity(intent)
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

    private fun showLoading(isLoading: Boolean) {
        if(isLoading){
            binding.loading.visibility = View.VISIBLE
        }else{
            binding.loading.visibility = View.GONE
        }
    }
}