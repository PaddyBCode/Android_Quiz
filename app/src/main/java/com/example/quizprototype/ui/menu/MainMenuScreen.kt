package com.example.quizprototype.ui.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.quizprototype.R

@Composable
fun MainMenuScreen(
    onQuickQuizClick: () -> Unit,
    onCategoryQuizClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Driver Theory Quiz",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Choose an option to get started.",
                style = MaterialTheme.typography.bodyMedium
            )

            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Main menu illustration",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentScale = ContentScale.Fit
            )

            Button(
                onClick = onQuickQuizClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Quick Quiz")
            }

            OutlinedButton(
                onClick = onCategoryQuizClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Quiz by Category")
            }

            OutlinedButton(
                onClick = onProfileClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Profile")
            }
        }
    }
}
