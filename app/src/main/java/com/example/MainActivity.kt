package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.TaskRepository
import com.example.ui.MainScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.MainViewModelFactory
import com.example.viewmodel.ThemeSelection

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Initialize Database & Repository
    val database = AppDatabase.getDatabase(applicationContext)
    val repository = TaskRepository(database.taskDao())

    setContent {
      val viewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(application, repository)
      )

      val themeSelection by viewModel.darkThemeMode.collectAsState()
      val darkTheme = when (themeSelection) {
        ThemeSelection.SYSTEM -> isSystemInDarkTheme()
        ThemeSelection.LIGHT -> false
        ThemeSelection.DARK -> true
      }

      MyApplicationTheme(darkTheme = darkTheme) {
        MainScreen(viewModel = viewModel)
      }
    }
  }
}
