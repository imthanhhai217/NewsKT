package com.jaroid.newskt.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.jaroid.newskt.R

class BindingAdapter {
    companion object {

        @BindingAdapter("imgUrl")
        @JvmStatic
        fun loadImage(view: ImageView, url: String?) = Glide.with(view.context).load(url).error(R.drawable.ic_launcher_background).into(view)

    }
}