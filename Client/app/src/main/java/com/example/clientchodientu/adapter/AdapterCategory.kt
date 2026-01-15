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

class AdapterCategory(private var ListCategory: List<Category>,
                      private val onItemClick:(Category)-> Unit
): RecyclerView.Adapter<AdapterCategory.CategoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_category,parent,false)
        return CategoryViewHolder(view)
    }
    fun updateData(newList: List<Category>) {
        this.ListCategory = newList
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(
        holder: AdapterCategory.CategoryViewHolder,
        position: Int
    ) {
        val category = ListCategory[position]
        holder.name.text= category.name
        Glide.with(holder.itemView.context)
            .load(category.iconUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_foreground)
            .into(holder.img)
        holder.itemView.setOnClickListener {
            onItemClick(category)
        }

    }

    override fun getItemCount(): Int=ListCategory.size
    inner class CategoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val name: TextView=itemView.findViewById(R.id.tvCategoryName)
        val img: ImageView=itemView.findViewById(R.id.imgCategory)
    }
}