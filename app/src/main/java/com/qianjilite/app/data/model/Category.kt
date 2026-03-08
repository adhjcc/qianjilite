package com.qianjilite.app.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class Category(
    val name: String,
    val type: TransactionType,
    val isDefault: Boolean = true,
    val icon: ImageVector = Icons.Default.Star
)

object CategoryIcons {
    val availableIcons = listOf(
        Icons.Default.Restaurant,
        Icons.Default.Movie,
        Icons.Default.ShoppingCart,
        Icons.Default.DirectionsCar,
        Icons.Default.LocalHospital,
        Icons.Default.School,
        Icons.Default.Home,
        Icons.Default.Phone,
        Icons.Default.Checkroom,
        Icons.Default.FitnessCenter,
        Icons.Default.Pets,
        Icons.Default.MoreHoriz,
        Icons.Default.Work,
        Icons.Default.Savings,
        Icons.Default.AttachMoney,
        Icons.Default.TrendingUp,
        Icons.Default.CardGiftcard,
        Icons.Default.Undo,
        Icons.Default.Star,
        Icons.Default.Favorite,
        Icons.Default.Book,
        Icons.Default.MusicNote,
        Icons.Default.SportsEsports,
        Icons.Default.Flight,
        Icons.Default.LocalCafe
    )

    fun getIconForCategory(name: String, type: TransactionType): ImageVector {
        return when (name) {
            "三餐", "餐饮" -> Icons.Default.Restaurant
            "娱乐" -> Icons.Default.Movie
            "购物" -> Icons.Default.ShoppingCart
            "交通" -> Icons.Default.DirectionsCar
            "医疗" -> Icons.Default.LocalHospital
            "教育" -> Icons.Default.School
            "住房" -> Icons.Default.Home
            "通讯", "水电" -> Icons.Default.Phone
            "服装" -> Icons.Default.Checkroom
            "健身", "运动" -> Icons.Default.FitnessCenter
            "数码产品" -> Icons.Default.Devices
            "工资" -> Icons.Default.Work
            "理财" -> Icons.Default.Savings
            "利息" -> Icons.Default.AttachMoney
            "奖金" -> Icons.Default.TrendingUp
            "红包" -> Icons.Default.CardGiftcard
            "退款" -> Icons.Default.Undo
            "兼职" -> Icons.Default.Person
            else -> if (type == TransactionType.EXPENSE) Icons.Default.MoreHoriz else Icons.Default.Star
        }
    }
}

object DefaultCategories {
    val expenseCategories = listOf(
        Category("三餐", TransactionType.EXPENSE),
        Category("娱乐", TransactionType.EXPENSE),
        Category("数码产品", TransactionType.EXPENSE),
        Category("水电", TransactionType.EXPENSE),
        Category("交通", TransactionType.EXPENSE),
        Category("购物", TransactionType.EXPENSE),
        Category("医疗", TransactionType.EXPENSE),
        Category("教育", TransactionType.EXPENSE),
        Category("住房", TransactionType.EXPENSE),
        Category("通讯", TransactionType.EXPENSE),
        Category("服装", TransactionType.EXPENSE),
        Category("其他支出", TransactionType.EXPENSE)
    )

    val incomeCategories = listOf(
        Category("工资", TransactionType.INCOME),
        Category("理财", TransactionType.INCOME),
        Category("利息", TransactionType.INCOME),
        Category("兼职", TransactionType.INCOME),
        Category("奖金", TransactionType.INCOME),
        Category("红包", TransactionType.INCOME),
        Category("退款", TransactionType.INCOME),
        Category("其他收入", TransactionType.INCOME)
    )

    val allCategories = expenseCategories + incomeCategories
}
