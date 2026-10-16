package com.example.bookproject1.book_feature.presentation.settings.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookproject1.app.ui.theme.UiFontFamily
import com.example.bookproject1.book_feature.presentation.component.ui.BottomBarGold

@Composable
fun ThemeSelectorSection(
    isDarkTheme: Boolean,
    onThemeChangeWithOffset: (isDark: Boolean, tapOffset: Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    val sunRotation by animateFloatAsState(
        targetValue = if (!isDarkTheme) 90f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "SunRotation"
    )
    val sunScale by animateFloatAsState(
        targetValue = if (!isDarkTheme) 1.15f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "SunScale"
    )

    val moonRotation by animateFloatAsState(
        targetValue = if (isDarkTheme) -20f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "MoonRotation"
    )
    val moonScale by animateFloatAsState(
        targetValue = if (isDarkTheme) 1.15f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "MoonScale"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "المظهر",
            fontFamily = UiFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ThemeOptionCard(
                title = "المظهر الفاتح",
                isSelected = !isDarkTheme,
                isSun = true,
                iconRotation = sunRotation,
                iconScale = sunScale,
                onClickWithOffset = { offset ->
                    onThemeChangeWithOffset(false, offset)
                },
                modifier = Modifier.weight(1f)
            )

            ThemeOptionCard(
                title = "المظهر الداكن",
                isSelected = isDarkTheme,
                isSun = false,
                iconRotation = moonRotation,
                iconScale = moonScale,
                onClickWithOffset = { offset ->
                    onThemeChangeWithOffset(true, offset)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ThemeOptionCard(
    title: String,
    isSelected: Boolean,
    isSun: Boolean,
    modifier: Modifier = Modifier,
    iconRotation: Float = 0f,
    iconScale: Float = 1f,
    onClickWithOffset: (Offset) -> Unit = {}
) {
    var cardCenterInWindow by remember { mutableStateOf(Offset.Zero) }

    val icon: ImageVector = if (isSun) Icons.Default.WbSunny else Icons.Default.NightlightRound
    val iconColor: Color = if (isSelected) {
        if (isSun) Color(0xFFF57C00) else Color(0xFFE0E0E0)
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    }

    val cardBg = if (isSelected) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.surface
    }

    val borderColor = if (isSelected) {
        BottomBarGold
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = cardBg,
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = borderColor
        ),
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                val pos = coordinates.positionInWindow()
                val size = coordinates.size
                cardCenterInWindow = Offset(
                    x = pos.x + size.width / 2f,
                    y = pos.y + size.height / 2f
                )
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = BottomBarGold),
                onClick = { onClickWithOffset(cardCenterInWindow) }
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp, horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier
                        .size(36.dp)
                        .graphicsLayer {
                            rotationZ = iconRotation
                            scaleX = iconScale
                            scaleY = iconScale
                        }
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = title,
                    fontFamily = UiFontFamily,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 15.sp,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    }
                )
            }
        }
    }
}