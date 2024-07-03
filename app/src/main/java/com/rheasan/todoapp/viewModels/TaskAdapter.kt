package com.rheasan.todoapp.viewModels
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rheasan.todoapp.R
import com.rheasan.todoapp.models.Task
import com.rheasan.todoapp.repositories.TaskRepository
import com.rheasan.todoapp.repositories.TaskRepositoryInstance
import com.rheasan.todoapp.views.TaskViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskAdapter : RecyclerView.Adapter<TaskViewHolder>() {
    private var repository : TaskRepository
    private var tasks: MutableList<Task>
    init {
        repository = TaskRepositoryInstance.getInstance()
        tasks = repository.getAllTasks().toMutableList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        repository = TaskRepositoryInstance.getInstance()
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
                CoroutineScope(Dispatchers.IO).launch {
                    val newTitle = holder.taskTitle.text.toString()
                    repository.updateTaskTitle(currentTask.id, newTitle)
                }
            }
        }
        holder.taskDeleteButton.setOnClickListener {
            removeTaskAt(position)
            CoroutineScope(Dispatchers.IO).launch {
                repository.deleteTask(currentTask.id)
            }
        }
    }

    private fun removeTaskAt(position: Int) {
        tasks.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, tasks.size)
    }

    fun addTask(newTask: Task) {
        tasks.add(newTask)
        notifyItemInserted(tasks.size - 1)
    }

    fun updateTasks(newTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }
}

