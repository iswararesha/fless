package com.resha.fless.ui.task

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.resha.fless.R
import com.resha.fless.databinding.ItemFinishBinding
import com.resha.fless.databinding.ItemOngoingBinding
import com.resha.fless.model.Material
import com.resha.fless.model.Task
import com.resha.fless.ui.task.Const.finished
import com.resha.fless.ui.task.Const.onGoing
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class ListTaskAdapter (private val listData : List<Task>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(material: Material)
    }

    lateinit var context: Context

    inner class OnGoingViewHolder(private val itemOngoingBinding: ItemOngoingBinding) :
        RecyclerView.ViewHolder(itemOngoingBinding.root) {
        fun bind(task: Task) {
            val z = ZoneId.systemDefault()
            val today: LocalDate = LocalDate.now(z)
            val startOfToday: ZonedDateTime = today.atStartOfDay(z)

            val todayDate = Date.from(startOfToday.toInstant())
            val deadLine =
                task.deadLine?.toDate()
                    ?.let {
                        SimpleDateFormat("dd MMMM yyyy",
                            Locale("id", "ID")).format(
                            it)
                    }

            itemOngoingBinding.tvTaskName.text = task.taskName
            itemOngoingBinding.tvCourseId.text = task.courseParent
            itemOngoingBinding.tvCourseName.text = task.courseName
            itemOngoingBinding.tvDeadline.text = "Deadline $deadLine"
            itemOngoingBinding.tvStatus.text = "Sedang Proses"

            if(todayDate > task.deadLine?.toDate()){
                itemOngoingBinding.tvDeadline.setTextColor(ContextCompat.getColor(context, R.color.firstRed))
                itemOngoingBinding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.firstRed))
            }else{
                itemOngoingBinding.tvDeadline.setTextColor(ContextCompat.getColor(context, R.color.firstYellow))
                itemOngoingBinding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.firstYellow))
            }

            val material = Material(
                task.subModulId,
                task.courseParent,
                task.modulParent
            )

            itemOngoingBinding.cardView.setOnClickListener{onItemClickCallback.onItemClicked(material)}
        }
    }

    inner class FinishViewHolder(private val itemFinishBinding: ItemFinishBinding) :
        RecyclerView.ViewHolder(itemFinishBinding.root) {
        fun bind(task: Task) {
            val z = ZoneId.systemDefault()
            val today: LocalDate = LocalDate.now(z)
            val startOfToday: ZonedDateTime = today.atStartOfDay(z)

            val todayDate = Date.from(startOfToday.toInstant())
            val finishDate =
                task.finishDate?.toDate()
                    ?.let {
                        SimpleDateFormat("dd MMMM yyyy - hh:mm a",
                            Locale("id", "ID")).format(
                            it)
                    }

            itemFinishBinding.tvTaskName.text = task.taskName
            itemFinishBinding.tvCourseId.text = task.courseParent
            itemFinishBinding.tvCourseName.text = task.courseName
            itemFinishBinding.tvDeadline.text = "Selesai pada $finishDate"

            if(task.finishDate?.toDate()!! > task.deadLine?.toDate()){
                itemFinishBinding.tvStatus.text = "Terlambat"
                itemFinishBinding.tvDeadline.setTextColor(ContextCompat.getColor(context, R.color.firstRed))
                itemFinishBinding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.firstRed))
            }else{
                itemFinishBinding.tvStatus.text = "Selesai"
                itemFinishBinding.tvDeadline.setTextColor(ContextCompat.getColor(context, R.color.firstGreen))
                itemFinishBinding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.firstGreen))
            }

            val material = Material(
                task.subModulId,
                task.courseParent,
                task.modulParent
            )

            itemFinishBinding.cardView.setOnClickListener{onItemClickCallback.onItemClicked(material)}
        }
    }

    override fun getItemViewType (position: Int): Int {
        return when (listData[position].isFinish) {
            false -> onGoing
            true -> finished
            else -> onGoing
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return when (viewType) {
            onGoing -> {
                val view =
                    ItemOngoingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                OnGoingViewHolder(view)
            }
            finished -> {
                val view =
                    ItemFinishBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FinishViewHolder(view)
            }
            else -> {
                val view =
                    ItemOngoingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                OnGoingViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType (position) == onGoing) {
            (holder as OnGoingViewHolder).bind(listData[position])
        } else if (getItemViewType (position) == finished) {
            (holder as FinishViewHolder).bind(listData[position])
        }
    }

    override fun getItemCount(): Int = listData.size
}

private object Const{
    const val onGoing = 0
    const val finished = 1
}