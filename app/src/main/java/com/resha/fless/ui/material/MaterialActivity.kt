package com.resha.fless.ui.material

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.resha.fless.R
import com.resha.fless.databinding.ActivityMaterialBinding
import com.resha.fless.model.*
import com.resha.fless.ui.ViewModelFactory


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class MaterialActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMaterialBinding
    private lateinit var materialViewModel: MaterialViewModel
    private lateinit var material: Material

    private var drawer = false

    companion object{
        const val MATERIAL_DETAIL = "data_detail"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaterialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        material = intent.getParcelableExtra<Material>(MATERIAL_DETAIL) as Material

        setupViewModel()
        setupAction()
    }

    private fun setupViewModel() {
        materialViewModel = ViewModelProvider(this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MaterialViewModel::class.java]

        materialViewModel.materialData.observe(this){
            setFragment(it)
        }

        materialViewModel.getSubModul(material)
    }

    private fun setupAction(){
        binding.nextButton.setOnClickListener(){
            if(drawer){
                drawer = false
                val oldFragment = supportFragmentManager.findFragmentById(R.id.drawer_fragment)
                supportFragmentManager.popBackStack()
                supportFragmentManager.beginTransaction()
                    .remove(oldFragment!!).commit()
            }

            materialViewModel.nextModul(this)
        }

        binding.previousButton.setOnClickListener(){
            if(drawer){
                drawer = false
                val oldFragment = supportFragmentManager.findFragmentById(R.id.drawer_fragment)
                supportFragmentManager.popBackStack()
                supportFragmentManager.beginTransaction()
                    .remove(oldFragment!!).commit()
            }

            materialViewModel.prevModul(this)
        }
    }

    private fun setFragment(subModul: SubModul){
        when (subModul.type) {
            "material" -> {
                setButtonOn()

                val fragmentManager = supportFragmentManager
                val materialFragment = MaterialFragment()

                fragmentManager
                    .beginTransaction()
                    .replace(R.id.material_view_fragment, materialFragment, MaterialFragment::class.java.simpleName)
                    .commit()

            }
            "task" -> {
                setButtonOff()

                val fragmentManager = supportFragmentManager
                val taskViewFragment = TaskViewFragment()

                fragmentManager
                    .beginTransaction()
                    .replace(R.id.material_view_fragment, taskViewFragment, TaskViewFragment::class.java.simpleName)
                    .commit()
            }
            else -> {
                setButtonOff()

                val fragmentManager = supportFragmentManager
                val evaluationFragment = EvaluationFragment()

                fragmentManager
                    .beginTransaction()
                    .replace(R.id.material_view_fragment, evaluationFragment, EvaluationFragment::class.java.simpleName)
                    .commit()
            }
        }
    }

    private fun setButtonOff(){
        binding.nextButton.isEnabled = false
        binding.nextButton.isClickable = false
        binding.nextButton.setBackgroundColor(ContextCompat.getColor(this, R.color.firstYellow))
    }

    fun setButtonOn(){
        binding.nextButton.isEnabled = true
        binding.nextButton.isClickable = true
        binding.nextButton.setBackgroundColor(ContextCompat.getColor(this, R.color.firstBlue))
    }

    fun getNewMaterial(material: Material){
        drawer = false
        supportFragmentManager.popBackStack()

        val oldFragment = supportFragmentManager.findFragmentById(R.id.drawer_fragment)
        supportFragmentManager.beginTransaction()
            .remove(oldFragment!!).commit()

        materialViewModel.getSubModul(material)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.material_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.materialDrawer -> {
                if (drawer) {
                    drawer = false
                    supportFragmentManager.popBackStack()

                    val oldFragment = supportFragmentManager.findFragmentById(R.id.drawer_fragment)
                    supportFragmentManager.beginTransaction()
                        .remove(oldFragment!!).commit()
                }else{
                    drawer = true
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.drawer_fragment, DrawerFragment())
                        .addToBackStack("material")
                        .commit()
                }
                true
            }
            else -> true
        }
    }
}