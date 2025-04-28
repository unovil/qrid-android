package com.unovil.tardyscan.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.unovil.tardyscan.R
import com.unovil.tardyscan.ui.theme.TardyScannerTheme

@Composable
fun PasswordTextField(value: String, onValueChange: (String) -> Unit) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.password)) },
        placeholder = { Text(stringResource(R.string.password)) },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, contentDescription =
                    if (passwordVisible) "Hide password" else "Show password"
                )
            }
        }
    )
}

// by Ken Ruiz Inoue <3
// https://medium.com/deuk/android-compose-tutorial-password-form-daa1d51a80b7
@Composable
fun PasswordStrengthIndicator(
    currentStrength: Int,  // 1 (no reqs) to 6 (all reqs met)
    totalIndicators: Int = 5,
    inactiveAlpha: Float = 0.3f,
    barHeight: Dp = 8.dp,
    spacing: Dp = 4.dp,
    cornerRadius: Dp = 2.dp,
) {
    val baseColors: List<Color> = listOf(
        Color(0xFFC62828),  // Red (strength 2)
        Color(0xFFF57C00),  // Orange (strength 3)
        Color(0xFFFBC02D),  // Yellow (strength 4)
        Color(0xFF689F38),  // Green (strength 5)
        Color(0xFF0288D1)   // Blue (strength 6)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        repeat(totalIndicators) { index ->
            val (activeColor, isActive) = when (currentStrength) {
                1 -> Pair(baseColors.first().copy(alpha = inactiveAlpha), false) // All dark red
                in 2..6 -> {
                    val colorIndex = (currentStrength - 2).coerceAtMost(baseColors.lastIndex)
                    val shouldBeActive = index < currentStrength - 1
                    Pair(baseColors[colorIndex], shouldBeActive)
                }
                else -> Pair(Color.Gray, false) // Fallback
            }

            val color = if (isActive) activeColor else activeColor.copy(alpha = inactiveAlpha)

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(barHeight)
                    .background(color, RoundedCornerShape(cornerRadius))
            )
        }
    }
}

@Composable
fun PasswordValidationFeedbackItem(
    message: String,
    isRuleMet: Boolean,
    picOnSuccess: Pair<ImageVector, Color> = Pair(
        Icons.Outlined.CheckCircle,
        TardyScannerTheme.extendedColors.onMatchedRequirement
    ),
    picOnFailure: Pair<ImageVector, Color> = Pair(
        Icons.Outlined.RemoveCircleOutline,
        TardyScannerTheme.extendedColors.onMissedRequirement
    )
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isRuleMet) picOnSuccess.first else picOnFailure.first,
            contentDescription = if (isRuleMet) "Success" else "Failure",
            tint = if (isRuleMet) picOnSuccess.second else picOnFailure.second,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = message,
            color = if (isRuleMet) picOnSuccess.second else picOnFailure.second,
            style = MaterialTheme.typography.bodySmall
        )
    }
}


@Composable
fun AuthorizeButton(
    enabled: Boolean,
    buttonText: String,
    onClick: () -> Unit
) {
    Button(
        enabled = enabled,
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(buttonText)
        Spacer(modifier = Modifier.width(5.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = buttonText
        )
    }
}