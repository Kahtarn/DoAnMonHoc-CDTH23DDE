package com.example.clientchodientu.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clientchodientu.R

class ProductImageAdapter(
    private val imageList: List<String>, private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<ProductImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imgProduct)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Glide.with(holder.imageView.context)
            .load(imageList[position])
            .placeholder(R.drawable.ic_launcher_foreground) // optional
            .error(R.drawable.ic_launcher_foreground)
            .fitCenter()// optional
            .into(holder.imageView)
        holder.itemView.setOnClickListener {
            onItemClick(position) // Truyền vị trí ảnh được click ra ngoài
        }
    }

    override fun getItemCount(): Int = imageList.size
}