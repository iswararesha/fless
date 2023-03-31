package com.resha.fless.ui.course

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.resha.fless.R
import com.resha.fless.databinding.ItemModulBinding
import com.resha.fless.model.Modul


class ListModulAdapter(private val listData : List<Modul>) : RecyclerView.Adapter<ListModulAdapter.ListViewHolder>(){
    lateinit var context: Context

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemModulBinding.bind(itemView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_modul, parent, false)
        context = parent.context
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.binding.apply {
            val collection = listData[position]
            tvModulName.text = collection.modulName

            val listModulAdapter = ListSubModulAdapter(collection.subModul!!)
            rvSubModul.adapter = listModulAdapter
            rvSubModul.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            cardView.setOnClickListener {
                rvSubModul.visibility = if (rvSubModul.isShown) View.GONE else View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int = listData.size
}