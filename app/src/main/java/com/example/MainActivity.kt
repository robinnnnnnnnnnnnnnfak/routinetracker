package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.data.AppDatabase
import com.example.data.RoutineRepository
import com.example.ui.RoutineApp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.RoutineViewModel
import com.example.ui.viewmodel.RoutineViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: RoutineViewModel by viewModels {
        val database = AppDatabase.getDatabase(this)
        val repository = RoutineRepository(database.taskDao(), database.taskCompletionDao())
        RoutineViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RoutineApp(viewModel = viewModel)
                }
            }
        }
    }
}
