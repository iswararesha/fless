package com.resha.fless.ui.material

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.resha.fless.R
import com.resha.fless.model.Attempt
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

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

        val date = SimpleDateFormat("dd MMMM yyyy - hh:mm a", Locale("id", "ID")).format(id.dateAttempt?.toDate())

        holder.tvDateAttempt.text = date
        holder.tvScoreAttempt.text = id.score
    }

    override fun getItemCount(): Int = listData.size
}