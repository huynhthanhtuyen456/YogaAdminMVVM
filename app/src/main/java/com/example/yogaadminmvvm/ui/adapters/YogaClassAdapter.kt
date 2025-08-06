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
import com.example.yogaadminmvvm.data.local.entities.YogaClassEntity

class YogaClassAdapter(
    private val listener: OnClassActionClickListener
) : ListAdapter<YogaClassEntity, YogaClassAdapter.YogaClassViewHolder>(YogaClassDiffCallback()) {

    interface OnClassActionClickListener {
        fun onEditClassClicked(yogaClass: YogaClassEntity)
        fun onDeleteClassClicked(yogaClass: YogaClassEntity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YogaClassViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_yoga_class, parent, false)
        return YogaClassViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: YogaClassViewHolder, position: Int) {
        val currentClass = getItem(position)
        holder.bind(currentClass)
    }

    inner class YogaClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewClassDate: TextView = itemView.findViewById(R.id.textViewClassDate)
        private val textViewTeacherName: TextView = itemView.findViewById(R.id.textViewTeacherName)
        private val textViewClassComments: TextView = itemView.findViewById(R.id.textViewClassComments)
        private val imageButtonEditClass: ImageButton = itemView.findViewById(R.id.imageButtonEditClass)
        private val imageButtonDeleteClass: ImageButton = itemView.findViewById(R.id.imageButtonDeleteClass)

        init {
            imageButtonEditClass.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onEditClassClicked(getItem(position))
                }
            }
            imageButtonDeleteClass.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClassClicked(getItem(position))
                }
            }
        }

        fun bind(yogaClass: YogaClassEntity) {
            textViewClassDate.text = yogaClass.date // Consider formatting this date
            textViewTeacherName.text = yogaClass.teacherName
            if (yogaClass.comments.isNullOrEmpty()) {
                textViewClassComments.visibility = View.GONE
            } else {
                textViewClassComments.visibility = View.VISIBLE
                textViewClassComments.text = yogaClass.comments
            }
        }
    }

    class YogaClassDiffCallback : DiffUtil.ItemCallback<YogaClassEntity>() {
        override fun areItemsTheSame(oldItem: YogaClassEntity, newItem: YogaClassEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: YogaClassEntity, newItem: YogaClassEntity): Boolean {
            return oldItem == newItem
        }
    }
}
