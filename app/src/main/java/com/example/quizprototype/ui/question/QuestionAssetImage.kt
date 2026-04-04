package com.example.quizprototype.ui.question

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun QuestionAssetImage(
    assetName: String?,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    if (assetName == null) return

    val context = LocalContext.current
    val drawableResId = remember(assetName, context.packageName) {
        assetName
            .takeUnless { it.contains("/") || it.contains(".") }
            ?.let { resourceName ->
                context.resources.getIdentifier(resourceName, "drawable", context.packageName)
            }
            ?.takeIf { it != 0 }
    }
    val imageBitmap = remember(assetName, drawableResId) {
        if (drawableResId != null) {
            null
        } else {
            runCatching {
                context.assets.open(assetName).use { stream ->
                    BitmapFactory.decodeStream(stream)?.asImageBitmap()
                }
            }.getOrNull()
        }
    }

    when {
        drawableResId != null -> {
            Image(
                painter = painterResource(id = drawableResId),
                contentDescription = contentDescription,
                modifier = modifier
                    .fillMaxWidth()
                    .heightIn(min = 140.dp, max = 220.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Fit
            )
        }

        imageBitmap != null -> {
            Image(
                bitmap = imageBitmap,
                contentDescription = contentDescription,
                modifier = modifier
                    .fillMaxWidth()
                    .heightIn(min = 140.dp, max = 220.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Fit
            )
        }

        else -> {
            Card(modifier = modifier) {
                Text(
                    text = "Question image unavailable",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
