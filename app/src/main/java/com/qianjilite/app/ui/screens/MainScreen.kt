package com.qianjilite.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.qianjilite.app.data.model.Transaction
import com.qianjilite.app.ui.components.*
import com.qianjilite.app.ui.theme.ExpenseColor
import com.qianjilite.app.ui.theme.IncomeColor
import com.qianjilite.app.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: TransactionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    var showStats by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }
    var editingTransaction by remember { mutableStateOf<Transaction?>(null) }

    LaunchedEffect(Unit) {
        val window = (context as? android.app.Activity)?.window
        window?.let {
            WindowCompat.getInsetsController(it, it.decorView).apply {
                isAppearanceLightStatusBars = false
            }
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            inputStream?.let { stream ->
                val file = java.io.File(context.cacheDir, "import_temp.xlsx")
                file.outputStream().use { output ->
                    stream.copyTo(output)
                }
                val result = viewModel.importData(file)
                result.fold(
                    onSuccess = { count ->
                        viewModel.setSuccessMessage("导入成功，共 $count 条记录")
                    },
                    onFailure = { error ->
                        viewModel.setErrorMessage(error.message ?: "导入失败")
                    }
                )
            }
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            MonthYearSelector(
                currentYear = uiState.currentYear,
                currentMonth = uiState.currentMonth,
                availableYears = uiState.availableYears,
                availableMonths = uiState.availableMonths,
                onYearChange = viewModel::setYear,
                onMonthChange = viewModel::setMonth,
                onStatsClick = {
                    viewModel.loadYearlyData()
                    showStats = true
                },
                onExportClick = {
                    val result = viewModel.exportData()
                    result.fold(
                        onSuccess = { file ->
                            viewModel.setSuccessMessage("本月导出成功: ${file.name}")
                        },
                        onFailure = { error ->
                            viewModel.setErrorMessage(error.message ?: "导出失败")
                        }
                    )
                },
                onExportAllClick = {
                    scope.launch {
                        val result = viewModel.exportAllData()
                        result.fold(
                            onSuccess = { file ->
                                viewModel.setSuccessMessage("全量导出成功: ${file.name}")
                            },
                            onFailure = { error ->
                                viewModel.setErrorMessage(error.message ?: "导出失败")
                            }
                        )
                    }
                },
                onCategoryClick = {
                    showCategoryDialog = true
                },
                onBatchModeClick = viewModel::toggleBatchMode,
                isBatchMode = uiState.isBatchMode
            )
        },
        floatingActionButton = {
            if (uiState.isBatchMode) {
                Column {
                    if (uiState.selectedTransactionIds.isNotEmpty()) {
                        FloatingActionButton(
                            onClick = viewModel::deleteSelectedTransactions,
                            containerColor = ExpenseColor,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "删除选中")
                        }
                    }
                    FloatingActionButton(
                        onClick = viewModel::selectAllTransactions,
                        containerColor = MaterialTheme.colorScheme.secondary
                    ) {
                        Text("全选", color = Color.White)
                    }
                }
            } else {
                Column {
                    FloatingActionButton(
                        onClick = { filePickerLauncher.launch("*/*") },
                        containerColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = "导入")
                    }
                    FloatingActionButton(
                        onClick = { showAddDialog = true },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "记账")
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(bottom = 80.dp)
        ) {
            SummaryBar(
                income = uiState.monthlyStats.income,
                expense = uiState.monthlyStats.expense,
                balance = uiState.monthlyStats.balance
            )

            if (uiState.transactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无账单记录",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    uiState.groupedTransactions.forEach { (date, transactions) ->
                        item {
                            Text(
                                text = date,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        items(transactions, key = { it.id }) { transaction ->
                            TransactionItem(
                                transaction = transaction,
                                onEdit = { editingTransaction = it },
                                onDelete = { viewModel.deleteTransaction(it) },
                                isSelected = uiState.selectedTransactionIds.contains(transaction.id),
                                onSelectChange = if (uiState.isBatchMode) {
                                    { viewModel.toggleTransactionSelection(transaction.id) }
                                } else null
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddTransactionDialog(
            expenseCategories = uiState.expenseCategories,
            incomeCategories = uiState.incomeCategories,
            onDismiss = { showAddDialog = false },
            onSave = { type, amount, category, note, timestamp ->
                viewModel.addTransaction(type, amount, category, note, timestamp)
            },
            onAddCategory = viewModel::addCustomCategory
        )
    }

    editingTransaction?.let { transaction ->
        AddTransactionDialog(
            expenseCategories = uiState.expenseCategories,
            incomeCategories = uiState.incomeCategories,
            existingTransaction = transaction,
            onDismiss = { editingTransaction = null },
            onSave = { type, amount, category, note, timestamp ->
                viewModel.updateTransaction(
                    transaction.copy(
                        type = type,
                        amount = amount,
                        category = category,
                        note = note,
                        timestamp = timestamp
                    )
                )
                editingTransaction = null
            },
            onAddCategory = viewModel::addCustomCategory
        )
    }

    if (showStats) {
        StatisticsScreen(
            monthlyStats = uiState.monthlyStats,
            yearlyStats = uiState.yearlyStats,
            currentYear = uiState.currentYear,
            currentMonth = uiState.currentMonth,
            onBack = { showStats = false }
        )
    }

    if (showCategoryDialog) {
        CategoryManagementDialog(
            expenseCategories = uiState.expenseCategories,
            incomeCategories = uiState.incomeCategories,
            onDismiss = { showCategoryDialog = false },
            onAddCategory = viewModel::addCustomCategory,
            onDeleteCategory = viewModel::deleteCustomCategory
        )
    }

    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
        uiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSuccess()
        }
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }
}

@Composable
fun SummaryBar(
    income: Double,
    expense: Double,
    balance: Double
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SummaryItem(
                label = "收入",
                amount = income,
                color = IncomeColor
            )
            SummaryItem(
                label = "支出",
                amount = expense,
                color = ExpenseColor
            )
            SummaryItem(
                label = "结余",
                amount = balance,
                color = if (balance >= 0) IncomeColor else ExpenseColor
            )
        }
    }
}

@Composable
fun SummaryItem(
    label: String,
    amount: Double,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
        Text(
            text = String.format("¥ %.0f", amount),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
