package com.example.maapanipusthakam.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maapanipusthakam.MaaPaniApp
import com.example.maapanipusthakam.core.currency.CurrencyFormatter
import com.example.maapanipusthakam.core.date.DateFormatter
import com.example.maapanipusthakam.data.repository.TodayWorkItem
import com.example.maapanipusthakam.theme.BrickTerracotta
import com.example.maapanipusthakam.theme.DividerColor
import com.example.maapanipusthakam.theme.ExpenseRed
import com.example.maapanipusthakam.theme.InkPrimary
import com.example.maapanipusthakam.theme.InkSecondary
import com.example.maapanipusthakam.theme.PaperBackground
import com.example.maapanipusthakam.theme.PaperCard
import com.example.maapanipusthakam.theme.PaperCardElevated
import com.example.maapanipusthakam.theme.ReceivedGreen
import com.example.maapanipusthakam.ui.components.BigButton
import com.example.maapanipusthakam.ui.components.NotebookCard

@Composable
fun HomeScreen(
    onOpenWork: (workId: Long) -> Unit,
    onAddNewWork: () -> Unit
) {
    val workRepo = remember { MaaPaniApp.instance.workRepository }
    val todayWorksFlow = remember(workRepo) { workRepo.getTodayWorks() }
    val todayWorks by todayWorksFlow.collectAsState(initial = emptyList())
    val todayDate = remember { DateFormatter.todayEpochDay() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // App Header & Today Date Card
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = PaperCard,
            border = BorderStroke(1.dp, DividerColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = BrickTerracotta,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ఈ రోజు",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = BrickTerracotta
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = DateFormatter.formatToTeluguDate(todayDate),
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp
                    ),
                    color = InkPrimary
                )
                Text(
                    text = "మీ రోజువారీ పని లెక్కల పుస్తకం",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Active Works Section Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ఈ రోజు పనులు",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = InkPrimary
            )
            if (todayWorks.isNotEmpty()) {
                Surface(
                    color = PaperCard,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, DividerColor)
                ) {
                    Text(
                        text = "${todayWorks.size} పనులు",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = InkSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // List of Active Works or Empty State
        if (todayWorks.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        Icons.Default.Book,
                        contentDescription = null,
                        tint = BrickTerracotta.copy(alpha = 0.6f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "ఇంకా పని రాయలేదు.",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = InkPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "మీరు చేస్తున్న ఇంటి పనిని లేదా కాంట్రాక్ట్‌ను మొదలుపెట్టండి.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    BigButton(
                        text = "కొత్త పని రాయండి",
                        icon = Icons.Default.Add,
                        onClick = onAddNewWork,
                        modifier = Modifier.fillMaxWidth(0.85f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(todayWorks, key = { it.work.id }) { item ->
                    TodayWorkCard(
                        item = item,
                        onClick = { onOpenWork(item.work.id) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Primary Bottom Action: కొత్త పని
            BigButton(
                text = "కొత్త పని",
                icon = Icons.Default.Add,
                onClick = onAddNewWork,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

/**
 * Work Card on Home Screen (PRD Section 25)
 */
@Composable
fun TodayWorkCard(
    item: TodayWorkItem,
    onClick: () -> Unit
) {
    NotebookCard(
        onClick = onClick,
        backgroundColor = PaperCardElevated
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(
                        text = "🏠",
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = item.work.name,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = InkPrimary
                        )
                        if (item.work.location.isNotBlank()) {
                            Text(
                                text = item.work.location,
                                style = MaterialTheme.typography.bodySmall,
                                color = InkSecondary
                            )
                        }
                    }
                }

                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "ఓపెన్ చేయి",
                    tint = BrickTerracotta,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = if (item.hasActivityToday) PaperCard else PaperCard.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (item.hasActivityToday) {
                        Text(
                            text = "ఈ రోజు పని చేశాం",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = BrickTerracotta
                        )
                    } else {
                        Text(
                            text = "ఈ రోజు ఇంకా లెక్క రాయలేదు",
                            style = MaterialTheme.typography.bodySmall,
                            color = InkSecondary
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (item.todayIncome > 0) {
                            Text(
                                text = "${CurrencyFormatter.formatInRupees(item.todayIncome)} వచ్చింది",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = ReceivedGreen
                            )
                        }
                        if (item.todayExpense > 0) {
                            Text(
                                text = "${CurrencyFormatter.formatInRupees(item.todayExpense)} ఖర్చు",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = ExpenseRed
                            )
                        }
                    }
                }
            }
        }
    }
}
