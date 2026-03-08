package com.qianjilite.app.data.repository

import com.qianjilite.app.data.local.CustomCategoryDao
import com.qianjilite.app.data.local.TransactionDao
import com.qianjilite.app.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val customCategoryDao: CustomCategoryDao
) {
    fun getTransactionsByMonth(year: Int, month: Int): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByMonth(year, month)
    }

    fun getTransactionsByYear(year: Int): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByYear(year)
    }

    fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions()
    }

    suspend fun getAllTransactionsList(): List<Transaction> {
        return transactionDao.getAllTransactionsList()
    }

    suspend fun addTransaction(transaction: Transaction): Long {
        return transactionDao.insert(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.update(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.delete(transaction)
    }

    suspend fun getTransactionById(id: Long): Transaction? {
        return transactionDao.getTransactionById(id)
    }

    suspend fun getMonthlyStats(year: Int, month: Int): MonthlyStats {
        val expense = transactionDao.getTotalByMonth(TransactionType.EXPENSE, year, month) ?: 0.0
        val income = transactionDao.getTotalByMonth(TransactionType.INCOME, year, month) ?: 0.0
        return MonthlyStats(income, expense, income - expense)
    }

    suspend fun getYearlyStats(year: Int): YearlyStats {
        val expense = transactionDao.getTotalByYear(TransactionType.EXPENSE, year) ?: 0.0
        val income = transactionDao.getTotalByYear(TransactionType.INCOME, year) ?: 0.0
        return YearlyStats(income, expense, income - expense)
    }

    suspend fun getAllYears(): List<Int> {
        return transactionDao.getAllYears()
    }

    suspend fun getMonthsByYear(year: Int): List<Int> {
        return transactionDao.getMonthsByYear(year)
    }

    suspend fun addCustomCategory(name: String, type: TransactionType) {
        customCategoryDao.insert(CustomCategory(name, type))
    }

    fun getCustomCategories(type: TransactionType): Flow<List<Category>> {
        return customCategoryDao.getCategoriesByType(type).map { list ->
            list.map { Category(it.name, it.type, false) }
        }
    }

    suspend fun deleteCustomCategory(name: String, type: TransactionType) {
        customCategoryDao.delete(CustomCategory(name, type))
    }

    suspend fun exportAllTransactions(): List<Transaction> {
        var transactions = mutableListOf<Transaction>()
        transactionDao.getAllTransactions().collect { list ->
            transactions = list.toMutableList()
        }
        return transactions
    }

    suspend fun importTransactions(transactions: List<Transaction>) {
        transactionDao.insertAll(transactions)
    }

    suspend fun clearAndImportTransactions(transactions: List<Transaction>) {
        transactionDao.deleteAll()
        transactionDao.insertAll(transactions)
    }

    suspend fun deleteTransactionsByIds(ids: List<Long>) {
        transactionDao.deleteByIds(ids)
    }
}

data class MonthlyStats(
    val income: Double,
    val expense: Double,
    val balance: Double
)

data class YearlyStats(
    val income: Double,
    val expense: Double,
    val balance: Double
)
