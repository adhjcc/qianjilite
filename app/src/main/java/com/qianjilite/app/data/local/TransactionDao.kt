package com.qianjilite.app.data.local

import androidx.room.*
import com.qianjilite.app.data.model.Transaction
import com.qianjilite.app.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: Transaction): Long

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE year = :year AND month = :month ORDER BY day DESC, hour DESC, minute DESC")
    fun getTransactionsByMonth(year: Int, month: Int): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE year = :year ORDER BY month DESC, day DESC, hour DESC, minute DESC")
    fun getTransactionsByYear(year: Int): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions ORDER BY year DESC, month DESC, day DESC, hour DESC, minute DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): Transaction?

    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type AND year = :year AND month = :month")
    suspend fun getTotalByMonth(type: TransactionType, year: Int, month: Int): Double?

    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type AND year = :year")
    suspend fun getTotalByYear(type: TransactionType, year: Int): Double?

    @Query("SELECT DISTINCT year FROM transactions ORDER BY year DESC")
    suspend fun getAllYears(): List<Int>

    @Query("SELECT DISTINCT month FROM transactions WHERE year = :year ORDER BY month DESC")
    suspend fun getMonthsByYear(year: Int): List<Int>

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<Transaction>)
}
