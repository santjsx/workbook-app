package com.example.maapanipusthakam.ui.work

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maapanipusthakam.MaaPaniApp
import com.example.maapanipusthakam.core.date.DateFormatter
import com.example.maapanipusthakam.core.voice.rememberVoiceInputLauncher
import com.example.maapanipusthakam.theme.BrickTerracotta
import com.example.maapanipusthakam.theme.DividerColor
import com.example.maapanipusthakam.theme.ExpenseRed
import com.example.maapanipusthakam.theme.InkPrimary
import com.example.maapanipusthakam.theme.InkSecondary
import com.example.maapanipusthakam.theme.PaperBackground
import com.example.maapanipusthakam.theme.PaperCardElevated
import com.example.maapanipusthakam.ui.components.BigButton
import com.example.maapanipusthakam.ui.components.SimpleConfirmDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NewWorkScreen(
    onNavigateBack: () -> Unit,
    onWorkCreated: (workId: Long) -> Unit
) {
    val workRepo = remember { MaaPaniApp.instance.workRepository }
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showBackConfirm by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    val nameVoiceLauncher = rememberVoiceInputLauncher { spoken ->
        name = spoken
        errorMessage = null
    }

    val locationVoiceLauncher = rememberVoiceInputLauncher { spoken ->
        location = spoken
    }

    val ownerVoiceLauncher = rememberVoiceInputLauncher { spoken ->
        ownerName = spoken
    }

    val notesVoiceLauncher = rememberVoiceInputLauncher { spoken ->
        notes = spoken
    }

    val workNamePresets = listOf(
        "🏠 కొత్త ఇల్లు",
        "🏗️ స్లాబ్ పని",
        "🧱 గోడ పని",
        "🖌️ ప్లాస్టరింగ్",
        "🪵 సెంట్రింగ్",
        "🛠️ రిపేర్ పని",
        "🧱 టైల్స్ పని",
        "🧱 కాంపౌండ్ గోడ"
    )

    fun handleBack() {
        if (name.isNotBlank() || location.isNotBlank() || ownerName.isNotBlank()) {
            showBackConfirm = true
        } else {
            onNavigateBack()
        }
    }

    if (showBackConfirm) {
        SimpleConfirmDialog(
            title = "ఇంకా పని రాయలేదు.",
            message = "వెనక్కి వెళ్లిపోవాలా?",
            confirmButtonText = "వెళ్లిపో",
            dismissButtonText = "ఇక్కడే ఉండు",
            onConfirm = {
                showBackConfirm = false
                onNavigateBack()
            },
            onDismiss = { showBackConfirm = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "కొత్త పని రాయండి",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = InkPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { handleBack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "వెనక్కి",
                            tint = InkPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperBackground)
            )
        },
        containerColor = PaperBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "ఏం పని మొదలుపెడుతున్నారు?",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = BrickTerracotta
            )
            Text(
                text = "కింద ఉన్న పేరు నొక్కండి లేదా మైక్ నొక్కి నోటితో చెప్పండి",
                style = MaterialTheme.typography.bodyMedium,
                color = InkSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Preset Chips
            Text(
                text = "ఏ పని? (ఒక్కటి ఎంచుకోండి):",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = InkPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                workNamePresets.forEach { preset ->
                    val cleanPreset = preset.substringAfter(" ")
                    val isSelected = name == cleanPreset || name == preset
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            name = cleanPreset
                            errorMessage = null
                        },
                        label = { Text(preset, fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrickTerracotta,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "పని జరుగుతున్న చోటు / పేరు *",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = InkPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (it.isNotBlank()) errorMessage = null
                    },
                    placeholder = { Text("ఉదా: రమేష్ ఇల్లు, కొత్త ఇల్లు") },
                    textStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = nameVoiceLauncher,
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = "నోటితో చెప్పండి",
                        tint = BrickTerracotta,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

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
                    .padding(top = 6.dp)
            ) {
                Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("🎙️ నోటితో పని పేరు చెప్పండి", fontWeight = FontWeight.Bold)
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = ExpenseRed,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "ఊరు / ఏరియా పేరు:",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = InkPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    placeholder = { Text("ఉదా: మియాపూర్, కొత్తగూడెం") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = locationVoiceLauncher,
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = "ఊరు పేరు చెప్పండి",
                        tint = BrickTerracotta,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "ఇంటి యజమాని పేరు:",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = InkPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = ownerName,
                    onValueChange = { ownerName = it },
                    placeholder = { Text("యజమాని పేరు") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = ownerVoiceLauncher,
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = "యజమాని పేరు చెప్పండి",
                        tint = BrickTerracotta,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "ఫోన్ నంబర్:",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = InkPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                placeholder = { Text("ఫోన్ నంబర్") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "గుర్తు కోసం చిన్న వివరాలు:",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = InkPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("ఒప్పందం, రేటు, రోజువారీ వివరాలు") },
                    maxLines = 3,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = notesVoiceLauncher,
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = "నోటితో చెప్పండి",
                        tint = BrickTerracotta,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            BigButton(
                text = "పని మొదలు పెట్టు",
                icon = Icons.Default.Check,
                containerColor = BrickTerracotta,
                contentColor = Color.White,
                enabled = !isSaving,
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = "ఏదైనా ఒక పని పేరు ఎంచుకోండి లేదా రాయండి."
                        return@BigButton
                    }
                    isSaving = true
                    scope.launch {
                        val newId = workRepo.createWork(
                            name = name,
                            location = location,
                            ownerName = ownerName,
                            phone = phone,
                            notes = notes,
                            startDate = DateFormatter.todayEpochDay()
                        )
                        onWorkCreated(newId)
                    }
                }
            )
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
