package com.qianjilite.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_categories")
data class CustomCategory(
    @PrimaryKey
    val name: String,
    val type: TransactionType
)
