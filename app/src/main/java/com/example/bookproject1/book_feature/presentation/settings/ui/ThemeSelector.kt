package com.example.bookproject1.book_feature.presentation.settings.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookproject1.R
import com.example.bookproject1.app.ui.theme.UiFontFamily

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
            text = stringResource(R.string.theme_section_title),
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
                title = stringResource(R.string.theme_light),
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
                title = stringResource(R.string.theme_dark),
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

