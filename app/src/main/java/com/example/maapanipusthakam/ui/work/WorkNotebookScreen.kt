package com.example.maapanipusthakam.ui.work

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import com.example.maapanipusthakam.core.voice.rememberVoiceInputLauncher
import com.example.maapanipusthakam.ui.components.StyledAppSnackbar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maapanipusthakam.MaaPaniApp
import com.example.maapanipusthakam.core.currency.CurrencyFormatter
import com.example.maapanipusthakam.core.date.DateFormatter
import com.example.maapanipusthakam.domain.model.DailyPage
import com.example.maapanipusthakam.domain.model.Expense
import com.example.maapanipusthakam.domain.model.IncomeEntry
import com.example.maapanipusthakam.domain.model.LabourPayment
import com.example.maapanipusthakam.domain.model.Work
import com.example.maapanipusthakam.domain.model.WorkStatus
import com.example.maapanipusthakam.domain.model.Worker
import com.example.maapanipusthakam.theme.BrickTerracotta
import com.example.maapanipusthakam.theme.BrickTerracottaLight
import com.example.maapanipusthakam.theme.DividerColor
import com.example.maapanipusthakam.theme.ExpenseRed
import com.example.maapanipusthakam.theme.ExpenseRedLight
import com.example.maapanipusthakam.theme.InkPrimary
import com.example.maapanipusthakam.theme.InkSecondary
import com.example.maapanipusthakam.theme.MarginLineColor
import com.example.maapanipusthakam.theme.PaperBackground
import com.example.maapanipusthakam.theme.RuledLineColor
import com.example.maapanipusthakam.theme.PaperCard
import com.example.maapanipusthakam.theme.PaperCardElevated
import com.example.maapanipusthakam.theme.ReceivedGreen
import com.example.maapanipusthakam.theme.ReceivedGreenLight
import com.example.maapanipusthakam.theme.RemainingWarm
import com.example.maapanipusthakam.theme.RemainingWarmLight
import com.example.maapanipusthakam.ui.components.BigButton
import com.example.maapanipusthakam.ui.components.BigOutlinedButton
import com.example.maapanipusthakam.ui.components.NotebookCard
import com.example.maapanipusthakam.ui.components.SimpleConfirmDialog
import com.example.maapanipusthakam.ui.components.ruledNotebookBackground
import com.example.maapanipusthakam.ui.work.dialogs.AddExpenseDialog
import com.example.maapanipusthakam.ui.work.dialogs.AddIncomeDialog
import com.example.maapanipusthakam.ui.work.dialogs.AddLabourDialog
import com.example.maapanipusthakam.ui.work.dialogs.EntryOptionsDialog
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkNotebookScreen(
    workId: Long,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val workRepo = remember { MaaPaniApp.instance.workRepository }
    val notebookRepo = remember { MaaPaniApp.instance.notebookRepository }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Work state
    val workFlow = remember(workRepo, workId) { workRepo.getWorkByIdFlow(workId) }
    val work by workFlow.collectAsState(initial = null)
    val workSummaryFlow = remember(workRepo, workId) { workRepo.getWorkSummary(workId) }
    val workSummary by workSummaryFlow.collectAsState(initial = null)

    // Current page date
    var currentDate by remember { mutableLongStateOf(DateFormatter.todayEpochDay()) }

    // Ensure DailyPage exists for currentDate
    val dailyPageFlow = remember(notebookRepo, workId, currentDate) { notebookRepo.getDailyPageFlow(workId, currentDate) }
    val dailyPage by dailyPageFlow.collectAsState(initial = null)
    val pageId = dailyPage?.id ?: 0L

    // Entries for current daily page
    val incomeFlow = remember(notebookRepo, pageId) { notebookRepo.getIncomeForPage(pageId) }
    val incomeList by incomeFlow.collectAsState(initial = emptyList())
    val labourFlow = remember(notebookRepo, pageId) { notebookRepo.getLabourForPage(pageId) }
    val labourList by labourFlow.collectAsState(initial = emptyList())
    val expenseFlow = remember(notebookRepo, pageId) { notebookRepo.getExpensesForPage(pageId) }
    val expenseList by expenseFlow.collectAsState(initial = emptyList())
    val dailySummaryFlow = remember(notebookRepo, workId, currentDate) { notebookRepo.getDailySummary(workId, currentDate) }
    val dailySummary by dailySummaryFlow.collectAsState(initial = null)

    // Dialog visibility states
    var showAddIncome by remember { mutableStateOf(false) }
    var showAddLabour by remember { mutableStateOf(false) }
    var showAddExpense by remember { mutableStateOf(false) }
    var showFutureDateWarning by remember { mutableStateOf<Long?>(null) }
    var showCompleteConfirm by remember { mutableStateOf(false) }
    var showDeleteWorkConfirm by remember { mutableStateOf(false) }
    var showEditDescriptionDialog by remember { mutableStateOf(false) }

    // Selection for editing / deleting
    var selectedIncomeToEdit by remember { mutableStateOf<IncomeEntry?>(null) }
    var selectedLabourToEdit by remember { mutableStateOf<LabourPayment?>(null) }
    var selectedExpenseToEdit by remember { mutableStateOf<Expense?>(null) }
    var itemToDeleteConfirm by remember { mutableStateOf<(() -> Unit)?>(null) }

    // Worker list for quick chips
    var recentWorkers by remember { mutableStateOf<List<Worker>>(emptyList()) }
    remember(showAddLabour) {
        if (showAddLabour) {
            recentWorkers = notebookRepo.getAllWorkers()
        }
    }

    // Auto-create page if missing
    remember(workId, currentDate, dailyPage) {
        if (dailyPage == null) {
            notebookRepo.getOrCreateDailyPage(workId, currentDate)
        }
    }

    fun navigateToDate(targetDate: Long) {
        if (DateFormatter.isFutureDate(targetDate)) {
            showFutureDateWarning = targetDate
        } else {
            currentDate = targetDate
        }
    }

    // Future date warning dialog
    if (showFutureDateWarning != null) {
        val target = showFutureDateWarning!!
        AlertDialog(
            onDismissRequest = { showFutureDateWarning = null },
            title = {
                Text(
                    text = "ఇది రాబోయే తేదీ",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = BrickTerracotta
                )
            },
            text = {
                Text(
                    text = "ఈ తేదీకి లెక్క రాయాలా?",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        currentDate = target
                        showFutureDateWarning = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrickTerracotta)
                ) {
                    Text("రాయాలి", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showFutureDateWarning = null }) {
                    Text("వెనక్కి")
                }
            },
            containerColor = PaperCardElevated,
            shape = RoundedCornerShape(18.dp)
        )
    }

    // Work complete confirmation
    if (showCompleteConfirm) {
        SimpleConfirmDialog(
            title = "ఈ పని పూర్తయ్యిందా?",
            message = "పని పూర్తయిన తర్వాత కూడా మీరు ఎప్పుడైనా పాత లెక్కలను చూడవచ్చు మరియు మార్చవచ్చు.",
            confirmButtonText = "పూర్తయ్యింది ✓",
            dismissButtonText = "ఇంకా ఉంది",
            onConfirm = {
                workRepo.completeWork(workId)
                showCompleteConfirm = false
                scope.launch {
                    snackbarHostState.showSnackbar("పని పూర్తయ్యింది ✓")
                }
            },
            onDismiss = { showCompleteConfirm = false }
        )
    }

    // Delete item confirmation
    if (itemToDeleteConfirm != null) {
        SimpleConfirmDialog(
            title = "ఈ లెక్కను తీసేయాలా?",
            confirmButtonText = "తీసేయి",
            dismissButtonText = "వెనక్కి",
            isDestructive = true,
            onConfirm = {
                itemToDeleteConfirm?.invoke()
                itemToDeleteConfirm = null
            },
            onDismiss = { itemToDeleteConfirm = null }
        )
    }

    // Delete entire work confirmation
    if (showDeleteWorkConfirm && work != null) {
        SimpleConfirmDialog(
            title = "ఈ పనిని తీసివేయాలా?",
            message = "\"${work!!.name}\" పని మరియు దీనికి సంబంధించిన అన్ని రోజువారీ లెక్కలు, కూలీల వివరాలు పూర్తిగా తొలగించబడతాయి. ఇది మళ్లీ తిరిగి రాదు.",
            confirmButtonText = "తీసివెయ్యి",
            dismissButtonText = "వద్దు",
            isDestructive = true,
            onConfirm = {
                showDeleteWorkConfirm = false
                workRepo.deleteWork(workId)
                onNavigateBack()
            },
            onDismiss = { showDeleteWorkConfirm = false }
        )
    }

    // Work description edit dialog - zero typing presets + voice input
    if (showEditDescriptionDialog && dailyPage != null) {
        var tempDesc by remember { mutableStateOf(dailyPage!!.workDescription) }
        val descVoiceLauncher = rememberVoiceInputLauncher { spoken -> tempDesc = spoken }
        val workPresets = listOf(
            "🧱 గోడలు కట్టాం",
            "🏗️ స్లాబ్ వేసాం",
            "🖌️ ప్లాస్టరింగ్ చేశాం",
            "🪵 సెంట్రింగ్ కొట్టాం",
            "⛏️ పునాది తీసాం",
            "🪨 కాంక్రీట్ వేసాం",
            "🧱 టైల్స్ వేశాం",
            "🎨 రంగులు వేశాం"
        )

        AlertDialog(
            onDismissRequest = { showEditDescriptionDialog = false },
            title = {
                Text("ఈ రోజు చేసిన పని", fontWeight = FontWeight.Bold, color = BrickTerracotta)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                    Text("పని రకం ఎంచుకోండి:", style = MaterialTheme.typography.bodySmall, color = InkSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        workPresets.forEach { wp ->
                            val clean = wp.substringAfter(" ")
                            FilterChip(
                                selected = tempDesc.contains(clean),
                                onClick = {
                                    tempDesc = if (tempDesc.isBlank()) clean else "$tempDesc, $clean"
                                },
                                label = { Text(wp, fontSize = 13.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrickTerracotta,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = tempDesc,
                            onValueChange = { tempDesc = it },
                            placeholder = { Text("ఉదా: గోడ కట్టడం, స్లాబ్ వేయడం") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(onClick = descVoiceLauncher) {
                            Icon(Icons.Default.Mic, contentDescription = "నోటితో చెప్పండి", tint = BrickTerracotta)
                        }
                    }
                    OutlinedButton(
                        onClick = descVoiceLauncher,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = BrickTerracotta.copy(alpha = 0.08f),
                            contentColor = BrickTerracotta
                        ),
                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("🎙️ నోటితో చెప్పండి", fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        notebookRepo.updateWorkDescription(pageId, tempDesc)
                        showEditDescriptionDialog = false
                        scope.launch {
                            snackbarHostState.showSnackbar("రాసుకున్నాం ✓", duration = SnackbarDuration.Short)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrickTerracotta, contentColor = Color.White)
                ) {
                    Text("రాసుకో ✓", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showEditDescriptionDialog = false }) {
                    Text("వెనక్కి")
                }
            },
            containerColor = PaperCardElevated
        )
    }

    // Dialogs: Add / Edit Income
    if (showAddIncome || selectedIncomeToEdit != null) {
        val editing = selectedIncomeToEdit
        AddIncomeDialog(
            initialAmount = editing?.amount,
            initialSource = editing?.source ?: "",
            initialReason = editing?.reason ?: "",
            initialNote = editing?.note ?: "",
            isEditing = editing != null,
            onDismiss = {
                showAddIncome = false
                selectedIncomeToEdit = null
            },
            onSave = { amount, src, rsn, nt ->
                if (editing != null) {
                    notebookRepo.updateIncome(editing.id, amount, src, rsn, nt)
                    selectedIncomeToEdit = null
                } else {
                    notebookRepo.addIncome(workId, pageId, amount, src, rsn, nt, currentDate)
                    showAddIncome = false
                }
                scope.launch {
                    snackbarHostState.showSnackbar("రాసుకున్నాం ✓", duration = SnackbarDuration.Short)
                }
            }
        )
    }

    // Dialogs: Add / Edit Labour
    if (showAddLabour || selectedLabourToEdit != null) {
        val editing = selectedLabourToEdit
        AddLabourDialog(
            recentWorkers = recentWorkers,
            initialWorkerName = editing?.workerName ?: "",
            initialWorkerRole = editing?.workerRole ?: "కూలీ",
            initialAmount = editing?.amount,
            initialNote = editing?.note ?: "",
            isEditing = editing != null,
            onDismiss = {
                showAddLabour = false
                selectedLabourToEdit = null
            },
            onSave = { name, role, amount, nt ->
                if (editing != null) {
                    notebookRepo.updateLabourPayment(editing.id, name, role, amount, nt)
                    selectedLabourToEdit = null
                } else {
                    notebookRepo.addLabourPayment(workId, pageId, name, role, amount, nt, currentDate)
                    showAddLabour = false
                }
                scope.launch {
                    snackbarHostState.showSnackbar("రాసుకున్నాం ✓", duration = SnackbarDuration.Short)
                }
            }
        )
    }

    // Dialogs: Add / Edit Expense
    if (showAddExpense || selectedExpenseToEdit != null) {
        val editing = selectedExpenseToEdit
        AddExpenseDialog(
            initialCategory = editing?.category ?: "సిమెంట్",
            initialAmount = editing?.amount,
            initialNote = editing?.note ?: "",
            isEditing = editing != null,
            onDismiss = {
                showAddExpense = false
                selectedExpenseToEdit = null
            },
            onSave = { cat, amount, nt ->
                if (editing != null) {
                    notebookRepo.updateExpense(editing.id, cat, amount, nt)
                    selectedExpenseToEdit = null
                } else {
                    notebookRepo.addExpense(workId, pageId, cat, amount, nt, currentDate)
                    showAddExpense = false
                }
                scope.launch {
                    snackbarHostState.showSnackbar("రాసుకున్నాం ✓", duration = SnackbarDuration.Short)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = work?.name ?: "పని లెక్కలు",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = InkPrimary
                        )
                        if (work != null) {
                            Text(
                                text = "${DateFormatter.formatToTeluguDate(work!!.startDate)} నుంచి",
                                style = MaterialTheme.typography.bodySmall,
                                color = InkSecondary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "వెనక్కి",
                            tint = InkPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteWorkConfirm = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "పనిని తీసివెయ్యి",
                            tint = ExpenseRed
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperBackground)
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                StyledAppSnackbar(data)
            }
        },
        containerColor = PaperBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // ==================== WORK OVERALL SUMMARY CARD ====================
            if (workSummary != null) {
                WorkCumulativeSummaryCard(
                    summary = workSummary!!,
                    isCompleted = work?.status == WorkStatus.COMPLETED,
                    onCompleteClick = { showCompleteConfirm = true },
                    onReopenClick = {
                        workRepo.reopenWork(workId)
                        scope.launch { snackbarHostState.showSnackbar("పని మళ్లీ మొదలైంది ✓") }
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ==================== PHYSICAL NOTEBOOK PAGE ====================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PaperCardElevated),
                border = BorderStroke(1.dp, DividerColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .ruledNotebookBackground()
                        .padding(bottom = 20.dp)
                ) {
                    // Page Header & Date Navigator
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PaperCard)
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { navigateToDate(currentDate - 1) },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, DividerColor)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "వెనక్కి",
                                modifier = Modifier.size(16.dp),
                                tint = InkPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("నిన్న", style = MaterialTheme.typography.labelMedium, color = InkPrimary)
                        }

                        // Center: Date picker button
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = PaperCardElevated,
                            border = BorderStroke(1.dp, BrickTerracotta.copy(alpha = 0.5f)),
                            modifier = Modifier.clickable {
                                val currentLocalDate = LocalDate.ofEpochDay(currentDate)
                                val dpd = DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        val picked = LocalDate.of(year, month + 1, dayOfMonth).toEpochDay()
                                        navigateToDate(picked)
                                    },
                                    currentLocalDate.year,
                                    currentLocalDate.monthValue - 1,
                                    currentLocalDate.dayOfMonth
                                )
                                dpd.show()
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = BrickTerracotta,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = DateFormatter.formatToTeluguDate(currentDate),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = InkPrimary
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { navigateToDate(currentDate + 1) },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, DividerColor)
                        ) {
                            Text("రేపు", style = MaterialTheme.typography.labelMedium, color = InkPrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "ముందుకు",
                                modifier = Modifier.size(16.dp),
                                tint = InkPrimary
                            )
                        }
                    }

                    HorizontalDivider(color = DividerColor)

                    // Padding inside the notebook page (leaving room for red margin guideline at left)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 54.dp, end = 16.dp, top = 14.dp)
                    ) {
                        // Section 1: ఈ రోజు చేసిన పని
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showEditDescriptionDialog = true },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ఈ రోజు చేసిన పని:",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = BrickTerracotta
                                )
                                Text(
                                    text = dailyPage?.workDescription?.ifBlank { "పని వివరాలు రాయడానికి ఇక్కడ నొక్కండి" }
                                        ?: "పని వివరాలు రాయడానికి ఇక్కడ నొక్కండి",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (dailyPage?.workDescription.isNullOrBlank()) InkSecondary.copy(alpha = 0.7f) else InkPrimary
                                )
                            }
                            IconButton(onClick = { showEditDescriptionDialog = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "మార్చు", tint = BrickTerracotta)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = RuledLineColor)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Section 2: వచ్చిన డబ్బు
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "వచ్చిన డబ్బు",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = ReceivedGreen
                            )
                            Button(
                                onClick = { showAddIncome = true },
                                colors = ButtonDefaults.buttonColors(containerColor = ReceivedGreen, contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("వచ్చిన డబ్బు", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (incomeList.isEmpty()) {
                            Text(
                                text = "— ఇంకా ఏమీ రాలేదు —",
                                style = MaterialTheme.typography.bodyMedium,
                                color = InkSecondary.copy(alpha = 0.6f),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            incomeList.forEach { item ->
                                var showOptions by remember { mutableStateOf(false) }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showOptions = true }
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = CurrencyFormatter.formatInRupees(item.amount),
                                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                            color = ReceivedGreen
                                        )
                                        if (item.source.isNotBlank() || item.reason.isNotBlank()) {
                                            Text(
                                                text = listOf(item.source, item.reason).filter { it.isNotBlank() }.joinToString(" • "),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = InkSecondary
                                            )
                                        }
                                    }
                                    Text("మార్చు ✎", style = MaterialTheme.typography.bodySmall, color = InkSecondary)
                                }
                                HorizontalDivider(color = RuledLineColor.copy(alpha = 0.5f))

                                if (showOptions) {
                                    EntryOptionsDialog(
                                        title = if (item.source.isNotBlank()) "వచ్చిన డబ్బు: ${item.source}" else "వచ్చిన డబ్బు",
                                        amountText = CurrencyFormatter.formatInRupees(item.amount),
                                        onEdit = {
                                            showOptions = false
                                            selectedIncomeToEdit = item
                                        },
                                        onDelete = {
                                            showOptions = false
                                            itemToDeleteConfirm = {
                                                notebookRepo.deleteIncome(item.id)
                                                scope.launch {
                                                    val res = snackbarHostState.showSnackbar(
                                                        message = "లెక్క తీసేశాం",
                                                        actionLabel = "మళ్లీ పెట్టు",
                                                        duration = SnackbarDuration.Long
                                                    )
                                                    if (res == SnackbarResult.ActionPerformed) {
                                                        notebookRepo.restoreIncome(item.id)
                                                    }
                                                }
                                            }
                                        },
                                        onDismiss = { showOptions = false }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = RuledLineColor)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Section 3: కూలీల డబ్బు
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "కూలీల డబ్బు",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = BrickTerracotta
                            )
                            Button(
                                onClick = { showAddLabour = true },
                                colors = ButtonDefaults.buttonColors(containerColor = BrickTerracotta, contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("కూలీ చేర్చు", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (labourList.isEmpty()) {
                            Text(
                                text = "— ఇంకా ఎవరికీ ఇవ్వలేదు —",
                                style = MaterialTheme.typography.bodyMedium,
                                color = InkSecondary.copy(alpha = 0.6f),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            labourList.forEach { item ->
                                var showOptions by remember { mutableStateOf(false) }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showOptions = true }
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${item.workerName} (${item.workerRole})",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = InkPrimary
                                        )
                                        if (item.note.isNotBlank()) {
                                            Text(
                                                text = item.note,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = InkSecondary
                                            )
                                        }
                                    }
                                    Text(
                                        text = CurrencyFormatter.formatInRupees(item.amount),
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = BrickTerracotta
                                    )
                                }
                                HorizontalDivider(color = RuledLineColor.copy(alpha = 0.5f))

                                if (showOptions) {
                                    EntryOptionsDialog(
                                        title = "${item.workerName} (${item.workerRole})",
                                        amountText = CurrencyFormatter.formatInRupees(item.amount),
                                        onEdit = {
                                            showOptions = false
                                            selectedLabourToEdit = item
                                        },
                                        onDelete = {
                                            showOptions = false
                                            itemToDeleteConfirm = {
                                                notebookRepo.deleteLabourPayment(item.id)
                                                scope.launch {
                                                    val res = snackbarHostState.showSnackbar(
                                                        message = "లెక్క తీసేశాం",
                                                        actionLabel = "మళ్లీ పెట్టు",
                                                        duration = SnackbarDuration.Long
                                                    )
                                                    if (res == SnackbarResult.ActionPerformed) {
                                                        notebookRepo.restoreLabourPayment(item.id)
                                                    }
                                                }
                                            }
                                        },
                                        onDismiss = { showOptions = false }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = RuledLineColor)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Section 4: ఇతర ఖర్చు
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ఇతర ఖర్చు",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = ExpenseRed
                            )
                            Button(
                                onClick = { showAddExpense = true },
                                colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed, contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ఖర్చు", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (expenseList.isEmpty()) {
                            Text(
                                text = "— ఇంకా ఖర్చు ఏదీ లేదు —",
                                style = MaterialTheme.typography.bodyMedium,
                                color = InkSecondary.copy(alpha = 0.6f),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            expenseList.forEach { item ->
                                var showOptions by remember { mutableStateOf(false) }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showOptions = true }
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.category,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = InkPrimary
                                        )
                                        if (item.note.isNotBlank()) {
                                            Text(
                                                text = item.note,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = InkSecondary
                                            )
                                        }
                                    }
                                    Text(
                                        text = CurrencyFormatter.formatInRupees(item.amount),
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = ExpenseRed
                                    )
                                }
                                HorizontalDivider(color = RuledLineColor.copy(alpha = 0.5f))

                                if (showOptions) {
                                    EntryOptionsDialog(
                                        title = item.category,
                                        amountText = CurrencyFormatter.formatInRupees(item.amount),
                                        onEdit = {
                                            showOptions = false
                                            selectedExpenseToEdit = item
                                        },
                                        onDelete = {
                                            showOptions = false
                                            itemToDeleteConfirm = {
                                                notebookRepo.deleteExpense(item.id)
                                                scope.launch {
                                                    val res = snackbarHostState.showSnackbar(
                                                        message = "లెక్క తీసేశాం",
                                                        actionLabel = "మళ్లీ పెట్టు",
                                                        duration = SnackbarDuration.Long
                                                    )
                                                    if (res == SnackbarResult.ActionPerformed) {
                                                        notebookRepo.restoreExpense(item.id)
                                                    }
                                                }
                                            }
                                        },
                                        onDismiss = { showOptions = false }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Section 5: ఈ రోజు లెక్క (Daily Summary Box)
                        if (dailySummary != null) {
                            DailySummaryBox(summary = dailySummary!!)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Top Cumulative Work Summary Card (వచ్చింది, కూలీలకు, ఇతర ఖర్చు, మొత్తం ఖర్చు, మిగిలింది)
 */
@Composable
fun WorkCumulativeSummaryCard(
    summary: com.example.maapanipusthakam.domain.model.WorkSummary,
    isCompleted: Boolean,
    onCompleteClick: () -> Unit,
    onReopenClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PaperCard),
        border = BorderStroke(1.dp, DividerColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "మొత్తం పని లెక్క",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = InkPrimary
                )
                if (isCompleted) {
                    Surface(
                        color = ReceivedGreenLight,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "✓ పూర్తయ్యింది",
                            color = ReceivedGreen,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SummaryItem(label = "వచ్చింది", amount = summary.totalIncome, color = ReceivedGreen)
                SummaryItem(label = "కూలీలకు", amount = summary.labourTotal, color = BrickTerracotta)
                SummaryItem(label = "ఇతర ఖర్చు", amount = summary.otherExpenseTotal, color = ExpenseRed)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "మొత్తం ఖర్చు",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkSecondary
                    )
                    Text(
                        text = CurrencyFormatter.formatInRupees(summary.totalExpense),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = ExpenseRed
                    )
                }

                // Strictly labeled "మిగిలింది" (Remaining), never Profit
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = RemainingWarmLight,
                    border = BorderStroke(1.dp, RemainingWarm.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                        Text(
                            text = "మిగిలింది",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = RemainingWarm
                        )
                        Text(
                            text = CurrencyFormatter.formatInRupees(summary.remaining),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = RemainingWarm
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!isCompleted) {
                BigOutlinedButton(
                    text = "పని పూర్తయ్యింది",
                    icon = Icons.Default.CheckCircle,
                    borderColor = ReceivedGreen,
                    contentColor = ReceivedGreen,
                    onClick = onCompleteClick
                )
            } else {
                BigOutlinedButton(
                    text = "పని మళ్లీ మొదలు పెట్టు",
                    icon = Icons.Default.Refresh,
                    borderColor = BrickTerracotta,
                    contentColor = BrickTerracotta,
                    onClick = onReopenClick
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(label: String, amount: Long, color: Color) {
    Column {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = InkSecondary)
        Text(
            text = CurrencyFormatter.formatInRupees(amount),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = color
        )
    }
}

/**
 * Daily Summary box at the bottom of the page
 */
@Composable
fun DailySummaryBox(summary: com.example.maapanipusthakam.domain.model.DailySummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PaperCard),
        border = BorderStroke(1.dp, DividerColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "ఈ రోజు లెక్క",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = InkPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("వచ్చింది", color = InkSecondary, style = MaterialTheme.typography.bodyMedium)
                Text(
                    CurrencyFormatter.formatInRupees(summary.incomeTotal),
                    color = ReceivedGreen,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("కూలీలకు", color = InkSecondary, style = MaterialTheme.typography.bodyMedium)
                Text(
                    CurrencyFormatter.formatInRupees(summary.labourTotal),
                    color = BrickTerracotta,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("ఇతర ఖర్చు", color = InkSecondary, style = MaterialTheme.typography.bodyMedium)
                Text(
                    CurrencyFormatter.formatInRupees(summary.otherExpenseTotal),
                    color = ExpenseRed,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(modifier = Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("మొత్తం ఖర్చు", color = InkPrimary, fontWeight = FontWeight.Bold)
                Text(
                    CurrencyFormatter.formatInRupees(summary.totalExpense),
                    color = ExpenseRed,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("మిగిలింది", color = RemainingWarm, fontWeight = FontWeight.Bold)
                Text(
                    CurrencyFormatter.formatInRupees(summary.remaining),
                    color = RemainingWarm,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}
