package com.example.yogaadminmvvm.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.yogaadminmvvm.R
import com.example.yogaadminmvvm.data.local.entities.YogaCourseEntity
import java.text.NumberFormat
import java.util.Locale

class YogaCourseAdapter : ListAdapter<YogaCourseEntity, YogaCourseAdapter.YogaCourseViewHolder>(YogaCourseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YogaCourseViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false)
        return YogaCourseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: YogaCourseViewHolder, position: Int) {
        val currentCourse = getItem(position)
        holder.bind(currentCourse)
    }

    class YogaCourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewCourseDayTime: TextView = itemView.findViewById(R.id.textViewCourseDayTime)
        private val textViewCourseType: TextView = itemView.findViewById(R.id.textViewCourseType)
        private val textViewCourseCapacity: TextView = itemView.findViewById(R.id.textViewCourseCapacity)
        private val textViewCoursePrice: TextView = itemView.findViewById(R.id.textViewCoursePrice)
        // You can add a NumberFormat instance for currency formatting if desired
        // private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())


        fun bind(course: YogaCourseEntity) {
            textViewCourseDayTime.text = "${course.dayOfWeek} - ${course.time}"
            textViewCourseType.text = course.type.name // Or course.type.displayName if you have it
            textViewCourseCapacity.text = "Capacity: ${course.capacity}"
            // Example of formatting price as currency, ensure course.price is Double
             try {
                val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
                textViewCoursePrice.text = "Price: ${currencyFormatter.format(course.price)}"
            } catch (e: Exception) {
                // Fallback or log error if formatting fails
                textViewCoursePrice.text = "Price: ${course.price}"
            }
        }
    }

    class YogaCourseDiffCallback : DiffUtil.ItemCallback<YogaCourseEntity>() {
        override fun areItemsTheSame(oldItem: YogaCourseEntity, newItem: YogaCourseEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: YogaCourseEntity, newItem: YogaCourseEntity): Boolean {
            return oldItem == newItem
        }
    }
}
