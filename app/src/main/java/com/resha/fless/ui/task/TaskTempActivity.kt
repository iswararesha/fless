package com.resha.fless.ui.task

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.resha.fless.databinding.ActivityTaskTempBinding
import com.resha.fless.model.Content
import com.resha.fless.model.Material
import com.resha.fless.preference.UserPreference
import com.resha.fless.ui.ViewModelFactory
import com.resha.fless.ui.material.*

private val Context.dataStore by preferencesDataStore("user")
class TaskTempActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskTempBinding
    private lateinit var materialViewModel: MaterialViewModel
    private lateinit var material: Material

    companion object{
        const val MATERIAL_DETAIL = "data_detail"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskTempBinding.inflate(layoutInflater)
        setContentView(binding.root)

        material = intent.getParcelableExtra<Material>(MaterialActivity.MATERIAL_DETAIL) as Material

        setupViewModel()
        setupAction()
    }

    private fun setupViewModel() {
        materialViewModel = ViewModelProvider(this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MaterialViewModel::class.java]

        materialViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        materialViewModel.materialData.observe(this) {
            title = it.name
        }

        materialViewModel.getSubModul(material)

        materialViewModel.materialData.observe(this) {
            val material = Material(
                it.subModulId,
                it.courseParent,
                it.modulParent
            )

            materialViewModel.getContent(material)
            materialViewModel.getUserTask(material)
            materialViewModel.getUserTaskComment(material)
        }

        materialViewModel.contentData.observe(this) {
            setMaterialData(it)
        }

        materialViewModel.taskStatus.observe(this) {
            setStatus(it)
        }

        materialViewModel.taskComment.observe(this) {
            binding.tvComment.text = it
        }
    }

    private fun setStatus(status: Boolean?) {
        if(status == true){
            binding.imgStatus.visibility = View.VISIBLE
            binding.tvTaskStatus.visibility = View.VISIBLE

            binding.tvCommentPage.visibility = View.VISIBLE
            binding.tvCommentTitle.visibility = View.VISIBLE
            binding.tvComment.visibility = View.VISIBLE

            binding.tvChosenFile.visibility = View.GONE
            binding.uploadButton.visibility = View.GONE
        }else{
            binding.imgStatus.visibility = View.INVISIBLE
            binding.tvTaskStatus.visibility = View.INVISIBLE

            binding.tvCommentPage.visibility = View.GONE
            binding.tvCommentTitle.visibility = View.INVISIBLE
            binding.tvComment.visibility = View.INVISIBLE

            binding.tvChosenFile.visibility = View.VISIBLE
            binding.uploadButton.visibility = View.VISIBLE
        }
    }

    private fun setMaterialData(data: List<Content>) {
        if (data.isNotEmpty()) {
            binding.rvMaterial.visibility = View.VISIBLE
            binding.rvMaterial.layoutManager = LinearLayoutManager(this)

            val materialAdapter = MaterialAdapter(data)
            binding.rvMaterial.adapter = materialAdapter

            materialAdapter.setOnItemClickCallback(object :
                MaterialAdapter.OnItemClickCallback {
                override fun onItemClicked(string: String) {
                    showImageDetail(string)
                }

                override fun onVideoClicked(string: String) {
                    showVideoDetail(string)
                }
            })

        } else {
            binding.rvMaterial.visibility = View.INVISIBLE
        }
    }

    private fun setupAction() {
        binding.tvChosenFile.setOnClickListener() {
            startDocument()
        }

        binding.uploadButton.setOnClickListener() {
            if (getFile != null) {
                materialViewModel.uploadTask(getFile!!, getFileName!!)
            } else {
                Toast.makeText(this, "Pilih file terlebih dahulu!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showImageDetail(string: String) {
        if (string != null) {
            val intent = Intent(this, DetailImageActivity::class.java)
            intent.putExtra(DetailImageActivity.IMAGE, string)
            startActivity(intent)
        }
    }

    private fun showVideoDetail(string: String) {
        if(string != null){
            val intent = Intent(this, VideoActivity::class.java)
            intent.putExtra(VideoActivity.VIDEO, string)
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
        if (result.resultCode == RESULT_OK) {
            val selectedFile: Uri = result.data?.data as Uri
            val selectedFileName = getFileName(this, selectedFile)

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
                    if (cursor.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }
        }
        return uri.path?.lastIndexOf('/')?.let { uri.path?.substring(it) }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loading.visibility = View.VISIBLE
        } else {
            binding.loading.visibility = View.GONE
        }
    }
}