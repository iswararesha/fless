package com.resha.fless.ui.evaluation

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.resha.fless.R
import com.resha.fless.model.Answer

class ListEssayAdapter (private val listData : List<Int>) : RecyclerView.Adapter<ListEssayAdapter.ListViewHolder>(){
    private lateinit var onTextCallback: OnTextCallback

    fun setOnTextChange(onTextCallback: OnTextCallback){
        this.onTextCallback = onTextCallback
    }

    interface OnTextCallback {
        fun onTextChange(answer: ArrayList<Answer>)
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvAnswerField: TextView = itemView.findViewById(R.id.tvAnswerField)
        var edtAnswerField: EditText = itemView.findViewById(R.id.edtAnswerField)
    }

    val answer = ArrayList<Answer>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_field, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val id = listData[position]
        answer.add(Answer("number$id", "answer$id"))

        holder.tvAnswerField.text = "Soal No $id"
        holder.edtAnswerField.hint = "Jawaban No $id"

        holder.edtAnswerField.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                answer[id-1] = Answer("number$id", s.toString().trim())
                onTextCallback.onTextChange(answer)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun getItemCount(): Int = listData.size
}