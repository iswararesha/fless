package com.resha.fless.ui.material

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.resha.fless.databinding.ActivityDetailImageBinding

class DetailImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailImageBinding
    private lateinit var image: String

    companion object{
        const val IMAGE = "image"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        image = intent.getStringExtra(IMAGE) as String

        setupView()
        setupAction()
    }

    private fun setupView(){
        supportActionBar?.hide()

        Glide.with(this)
            .load(image)
            .into(binding.imgDetail)
    }

    private fun setupAction(){
        binding.btnClear.setOnClickListener(){
            finish()
        }
    }
}