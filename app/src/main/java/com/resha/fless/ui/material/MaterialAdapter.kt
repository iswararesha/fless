package com.resha.fless.ui.material

import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.resha.fless.databinding.ItemGifBinding
import com.resha.fless.databinding.ItemImgBinding
import com.resha.fless.databinding.ItemTextBinding
import com.resha.fless.model.Content
import com.resha.fless.ui.material.Const.gifType
import com.resha.fless.ui.material.Const.imgType
import com.resha.fless.ui.material.Const.txtType
import com.resha.fless.ui.material.Const.videoType

class MaterialAdapter (private val listData : List<Content>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var context: Context

    inner class TextMaterialViewHolder(private val itemTextBinding: ItemTextBinding) :
        RecyclerView.ViewHolder(itemTextBinding.root) {
        fun bind(content: Content) {
            itemTextBinding.tvContent.text = content.content
        }
    }

    inner class ImgMaterialViewHolder(private val itemImgBinding: ItemImgBinding) :
        RecyclerView.ViewHolder(itemImgBinding.root) {
        fun bind(content: Content) {
            Glide.with(context)
                .load(content.content)
                .into(itemImgBinding.tvContent)
        }
    }

    inner class GifMaterialViewHolder(private val itemGifBinding: ItemGifBinding) :
        RecyclerView.ViewHolder(itemGifBinding.root) {
        fun bind(content: Content) {
            Glide.with(context)
                .load(content.content)
                .into(itemGifBinding.tvContent)
        }
    }

     override fun getItemViewType (position: Int): Int {
        Log.e(ContentValues.TAG, listData[position].type!! + " from getContent")
        return when (listData[position].type) {
            "text" -> txtType
            "img" -> imgType
            "gif" -> gifType
            else ->  videoType
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.e(ContentValues.TAG, "$viewType from onCreate")
        context = parent.context
        return when (viewType) {
            txtType -> {
                val view =
                    ItemTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TextMaterialViewHolder(view)
            }
            imgType -> {
                val view =
                    ItemImgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ImgMaterialViewHolder(view)
            }
            gifType -> {
                val view =
                    ItemGifBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                GifMaterialViewHolder(view)
            }
            else -> {
                val view =
                    ItemTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TextMaterialViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.e(ContentValues.TAG, getItemViewType (position).toString() + " from onBind")
        if (getItemViewType (position) == txtType) {
            (holder as TextMaterialViewHolder).bind(listData[position])
        } else if (getItemViewType (position) == imgType) {
            (holder as ImgMaterialViewHolder).bind(listData[position])
        } else if (getItemViewType (position) == gifType) {
            (holder as GifMaterialViewHolder).bind(listData[position])
        } else {
            (holder as TextMaterialViewHolder).bind(listData[position])
        }
    }

    override fun getItemCount(): Int = listData.size
}

private object Const{
    const val txtType = 0
    const val imgType = 1
    const val gifType = 2
    const val videoType = 3
}