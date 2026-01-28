package com.example.clientchodientu.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clientchodientu.R

class SelectedImageAdapter(
    private val onAddImageClick: () -> Unit,
    private val onRemoveImageClick: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val imageList = mutableListOf<Uri>()

    companion object {
        private const val TYPE_ADD = 0
        private const val TYPE_IMAGE = 1
        private const val MAX_IMAGES = 6
    }

    // Cập nhật danh sách ảnh
    fun setData(uris: List<Uri>) {
        imageList.clear()
        imageList.addAll(uris)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        // Nếu vị trí bằng size danh sách thì hiện nút "Thêm" (nằm cuối cùng)
        return if (position == imageList.size) TYPE_ADD else TYPE_IMAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ADD) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_add_image, parent, false)
            AddViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_selected_image, parent, false)
            ImageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ImageViewHolder) {
            val uri = imageList[position]
            holder.ivSelected.setImageURI(uri)

            if (position == 0) {
                holder.tvCover.visibility = View.VISIBLE
                holder.tvCover.text = "Ảnh bìa"
                holder.tvCover.setBackgroundColor(0xCCFF9800.toInt())
            } else {
                holder.tvCover.visibility = View.VISIBLE
                holder.tvCover.text = "Ảnh ${position}"
                holder.tvCover.setBackgroundColor(0x80000000.toInt())
            }

            holder.btnRemove.setOnClickListener { onRemoveImageClick(position) }

        } else if (holder is AddViewHolder) {
            holder.itemView.setOnClickListener { onAddImageClick() }
        }
    }

    override fun getItemCount(): Int {
        return if (imageList.size >= MAX_IMAGES) imageList.size else imageList.size + 1
    }

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivSelected: ImageView = view.findViewById(R.id.ivSelectedImage)
        val btnRemove: ImageView = view.findViewById(R.id.btnRemoveImage)
        val tvCover: TextView = view.findViewById(R.id.tvCoverLabel)
    }
    class AddViewHolder(view: View) : RecyclerView.ViewHolder(view)
}