package com.example.quizprototype.ui.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.quizprototype.domain.model.ProfileAvatarId

@Composable
fun ProfileAvatarBubble(
    avatarId: ProfileAvatarId,
    modifier: Modifier = Modifier,
    borderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(containerColor, CircleShape)
            .border(width = 1.5.dp, color = borderColor, shape = CircleShape)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawProfileAvatar(spec = avatarSpecFor(avatarId))
        }
    }
}

@Composable
fun ProfileAvatarPicker(
    selectedAvatarId: ProfileAvatarId,
    enabled: Boolean,
    onAvatarSelected: (ProfileAvatarId) -> Unit,
    showLabels: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ProfileAvatarId.entries.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { avatarId ->
                    val selected = avatarId == selectedAvatarId
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                enabled = enabled,
                                role = Role.RadioButton,
                                onClick = { onAvatarSelected(avatarId) }
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (selected) 2.dp else 1.dp,
                            color = if (selected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.32f)
                            }
                        ),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(if (showLabels) 8.dp else 0.dp)
                        ) {
                            ProfileAvatarBubble(
                                avatarId = avatarId,
                                modifier = Modifier.size(88.dp),
                                borderColor = if (selected) {
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                } else {
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                },
                                containerColor = if (selected) {
                                    MaterialTheme.colorScheme.surface
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                }
                            )
                            if (showLabels) {
                                Text(
                                    text = profileAvatarLabel(avatarId),
                                    style = MaterialTheme.typography.labelLarge,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.widthIn(min = 0.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun profileAvatarLabel(avatarId: ProfileAvatarId): String {
    return when (avatarId) {
        ProfileAvatarId.WOMAN_DOG -> "Woman + dog"
        ProfileAvatarId.WOMAN_CAT -> "Woman + cat"
        ProfileAvatarId.MAN_DOG -> "Man + dog"
        ProfileAvatarId.MAN_CAT -> "Man + cat"
    }
}

private enum class DriverStyle {
    WOMAN,
    MAN
}

private enum class PetStyle {
    DOG,
    CAT
}

private data class AvatarSpec(
    val skyColor: Color,
    val groundColor: Color,
    val carBodyColor: Color,
    val carAccentColor: Color,
    val driverHairColor: Color,
    val driverShirtColor: Color,
    val petPrimaryColor: Color,
    val petAccentColor: Color,
    val driverStyle: DriverStyle,
    val petStyle: PetStyle
)

private fun avatarSpecFor(avatarId: ProfileAvatarId): AvatarSpec {
    return when (avatarId) {
        ProfileAvatarId.WOMAN_DOG -> AvatarSpec(
            skyColor = Color(0xFFD8F0FF),
            groundColor = Color(0xFFCAE6D5),
            carBodyColor = Color(0xFF78B9DA),
            carAccentColor = Color(0xFF5B95B3),
            driverHairColor = Color(0xFF9B634A),
            driverShirtColor = Color(0xFF58A86D),
            petPrimaryColor = Color(0xFFF2DFC0),
            petAccentColor = Color(0xFFD59662),
            driverStyle = DriverStyle.WOMAN,
            petStyle = PetStyle.DOG
        )

        ProfileAvatarId.WOMAN_CAT -> AvatarSpec(
            skyColor = Color(0xFFF5E9FF),
            groundColor = Color(0xFFDDECD8),
            carBodyColor = Color(0xFFA77BD9),
            carAccentColor = Color(0xFF845FB6),
            driverHairColor = Color(0xFF6B4A33),
            driverShirtColor = Color(0xFF4F8AC9),
            petPrimaryColor = Color(0xFFE8C8A4),
            petAccentColor = Color(0xFF9C6A42),
            driverStyle = DriverStyle.WOMAN,
            petStyle = PetStyle.CAT
        )

        ProfileAvatarId.MAN_DOG -> AvatarSpec(
            skyColor = Color(0xFFE6F4FF),
            groundColor = Color(0xFFD5EBD3),
            carBodyColor = Color(0xFF6EA3C8),
            carAccentColor = Color(0xFF4E7FA4),
            driverHairColor = Color(0xFF4A352B),
            driverShirtColor = Color(0xFFCA6E4D),
            petPrimaryColor = Color(0xFFF0D7B8),
            petAccentColor = Color(0xFFD18C5D),
            driverStyle = DriverStyle.MAN,
            petStyle = PetStyle.DOG
        )

        ProfileAvatarId.MAN_CAT -> AvatarSpec(
            skyColor = Color(0xFFFFF0D8),
            groundColor = Color(0xFFDCE8D1),
            carBodyColor = Color(0xFFDF9956),
            carAccentColor = Color(0xFFB8763B),
            driverHairColor = Color(0xFF3B302B),
            driverShirtColor = Color(0xFF5A9A7B),
            petPrimaryColor = Color(0xFFE8D2B8),
            petAccentColor = Color(0xFF8C6946),
            driverStyle = DriverStyle.MAN,
            petStyle = PetStyle.CAT
        )
    }
}

private fun DrawScope.drawProfileAvatar(spec: AvatarSpec) {
    val width = size.width
    val height = size.height

    drawCircle(
        color = spec.skyColor,
        radius = size.minDimension / 2f,
        center = center
    )
    drawCircle(
        color = spec.groundColor,
        radius = width * 0.34f,
        center = Offset(width / 2f, height * 0.84f)
    )

    val windshieldLeft = width * 0.18f
    val windshieldTop = height * 0.16f
    val windshieldWidth = width * 0.64f
    val windshieldHeight = height * 0.34f
    val windshieldGlassInset = width * 0.03f

    drawRoundRect(
        color = spec.carBodyColor.copy(alpha = 0.65f),
        topLeft = Offset(windshieldLeft, windshieldTop),
        size = Size(windshieldWidth, windshieldHeight),
        cornerRadius = CornerRadius(width * 0.11f, width * 0.11f),
        style = Stroke(width = width * 0.028f)
    )
    drawRoundRect(
        color = Color(0x99FFFFFF),
        topLeft = Offset(
            windshieldLeft + windshieldGlassInset,
            windshieldTop + windshieldGlassInset
        ),
        size = Size(
            windshieldWidth - windshieldGlassInset * 2f,
            windshieldHeight - windshieldGlassInset * 2f
        ),
        cornerRadius = CornerRadius(width * 0.09f, width * 0.09f)
    )

    val carBodyTop = height * 0.50f
    val carBodyHeight = height * 0.24f
    val carBodyLeft = width * 0.12f
    val carBodyWidth = width * 0.76f
    drawRoundRect(
        color = spec.carBodyColor,
        topLeft = Offset(carBodyLeft, carBodyTop),
        size = Size(carBodyWidth, carBodyHeight),
        cornerRadius = CornerRadius(width * 0.14f, width * 0.14f)
    )
    drawRoundRect(
        color = spec.carAccentColor,
        topLeft = Offset(width * 0.37f, carBodyTop + carBodyHeight * 0.18f),
        size = Size(width * 0.26f, carBodyHeight * 0.32f),
        cornerRadius = CornerRadius(width * 0.03f, width * 0.03f)
    )

    val grillBarWidth = width * 0.018f
    var barX = width * 0.40f
    repeat(5) {
        drawRoundRect(
            color = spec.carBodyColor.copy(alpha = 0.42f),
            topLeft = Offset(barX, carBodyTop + carBodyHeight * 0.22f),
            size = Size(grillBarWidth, carBodyHeight * 0.24f),
            cornerRadius = CornerRadius(width * 0.01f, width * 0.01f)
        )
        barX += width * 0.045f
    }

    drawHeadlight(left = true, bodyTop = carBodyTop, bodyHeight = carBodyHeight, width = width)
    drawHeadlight(left = false, bodyTop = carBodyTop, bodyHeight = carBodyHeight, width = width)

    drawRoundRect(
        color = Color(0xFF4A3732),
        topLeft = Offset(width * 0.18f, carBodyTop + carBodyHeight * 0.84f),
        size = Size(width * 0.10f, height * 0.18f),
        cornerRadius = CornerRadius(width * 0.03f, width * 0.03f)
    )
    drawRoundRect(
        color = Color(0xFF4A3732),
        topLeft = Offset(width * 0.72f, carBodyTop + carBodyHeight * 0.84f),
        size = Size(width * 0.10f, height * 0.18f),
        cornerRadius = CornerRadius(width * 0.03f, width * 0.03f)
    )

    drawPet(spec = spec, width = width, height = height)
    drawDriver(spec = spec, width = width, height = height)

    drawCircle(
        color = spec.carAccentColor.copy(alpha = 0.8f),
        radius = width * 0.06f,
        center = Offset(width * 0.60f, height * 0.57f),
        style = Stroke(width = width * 0.018f)
    )
    drawLine(
        color = spec.carAccentColor.copy(alpha = 0.85f),
        start = Offset(width * 0.63f, height * 0.49f),
        end = Offset(width * 0.60f, height * 0.57f),
        strokeWidth = width * 0.018f
    )
}

private fun DrawScope.drawHeadlight(
    left: Boolean,
    bodyTop: Float,
    bodyHeight: Float,
    width: Float
) {
    val path = Path().apply {
        if (left) {
            moveTo(width * 0.16f, bodyTop + bodyHeight * 0.46f)
            quadraticTo(width * 0.20f, bodyTop + bodyHeight * 0.24f, width * 0.31f, bodyTop + bodyHeight * 0.42f)
            quadraticTo(width * 0.27f, bodyTop + bodyHeight * 0.58f, width * 0.18f, bodyTop + bodyHeight * 0.60f)
        } else {
            moveTo(width * 0.84f, bodyTop + bodyHeight * 0.46f)
            quadraticTo(width * 0.80f, bodyTop + bodyHeight * 0.24f, width * 0.69f, bodyTop + bodyHeight * 0.42f)
            quadraticTo(width * 0.73f, bodyTop + bodyHeight * 0.58f, width * 0.82f, bodyTop + bodyHeight * 0.60f)
        }
        close()
    }
    drawPath(path = path, color = Color(0xFFFFF4E7))
}

private fun DrawScope.drawDriver(spec: AvatarSpec, width: Float, height: Float) {
    val skinColor = Color(0xFFF6C8A6)
    val headCenter = Offset(width * 0.64f, height * 0.38f)
    val headRadius = width * 0.08f

    drawCircle(color = skinColor, radius = headRadius, center = headCenter)

    if (spec.driverStyle == DriverStyle.WOMAN) {
        val hairPath = Path().apply {
            moveTo(headCenter.x - headRadius * 1.15f, headCenter.y - headRadius * 0.35f)
            quadraticTo(headCenter.x, headCenter.y - headRadius * 1.55f, headCenter.x + headRadius * 1.1f, headCenter.y - headRadius * 0.2f)
            lineTo(headCenter.x + headRadius * 0.95f, headCenter.y + headRadius * 1.5f)
            quadraticTo(headCenter.x, headCenter.y + headRadius * 1.05f, headCenter.x - headRadius * 0.95f, headCenter.y + headRadius * 1.45f)
            close()
        }
        drawPath(path = hairPath, color = spec.driverHairColor)
        drawCircle(color = skinColor, radius = headRadius * 0.86f, center = headCenter)
    } else {
        val hairPath = Path().apply {
            moveTo(headCenter.x - headRadius, headCenter.y - headRadius * 0.1f)
            quadraticTo(headCenter.x, headCenter.y - headRadius * 1.2f, headCenter.x + headRadius, headCenter.y - headRadius * 0.05f)
            lineTo(headCenter.x + headRadius * 0.82f, headCenter.y - headRadius * 0.52f)
            quadraticTo(headCenter.x, headCenter.y - headRadius * 0.96f, headCenter.x - headRadius * 0.82f, headCenter.y - headRadius * 0.52f)
            close()
        }
        drawPath(path = hairPath, color = spec.driverHairColor)
    }

    drawCircle(color = Color(0xFF3A2B25), radius = width * 0.008f, center = Offset(headCenter.x - width * 0.022f, headCenter.y - width * 0.01f))
    drawCircle(color = Color(0xFF3A2B25), radius = width * 0.008f, center = Offset(headCenter.x + width * 0.022f, headCenter.y - width * 0.01f))

    val smilePath = Path().apply {
        moveTo(headCenter.x - width * 0.022f, headCenter.y + width * 0.022f)
        quadraticTo(headCenter.x, headCenter.y + width * 0.04f, headCenter.x + width * 0.022f, headCenter.y + width * 0.022f)
    }
    drawPath(path = smilePath, color = Color(0xFF8D5442), style = Stroke(width = width * 0.006f))

    drawRoundRect(
        color = spec.driverShirtColor,
        topLeft = Offset(width * 0.55f, height * 0.47f),
        size = Size(width * 0.19f, height * 0.16f),
        cornerRadius = CornerRadius(width * 0.05f, width * 0.05f)
    )
    drawLine(
        color = Color(0xFF8D5442),
        start = Offset(width * 0.71f, height * 0.44f),
        end = Offset(width * 0.79f, height * 0.61f),
        strokeWidth = width * 0.02f
    )
}

private fun DrawScope.drawPet(spec: AvatarSpec, width: Float, height: Float) {
    val headCenter = Offset(width * 0.37f, height * 0.39f)
    val headRadius = width * 0.075f
    val earColor = spec.petAccentColor

    if (spec.petStyle == PetStyle.DOG) {
        drawRoundRect(
            color = earColor,
            topLeft = Offset(headCenter.x - headRadius * 1.45f, headCenter.y - headRadius * 0.05f),
            size = Size(headRadius * 0.6f, headRadius * 1.45f),
            cornerRadius = CornerRadius(width * 0.03f, width * 0.03f)
        )
        drawRoundRect(
            color = earColor,
            topLeft = Offset(headCenter.x + headRadius * 0.85f, headCenter.y - headRadius * 0.05f),
            size = Size(headRadius * 0.6f, headRadius * 1.45f),
            cornerRadius = CornerRadius(width * 0.03f, width * 0.03f)
        )
    } else {
        val leftEar = Path().apply {
            moveTo(headCenter.x - headRadius * 0.95f, headCenter.y - headRadius * 0.4f)
            lineTo(headCenter.x - headRadius * 0.35f, headCenter.y - headRadius * 1.35f)
            lineTo(headCenter.x - headRadius * 0.05f, headCenter.y - headRadius * 0.3f)
            close()
        }
        val rightEar = Path().apply {
            moveTo(headCenter.x + headRadius * 0.95f, headCenter.y - headRadius * 0.4f)
            lineTo(headCenter.x + headRadius * 0.35f, headCenter.y - headRadius * 1.35f)
            lineTo(headCenter.x + headRadius * 0.05f, headCenter.y - headRadius * 0.3f)
            close()
        }
        drawPath(path = leftEar, color = earColor)
        drawPath(path = rightEar, color = earColor)
    }

    drawCircle(color = spec.petPrimaryColor, radius = headRadius, center = headCenter)
    drawRoundRect(
        color = spec.petPrimaryColor,
        topLeft = Offset(width * 0.30f, height * 0.47f),
        size = Size(width * 0.14f, height * 0.14f),
        cornerRadius = CornerRadius(width * 0.05f, width * 0.05f)
    )

    drawCircle(color = Color(0xFF4A382E), radius = width * 0.008f, center = Offset(headCenter.x - width * 0.02f, headCenter.y - width * 0.008f))
    drawCircle(color = Color(0xFF4A382E), radius = width * 0.008f, center = Offset(headCenter.x + width * 0.02f, headCenter.y - width * 0.008f))
    drawCircle(color = Color(0xFF4A382E), radius = width * 0.009f, center = Offset(headCenter.x, headCenter.y + width * 0.012f))
}
