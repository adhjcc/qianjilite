package com.qianjilite.app.data.local

import androidx.room.*
import com.qianjilite.app.data.model.CustomCategory
import com.qianjilite.app.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomCategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CustomCategory)

    @Delete
    suspend fun delete(category: CustomCategory)

    @Query("SELECT * FROM custom_categories WHERE type = :type")
    fun getCategoriesByType(type: TransactionType): Flow<List<CustomCategory>>

    @Query("SELECT * FROM custom_categories")
    fun getAllCategories(): Flow<List<CustomCategory>>

    @Query("SELECT EXISTS(SELECT 1 FROM custom_categories WHERE name = :name)")
    suspend fun exists(name: String): Boolean
}
