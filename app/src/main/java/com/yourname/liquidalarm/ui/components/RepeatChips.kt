package com.yourname.liquidalarm.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Chip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourname.liquidalarm.data.RepeatRule
import kotlinx.datetime.DayOfWeek

@Composable
fun RepeatChips(
    currentRule: RepeatRule,
    onRuleChange: (RepeatRule) -> Unit,
    modifier: Modifier = Modifier
) {
    val quickOptions = listOf(
        RepeatOption("Once", RepeatRule.Once, false),
        RepeatOption("Daily", RepeatRule.Daily, false),
        RepeatOption("Weekdays", RepeatRule.Weekdays, false),
        RepeatOption("Weekends", RepeatRule.Weekends, false)
    )

    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("Repeat", style = MaterialTheme.typography.titleMedium)
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))

        // Quick options
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            quickOptions.forEach { option ->
                val isSelected = isRuleEqual(currentRule, option.rule)
                Chip(
                    onClick = { onRuleChange(option.rule) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.ChipDefaults.chipColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = option.label,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSurface
                            )
                        )
                        if (isSelected) {
                            androidx.compose.material.icons.filled.Check(
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))

        // Custom days
        Text("Custom Days", style = MaterialTheme.typography.titleMedium)
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))

        DayOfWeek.values().forEach { day ->
            val isSelected = currentRule is RepeatRule.Weekly && day in currentRule.days
            val weekRule = if (currentRule is RepeatRule.Weekly) currentRule.days else emptySet<DayOfWeek>()
            val newDays = if (isSelected) weekRule - day else weekRule + day

            Chip(
                onClick = { onRuleChange(RepeatRule.Weekly(newDays)) },
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.ChipDefaults.chipColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = day.shortName,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurface
                        )
                    )
                    if (isSelected) {
                        androidx.compose.material.icons.filled.Check(
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Advanced options
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
        Text("Advanced", style = MaterialTheme.typography.titleMedium)
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))

        AdvancedRepeatOption(
            label = "Monthly (day 1-31)",
            isSelected = currentRule is RepeatRule.Monthly,
            onClick = { /* Show day picker */ }
        )
        AdvancedRepeatOption(
            label = "Yearly",
            isSelected = currentRule is RepeatRule.Yearly,
            onClick = { /* Show date picker */ }
        )
        AdvancedRepeatOption(
            label = "Every N days",
            isSelected = currentRule is RepeatRule.Interval,
            onClick = { /* Show interval picker */ }
        )
    }
}

@Composable
fun AdvancedRepeatOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Chip(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = androidx.compose.material3.ChipDefaults.chipColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurface
                )
            )
            if (isSelected) {
                androidx.compose.material.icons.filled.Check(
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private data class RepeatOption(
    val label: String,
    val rule: RepeatRule,
    val hasConfig: Boolean
)

private fun isRuleEqual(a: RepeatRule, b: RepeatRule): Boolean {
    return when {
        a is RepeatRule.Once && b is RepeatRule.Once -> true
        a is RepeatRule.Daily && b is RepeatRule.Daily -> true
        a is RepeatRule.Weekdays && b is RepeatRule.Weekdays -> true
        a is RepeatRule.Weekends && b is RepeatRule.Weekends -> true
        a is RepeatRule.Weekly && b is RepeatRule.Weekly -> a.days == b.days
        a is RepeatRule.Monthly && b is RepeatRule.Monthly -> a.dayOfMonth == b.dayOfMonth
        a is RepeatRule.Yearly && b is RepeatRule.Yearly -> a.monthDay == b.monthDay
        a is RepeatRule.Interval && b is RepeatRule.Interval -> a.days == b.days
        else -> false
    }
}
