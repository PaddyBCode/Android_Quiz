package com.example.quizprototype

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.quizprototype.ui.app.DriverTheoryApp
import com.example.quizprototype.ui.theme.QuizPrototypeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuizPrototypeTheme {
                DriverTheoryApp(
                    appContainer = (application as QuizApplication).appContainer
                )
            }
        }
    }
}
