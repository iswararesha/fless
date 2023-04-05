package com.resha.fless.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.resha.fless.R
import com.resha.fless.model.Course
import com.resha.fless.model.Material
import com.resha.fless.model.Recent
import com.resha.fless.ui.course.ListCourseAdapter
import java.text.SimpleDateFormat
import java.util.*

class ListRecentAdapter (private val listData : List<Recent>) :
    RecyclerView.Adapter<ListRecentAdapter.ListViewHolder>(){
    private lateinit var onItemClickCallback: OnItemClickCallback
    lateinit var context: Context

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(material: Material)
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvRecentName: TextView = itemView.findViewById(R.id.tvRecentName)
        var tvDate: TextView = itemView.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_recent, parent, false)
        context = parent.context
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (_, subModulName, subModulId, modulParent, courseParent, date, _) = listData[position]
        val d = date?.toDate()?.let {
            SimpleDateFormat("dd MMMM yyyy - hh:mm a", Locale("id", "ID")).format(
                it)
        }

        holder.tvRecentName.text = subModulName
        holder.tvDate.text = d

        val material = Material(
            subModulId,
            courseParent,
            modulParent
        )

        holder.itemView.setOnClickListener{onItemClickCallback.onItemClicked(material)}
    }

    override fun getItemCount(): Int = listData.size
}