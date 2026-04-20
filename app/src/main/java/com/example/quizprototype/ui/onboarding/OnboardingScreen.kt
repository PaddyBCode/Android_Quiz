package com.example.quizprototype.ui.onboarding

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.verticalScroll
import com.example.quizprototype.domain.model.ProfileAvatarId
import com.example.quizprototype.ui.profile.ProfileAvatarPicker
import com.example.quizprototype.ui.theme.LaneWhite
import com.example.quizprototype.ui.theme.RoadGreen
import com.example.quizprototype.ui.theme.TricolourOrange

@Composable
fun OnboardingScreen(
    uiState: OnboardingUiState,
    onAvatarSelected: (ProfileAvatarId) -> Unit,
    onUsernameChanged: (String) -> Unit,
    onCreateProfile: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            OnboardingHero()

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Welcome to your personal driver's theory trainer",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Build your practice routine, track your scores, and jump back into study sessions whenever you need them.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Setup your studyroom",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Choose a profile picture and the username you want to see on the dashboard and progress pages.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    ProfileAvatarPicker(
                        selectedAvatarId = uiState.avatarId,
                        enabled = !uiState.isSaving,
                        onAvatarSelected = onAvatarSelected,
                        showLabels = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = uiState.username,
                        onValueChange = onUsernameChanged,
                        label = { Text("Username") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Button(
                        onClick = onCreateProfile,
                        enabled = !uiState.isSaving,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (uiState.isSaving) "Creating account..." else "Create account")
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "How it works",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Practice by topic, run mock exams, bookmark tricky questions, and keep all of your progress saved on your phone.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingHero() {
    val infiniteTransition = rememberInfiniteTransition(label = "onboardingHero")
    val carProgress = infiniteTransition.animateFloat(
        initialValue = -0.15f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "carProgress"
    )
    val lightPulse = infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lightPulse"
    )

    Card(
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = LaneWhite)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val roadTop = height * 0.58f
                val roadHeight = height * 0.26f

                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFEAF6FF), Color(0xFFCFE8F6), Color(0xFF065535))
                    ),
                    size = size,
                    cornerRadius = CornerRadius(30f, 30f)
                )

                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(RoadGreen, LaneWhite, TricolourOrange)
                    ),
                    topLeft = Offset(0f, 0f),
                    size = Size(width, 16f)
                )

                val farHill = Path().apply {
                    moveTo(0f, height * 0.52f)
                    quadraticTo(width * 0.2f, height * 0.34f, width * 0.42f, height * 0.46f)
                    quadraticTo(width * 0.62f, height * 0.6f, width, height * 0.4f)
                    lineTo(width, roadTop)
                    lineTo(0f, roadTop)
                    close()
                }
                drawPath(
                    path = farHill,
                    color = Color(0xFF6E9661)
                )

                val nearHill = Path().apply {
                    moveTo(0f, height * 0.58f)
                    quadraticTo(width * 0.28f, height * 0.42f, width * 0.54f, height * 0.57f)
                    quadraticTo(width * 0.74f, height * 0.68f, width, height * 0.52f)
                    lineTo(width, roadTop + 10f)
                    lineTo(0f, roadTop + 10f)
                    close()
                }
                drawPath(
                    path = nearHill,
                    color = Color(0xFF065535)
                )

                drawCircle(
                    color = Color(0x33FFD54F),
                    radius = 62f * lightPulse.value,
                    center = Offset(width * 0.9f, height * 0.14f)
                )
                drawCircle(
                    color = Color(0xFFF6C453),
                    radius = 24f,
                    center = Offset(width * 0.9f, height * 0.14f)
                )

                drawCircle(
                    color = Color(0x26FFFFFF),
                    radius = 18f,
                    center = Offset(width * 0.17f, height * 0.22f)
                )
                drawCircle(
                    color = Color(0x33FFFFFF),
                    radius = 22f,
                    center = Offset(width * 0.22f, height * 0.2f)
                )
                drawCircle(
                    color = Color(0x2AFFFFFF),
                    radius = 16f,
                    center = Offset(width * 0.27f, height * 0.22f)
                )

                drawRoundRect(
                    color = Color(0xFF2B3A40),
                    topLeft = Offset(0f, roadTop),
                    size = Size(width, roadHeight),
                    cornerRadius = CornerRadius(18f, 18f)
                )

                val dashWidth = width * 0.14f
                val dashGap = width * 0.06f
                val dashY = roadTop + (roadHeight / 2f) - 5f
                var dashX = width * 0.08f
                while (dashX < width) {
                    drawRoundRect(
                        color = Color(0xFFFFF3C4),
                        topLeft = Offset(dashX, dashY),
                        size = Size(dashWidth, 10f),
                        cornerRadius = CornerRadius(6f, 6f)
                    )
                    dashX += dashWidth + dashGap
                }

                val poleX = width * 0.8f
                val poleTop = height * 0.20f
                val poleHeight = height * 0.36f
                drawRoundRect(
                    color = Color(0xFF455A64),
                    topLeft = Offset(poleX, poleTop),
                    size = Size(12f, poleHeight),
                    cornerRadius = CornerRadius(8f, 8f)
                )
                drawRoundRect(
                    color = Color(0xFF263238),
                    topLeft = Offset(poleX - 18f, poleTop - 6f),
                    size = Size(48f, 78f),
                    cornerRadius = CornerRadius(20f, 20f)
                )
                drawCircle(
                    color = Color(0xFF5F666A),
                    radius = 12f,
                    center = Offset(poleX + 6f, poleTop + 20f)
                )
                drawCircle(
                    color = Color(0xFF6B7074),
                    radius = 12f,
                    center = Offset(poleX + 6f, poleTop + 46f)
                )
                drawCircle(
                    color = Color(0x5500C853),
                    radius = 24f * lightPulse.value,
                    center = Offset(poleX + 6f, poleTop + 72f)
                )
                drawCircle(
                    color = Color(0xFF00C853),
                    radius = 12f,
                    center = Offset(poleX + 6f, poleTop + 72f)
                )

                val carX = width * carProgress.value
                val carBaseY = roadTop - 24f
                val carBodyWidth = 108f
                val carBodyHeight = 30f

                drawRoundRect(
                    color = Color(0xFF146356),
                    topLeft = Offset(carX, carBaseY),
                    size = Size(carBodyWidth, carBodyHeight),
                    cornerRadius = CornerRadius(16f, 16f)
                )
                drawRoundRect(
                    color = Color(0xFF1F7A68),
                    topLeft = Offset(carX + 20f, carBaseY - 18f),
                    size = Size(52f, 24f),
                    cornerRadius = CornerRadius(14f, 14f)
                )
                drawRoundRect(
                    color = Color(0xFFBFE4F8),
                    topLeft = Offset(carX + 26f, carBaseY - 14f),
                    size = Size(18f, 14f),
                    cornerRadius = CornerRadius(6f, 6f)
                )
                drawRoundRect(
                    color = Color(0xFFBFE4F8),
                    topLeft = Offset(carX + 48f, carBaseY - 14f),
                    size = Size(18f, 14f),
                    cornerRadius = CornerRadius(6f, 6f)
                )
                drawCircle(
                    color = Color(0xFF1C1F22),
                    radius = 13f,
                    center = Offset(carX + 24f, carBaseY + carBodyHeight + 2f)
                )
                drawCircle(
                    color = Color(0xFF1C1F22),
                    radius = 13f,
                    center = Offset(carX + 84f, carBaseY + carBodyHeight + 2f)
                )
                drawCircle(
                    color = Color(0xFF9EA7AD),
                    radius = 5f,
                    center = Offset(carX + 24f, carBaseY + carBodyHeight + 2f)
                )
                drawCircle(
                    color = Color(0xFF9EA7AD),
                    radius = 5f,
                    center = Offset(carX + 84f, carBaseY + carBodyHeight + 2f)
                )

                drawRoundRect(
                    color = Color(0x22FFFFFF),
                    topLeft = Offset(18f, 18f),
                    size = Size(width - 36f, height - 36f),
                    cornerRadius = CornerRadius(28f, 28f),
                    style = Stroke(width = 3f)
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Welcome",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF123B33)
                )
                Text(
                    text = "Your personal study space",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF23453D)
                )
            }
        }
    }
}
