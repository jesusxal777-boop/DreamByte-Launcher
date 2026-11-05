package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dreambyte.data.LauncherRepository
import com.example.dreambyte.ui.screens.HomeScreen
import com.example.dreambyte.ui.theme.DreamByteThemeProvider
import com.example.dreambyte.viewmodel.LauncherViewModel

class MainActivity : ComponentActivity() {

  private lateinit var repository: LauncherRepository
  private lateinit var launcherViewModel: LauncherViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    repository = LauncherRepository(applicationContext)
    launcherViewModel = ViewModelProvider(
      this,
      object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          return LauncherViewModel(repository) as T
        }
      }
    )[LauncherViewModel::class.java]

    setContent {
      val settings by launcherViewModel.settings.collectAsState()

      // The home launcher stays on screen; back button dismisses any open overlay/sheet
      BackHandler(enabled = true) {
        if (launcherViewModel.isWelcomeOpen.value) {
          launcherViewModel.closeWelcome()
        } else if (launcherViewModel.isHudOpen.value) {
          launcherViewModel.closeHud()
        } else if (launcherViewModel.isWidgetPickerOpen.value) {
          launcherViewModel.closeWidgetPicker()
        } else if (launcherViewModel.isSettingsOpen.value) {
          launcherViewModel.closeSettings()
        } else if (launcherViewModel.isDrawerOpen.value) {
          launcherViewModel.closeDrawer()
        } else if (launcherViewModel.selectedFolder.value != null) {
          launcherViewModel.closeFolder()
        } else if (launcherViewModel.selectedAppContext.value != null) {
          launcherViewModel.closeAppContext()
        } else if (launcherViewModel.isEditMode.value) {
          launcherViewModel.exitEditMode()
        }
      }

      DreamByteThemeProvider(appearance = settings.appearance) {
        Surface(modifier = Modifier.fillMaxSize()) {
          HomeScreen(
            viewModel = launcherViewModel,
            activity = this@MainActivity
          )
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    if (::launcherViewModel.isInitialized) {
      launcherViewModel.refreshData()
      launcherViewModel.checkDefaultLauncherStatus(this)
    }
  }
}
