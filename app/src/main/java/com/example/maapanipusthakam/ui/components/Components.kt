package com.example.maapanipusthakam.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.example.maapanipusthakam.core.currency.CurrencyFormatter
import com.example.maapanipusthakam.core.voice.rememberVoiceInputLauncher
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maapanipusthakam.theme.BrickTerracotta
import com.example.maapanipusthakam.theme.DividerColor
import com.example.maapanipusthakam.theme.ExpenseRed
import com.example.maapanipusthakam.theme.InkPrimary
import com.example.maapanipusthakam.theme.InkSecondary
import com.example.maapanipusthakam.theme.MarginLineColor
import com.example.maapanipusthakam.theme.PaperBackground
import com.example.maapanipusthakam.theme.PaperCard
import com.example.maapanipusthakam.theme.PaperCardElevated
import com.example.maapanipusthakam.theme.RuledLineColor

/**
 * Physical Notebook Ledger Background
 * Draws soft horizontal ruled lines and a subtle red margin line on the left
 */
fun Modifier.ruledNotebookBackground(): Modifier = this.drawBehind {
    val lineHeightPx = 40.dp.toPx()
    val marginX = 48.dp.toPx()

    // Draw vertical red margin guideline
    drawLine(
        color = MarginLineColor.copy(alpha = 0.5f),
        start = Offset(marginX, 0f),
        end = Offset(marginX, size.height),
        strokeWidth = 1.5.dp.toPx()
    )

    // Draw horizontal ruled lines
    var currentY = lineHeightPx
    while (currentY < size.height) {
        drawLine(
            color = RuledLineColor.copy(alpha = 0.7f),
            start = Offset(0f, currentY),
            end = Offset(size.width, currentY),
            strokeWidth = 1.dp.toPx()
        )
        currentY += lineHeightPx
    }
}

/**
 * Standard notebook card container
 */
@Composable
fun NotebookCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = PaperCardElevated,
    borderColor: Color = DividerColor,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        content()
    }
}

/**
 * Elderly-friendly large action button with Icon AND Text (Section 80: never icon-only!)
 */
@Composable
fun BigButton(
    text: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = BrickTerracotta,
    contentColor: Color = Color.White,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.5f),
            disabledContentColor = contentColor.copy(alpha = 0.7f)
        ),
        enabled = enabled
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                text = text,
                color = contentColor,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            )
        }
    }
}

/**
 * Outlined variant for secondary actions
 */
@Composable
fun BigOutlinedButton(
    text: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    borderColor: Color = BrickTerracotta,
    contentColor: Color = BrickTerracotta
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 52.dp),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.5.dp, borderColor),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = contentColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = contentColor,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
        }
    }
}

/**
 * Voice input button for low-literacy users who cannot type
 */
@Composable
fun VoiceInputButton(
    onResult: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "🎙️ నోటితో చెప్పండి"
) {
    val launchVoice = rememberVoiceInputLauncher(onResult = onResult)
    OutlinedButton(
        onClick = launchVoice,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = BrickTerracotta.copy(alpha = 0.08f),
            contentColor = BrickTerracotta
        ),
        border = BorderStroke(1.5.dp, BrickTerracotta.copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
    ) {
        Icon(
            Icons.Default.Mic,
            contentDescription = "మైక్",
            tint = BrickTerracotta,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            color = BrickTerracotta,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}

/**
 * Fast Rupee chips so user doesn't need to type amounts
 */
@Composable
fun QuickAmountBar(
    presets: List<Long> = listOf(100L, 500L, 800L, 1000L, 2000L),
    onAmountSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        presets.forEach { amt ->
            SuggestionChip(
                onClick = { onAmountSelected(amt) },
                label = {
                    Text(
                        text = "+${CurrencyFormatter.formatInRupees(amt)}",
                        fontWeight = FontWeight.Bold,
                        color = InkPrimary
                    )
                },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = PaperCardElevated
                ),
                border = BorderStroke(1.dp, DividerColor),
                shape = RoundedCornerShape(10.dp)
            )
        }
    }
}

/**
 * High-contrast dark charcoal snackbar with crisp white text
 */
@Composable
fun StyledAppSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier
) {
    Snackbar(
        snackbarData = snackbarData,
        modifier = modifier.padding(16.dp),
        containerColor = Color(0xFF2C2724),
        contentColor = Color(0xFFFFFDF9),
        actionColor = Color(0xFF81C784),
        actionContentColor = Color(0xFF81C784),
        shape = RoundedCornerShape(14.dp)
    )
}

/**
 * Simple Telugu confirmation dialog
 */
@Composable
fun SimpleConfirmDialog(
    title: String,
    message: String = "",
    confirmButtonText: String,
    dismissButtonText: String = "వెనక్కి",
    isDestructive: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = InkPrimary
            )
        },
        text = if (message.isNotBlank()) {
            {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = InkSecondary
                )
            }
        } else null,
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDestructive) ExpenseRed else BrickTerracotta
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = confirmButtonText,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = dismissButtonText,
                    style = MaterialTheme.typography.titleMedium,
                    color = InkSecondary
                )
            }
        },
        containerColor = PaperCardElevated,
        shape = RoundedCornerShape(20.dp)
    )
}
