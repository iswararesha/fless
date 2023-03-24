package com.resha.fless.evaluation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.resha.fless.R
import com.resha.fless.databinding.ItemObjectiveBinding
import com.resha.fless.model.Answer
import com.resha.fless.model.Objective


class ListObjectiveAdapter (private val listData : List<Objective>) : RecyclerView.Adapter<ListObjectiveAdapter.ListViewHolder>(){
    private lateinit var onButtonCallback: OnButtonCallback

    fun setOnButtonChange(onButtonCallback: OnButtonCallback){
        this.onButtonCallback = onButtonCallback
    }

    interface OnButtonCallback {
        fun onButtonChange(answer: Answer, int: Int)
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemObjectiveBinding.bind(itemView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_objective, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val collection = listData[position]
        val id = position + 1
        var answer = Answer("number", "answer")

        holder.binding.apply {
            tvQuestion.text = collection.question
            optionA.text = collection.optionA
            optionB.text = collection.optionB
            optionC.text = collection.optionC
            optionD.text = collection.optionD
            optionE.text = collection.optionE
        }
        holder.binding.option.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.optionA -> {
                    answer = Answer("number$id", "optionA")
                }
                R.id.optionB -> {
                    answer = Answer("number$id", "optionB")
                }
                R.id.optionC -> {
                    answer = Answer("number$id", "optionC")
                }
                R.id.optionD -> {
                    answer = Answer("number$id", "optionD")
                }
                R.id.optionE -> {
                    answer = Answer("number$id", "optionE")
                }
            }

            onButtonCallback.onButtonChange(answer, id)
        }
    }

    override fun getItemCount(): Int = listData.size
}