package com.example.yogaadminmvvm.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.yogaadminmvvm.R
import com.example.yogaadminmvvm.data.local.entities.YogaCourseEntity
import java.text.NumberFormat
import java.util.Locale

class YogaCourseAdapter(
    private val onCourseActionClickListener: OnCourseActionClickListener
) : ListAdapter<YogaCourseEntity, YogaCourseAdapter.YogaCourseViewHolder>(YogaCourseDiffCallback()) {

    interface OnCourseActionClickListener {
        fun onEditCourseClicked(course: YogaCourseEntity)
        fun onDeleteCourseClicked(course: YogaCourseEntity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YogaCourseViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false)
        return YogaCourseViewHolder(itemView, onCourseActionClickListener)
    }

    override fun onBindViewHolder(holder: YogaCourseViewHolder, position: Int) {
        val currentCourse = getItem(position)
        holder.bind(currentCourse)
    }

    class YogaCourseViewHolder(
        itemView: View,
        private val listener: OnCourseActionClickListener
        ) : RecyclerView.ViewHolder(itemView) {
        private val textViewCourseDayTime: TextView = itemView.findViewById(R.id.textViewCourseDayTime)
        private val textViewCourseType: TextView = itemView.findViewById(R.id.textViewCourseType)
        private val textViewCourseCapacity: TextView = itemView.findViewById(R.id.textViewCourseCapacity)
        private val textViewCoursePrice: TextView = itemView.findViewById(R.id.textViewCoursePrice)
        private val imageButtonEditCourse: ImageButton = itemView.findViewById(R.id.imageButtonEditCourse)
        private val imageButtonDeleteCourse: ImageButton = itemView.findViewById(R.id.imageButtonDeleteCourse)

        fun bind(course: YogaCourseEntity) {
            val context = itemView.context
            textViewCourseDayTime.text = context.getString(R.string.course_day_time_format, course.dayOfWeek, course.time)
            textViewCourseType.text = course.type.name // Or course.type.displayName if you have it
            
            textViewCourseCapacity.text = context.getString(R.string.label_capacity, course.capacity)
            try {
                val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
                textViewCoursePrice.text = context.getString(R.string.label_price, currencyFormatter.format(course.price))
            } catch (e: Exception) {
                textViewCoursePrice.text = context.getString(R.string.label_price, course.price.toString())
            }

            imageButtonEditCourse.setOnClickListener {
                listener.onEditCourseClicked(course)
            }

            imageButtonDeleteCourse.setOnClickListener {
                listener.onDeleteCourseClicked(course)
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
