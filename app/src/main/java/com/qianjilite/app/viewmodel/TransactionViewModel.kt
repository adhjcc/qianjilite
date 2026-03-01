package com.qianjilite.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qianjilite.app.data.local.AppDatabase
import com.qianjilite.app.data.model.*
import com.qianjilite.app.data.repository.ExcelManager
import com.qianjilite.app.data.repository.MonthlyStats
import com.qianjilite.app.data.repository.TransactionRepository
import com.qianjilite.app.data.repository.YearlyStats
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

data class TransactionUiState(
    val transactions: List<Transaction> = emptyList(),
    val groupedTransactions: Map<String, List<Transaction>> = emptyMap(),
    val currentYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val currentMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1,
    val availableYears: List<Int> = listOf(Calendar.getInstance().get(Calendar.YEAR)),
    val availableMonths: List<Int> = listOf(Calendar.getInstance().get(Calendar.MONTH) + 1),
    val monthlyStats: MonthlyStats = MonthlyStats(0.0, 0.0, 0.0),
    val yearlyStats: YearlyStats = YearlyStats(0.0, 0.0, 0.0),
    val expenseCategories: List<Category> = DefaultCategories.expenseCategories,
    val incomeCategories: List<Category> = DefaultCategories.incomeCategories,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val repository = TransactionRepository(database.transactionDao(), database.customCategoryDao())
    val excelManager = ExcelManager(application)

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    init {
        loadAvailableDates()
        loadTransactions()
        loadCategories()
    }

    private fun loadAvailableDates() {
        viewModelScope.launch {
            val years = repository.getAllYears()
            val currentYear = _uiState.value.currentYear
            val months = repository.getMonthsByYear(currentYear)
            
            _uiState.update { state ->
                state.copy(
                    availableYears = if (years.isEmpty()) listOf(Calendar.getInstance().get(Calendar.YEAR)) else years,
                    availableMonths = if (months.isEmpty()) listOf(Calendar.getInstance().get(Calendar.MONTH) + 1) else months
                )
            }
        }
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            val year = _uiState.value.currentYear
            val month = _uiState.value.currentMonth
            
            repository.getTransactionsByMonth(year, month).collect { transactions ->
                val grouped = transactions.groupBy { transaction ->
                    "${transaction.month}月${transaction.day}日"
                }
                
                _uiState.update { state ->
                    state.copy(
                        transactions = transactions,
                        groupedTransactions = grouped
                    )
                }
                
                loadMonthlyStats()
            }
        }
    }

    private fun loadMonthlyStats() {
        viewModelScope.launch {
            val year = _uiState.value.currentYear
            val month = _uiState.value.currentMonth
            val stats = repository.getMonthlyStats(year, month)
            _uiState.update { it.copy(monthlyStats = stats) }
        }
    }

    private fun loadYearlyStats() {
        viewModelScope.launch {
            val year = _uiState.value.currentYear
            val stats = repository.getYearlyStats(year)
            _uiState.update { it.copy(yearlyStats = stats) }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            repository.getCustomCategories(TransactionType.EXPENSE).collect { customExpense ->
                repository.getCustomCategories(TransactionType.INCOME).collect { customIncome ->
                    _uiState.update { state ->
                        state.copy(
                            expenseCategories = DefaultCategories.expenseCategories + customExpense,
                            incomeCategories = DefaultCategories.incomeCategories + customIncome
                        )
                    }
                }
            }
        }
    }

    fun setYear(year: Int) {
        viewModelScope.launch {
            val months = repository.getMonthsByYear(year)
            _uiState.update { state ->
                state.copy(
                    currentYear = year,
                    currentMonth = months.firstOrNull() ?: 1,
                    availableMonths = if (months.isEmpty()) listOf(1) else months
                )
            }
            loadTransactions()
            loadMonthlyStats()
        }
    }

    fun setMonth(month: Int) {
        _uiState.update { it.copy(currentMonth = month) }
        loadTransactions()
        loadMonthlyStats()
    }

    fun addTransaction(
        type: TransactionType,
        amount: Double,
        category: String,
        note: String,
        timestamp: Long
    ) {
        viewModelScope.launch {
            val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
            val transaction = Transaction(
                type = type,
                amount = amount,
                category = category,
                note = note,
                timestamp = timestamp,
                year = calendar.get(Calendar.YEAR),
                month = calendar.get(Calendar.MONTH) + 1,
                day = calendar.get(Calendar.DAY_OF_MONTH),
                hour = calendar.get(Calendar.HOUR_OF_DAY),
                minute = calendar.get(Calendar.MINUTE)
            )
            repository.addTransaction(transaction)
            loadAvailableDates()
            _uiState.update { it.copy(successMessage = "记账成功") }
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
            _uiState.update { it.copy(successMessage = "更新成功") }
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            _uiState.update { it.copy(successMessage = "删除成功") }
        }
    }

    fun addCustomCategory(name: String, type: TransactionType) {
        viewModelScope.launch {
            repository.addCustomCategory(name, type)
            _uiState.update { it.copy(successMessage = "分类添加成功") }
        }
    }

    fun deleteCustomCategory(name: String, type: TransactionType) {
        viewModelScope.launch {
            repository.deleteCustomCategory(name, type)
            _uiState.update { it.copy(successMessage = "分类删除成功") }
        }
    }

    fun exportData(): Result<File> {
        val transactions = _uiState.value.transactions
        if (transactions.isEmpty()) {
            return Result.failure(Exception("当前月份没有数据可导出"))
        }
        val result = excelManager.exportToExcel(transactions, getApplication<Application>())
        return result
    }

    fun importData(file: File): Result<Int> {
        return try {
            val result = excelManager.importFromExcel(file)
            result.fold(
                onSuccess = { transactions ->
                    viewModelScope.launch {
                        repository.importTransactions(transactions)
                        loadAvailableDates()
                        loadTransactions()
                    }
                    Result.success(transactions.size)
                },
                onFailure = { error ->
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearSuccess() {
        _uiState.update { it.copy(successMessage = null) }
    }

    fun setSuccessMessage(message: String) {
        _uiState.update { it.copy(successMessage = message) }
    }

    fun setErrorMessage(message: String) {
        _uiState.update { it.copy(errorMessage = message) }
    }

    fun loadYearlyData() {
        loadYearlyStats()
    }
}
