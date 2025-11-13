package com.divinebudget.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.divinebudget.app.ejgpoer.presentation.app.DivineBudgetApplication
import com.divinebudget.app.navigation.Screen
import com.divinebudget.app.ui.screens.MainScreen
import com.divinebudget.app.ui.screens.WelcomeScreen
import com.divinebudget.app.ui.theme.DivineBudgetTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val application = application as DivineBudgetApplication
        
        setContent {
            val isFirstLaunch by application.userPreferences.isFirstLaunch
                .collectAsState(initial = true)
            val theme by application.userPreferences.theme
                .collectAsState(initial = "light")
            
            val coroutineScope = rememberCoroutineScope()
            
            DivineBudgetTheme(darkTheme = theme == "dark") {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    NavHost(
                        navController = navController,
                        startDestination = if (isFirstLaunch) Screen.Welcome.route else "main"
                    ) {
                        composable(Screen.Welcome.route) {
                            WelcomeScreen(
                                onContinue = {
                                    coroutineScope.launch {
                                        application.userPreferences.setFirstLaunchCompleted()
                                    }
                                    navController.navigate("main") {
                                        popUpTo(Screen.Welcome.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                        
                        composable("main") {
                            MainScreen(application = application)
                        }
                    }
                }
            }
        }
    }
}

