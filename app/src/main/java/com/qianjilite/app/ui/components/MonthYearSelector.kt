package com.qianjilite.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MonthYearSelector(
    currentYear: Int,
    currentMonth: Int,
    availableYears: List<Int>,
    availableMonths: List<Int>,
    onYearChange: (Int) -> Unit,
    onMonthChange: (Int) -> Unit,
    onStatsClick: () -> Unit,
    onExportClick: () -> Unit,
    onExportAllClick: () -> Unit,
    onCategoryClick: () -> Unit,
    onBatchModeClick: () -> Unit,
    isBatchMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    var showYearPicker by remember { mutableStateOf(false) }
    
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        val currentIndex = availableMonths.indexOf(currentMonth)
                        if (currentIndex > 0) {
                            onMonthChange(availableMonths[currentIndex - 1])
                        } else {
                            val yearIndex = availableYears.indexOf(currentYear)
                            if (yearIndex > 0) {
                                onYearChange(availableYears[yearIndex - 1])
                                onMonthChange(availableYears.getOrNull(yearIndex - 1)?.let {
                                    availableMonths.lastOrNull() ?: 12
                                } ?: 12)
                            }
                        }
                    }) {
                        Icon(
                            Icons.Default.KeyboardArrowLeft,
                            contentDescription = "上个月",
                            tint = Color.White
                        )
                    }

                    Box {
                        TextButton(onClick = { showYearPicker = true }) {
                            Text(
                                text = "${currentYear}年",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        DropdownMenu(
                            expanded = showYearPicker,
                            onDismissRequest = { showYearPicker = false }
                        ) {
                            availableYears.forEach { year ->
                                DropdownMenuItem(
                                    text = { Text("${year}年") },
                                    onClick = {
                                        onYearChange(year)
                                        showYearPicker = false
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("更多...") },
                                onClick = {
                                    showYearPicker = false
                                }
                            )
                        }
                    }

                    IconButton(onClick = {
                        val currentIndex = availableMonths.indexOf(currentMonth)
                        if (currentIndex < availableMonths.size - 1) {
                            onMonthChange(availableMonths[currentIndex + 1])
                        } else {
                            val yearIndex = availableYears.indexOf(currentYear)
                            if (yearIndex < availableYears.size - 1) {
                                onYearChange(availableYears[yearIndex + 1])
                                onMonthChange(availableMonths.firstOrNull() ?: 1)
                            }
                        }
                    }) {
                        Icon(
                            Icons.Default.KeyboardArrowRight,
                            contentDescription = "下个月",
                            tint = Color.White
                        )
                    }
                }

                Row {
                    TextButton(onClick = onStatsClick) {
                        Text("统计", color = Color.White)
                    }
                    TextButton(onClick = onExportClick) {
                        Text("本月", color = Color.White)
                    }
                    TextButton(onClick = onExportAllClick) {
                        Text("全量", color = Color.White)
                    }
                    TextButton(onClick = onCategoryClick) {
                        Text("分类", color = Color.White)
                    }
                    TextButton(onClick = onBatchModeClick) {
                        Text(
                            text = if (isBatchMode) "完成" else "批量",
                            color = if (isBatchMode) Color.Yellow else Color.White
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                availableMonths.forEach { month ->
                    val isSelected = month == currentMonth
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color.White else Color.Transparent)
                            .clickable { onMonthChange(month) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${month}月",
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}
