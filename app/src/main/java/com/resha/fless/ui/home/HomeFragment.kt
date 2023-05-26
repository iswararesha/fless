package com.resha.fless.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.resha.fless.R
import com.resha.fless.databinding.FragmentHomeBinding
import com.resha.fless.model.Material
import com.resha.fless.model.Recent
import com.resha.fless.preference.UserPreference
import com.resha.fless.ui.ViewModelFactory
import com.resha.fless.ui.material.MaterialActivity
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

private val Context.dataStore by preferencesDataStore("user")
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var dataStore : DataStore<Preferences>

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        dataStore = requireContext().dataStore
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupViewModel()
        setupAction()

        return root
    }

    private fun setupViewModel() {
        homeViewModel = ViewModelProvider(this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[HomeViewModel::class.java]

        homeViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        homeViewModel.getUser()

        homeViewModel.userDetail.observe(viewLifecycleOwner) {
            binding.tvGreetUser.text = "Hi, ${it.name}"
        }

        homeViewModel.getGraph()

        homeViewModel.recent.observe(viewLifecycleOwner) {
            homeViewModel.getSuggestion(it)
            homeViewModel.getPoint(it)
            homeViewModel.getTotal(it)
            setView(it)
        }

        homeViewModel.suggestionData.observe(viewLifecycleOwner){
            binding.tvSuggestion.text = it
        }

        homeViewModel.pointData.observe(viewLifecycleOwner){
            setGraph(it)
        }

        homeViewModel.totalMaterialData.observe(viewLifecycleOwner){
            binding.tvTotalMaterial.text = it.toString()
        }

        homeViewModel.totalEvaluationData.observe(viewLifecycleOwner){
            binding.tvTotalEvaluation.text = it.toString()
        }

        homeViewModel.totalTaskData.observe(viewLifecycleOwner){
            binding.tvTotalTask.text = it.toString()
        }

        homeViewModel.getRecent()

        homeViewModel.recentData.observe(viewLifecycleOwner){
            setRecent(it)
        }
    }

    private fun setupAction(){
        binding.btnTutorial.setOnClickListener{
            val intent = Intent(context, TutorialActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setView(data: List<Recent>){
        binding.gvUserLog.visibility = View.INVISIBLE
        binding.tvSuggestion.visibility = View.INVISIBLE
        binding.graphBorder.visibility = View.INVISIBLE
        binding.imgEmpty.visibility = View.INVISIBLE
        binding.tvProses.visibility = View.INVISIBLE
        binding.tvTime.visibility = View.INVISIBLE

        if(data.isEmpty()){
            binding.tvSuggestion.visibility = View.VISIBLE
            binding.imgEmpty.visibility = View.VISIBLE
        }else{
            binding.gvUserLog.visibility = View.VISIBLE
            binding.tvSuggestion.visibility = View.VISIBLE
            binding.graphBorder.visibility = View.VISIBLE
            binding.tvProses.visibility = View.VISIBLE
            binding.tvTime.visibility = View.VISIBLE
        }
    }

    private fun setRecent(data: List<Recent>){
        if(data.isNotEmpty()){
            binding.rvRecent.visibility = View.VISIBLE
            binding.tvRecent.visibility = View.VISIBLE

            binding.rvRecent.layoutManager = LinearLayoutManager(context)

            val listRecentAdapter = ListRecentAdapter(data)
            binding.rvRecent.adapter = listRecentAdapter

            listRecentAdapter.setOnItemClickCallback(object: ListRecentAdapter.OnItemClickCallback{
                override fun onItemClicked(material: Material) {
                    showSelectedRecent(material)
                }
            })
        }else{
            binding.rvRecent.visibility = View.INVISIBLE
            binding.tvRecent.visibility = View.INVISIBLE
        }
    }

    private fun showSelectedRecent(material: Material) {
        val intent = Intent(context, MaterialActivity::class.java)
        intent.putExtra(MaterialActivity.MATERIAL_DETAIL, material)
        startActivity(intent)
    }

    private fun setGraph(data: List<DataPoint>){
        val lineGraphView = binding.gvUserLog
        lineGraphView.removeAllSeries()

        val z = ZoneId.systemDefault()
        val today: LocalDate = LocalDate.now(z)
        val startOfToday: ZonedDateTime = today.atStartOfDay(z)

        val dStart = startOfToday.minusDays(2)

        val dateStart = Date.from(dStart.toInstant())
        val dateEnd = Date.from(startOfToday.toInstant())

        val points = arrayOfNulls<DataPoint>(data.size)

        for (i in data.size downTo 1) {
            points[i-1] = DataPoint(data[i-1].x, data[i-1].y)
        }

        val series = LineGraphSeries(points)
        series.setAnimated(true)
        series.color = ContextCompat.getColor(activity?.applicationContext!!, R.color.firstYellow)

        lineGraphView.gridLabelRenderer.labelFormatter =  DateAsXAxisLabelFormatter(lineGraphView.context)
        lineGraphView.gridLabelRenderer.numHorizontalLabels = 3

        // set manual x bounds to have nice steps

        lineGraphView.viewport.setMinX(dateStart.time.toDouble())
        lineGraphView.viewport.setMaxX(dateEnd.time.toDouble())
        lineGraphView.viewport.isXAxisBoundsManual = true

        lineGraphView.viewport.setMinY(0.0)
        lineGraphView.viewport.setMaxY(20.0)
        lineGraphView.viewport.isYAxisBoundsManual = true

        // as we use dates as labels, the human rounding to nice readable numbers
        // is not nessecary
        lineGraphView.gridLabelRenderer.setHumanRounding(false)
        lineGraphView.addSeries(series)
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

        homeViewModel.getGraph()
        homeViewModel.getRecent()
    }
}