package com.resha.fless.evaluation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.resha.fless.R
import com.resha.fless.databinding.ItemObjectiveBinding
import com.resha.fless.model.Answer
import com.resha.fless.model.Objective
import java.util.Collections.shuffle


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
        val id = collection.objectiveId?.toInt()
        var answer = Answer("number", "answer")

        val listAnswer = listOf(
            collection.optionA.toString(),
            collection.optionB.toString(),
            collection.optionC.toString(),
            collection.optionD.toString(),
            collection.optionE.toString())

        shuffle(listAnswer)

        holder.binding.apply {
            tvQuestion.text = collection.question
            optionA.text = listAnswer[0]
            optionB.text = listAnswer[1]
            optionC.text = listAnswer[2]
            optionD.text = listAnswer[3]
            optionE.text = listAnswer[4]
        }

        holder.binding.option.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.optionA -> {
                    when (holder.binding.optionA.text) {
                        collection.optionA.toString() -> {
                            answer = Answer("number$id", "optionA")
                        }
                        collection.optionB.toString() -> {
                            answer = Answer("number$id", "optionB")
                        }
                        collection.optionC.toString() -> {
                            answer = Answer("number$id", "optionC")
                        }
                        collection.optionD.toString() -> {
                            answer = Answer("number$id", "optionD")
                        }
                        collection.optionE.toString() -> {
                            answer = Answer("number$id", "optionE")
                        }
                    }
                }
                R.id.optionB -> {
                    when (holder.binding.optionB.text) {
                        collection.optionA.toString() -> {
                            answer = Answer("number$id", "optionA")
                        }
                        collection.optionB.toString() -> {
                            answer = Answer("number$id", "optionB")
                        }
                        collection.optionC.toString() -> {
                            answer = Answer("number$id", "optionC")
                        }
                        collection.optionD.toString() -> {
                            answer = Answer("number$id", "optionD")
                        }
                        collection.optionE.toString() -> {
                            answer = Answer("number$id", "optionE")
                        }
                    }
                }
                R.id.optionC -> {
                    when (holder.binding.optionC.text) {
                        collection.optionA.toString() -> {
                            answer = Answer("number$id", "optionA")
                        }
                        collection.optionB.toString() -> {
                            answer = Answer("number$id", "optionB")
                        }
                        collection.optionC.toString() -> {
                            answer = Answer("number$id", "optionC")
                        }
                        collection.optionD.toString() -> {
                            answer = Answer("number$id", "optionD")
                        }
                        collection.optionE.toString() -> {
                            answer = Answer("number$id", "optionE")
                        }
                    }
                }
                R.id.optionD -> {
                    when (holder.binding.optionD.text) {
                        collection.optionA.toString() -> {
                            answer = Answer("number$id", "optionA")
                        }
                        collection.optionB.toString() -> {
                            answer = Answer("number$id", "optionB")
                        }
                        collection.optionC.toString() -> {
                            answer = Answer("number$id", "optionC")
                        }
                        collection.optionD.toString() -> {
                            answer = Answer("number$id", "optionD")
                        }
                        collection.optionE.toString() -> {
                            answer = Answer("number$id", "optionE")
                        }
                    }
                }
                R.id.optionE -> {
                    when (holder.binding.optionE.text) {
                        collection.optionA.toString() -> {
                            answer = Answer("number$id", "optionA")
                        }
                        collection.optionB.toString() -> {
                            answer = Answer("number$id", "optionB")
                        }
                        collection.optionC.toString() -> {
                            answer = Answer("number$id", "optionC")
                        }
                        collection.optionD.toString() -> {
                            answer = Answer("number$id", "optionD")
                        }
                        collection.optionE.toString() -> {
                            answer = Answer("number$id", "optionE")
                        }
                    }
                }
            }

            onButtonCallback.onButtonChange(answer, id!!)
        }
    }

    override fun getItemCount(): Int = listData.size
}