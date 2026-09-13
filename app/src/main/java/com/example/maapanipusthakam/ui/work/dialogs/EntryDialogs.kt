package com.example.maapanipusthakam.ui.work.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
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
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.maapanipusthakam.core.currency.CurrencyFormatter
import com.example.maapanipusthakam.core.voice.rememberVoiceInputLauncher
import com.example.maapanipusthakam.domain.model.Worker
import com.example.maapanipusthakam.theme.BrickTerracotta
import com.example.maapanipusthakam.theme.DividerColor
import com.example.maapanipusthakam.theme.ExpenseRed
import com.example.maapanipusthakam.theme.InkPrimary
import com.example.maapanipusthakam.theme.InkSecondary
import com.example.maapanipusthakam.theme.PaperCardElevated
import com.example.maapanipusthakam.theme.ReceivedGreen
import com.example.maapanipusthakam.ui.components.BigButton

/**
 * Dialog for adding income (వచ్చిన డబ్బు) - Zero-typing & spoken Telugu
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddIncomeDialog(
    initialAmount: Long? = null,
    initialSource: String = "",
    initialReason: String = "",
    initialNote: String = "",
    isEditing: Boolean = false,
    onDismiss: () -> Unit,
    onSave: (amount: Long, source: String, reason: String, note: String) -> Unit
) {
    var amountText by remember { mutableStateOf(initialAmount?.toString() ?: "") }
    var source by remember { mutableStateOf(initialSource) }
    var reason by remember { mutableStateOf(initialReason) }
    var note by remember { mutableStateOf(initialNote) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    val payerPresets = listOf("🏠 ఇంటి యజమాని", "👷 పెద్ద మేస్త్రీ", "🏢 ఇంజనీర్ / మేనేజర్", "👤 ఇతర మనిషి")
    val reasonPresets = listOf("💰 పని మొదలు అడ్వాన్స్", "📅 శనివారం వారపు లెక్క", "🧱 సామాను ఖర్చుల కోసం", "🏁 ముగింపు లెక్క")
    val amountPresets = listOf(1000L, 2000L, 5000L, 10000L, 20000L)

    val noteVoiceLauncher = rememberVoiceInputLauncher { note = it }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = PaperCardElevated,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEditing) "వచ్చిన డబ్బు మార్చు" else "వచ్చిన డబ్బు",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = ReceivedGreen
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "మూసివేయి")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "ఎంత డబ్బు వచ్చింది? *",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = InkPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        val filtered = it.filter { ch -> ch.isDigit() }
                        amountText = filtered
                        if (filtered.isNotBlank()) errorMessage = null
                    },
                    prefix = {
                        Text("₹ ", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
                    },
                    placeholder = { Text("0") },
                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = InkPrimary
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Quick Rupee presets for zero-typing
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "డబ్బు కలపడానికి నొక్కండి:",
                    style = MaterialTheme.typography.bodySmall,
                    color = InkSecondary
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    amountPresets.forEach { amt ->
                        SuggestionChip(
                            onClick = {
                                val current = CurrencyFormatter.parseAmount(amountText) ?: 0L
                                amountText = (current + amt).toString()
                                errorMessage = null
                            },
                            label = {
                                Text("+${CurrencyFormatter.formatInRupees(amt)}", fontWeight = FontWeight.Bold)
                            },
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = ReceivedGreen.copy(alpha = 0.08f)),
                            border = BorderStroke(1.dp, ReceivedGreen.copy(alpha = 0.3f))
                        )
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = ExpenseRed,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Payer Presets
                Text(
                    text = "ఎవరిచ్చారు? (ఒక్కటి ఎంచుకోండి):",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = InkPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    payerPresets.forEach { p ->
                        val clean = p.substringAfter(" ")
                        val isSelected = source == clean || source == p
                        FilterChip(
                            selected = isSelected,
                            onClick = { source = clean },
                            label = { Text(p, fontSize = 13.sp, fontWeight = FontWeight.Medium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ReceivedGreen,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Reason Presets
                Text(
                    text = "ఏం కోసం ఇచ్చారు?:",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = InkPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    reasonPresets.forEach { r ->
                        val clean = r.substringAfter(" ")
                        val isSelected = reason == clean || reason == r
                        FilterChip(
                            selected = isSelected,
                            onClick = { reason = clean },
                            label = { Text(r, fontSize = 13.sp, fontWeight = FontWeight.Medium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ReceivedGreen,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "గుర్తు కోసం మరిన్ని వివరాలు:",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = InkSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        placeholder = { Text("చిన్న గమనిక") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = noteVoiceLauncher) {
                        Icon(Icons.Default.Mic, contentDescription = "నోటితో చెప్పండి", tint = ReceivedGreen)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                BigButton(
                    text = if (isEditing) "మార్చు" else "రాసుకో",
                    icon = Icons.Default.Check,
                    containerColor = ReceivedGreen,
                    contentColor = Color.White,
                    enabled = !isSaving,
                    onClick = {
                        val parsed = CurrencyFormatter.parseAmount(amountText)
                        if (parsed == null || parsed <= 0) {
                            errorMessage = "ఎంత మొత్తమో రాయండి లేదా పైన బటన్ నొక్కండి."
                            return@BigButton
                        }
                        isSaving = true
                        onSave(parsed, source, reason, note)
                    }
                )
            }
        }
    }
}

/**
 * Dialog for adding labour payment (కూలీల డబ్బు) - Zero-typing & spoken Telugu
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddLabourDialog(
    recentWorkers: List<Worker> = emptyList(),
    initialWorkerName: String = "",
    initialWorkerRole: String = "కూలీ",
    initialAmount: Long? = null,
    initialNote: String = "",
    isEditing: Boolean = false,
    onDismiss: () -> Unit,
    onSave: (name: String, role: String, amount: Long, note: String) -> Unit
) {
    var workerName by remember { mutableStateOf(initialWorkerName) }
    var workerRole by remember { mutableStateOf(initialWorkerRole) }
    var amountText by remember { mutableStateOf(initialAmount?.toString() ?: "") }
    var note by remember { mutableStateOf(initialNote) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    val commonNames = listOf("రవి", "రాము", "శంకర్", "సురేష్", "వెంకట్", "లక్ష్మి", "సత్యం", "నాగరాజు", "మంగ")
    val rolePresets = listOf("తాపీ మేస్త్రీ", "మేస్త్రీ", "కూలీ (మగ)", "కూలీ (ఆడ)", "సిమెంట్ పని", "సహాయకుడు")
    val wagePresets = listOf(500L, 700L, 800L, 1000L, 1200L, 1500L)

    val nameVoiceLauncher = rememberVoiceInputLauncher { spoken ->
        workerName = spoken
        errorMessage = null
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = PaperCardElevated,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEditing) "కూలీ లెక్క మార్చు" else "కూలీ డబ్బు",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = BrickTerracotta
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "మూసివేయి")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Worker Name selection
                Text(
                    text = "కూలీ / మేస్త్రీ పేరు *",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = InkPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = workerName,
                        onValueChange = {
                            workerName = it
                            if (it.isNotBlank()) errorMessage = null
                        },
                        placeholder = { Text("ఉదా: రవి, శంకర్") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = nameVoiceLauncher) {
                        Icon(Icons.Default.Mic, contentDescription = "పేరు చెప్పండి", tint = BrickTerracotta, modifier = Modifier.size(28.dp))
                    }
                }

                // Quick voice button
                OutlinedButton(
                    onClick = nameVoiceLauncher,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = BrickTerracotta.copy(alpha = 0.06f),
                        contentColor = BrickTerracotta
                    ),
                    border = BorderStroke(1.dp, BrickTerracotta.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("🎙️ నోటితో పేరు చెప్పండి", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                // Common Names Chips
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "తరచూ పనిచేసే వారి పేర్లు:",
                    style = MaterialTheme.typography.bodySmall,
                    color = InkSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Show saved workers first, or defaults
                    val namesToShow = (recentWorkers.map { it.name } + commonNames).distinct().take(8)
                    namesToShow.forEach { n ->
                        FilterChip(
                            selected = workerName.trim().equals(n.trim(), ignoreCase = true),
                            onClick = {
                                workerName = n
                                errorMessage = null
                            },
                            label = { Text(n, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrickTerracotta,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Role Chips
                Text(
                    text = "ఏం పని చేశారు?:",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = InkPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    rolePresets.forEach { r ->
                        FilterChip(
                            selected = workerRole == r,
                            onClick = { workerRole = r },
                            label = { Text(r, fontSize = 13.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrickTerracotta,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Amount
                Text(
                    text = "ఎంత కూలీ ఇచ్చారు? *",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = InkPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        val filtered = it.filter { ch -> ch.isDigit() }
                        amountText = filtered
                        if (filtered.isNotBlank()) errorMessage = null
                    },
                    prefix = {
                        Text("₹ ", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
                    },
                    placeholder = { Text("0") },
                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = InkPrimary
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Quick wage presets
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "రోజువారీ కూలీ బటన్లు:",
                    style = MaterialTheme.typography.bodySmall,
                    color = InkSecondary
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    wagePresets.forEach { wage ->
                        SuggestionChip(
                            onClick = {
                                amountText = wage.toString()
                                errorMessage = null
                            },
                            label = {
                                Text("₹${wage}", fontWeight = FontWeight.Bold)
                            },
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = BrickTerracotta.copy(alpha = 0.08f)),
                            border = BorderStroke(1.dp, BrickTerracotta.copy(alpha = 0.3f))
                        )
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = ExpenseRed,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                BigButton(
                    text = if (isEditing) "మార్చు" else "రాసుకో",
                    icon = Icons.Default.Check,
                    containerColor = BrickTerracotta,
                    contentColor = Color.White,
                    enabled = !isSaving,
                    onClick = {
                        if (workerName.isBlank()) {
                            errorMessage = "కూలీ పేరు ఎంచుకోండి లేదా పైన మైక్ నొక్కి చెప్పండి."
                            return@BigButton
                        }
                        val parsed = CurrencyFormatter.parseAmount(amountText)
                        if (parsed == null || parsed <= 0) {
                            errorMessage = "ఎంత కూలీ ఇచ్చారో రాయండి లేదా కూలీ బటన్ నొక్కండి."
                            return@BigButton
                        }
                        isSaving = true
                        onSave(workerName.trim(), workerRole, parsed, note)
                    }
                )
            }
        }
    }
}

/**
 * Dialog for adding expenses (ఇతర ఖర్చు) - Zero-typing & spoken Telugu
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddExpenseDialog(
    initialCategory: String = "సిమెంట్",
    initialAmount: Long? = null,
    initialNote: String = "",
    isEditing: Boolean = false,
    onDismiss: () -> Unit,
    onSave: (category: String, amount: Long, note: String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var customCategoryText by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf(initialAmount?.toString() ?: "") }
    var note by remember { mutableStateOf(initialNote) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    val expenseCategories = listOf(
        "🧱 ఇటుకలు",
        "⚪ సిమెంట్",
        "🏖️ ఇసుక",
        "🪨 కంకర",
        "🪵 కర్రలు / సెంట్రింగ్",
        "🔩 రాడ్లు / ఇనుము",
        "🚚 బండి కిరాయి",
        "☕ టీ / తిండి",
        "💧 నీళ్ల ట్యాంకర్",
        "🔨 పనిముట్లు",
        "⚡ కరెంట్ / మోటార్",
        "➕ ఇతర ఖర్చు"
    )

    val expenseAmountPresets = listOf(100L, 200L, 500L, 1000L, 2000L, 5000L)

    val customCatVoiceLauncher = rememberVoiceInputLauncher { customCategoryText = it }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = PaperCardElevated,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEditing) "ఖర్చు మార్చు" else "ఇతర ఖర్చు",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = ExpenseRed
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "మూసివేయి")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "ఏం సామాను ఖర్చు? (నొక్కండి):",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = InkPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    expenseCategories.forEach { cat ->
                        val cleanCat = cat.substringAfter(" ")
                        val isSelected = selectedCategory == cleanCat || selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cleanCat },
                            label = { Text(cat, fontSize = 13.sp, fontWeight = FontWeight.Medium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ExpenseRed,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                if (selectedCategory == "ఇతర ఖర్చు" || selectedCategory == "ఇతర") {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = customCategoryText,
                            onValueChange = { customCategoryText = it },
                            placeholder = { Text("ఏం ఖర్చు? (ఉదా: పెయింట్, పైపులు)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(onClick = customCatVoiceLauncher) {
                            Icon(Icons.Default.Mic, contentDescription = "నోటితో చెప్పండి", tint = ExpenseRed)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "ఎంత ఖర్చయ్యింది? *",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = InkPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        val filtered = it.filter { ch -> ch.isDigit() }
                        amountText = filtered
                        if (filtered.isNotBlank()) errorMessage = null
                    },
                    prefix = {
                        Text("₹ ", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
                    },
                    placeholder = { Text("0") },
                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = InkPrimary
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Quick expense amount buttons
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "డబ్బు కలపడానికి నొక్కండి:",
                    style = MaterialTheme.typography.bodySmall,
                    color = InkSecondary
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    expenseAmountPresets.forEach { amt ->
                        SuggestionChip(
                            onClick = {
                                val current = CurrencyFormatter.parseAmount(amountText) ?: 0L
                                amountText = (current + amt).toString()
                                errorMessage = null
                            },
                            label = {
                                Text("+${CurrencyFormatter.formatInRupees(amt)}", fontWeight = FontWeight.Bold)
                            },
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = ExpenseRed.copy(alpha = 0.08f)),
                            border = BorderStroke(1.dp, ExpenseRed.copy(alpha = 0.3f))
                        )
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = ExpenseRed,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "గుర్తు కోసం చిన్న గమనిక:",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = InkSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    placeholder = { Text("ఉదా: 50 బస్తాలు, 2 ట్రాక్టర్లు") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                BigButton(
                    text = if (isEditing) "మార్చు" else "రాసుకో",
                    icon = Icons.Default.Check,
                    containerColor = ExpenseRed,
                    contentColor = Color.White,
                    enabled = !isSaving,
                    onClick = {
                        val parsed = CurrencyFormatter.parseAmount(amountText)
                        if (parsed == null || parsed <= 0) {
                            errorMessage = "ఎంత ఖర్చయ్యిందో రాయండి లేదా బటన్ నొక్కండి."
                            return@BigButton
                        }
                        val finalCategory = if (selectedCategory == "ఇతర ఖర్చు" || selectedCategory == "ఇతర") {
                            customCategoryText.trim().ifEmpty { "ఇతర ఖర్చు" }
                        } else {
                            selectedCategory
                        }
                        isSaving = true
                        onSave(finalCategory, parsed, note)
                    }
                )
            }
        }
    }
}

/**
 * Entry Options Dialog when user taps on an existing entry:
 * "లెక్క మార్చాలా?" -> [మార్చు] [తీసేయి] [వద్దు]
 */
@Composable
fun EntryOptionsDialog(
    title: String,
    amountText: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = PaperCardElevated,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "ఈ లెక్కను మార్చాలా?",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = InkPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = InkSecondary
                )
                Text(
                    text = amountText,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = BrickTerracotta,
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                BigButton(
                    text = "మార్చు",
                    icon = Icons.Default.Edit,
                    containerColor = BrickTerracotta,
                    contentColor = Color.White,
                    onClick = onEdit
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onDelete,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ExpenseRed),
                    border = BorderStroke(1.5.dp, ExpenseRed),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = ExpenseRed, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("తీసేయి 🗑️", color = ExpenseRed, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, DividerColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("వెనక్కి", color = InkPrimary)
                }
            }
        }
    }
}
