package com.example.bookproject1.book_feature.presentation.home.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.bookproject1.R
import com.example.bookproject1.app.ui.theme.AlMasabeehTheme
import com.example.bookproject1.book_feature.presentation.component.logic.BookPart

@Composable
fun PartGridCard(
    part: BookPart,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onClick(part.partNumber) },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // صورة الغلاف بالأعلى
                Image(
                    painter = painterResource(id = part.coverRes),
                    contentDescription = stringResource(id = part.titleRes),
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.72f)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(10.dp))

                // اسم الجزء بالأسفل
                Text(
                    text = stringResource(id = part.titleRes),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// ===================================
// Previews (معاينة وضع الشبكة)
// ===================================

@Preview(name = "Grid Card - Light", showBackground = true, widthDp = 180)
@Composable
private fun PartGridCardLightPreview() {
    AlMasabeehTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            PartGridCard(
                part = BookPart(
                    partNumber = 1,
                    titleRes = R.string.part_one,
                    coverRes = R.drawable.part1
                ),
                onClick = {},
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Preview(name = "Grid Card - Dark", showBackground = true, widthDp = 180, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PartGridCardDarkPreview() {
    AlMasabeehTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            PartGridCard(
                part = BookPart(
                    partNumber = 1,
                    titleRes = R.string.part_one,
                    coverRes = R.drawable.part1
                ),
                onClick = {},
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}