package com.qianjilite.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.qianjilite.app.data.model.Category
import com.qianjilite.app.data.model.Transaction
import com.qianjilite.app.data.model.TransactionType
import com.qianjilite.app.ui.theme.ExpenseColor
import com.qianjilite.app.ui.theme.IncomeColor
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    expenseCategories: List<Category>,
    incomeCategories: List<Category>,
    existingTransaction: Transaction? = null,
    onDismiss: () -> Unit,
    onSave: (TransactionType, Double, String, String, Long) -> Unit,
    onAddCategory: (String, TransactionType) -> Unit
) {
    var type by remember { mutableStateOf(existingTransaction?.type ?: TransactionType.EXPENSE) }
    var amount by remember { mutableStateOf(existingTransaction?.amount?.toString() ?: "") }
    var selectedCategory by remember { mutableStateOf(existingTransaction?.category ?: expenseCategories.first().name) }
    var note by remember { mutableStateOf(existingTransaction?.note ?: "") }
    var selectedTimestamp by remember { mutableStateOf(existingTransaction?.timestamp ?: System.currentTimeMillis()) }
    
    var showCategoryPicker by remember { mutableStateOf(false) }
    var showDateTimePicker by remember { mutableStateOf(false) }
    var showAddCategory by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    val categories = if (type == TransactionType.EXPENSE) expenseCategories else incomeCategories
    val dateFormat = SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault())

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (existingTransaction != null) "编辑账单" else "记账",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "关闭")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = type == TransactionType.EXPENSE,
                        onClick = {
                            type = TransactionType.EXPENSE
                            selectedCategory = expenseCategories.firstOrNull()?.name ?: ""
                        },
                        label = { Text("支出") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ExpenseColor.copy(alpha = 0.2f),
                            selectedLabelColor = ExpenseColor
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = type == TransactionType.INCOME,
                        onClick = {
                            type = TransactionType.INCOME
                            selectedCategory = incomeCategories.firstOrNull()?.name ?: ""
                        },
                        label = { Text("收入") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IncomeColor.copy(alpha = 0.2f),
                            selectedLabelColor = IncomeColor
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("金额") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    prefix = { Text("¥ ") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = { },
                    label = { Text("分类") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCategoryPicker = true },
                    enabled = false,
                    trailingIcon = {
                        IconButton(onClick = { showCategoryPicker = true }) {
                            Icon(Icons.Default.Add, contentDescription = "选择分类")
                        }
                    }
                )

                if (showCategoryPicker) {
                    AlertDialog(
                        onDismissRequest = { showCategoryPicker = false },
                        title = { Text("选择分类") },
                        text = {
                            Column {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(3),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.height(200.dp)
                                ) {
                                    items(categories) { category ->
                                        FilterChip(
                                            selected = category.name == selectedCategory,
                                            onClick = {
                                                selectedCategory = category.name
                                                showCategoryPicker = false
                                            },
                                            label = { Text(category.name, fontSize = 12.sp) }
                                        )
                                    }
                                }
                                TextButton(
                                    onClick = {
                                        showCategoryPicker = false
                                        showAddCategory = true
                                    },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("添加分类")
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showCategoryPicker = false }) {
                                Text("关闭")
                            }
                        }
                    )
                }

                if (showAddCategory) {
                    AlertDialog(
                        onDismissRequest = { showAddCategory = false },
                        title = { Text("添加分类") },
                        text = {
                            Column {
                                OutlinedTextField(
                                    value = newCategoryName,
                                    onValueChange = { newCategoryName = it },
                                    label = { Text("分类名称") },
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    FilterChip(
                                        selected = type == TransactionType.EXPENSE,
                                        onClick = { type = TransactionType.EXPENSE },
                                        label = { Text("支出") }
                                    )
                                    FilterChip(
                                        selected = type == TransactionType.INCOME,
                                        onClick = { type = TransactionType.INCOME },
                                        label = { Text("收入") }
                                    )
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    if (newCategoryName.isNotBlank()) {
                                        onAddCategory(newCategoryName, type)
                                        newCategoryName = ""
                                        showAddCategory = false
                                    }
                                }
                            ) {
                                Text("添加")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showAddCategory = false }) {
                                Text("取消")
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("备注（可选）") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = dateFormat.format(Date(selectedTimestamp)),
                    onValueChange = { },
                    label = { Text("时间") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDateTimePicker = true },
                    enabled = false,
                    trailingIcon = {
                        IconButton(onClick = { showDateTimePicker = true }) {
                            Icon(Icons.Default.Add, contentDescription = "选择时间")
                        }
                    }
                )

                if (showDateTimePicker) {
                    DateTimePickerDialog(
                        initialTimestamp = selectedTimestamp,
                        onDismiss = { showDateTimePicker = false },
                        onConfirm = { timestamp ->
                            selectedTimestamp = timestamp
                            showDateTimePicker = false
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val amountValue = amount.toDoubleOrNull() ?: 0.0
                        if (amountValue > 0 && selectedCategory.isNotBlank()) {
                            onSave(type, amountValue, selectedCategory, note, selectedTimestamp)
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (type == TransactionType.EXPENSE) ExpenseColor else IncomeColor
                    )
                ) {
                    Text("保存")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerDialog(
    initialTimestamp: Long,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    val calendar = remember { Calendar.getInstance().apply { timeInMillis = initialTimestamp } }
    var year by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }
    var month by remember { mutableIntStateOf(calendar.get(Calendar.MONTH)) }
    var day by remember { mutableIntStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    var hour by remember { mutableIntStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var minute by remember { mutableIntStateOf(calendar.get(Calendar.MINUTE)) }

    val timePickerState = rememberTimePickerState(
        initialHour = hour,
        initialMinute = minute
    )

    var showDatePicker by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (showDatePicker) "选择日期" else "选择时间") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showDatePicker) {
                    CalendarDateSelector(
                        initialTimestamp = initialTimestamp,
                        onDateSelected = { timestamp ->
                            val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
                            year = cal.get(Calendar.YEAR)
                            month = cal.get(Calendar.MONTH)
                            day = cal.get(Calendar.DAY_OF_MONTH)
                        }
                    )
                } else {
                    TimePicker(state = timePickerState)
                    hour = timePickerState.hour
                    minute = timePickerState.minute
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (showDatePicker) {
                        showDatePicker = false
                    } else {
                        calendar.set(year, month, day, hour, minute)
                        onConfirm(calendar.timeInMillis)
                    }
                }
            ) {
                Text(if (showDatePicker) "下一步" else "确定")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                if (!showDatePicker) {
                    showDatePicker = true
                } else {
                    onDismiss()
                }
            }) {
                Text(if (showDatePicker) "取消" else "返回")
            }
        }
    )
}

@Composable
fun CalendarDateSelector(
    initialTimestamp: Long,
    onDateSelected: (Long) -> Unit
) {
    val calendar = remember { Calendar.getInstance().apply { timeInMillis = initialTimestamp } }
    var selectedYear by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableIntStateOf(calendar.get(Calendar.MONTH)) }
    var selectedDay by remember { mutableIntStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }

    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val weekDays = listOf("日", "一", "二", "三", "四", "五", "六")

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                if (selectedMonth > 0) {
                    selectedMonth--
                } else {
                    selectedMonth = 11
                    selectedYear--
                }
                val maxDay = Calendar.getInstance().apply { set(selectedYear, selectedMonth, 1) }.getActualMaximum(Calendar.DAY_OF_MONTH)
                if (selectedDay > maxDay) selectedDay = maxDay
            }) {
                Text("<")
            }
            Text("${selectedYear}年${selectedMonth + 1}月", style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = {
                if (selectedMonth < 11) {
                    selectedMonth++
                } else {
                    selectedMonth = 0
                    selectedYear++
                }
                val maxDay = Calendar.getInstance().apply { set(selectedYear, selectedMonth, 1) }.getActualMaximum(Calendar.DAY_OF_MONTH)
                if (selectedDay > maxDay) selectedDay = maxDay
            }) {
                Text(">")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            weekDays.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (day == "日" || day == "六") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        val firstDayOfMonth = Calendar.getInstance().apply {
            set(selectedYear, selectedMonth, 1)
        }.get(Calendar.DAY_OF_WEEK) - 1

        val daysInMonth = Calendar.getInstance().apply {
            set(selectedYear, selectedMonth, 1)
        }.getActualMaximum(Calendar.DAY_OF_MONTH)

        val totalCells = firstDayOfMonth + daysInMonth
        val rows = (totalCells + 6) / 7

        Column {
            repeat(rows) { row ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    repeat(7) { col ->
                        val cellIndex = row * 7 + col
                        val day = cellIndex - firstDayOfMonth + 1
                        if (day in 1..daysInMonth) {
                            val isSelected = day == selectedDay
                            val isToday = selectedYear == currentYear &&
                                    selectedMonth == Calendar.getInstance().get(Calendar.MONTH) &&
                                    day == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isSelected -> MaterialTheme.colorScheme.primary
                                            isToday -> MaterialTheme.colorScheme.primaryContainer
                                            else -> Color.Transparent
                                        }
                                    )
                                    .clickable {
                                        selectedDay = day
                                        val cal = Calendar.getInstance().apply {
                                            set(selectedYear, selectedMonth, day, 0, 0)
                                        }
                                        onDateSelected(cal.timeInMillis)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$day",
                                    color = when {
                                        isSelected -> MaterialTheme.colorScheme.onPrimary
                                        isToday -> MaterialTheme.colorScheme.onPrimaryContainer
                                        else -> MaterialTheme.colorScheme.onSurface
                                    },
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        } else {
                            Box(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
