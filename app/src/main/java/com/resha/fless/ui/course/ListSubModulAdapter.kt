package com.resha.fless.ui.course

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.resha.fless.R
import com.resha.fless.databinding.ItemSubModulBinding
import com.resha.fless.model.SubModul
import com.resha.fless.ui.material.MaterialActivity

class ListSubModulAdapter(private val listData : List<SubModul>) :
    RecyclerView.Adapter<ListSubModulAdapter.ListViewHolder>(){

    lateinit var context: Context

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemSubModulBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sub_modul, parent, false)
        context = parent.context
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListSubModulAdapter.ListViewHolder, position: Int) {
        holder.binding.apply {
            tvSubModulName.text= listData[position].name
            if(listData[position].type == "material"){
                cardView.setOnClickListener{
                    val intent = Intent(context, MaterialActivity::class.java)
                    intent.putExtra(MaterialActivity.MATERIAL_DETAIL, listData[position])
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int = listData.size
}