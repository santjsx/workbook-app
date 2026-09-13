package com.example.maapanipusthakam.ui.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.maapanipusthakam.core.date.DateFormatter
import com.example.maapanipusthakam.core.update.GitHubUpdateManager
import com.example.maapanipusthakam.core.update.UpdateCheckResult
import com.example.maapanipusthakam.data.repository.BackupEnvelope
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
import com.example.maapanipusthakam.ui.components.BigButton
import com.example.maapanipusthakam.ui.components.BigOutlinedButton
import com.example.maapanipusthakam.ui.components.NotebookCard
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val app = remember { MaaPaniApp.instance }
    val backupRepo = remember { app.backupRepository }
    val scope = rememberCoroutineScope()

    var isLargeText by remember { mutableStateOf(app.isLargeTextEnabled) }
    var isPageAnim by remember { mutableStateOf(app.isPageAnimationEnabled) }

    // Backup dialog states
    var showBackupCreatedDialog by remember { mutableStateOf<String?>(null) }
    var showRestoreInputDialog by remember { mutableStateOf(false) }
    var validatedEnvelopeForRestore by remember { mutableStateOf<BackupEnvelope?>(null) }
    var restoreErrorMessage by remember { mutableStateOf<String?>(null) }

    // Update check states
    var isCheckingUpdate by remember { mutableStateOf(false) }
    var updateResultDialog by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "సదుపాయాలు",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp
            ),
            color = InkPrimary
        )
        Text(
            text = "లెక్కల భద్రత మరియు చూపు విధానం",
            style = MaterialTheme.typography.bodyMedium,
            color = InkSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ==================== SECTION 1: లెక్కల భద్రత (BACKUP & RESTORE) ====================
        Text(
            text = "లెక్కల భద్రత",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = BrickTerracotta
        )
        Spacer(modifier = Modifier.height(8.dp))

        NotebookCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "మీ పని లెక్కలు పోకుండా దాచుకోండి",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = InkPrimary
                )
                Text(
                    text = "ఫోన్ మారినా లేదా పోయినా లెక్కలు పోకుండా ఒక కాపీని వాట్సాప్‌లో లేదా ఫోన్‌లో దాచుకోవచ్చు.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                BigButton(
                    text = "లెక్కలు ఫోన్‌లో దాచుకో",
                    icon = Icons.Default.Backup,
                    containerColor = BrickTerracotta,
                    contentColor = Color.White,
                    onClick = {
                        scope.launch {
                            val backupText = backupRepo.createBackupJson()
                            showBackupCreatedDialog = backupText
                        }
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                BigOutlinedButton(
                    text = "పాత లెక్కలు వెనక్కి తెచ్చుకో",
                    icon = Icons.Default.Restore,
                    borderColor = BrickTerracotta,
                    contentColor = BrickTerracotta,
                    onClick = { showRestoreInputDialog = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ==================== SECTION 2: UX PREFERENCES ====================
        Text(
            text = "చూసే విధానం",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = BrickTerracotta
        )
        Spacer(modifier = Modifier.height(8.dp))

        NotebookCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.FormatSize,
                            contentDescription = null,
                            tint = BrickTerracotta,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "పెద్ద అక్షరాలు",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = InkPrimary
                            )
                            Text(
                                text = "చదవడానికి సులభంగా పెద్ద అక్షరాలు కనిపించేలా",
                                style = MaterialTheme.typography.bodySmall,
                                color = InkSecondary
                            )
                        }
                    }
                    Switch(
                        checked = isLargeText,
                        onCheckedChange = {
                            isLargeText = it
                            app.setLargeText(it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = BrickTerracotta
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = DividerColor)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Animation,
                            contentDescription = null,
                            tint = BrickTerracotta,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "పుస్తకం పేజీలు తిప్పేలా",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = InkPrimary
                            )
                            Text(
                                text = "నిజమైన పుస్తకం తిప్పినట్లుగా ఉండేలా",
                                style = MaterialTheme.typography.bodySmall,
                                color = InkSecondary
                            )
                        }
                    }
                    Switch(
                        checked = isPageAnim,
                        onCheckedChange = {
                            isPageAnim = it
                            app.setPageAnimation(it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = BrickTerracotta
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ==================== SECTION 3: అప్‌డేట్ (UPDATE) ====================
        Text(
            text = "కొత్త మార్పులు",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = BrickTerracotta
        )
        Spacer(modifier = Modifier.height(8.dp))

        NotebookCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "యాప్ వెర్షన్",
                            style = MaterialTheme.typography.bodyMedium,
                            color = InkSecondary
                        )
                        Text(
                            text = "వెర్షన్ ${GitHubUpdateManager.CURRENT_VERSION}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = InkPrimary
                        )
                    }

                    if (isCheckingUpdate) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = BrickTerracotta)
                    } else {
                        Button(
                            onClick = {
                                isCheckingUpdate = true
                                scope.launch {
                                    val result = GitHubUpdateManager.checkForUpdates()
                                    isCheckingUpdate = false
                                    when (result) {
                                        is UpdateCheckResult.UpdateAvailable -> {
                                            updateResultDialog = "కొత్త మార్పులు వచ్చాయి! (${result.version})\n\nవివరాలు: ${result.releaseNotes}"
                                        }
                                        is UpdateCheckResult.UpToDate -> {
                                            updateResultDialog = "మీ దగ్గర ఉన్నదే తాజా యాప్. ఏ కొత్త మార్పులూ లేవు."
                                        }
                                        is UpdateCheckResult.Error -> {
                                            updateResultDialog = result.message
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrickTerracotta,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.SystemUpdate, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("కొత్తవి వచ్చాయా చూడు", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ==================== SECTION 4: యాప్ గురించి (ABOUT) ====================
        NotebookCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "మా పని పుస్తకం",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = BrickTerracotta
                )
                Text(
                    text = "పని లెక్కలు సులభంగా",
                    style = MaterialTheme.typography.titleMedium,
                    color = InkSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "తాపీ మేస్త్రీలు, కాంట్రాక్టర్ల కోసం చేసిన సులువైన లెక్కల పుస్తకం.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = InkPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = ReceivedGreenLight,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "నెట్ (ఇంటర్నెట్) లేకపోయినా 100% పనిచేస్తుంది",
                        color = ReceivedGreen,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }

    // Backup Created Dialog
    if (showBackupCreatedDialog != null) {
        val backupContent = showBackupCreatedDialog!!
        AlertDialog(
            onDismissRequest = { showBackupCreatedDialog = null },
            title = {
                Text(
                    text = "లెక్కలు భద్రపరచబడ్డాయి ✓",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = ReceivedGreen
                )
            },
            text = {
                Column {
                    Text(
                        text = "మీ లెక్కల సమాచారం తయారయ్యింది. దీనిని కాపీ చేసి వాట్సాప్‌లో లేదా ఫోన్‌లో దాచుకోవచ్చు.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = backupContent.take(150) + "...",
                        onValueChange = {},
                        readOnly = true,
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("MaaPaniBackup", backupContent)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "లెక్కల కోడ్ కాపీ అయ్యింది ✓", Toast.LENGTH_SHORT).show()
                        showBackupCreatedDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrickTerracotta,
                        contentColor = Color.White
                    )
                ) {
                    Text("కాపీ చేయి", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showBackupCreatedDialog = null }) {
                    Text("మూసివేయి")
                }
            },
            containerColor = PaperCardElevated
        )
    }

    // Restore Input Dialog
    if (showRestoreInputDialog) {
        var inputBackupText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = {
                showRestoreInputDialog = false
                restoreErrorMessage = null
            },
            title = {
                Text(
                    text = "పాత లెక్కలు మళ్లీ తెచ్చుకో",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = BrickTerracotta
                )
            },
            text = {
                Column {
                    Text(
                        text = "ఇంతకు ముందు దాచుకున్న లెక్కల కోడ్‌ను ఇక్కడ పేస్ట్ చేయండి:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = inputBackupText,
                        onValueChange = {
                            inputBackupText = it
                            restoreErrorMessage = null
                        },
                        placeholder = { Text("ఇక్కడ పేస్ట్ చేయండి") },
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (restoreErrorMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = restoreErrorMessage!!,
                            color = ExpenseRed,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val envelope = backupRepo.validateBackup(inputBackupText.trim())
                        if (envelope != null) {
                            validatedEnvelopeForRestore = envelope
                            showRestoreInputDialog = false
                        } else {
                            restoreErrorMessage = "ఈ కోడ్ సరైనది కాదు. సరిగ్గా చూసి పేస్ట్ చేయండి."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrickTerracotta,
                        contentColor = Color.White
                    )
                ) {
                    Text("చూడు ✓", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showRestoreInputDialog = false }) {
                    Text("వెనక్కి")
                }
            },
            containerColor = PaperCardElevated
        )
    }

    // Restore Confirmation Dialog
    if (validatedEnvelopeForRestore != null) {
        val envelope = validatedEnvelopeForRestore!!
        AlertDialog(
            onDismissRequest = { validatedEnvelopeForRestore = null },
            title = {
                Text(
                    text = "పాత లెక్కలు పెట్టాలా?",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = ExpenseRed
                )
            },
            text = {
                Column {
                    Text(
                        text = "ఈ లెక్కలు ${DateFormatter.formatToTeluguDate(envelope.backupDate)} నాటివి.",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "ఇప్పుడున్న లెక్కల స్థానంలో ఈ పాత లెక్కలు వస్తాయి. పెట్టమంటారా?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkSecondary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            val res = backupRepo.restoreBackup(envelope)
                            validatedEnvelopeForRestore = null
                            if (res.isSuccess) {
                                Toast.makeText(context, "పాత లెక్కలు మళ్లీ వచ్చాయి ✓", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "లెక్కలు పెట్టడం కుదరలేదు.", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ExpenseRed,
                        contentColor = Color.White
                    )
                ) {
                    Text("అవును, పెట్టు ✓", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { validatedEnvelopeForRestore = null }) {
                    Text("వెనక్కి")
                }
            },
            containerColor = PaperCardElevated
        )
    }

    // Update Result Dialog
    if (updateResultDialog != null) {
        AlertDialog(
            onDismissRequest = { updateResultDialog = null },
            title = {
                Text(
                    text = "కొత్త మార్పులు",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = BrickTerracotta
                )
            },
            text = {
                Text(
                    text = updateResultDialog!!,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = { updateResultDialog = null },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrickTerracotta,
                        contentColor = Color.White
                    )
                ) {
                    Text("సరే", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = PaperCardElevated
        )
    }
}
