package com.resha.fless.ui.course

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.resha.fless.R
import com.resha.fless.model.Course

class ListCourseAdapter(private val listData : List<Course>) :RecyclerView.Adapter<ListCourseAdapter.ListViewHolder>(){
    private lateinit var onItemClickCallback: OnItemClickCallback
    lateinit var context: Context

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(course: Course)
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvCourseName: TextView = itemView.findViewById(R.id.tvCourseName)
        var tvTotalMaterial: TextView = itemView.findViewById(R.id.tvTotalMaterial)
        var tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        var imgCourse: ImageView = itemView.findViewById(R.id.imgCourse)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false)
        context = parent.context
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (_, _, _, courseName, totalMaterial, isOpen, courseImg) = listData[position]
        holder.tvCourseName.text = courseName
        holder.tvTotalMaterial.text = totalMaterial.toString() + " Hari Pelajaran"

        if(isOpen!!){
            holder.tvStatus.text = "Terdaftar"
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.firstGreen))
        }else{
            holder.tvStatus.text = "Belum Dibuka"
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.firstYellow))
        }

        Glide.with(context)
            .load(courseImg)
            .into(holder.imgCourse)
        holder.itemView.setOnClickListener{onItemClickCallback.onItemClicked(listData[holder.adapterPosition])}
    }

    override fun getItemCount(): Int = listData.size
}