package com.example.exampletemplate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clientchodientu.R
import com.example.clientchodientu.entity.Category

class AdapterCategory(private var listCategory: List<Category>) :
    RecyclerView.Adapter<AdapterCategory.CategoryViewHolder>() {
    var onItemClick: ((Category) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = listCategory[position]
        holder.name.text = category.name

        val BASE_URL = "http://192.168.1.111:8080"
        val listImg = BASE_URL + category.iconUrl

        Glide.with(holder.itemView.context)
            .load(listImg)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_foreground)
            .centerCrop()
            .into(holder.img)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(category)
        }
    }

    override fun getItemCount(): Int = listCategory.size

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tvCategoryName)
        val img: ImageView = itemView.findViewById(R.id.imgCategory)
    }
}