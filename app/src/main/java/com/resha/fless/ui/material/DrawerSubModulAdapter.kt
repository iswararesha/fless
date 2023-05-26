package com.resha.fless.ui.material

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.resha.fless.R
import com.resha.fless.databinding.ItemSubModulBinding
import com.resha.fless.model.Material
import com.resha.fless.model.SubModul
import com.resha.fless.ui.course.ListSubModulAdapter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class DrawerSubModulAdapter(private var listData : List<SubModul>) :
    RecyclerView.Adapter<DrawerSubModulAdapter.ListViewHolder>(){

    lateinit var context: Context

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemSubModulBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sub_modul, parent, false)
        context = parent.context
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.binding.apply {
            if(listData[position].type == "task"){
                val z = ZoneId.systemDefault()
                val today: LocalDate = LocalDate.now(z)
                val startOfToday: ZonedDateTime = today.atStartOfDay(z)

                val todayDate = Date.from(startOfToday.toInstant())

                val deadLine =
                    listData[position].deadLine?.toDate()
                        ?.let {
                            SimpleDateFormat("dd MMMM yyyy",
                                Locale("id", "ID")).format(
                                it)
                        }

                val dateOpen =
                    listData[position].dateOpen?.toDate()
                        ?.let {
                            SimpleDateFormat("dd MMMM yyyy",
                                Locale("id", "ID")).format(
                                it)
                        }

                tvSubModulName.text= listData[position].name

                if(todayDate >= listData[position].dateOpen?.toDate()){
                    cardView.setOnClickListener{
                        val nextMaterial = Material(
                            listData[position].subModulId,
                            listData[position].courseParent,
                            listData[position].modulParent
                        )

                        if (context is MaterialActivity) {
                            (context as MaterialActivity).getNewMaterial(nextMaterial)
                        }
                    }

                    if(listData[position].isFinish!!){
                        var drawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_check_circle_24)
                        drawable = DrawableCompat.wrap(drawable!!)
                        DrawableCompat.setTint(drawable.mutate(), ContextCompat.getColor(context, R.color.firstGreen))

                        tvDeadline.visibility = View.VISIBLE
                        tvDeadline.text = "Deadline $deadLine"

                        tvSubModulName.setCompoundDrawables(null, null, null, null)
                        tvSubModulName.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)

                        if(listData[position].finishDate != null){
                            if(listData[position].finishDate?.toDate()!! <= listData[position].deadLine?.toDate()){
                                tvSubModulName.setTextColor(ContextCompat.getColor(context, R.color.firstGreen))
                                tvDeadline.setTextColor(ContextCompat.getColor(context, R.color.firstGreen))
                            }else{
                                tvSubModulName.setTextColor(ContextCompat.getColor(context, R.color.firstRed))
                                tvDeadline.setTextColor(ContextCompat.getColor(context, R.color.firstRed))
                            }
                        }
                    }else{
                        var drawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_access_time_24)
                        drawable = DrawableCompat.wrap(drawable!!)
                        DrawableCompat.setTint(drawable.mutate(), ContextCompat.getColor(context, R.color.firstYellow))

                        tvDeadline.visibility = View.VISIBLE
                        tvDeadline.text = "Deadline $deadLine"

                        tvSubModulName.setCompoundDrawables(null, null, null, null)
                        tvSubModulName.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)

                        if(todayDate <= listData[position].deadLine?.toDate()){
                            tvSubModulName.setTextColor(ContextCompat.getColor(context, R.color.firstYellow))
                            tvDeadline.setTextColor(ContextCompat.getColor(context, R.color.firstYellow))
                        }else{
                            tvSubModulName.setTextColor(ContextCompat.getColor(context, R.color.firstRed))
                            tvDeadline.setTextColor(ContextCompat.getColor(context, R.color.firstRed))
                        }
                    }
                }else{
                    var drawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_lock_24)
                    drawable = DrawableCompat.wrap(drawable!!)
                    DrawableCompat.setTint(drawable.mutate(), ContextCompat.getColor(context, R.color.firstYellow))

                    tvDeadline.visibility = View.VISIBLE
                    tvDeadline.text = "Terbuka pada $dateOpen"
                    tvDeadline.setTextColor(ContextCompat.getColor(context, R.color.firstYellow))

                    tvSubModulName.setCompoundDrawables(null, null, null, null)
                    tvSubModulName.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
                    tvSubModulName.setTextColor(ContextCompat.getColor(context, R.color.firstYellow))
                }
            } else {
                tvSubModulName.text= listData[position].name
                cardView.setOnClickListener{
                    val nextMaterial = Material(
                        listData[position].subModulId,
                        listData[position].courseParent,
                        listData[position].modulParent
                    )

                    if (context is MaterialActivity) {
                        (context as MaterialActivity).getNewMaterial(nextMaterial)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = listData.size
}