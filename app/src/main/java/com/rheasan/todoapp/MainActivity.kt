package com.rheasan.todoapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rheasan.todoapp.viewModels.MainActivityViewModel
import com.rheasan.todoapp.adapters.TaskAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var headerTextView: TextView
    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var addTaskButton : Button
    private lateinit var taskEditText: EditText

    private lateinit var viewModel : MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        viewModel = MainActivityViewModel(this)

        headerTextView = findViewById(R.id.headerTextView)
        taskRecyclerView = findViewById(R.id.taskRecyclerView)
        addTaskButton = findViewById(R.id.addTaskButton)
        taskEditText = findViewById(R.id.taskTitleEditText)
        headerTextView.text = getString(R.string.loading_text)


        // update the header text
        CoroutineScope(Dispatchers.IO).launch {
            val name = viewModel.devName()
            headerTextView.text = getString(R.string.todo_header, name)
        }

        // setup recyclerView
        val layoutManager = LinearLayoutManager(this)
        taskRecyclerView.layoutManager = layoutManager
        val adapter = TaskAdapter()
        taskRecyclerView.adapter = adapter

        // add new tasks
        addTaskButton.setOnClickListener {
            val taskTitle = taskEditText.text.toString()
            taskEditText.setText(R.string.empty_string)
            viewModel.createTask(taskTitle)?.let {
                adapter.addTask(it)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}