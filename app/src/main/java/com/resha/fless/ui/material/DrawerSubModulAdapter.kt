package com.resha.fless.ui.material

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.resha.fless.R
import com.resha.fless.databinding.ItemSubModulBinding
import com.resha.fless.model.Material
import com.resha.fless.model.SubModul

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
            if(listData[position].isOpen!!){
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
            }else{
                tvSubModulName.setTextColor(ContextCompat.getColor(context, R.color.firstYellow))
                tvSubModulName.text= listData[position].name
            }
        }
    }

    override fun getItemCount(): Int = listData.size
}