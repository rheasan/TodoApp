package com.rheasan.todoapp.views

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rheasan.todoapp.R

class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val taskTitle = itemView.findViewById<TextView>(R.id.taskCardTitle)
    val taskEditButton = itemView.findViewById<Button>(R.id.taskCardEditButton)
    val taskDeleteButton = itemView.findViewById<Button>(R.id.taskCardDeleteButton)
}