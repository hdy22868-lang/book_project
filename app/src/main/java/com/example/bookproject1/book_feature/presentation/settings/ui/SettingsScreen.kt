package com.example.bookproject1.book_feature.presentation.settings.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookproject1.app.ui.theme.BrownBackgroundDark
import com.example.bookproject1.app.ui.theme.CreamBackgroundLight
import com.example.bookproject1.app.ui.theme.UiFontFamily
import com.example.bookproject1.book_feature.presentation.component.ui.BottomBar
import com.example.bookproject1.book_feature.presentation.component.ui.BottomBarGold
import com.example.bookproject1.book_feature.presentation.component.ui.BottomTab
import com.example.bookproject1.book_feature.presentation.settings.logic.SettingsViewModel
import kotlinx.coroutines.launch
import kotlin.math.hypot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateHome: () -> Unit,
    onNavigateSearch: () -> Unit,
    modifier: Modifier = Modifier,
    onHighlightClick: (highlightId: Int) -> Unit = {}
) {
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val highlights by viewModel.highlights.collectAsState()
    var showFavoritesSheet by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    var isRippling by remember { mutableStateOf(false) }
    var rippleCenter by remember { mutableStateOf(Offset.Zero) }
    var rippleColor by remember { mutableStateOf(Color.Transparent) }
    val rippleProgress = remember { Animatable(0f) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(modifier = modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = "الإعدادات",
                                fontFamily = UiFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                },
                bottomBar = {
                    BottomBar(
                        currentTab = BottomTab.SETTINGS,
                        onTabSelected = { tab ->
                            when (tab) {
                                BottomTab.HOME -> onNavigateHome()
                                BottomTab.SEARCH -> onNavigateSearch()
                                BottomTab.SETTINGS -> {}
                            }
                        }
                    )
                }
            ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        // 1. قسم اختيار المظهر
                        ThemeSelectorSection(
                            isDarkTheme = isDarkTheme,
                            onThemeChangeWithOffset = { newIsDark, offset ->
                                if (newIsDark != isDarkTheme) {
                                    rippleCenter = offset
                                    rippleColor = if (newIsDark) BrownBackgroundDark else CreamBackgroundLight
                                    viewModel.onThemeChange(newIsDark)
                                    coroutineScope.launch {
                                        rippleProgress.snapTo(0f)
                                        isRippling = true
                                        rippleProgress.animateTo(
                                            targetValue = 1f,
                                            animationSpec = tween(
                                                durationMillis = 900,
                                                easing = FastOutSlowInEasing
                                            )
                                        )
                                        isRippling = false
                                    }
                                }
                            }
                        )
                    }

                    item {
                        // 2. كارت النصوص المفضلة والمظللة
                        SettingsItemCard(
                            title = "النصوص المفضلة والمظللة",
                            subtitle = "عرض وإدارة النصوص التي قمت بحفظها أثناء القراءة",
                            icon = Icons.Outlined.BookmarkBorder,
                            badgeCount = highlights.size,
                            onClick = { showFavoritesSheet = true }
                        )
                    }

                    item {
                        // 3. كارت عن التطبيق
                        SettingsItemCard(
                            title = "عن التطبيق",
                            subtitle = "فريق العمل ومعلومات الإصدار",
                            icon = Icons.Outlined.Info,
                            onClick = { showAboutDialog = true }
                        )
                    }
                }
            }

            // أنميشن الموجة الضوئية المتوسعة بسلاسة دون حجب محتوى الشاشة
            if (isRippling) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val maxRadius = hypot(size.width, size.height) * 1.15f
                    val currentRadius = maxRadius * rippleProgress.value
                    val fadeAlpha = (1f - rippleProgress.value).coerceIn(0f, 1f)

                    // 1. هالة ضوئية ناعمة من لون المظهر الجديد
                    drawCircle(
                        color = rippleColor.copy(alpha = fadeAlpha * 0.35f),
                        radius = currentRadius,
                        center = rippleCenter
                    )

                    // 2. حلقة تموجية ذهبية بارزة تنبثق وتتلاشى نحو الخارج
                    drawCircle(
                        color = BottomBarGold.copy(alpha = fadeAlpha * 0.65f),
                        radius = currentRadius,
                        center = rippleCenter,
                        style = Stroke(width = (8.dp * fadeAlpha).toPx())
                    )
                }
            }
        }
    }

    if (showFavoritesSheet) {
        FavoritesBottomSheet(
            highlights = highlights,
            onDismiss = { showFavoritesSheet = false },
            onHighlightClick = { id ->
                showFavoritesSheet = false
                onHighlightClick(id)
            },
            onDeleteHighlight = { id -> viewModel.deleteHighlight(id) }
        )
    }

    if (showAboutDialog) {
        AboutDialog(
            onDismiss = { showAboutDialog = false }
        )
    }
}