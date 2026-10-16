package com.example.bookproject1.book_feature.presentation.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookproject1.app.ui.theme.UiFontFamily
import com.example.bookproject1.book_feature.presentation.component.ui.BottomBarGold

@Composable
fun AboutDialog(
    onDismiss: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "عن التطبيق",
                    fontFamily = UiFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "تطبيق كتاب المصابيح",
                        fontFamily = UiFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = BottomBarGold
                    )

                    Text(
                        text = "يهدف التطبيق الى وضع نص كتاب المصابيح ( شرح ادعية مختارة من الصحيفة السجادية ودعاء ابي حمزة الثمالي) للامام زين العابدين بين يدي القارئ مع امكانية البحث وسهولة التصفح والمطالعة.",
                        fontFamily = UiFontFamily,
                        fontSize = 13.5.sp,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                    )

                    AboutSectionItem(
                        title = "المراجعة اللغوية والتحقيق",
                        names = listOf(
                            "رضوان عبدالهادي السلامي",
                            "صباح نعيم جاسم الصافي",
                            "محمد رضا الأسدي",
                            "عباس صباح الجنابي"
                        )
                    )

                    AboutSectionItem(
                        title = "الجمع والتدوين",
                        names = listOf(
                            "حيدر فائق الهنداوي",
                            "محمد يوسف السعدي",
                            "أحمد نعمة الخفاجي",
                            "حيدر محمد الدفاعي"
                        )
                    )

                    AboutSectionItem(
                        title = "التصميم والإخراج",
                        names = listOf("علي عبدالحليم المظفر")
                    )

                    AboutSectionItem(
                        title = "تطوير وبرمجة",
                        names = listOf("مركز قنطرة لتكنولوجيا المعلومات")
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "إغلاق",
                        fontFamily = UiFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = BottomBarGold
                    )
                }
            },
            shape = RoundedCornerShape(18.dp),
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun AboutSectionItem(
    title: String,
    names: List<String>
) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
            text = title,
            fontFamily = UiFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 13.5.sp,
            color = BottomBarGold
        )
        names.forEach { name ->
            Text(
                text = "• $name",
                fontFamily = UiFontFamily,
                fontSize = 12.5.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}