package com.example.clientchodientu.adapter

import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.clientchodientu.entity.Category

class CategoryAdapterAutoCompleteTextView(
    context: Context,
    private val allCategories: List<Category>
) : ArrayAdapter<Category>(context, 0, allCategories) {

    // Danh sách dùng để hiển thị (đã qua lọc)
    private var displayList: List<Category> = allCategories

    override fun getCount(): Int = displayList.size

    override fun getItem(position: Int): Category? {
        return if (position >= 0 && position < displayList.size) displayList[position] else null
    }

    override fun getFilter(): android.widget.Filter {
        return object : android.widget.Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()

                // Vì bạn muốn hiện toàn bộ khi click,
                // nên ta luôn trả về bản gốc allCategories
                results.values = allCategories
                results.count = allCategories.size
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                if (results != null && results.count >= 0) {
                    displayList = results.values as List<Category>
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }

            override fun convertResultToString(resultValue: Any?): CharSequence {
                return (resultValue as Category).name
            }
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    private fun createViewFromResource(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(android.R.layout.simple_dropdown_item_1line, parent, false)

        val category = getItem(position)
        val textView = view.findViewById<TextView>(android.R.id.text1)

        textView.text = category?.name ?: ""
        return view
    }
}