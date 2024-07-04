package com.rheasan.todoapp.adapters

import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rheasan.todoapp.R
import com.rheasan.todoapp.models.Task
import com.rheasan.todoapp.viewModels.TaskAdapterViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskAdapter() : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTitle = itemView.findViewById<TextView>(R.id.taskCardTitle)
        val taskEditButton = itemView.findViewById<Button>(R.id.taskCardEditButton)
        val taskDeleteButton = itemView.findViewById<Button>(R.id.taskCardDeleteButton)
    }
    private val differCallback = object : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    private var viewModel: TaskAdapterViewModel = TaskAdapterViewModel()
    // have the list empty initially and then load tasks into it
    private var tasks: MutableList<Task> = mutableListOf()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            tasks = viewModel.getAllTasks()
            withContext(Dispatchers.Main) {
                notifyItemRangeChanged(0, tasks.size)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val viewLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_card_layout, parent, false)
        return TaskViewHolder(viewLayout)
    }

    override fun getItemCount(): Int {
        return tasks.count()
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = tasks[position]
        holder.taskTitle.text = currentTask.taskTitle
        holder.taskEditButton.setOnClickListener {
            if(holder.taskTitle.inputType == InputType.TYPE_NULL) {
                // enable the textEdit
                holder.taskTitle.inputType = InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
                holder.taskTitle.isFocusableInTouchMode = true
                holder.taskEditButton.text = "Submit"
            }
            else {
                // disable
                holder.taskTitle.inputType = InputType.TYPE_NULL
                holder.taskEditButton.text = "Edit"
                val newTitle = holder.taskTitle.text.toString()
                viewModel.updateTitle(currentTask.id, newTitle)
            }
        }
        holder.taskDeleteButton.setOnClickListener {
            tasks.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, tasks.size)
            viewModel.deleteTask(currentTask.id)
        }
    }
    fun addTask(newTask: Task) {
        tasks.add(newTask)
        notifyItemInserted(tasks.size - 1)
    }
}