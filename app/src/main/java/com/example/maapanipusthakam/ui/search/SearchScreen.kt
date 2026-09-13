package com.example.maapanipusthakam.ui.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maapanipusthakam.MaaPaniApp
import com.example.maapanipusthakam.core.currency.CurrencyFormatter
import com.example.maapanipusthakam.core.date.DateFormatter
import com.example.maapanipusthakam.data.repository.SearchResultItem
import com.example.maapanipusthakam.theme.BrickTerracotta
import com.example.maapanipusthakam.theme.DividerColor
import com.example.maapanipusthakam.theme.ExpenseRed
import com.example.maapanipusthakam.theme.InkPrimary
import com.example.maapanipusthakam.theme.InkSecondary
import com.example.maapanipusthakam.theme.PaperBackground
import com.example.maapanipusthakam.theme.PaperCard
import com.example.maapanipusthakam.theme.PaperCardElevated
import com.example.maapanipusthakam.theme.ReceivedGreen
import com.example.maapanipusthakam.ui.components.NotebookCard

@Composable
fun SearchScreen(
    onOpenWork: (workId: Long) -> Unit
) {
    val notebookRepo = remember { MaaPaniApp.instance.notebookRepository }
    var searchQuery by remember { mutableStateOf("") }
    val searchFlow = remember(notebookRepo, searchQuery) { notebookRepo.searchAll(searchQuery) }
    val results by searchFlow.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "వెతుకు",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp
            ),
            color = InkPrimary
        )
        Text(
            text = "పని పేరు, కూలీ పేరు, లేదా ఖర్చును వెతకండి",
            style = MaterialTheme.typography.bodyMedium,
            color = InkSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        val searchVoiceLauncher = com.example.maapanipusthakam.core.voice.rememberVoiceInputLauncher { spoken ->
            searchQuery = spoken
        }

        // Search Input Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("ఉదా: రవి, సిమెంట్, ఇల్లు") },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = BrickTerracotta
                )
            },
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "ఖాళీ చేయి")
                        }
                    }
                    IconButton(onClick = searchVoiceLauncher) {
                        Icon(
                            Icons.Default.Mic,
                            contentDescription = "నోటితో వెతకండి",
                            tint = BrickTerracotta,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Large Voice Search Button
        OutlinedButton(
            onClick = searchVoiceLauncher,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = BrickTerracotta.copy(alpha = 0.08f),
                contentColor = BrickTerracotta
            ),
            border = BorderStroke(1.dp, BrickTerracotta.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("🎙️ నోటితో చెప్పి వెతకండి", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Category Filter Chips
        val quickFilters = listOf("⚪ సిమెంట్", "🏖️ ఇసుక", "🧱 ఇటుకలు", "👷 కూలీ", "💰 అడ్వాన్స్", "🏠 ఇల్లు")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickFilters.forEach { chipText ->
                val clean = chipText.substringAfter(" ")
                val isSelected = searchQuery == clean
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        searchQuery = if (isSelected) "" else clean
                    },
                    label = { Text(chipText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BrickTerracotta,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (searchQuery.isBlank()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = null,
                        tint = BrickTerracotta.copy(alpha = 0.6f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "వెతకడానికి పై బటన్ నొక్కి నోటితో చెప్పండి\nలేదా కింద ఉన్న పేర్లను నొక్కండి.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = InkSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else if (results.isEmpty()) {
            // PRD Section 49: Exactly "ఇది దొరకలేదు."
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "ఇది దొరకలేదు.",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = InkPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "సరైన పేరు లేదా అక్షరాలు రాయండి.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(results, key = { "${it.type}_${it.id}" }) { item ->
                    SearchResultCard(
                        item = item,
                        onClick = { onOpenWork(item.workId) }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(
    item: SearchResultItem,
    onClick: () -> Unit
) {
    NotebookCard(
        onClick = onClick,
        backgroundColor = PaperCardElevated
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🏠 ${item.workName}",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = BrickTerracotta
                )
                Text(
                    text = DateFormatter.formatShortTeluguDate(item.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = InkSecondary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = InkPrimary
                    )
                    if (item.subtitle.isNotBlank()) {
                        Text(
                            text = item.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = InkSecondary
                        )
                    }
                }

                if (item.amount != null) {
                    val amountColor = when (item.type) {
                        "INCOME" -> ReceivedGreen
                        "LABOUR" -> BrickTerracotta
                        else -> ExpenseRed
                    }
                    Text(
                        text = CurrencyFormatter.formatInRupees(item.amount),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = amountColor
                    )
                }
            }
        }
    }
}
