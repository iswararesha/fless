package com.resha.fless.ui.task

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.resha.fless.databinding.ItemFinishBinding
import com.resha.fless.databinding.ItemOngoingBinding
import com.resha.fless.model.Progress
import com.resha.fless.model.Material
import com.resha.fless.ui.task.Const.finished
import com.resha.fless.ui.task.Const.onGoing

class ListTaskAdapter (private val listData : List<Progress>) :
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
        fun bind(progress: Progress) {
            itemOngoingBinding.tvTaskName.text = progress.taskId
            itemOngoingBinding.tvDateOpen.text = "Dibuka ${progress.dateOpen}"

            val material = Material(
                progress.subModulId,
                progress.courseParent,
                progress.modulParent
            )

            itemOngoingBinding.cardView.setOnClickListener{onItemClickCallback.onItemClicked(material)}
        }
    }

    inner class FinishViewHolder(private val itemFinishBinding: ItemFinishBinding) :
        RecyclerView.ViewHolder(itemFinishBinding.root) {
        fun bind(progress: Progress) {
            itemFinishBinding.tvTaskName.text = progress.taskId
            itemFinishBinding.tvDateFinish.text = "Selesai ${progress.dateFinish}"

            val material = Material(
                progress.subModulId,
                progress.courseParent,
                progress.modulParent
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