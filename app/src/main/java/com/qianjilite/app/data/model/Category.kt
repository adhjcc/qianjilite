package com.qianjilite.app.data.model

data class Category(
    val name: String,
    val type: TransactionType,
    val isDefault: Boolean = true
)

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
