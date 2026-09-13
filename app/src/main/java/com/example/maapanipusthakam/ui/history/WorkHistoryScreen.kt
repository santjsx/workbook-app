package com.example.maapanipusthakam.ui.history

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.maapanipusthakam.domain.model.Work
import com.example.maapanipusthakam.domain.model.WorkStatus
import com.example.maapanipusthakam.theme.BrickTerracotta
import com.example.maapanipusthakam.theme.DividerColor
import com.example.maapanipusthakam.theme.ExpenseRed
import com.example.maapanipusthakam.theme.InkPrimary
import com.example.maapanipusthakam.theme.InkSecondary
import com.example.maapanipusthakam.theme.PaperBackground
import com.example.maapanipusthakam.theme.PaperCard
import com.example.maapanipusthakam.theme.PaperCardElevated
import com.example.maapanipusthakam.theme.ReceivedGreen
import com.example.maapanipusthakam.theme.ReceivedGreenLight
import com.example.maapanipusthakam.theme.RemainingWarm
import com.example.maapanipusthakam.ui.components.BigButton
import com.example.maapanipusthakam.ui.components.NotebookCard

@Composable
fun WorkHistoryScreen(
    onOpenWork: (workId: Long) -> Unit,
    onAddNewWork: () -> Unit
) {
    val workRepo = remember { MaaPaniApp.instance.workRepository }
    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: జరుగుతున్నవి, 1: పూర్తయినవి

    val activeWorksFlow = remember(workRepo) { workRepo.getActiveWorks() }
    val activeWorks by activeWorksFlow.collectAsState(initial = emptyList())
    val completedWorksFlow = remember(workRepo) { workRepo.getCompletedWorks() }
    val completedWorks by completedWorksFlow.collectAsState(initial = emptyList())

    val displayedWorks = if (selectedTabIndex == 0) activeWorks else completedWorks

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "నా పనులు",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp
            ),
            color = InkPrimary
        )
        Text(
            text = "మీ అన్ని పనుల పుస్తకం",
            style = MaterialTheme.typography.bodyMedium,
            color = InkSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = PaperCard,
            contentColor = BrickTerracotta,
            indicator = { tabPositions ->
                SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = BrickTerracotta,
                    height = 3.dp
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = {
                    Text(
                        text = "నడుస్తున్నవి (${activeWorks.size})",
                        fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 15.sp
                    )
                }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = {
                    Text(
                        text = "పూర్తయినవి (${completedWorks.size})",
                        fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 15.sp
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (displayedWorks.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (selectedTabIndex == 0) "ప్రస్తుతం జరుగుతున్న పనులు లేవు." else "పూర్తయిన పనుల చరిత్ర ఇంకా లేదు.",
                        style = MaterialTheme.typography.titleMedium,
                        color = InkSecondary,
                        textAlign = TextAlign.Center
                    )
                    if (selectedTabIndex == 0) {
                        Spacer(modifier = Modifier.height(16.dp))
                        BigButton(
                            text = "కొత్త పని",
                            icon = Icons.Default.Add,
                            onClick = onAddNewWork,
                            modifier = Modifier.fillMaxWidth(0.7f)
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(displayedWorks, key = { it.id }) { work ->
                    WorkHistoryCard(
                        work = work,
                        onClick = { onOpenWork(work.id) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            BigButton(
                text = "కొత్త పని రాయండి",
                icon = Icons.Default.Add,
                onClick = onAddNewWork,
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }
    }
}

@Composable
fun WorkHistoryCard(
    work: Work,
    onClick: () -> Unit
) {
    val workRepo = remember { MaaPaniApp.instance.workRepository }
    val summaryFlow = remember(workRepo, work.id) { workRepo.getWorkSummary(work.id) }
    val summary by summaryFlow.collectAsState(initial = null)

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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = work.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = InkPrimary
                    )
                    if (work.location.isNotBlank()) {
                        Text(
                            text = work.location,
                            style = MaterialTheme.typography.bodyMedium,
                            color = InkSecondary
                        )
                    }
                }

                Surface(
                    color = if (work.status == WorkStatus.COMPLETED) ReceivedGreenLight else PaperCard,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, DividerColor)
                ) {
                    Text(
                        text = if (work.status == WorkStatus.COMPLETED) "✓ పూర్తయ్యింది" else "ఇంకా జరుగుతోంది",
                        color = if (work.status == WorkStatus.COMPLETED) ReceivedGreen else BrickTerracotta,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${DateFormatter.formatToTeluguDate(work.startDate)} న మొదలైంది",
                style = MaterialTheme.typography.bodySmall,
                color = InkSecondary
            )

            if (summary != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = PaperCard,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("వచ్చింది", style = MaterialTheme.typography.bodySmall, color = InkSecondary)
                            Text(
                                CurrencyFormatter.formatInRupees(summary!!.totalIncome),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = ReceivedGreen
                            )
                        }

                        Column {
                            Text("మొత్తం ఖర్చు", style = MaterialTheme.typography.bodySmall, color = InkSecondary)
                            Text(
                                CurrencyFormatter.formatInRupees(summary!!.totalExpense),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = ExpenseRed
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("మిగిలింది", style = MaterialTheme.typography.bodySmall, color = InkSecondary)
                            Text(
                                CurrencyFormatter.formatInRupees(summary!!.remaining),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = RemainingWarm
                            )
                        }
                    }
                }
            }
        }
    }
}
