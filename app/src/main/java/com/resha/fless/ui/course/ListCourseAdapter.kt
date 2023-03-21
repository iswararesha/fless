package com.resha.fless.ui.course

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.resha.fless.R
import com.resha.fless.model.Course

class ListCourseAdapter(private val listData : List<Course>) :RecyclerView.Adapter<ListCourseAdapter.ListViewHolder>(){
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(course: Course)
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvCourseName: TextView = itemView.findViewById(R.id.tvCourseName)
        var tvCourseDescription: TextView = itemView.findViewById(R.id.tvCourseDescription)
        var tvHourNeed: TextView = itemView.findViewById(R.id.tvHourNeed)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListCourseAdapter.ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListCourseAdapter.ListViewHolder, position: Int) {
        val (_, courseDescription, courseName, hourNeed) = listData[position]
        holder.tvCourseDescription.text = courseDescription
        holder.tvCourseName.text = courseName
        holder.tvHourNeed.text = hourNeed
        holder.itemView.setOnClickListener{onItemClickCallback.onItemClicked(listData[holder.adapterPosition])}
    }

    override fun getItemCount(): Int = listData.size
}