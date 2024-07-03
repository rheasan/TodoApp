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
import com.google.firebase.FirebaseApp
import com.rheasan.todoapp.models.FirebaseHelper
import com.rheasan.todoapp.models.RoomDBProxy
import com.rheasan.todoapp.models.SaveLocationType
import com.rheasan.todoapp.models.Task
import com.rheasan.todoapp.models.updateDevName
import com.rheasan.todoapp.repositories.TaskRepository
import com.rheasan.todoapp.repositories.TaskRepositoryInstance
import com.rheasan.todoapp.viewModels.TaskAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private lateinit var headerTextView: TextView
    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var addTaskButton : Button
    private lateinit var taskEditText: EditText

    private lateinit var repository: TaskRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // initialize firebase
        FirebaseApp.initializeApp(this)

        headerTextView = findViewById(R.id.headerTextView)
        taskRecyclerView = findViewById(R.id.taskRecyclerView)
        addTaskButton = findViewById(R.id.addTaskButton)
        taskEditText = findViewById(R.id.taskTitleEditText)
        headerTextView.text = getString(R.string.loading_text)

        // setup task repository
        repository = TaskRepositoryInstance.getInstance()
        repository.setDefaultReadLocation(SaveLocationType.firebase)
        repository.addSaveLocation(RoomDBProxy(this))
        repository.addSaveLocation(FirebaseHelper())

        // update the header text
        updateDevName(headerTextView, this)

        // add tasks to the recycler view
        val layoutManager = LinearLayoutManager(this)
        taskRecyclerView.layoutManager = layoutManager
        val adapter = TaskAdapter()
        taskRecyclerView.adapter = adapter

        // add new tasks

        addTaskButton.setOnClickListener {
            val taskTitle = taskEditText.text.toString()
            if (taskTitle.isNotEmpty()) {
                val newTask = Task(
                    id = UUID.randomUUID(),
                    createdAt = Date(),
                    updatedAt = Date(),
                    deletedAt = null,
                    taskTitle = taskTitle
                )
                CoroutineScope(Dispatchers.IO).launch {
                    repository.addTask(newTask)
                    withContext(Dispatchers.Main) {
                        taskEditText.setText(getString(R.string.empty_string))
                        adapter.addTask(newTask)
                    }
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}