package com.resha.fless.ui.material

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.resha.fless.R
import com.resha.fless.model.Attempt

class EvaluationAdapter (private val listData : List<Attempt>) :
    RecyclerView.Adapter<EvaluationAdapter.ListViewHolder>() {
    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvDateAttempt: TextView = itemView.findViewById(R.id.tvDateAttempt)
        var tvScoreAttempt: TextView = itemView.findViewById(R.id.tvScoreAttempt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ListViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_attempt, parent, false)
            return ListViewHolder(view)
        }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val id = listData[position]

        holder.tvDateAttempt.text = id.dateAttempt
        holder.tvScoreAttempt.text = id.score
    }

    override fun getItemCount(): Int = listData.size
}