package com.qianjilite.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qianjilite.app.data.repository.MonthlyStats
import com.qianjilite.app.data.repository.YearlyStats
import com.qianjilite.app.ui.theme.ExpenseColor
import com.qianjilite.app.ui.theme.IncomeColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    monthlyStats: MonthlyStats,
    yearlyStats: YearlyStats,
    currentYear: Int,
    currentMonth: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("统计") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("${currentMonth}月统计") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("${currentYear}年统计") }
                )
            }

            when (selectedTab) {
                0 -> MonthlyStatsContent(monthlyStats)
                1 -> YearlyStatsContent(yearlyStats)
            }
        }
    }
}

@Composable
fun MonthlyStatsContent(stats: MonthlyStats) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        StatsCard(
            title = "本月收入",
            amount = stats.income,
            color = IncomeColor
        )
        Spacer(modifier = Modifier.height(12.dp))
        StatsCard(
            title = "本月支出",
            amount = stats.expense,
            color = ExpenseColor
        )
        Spacer(modifier = Modifier.height(12.dp))
        StatsCard(
            title = "本月结余",
            amount = stats.balance,
            color = if (stats.balance >= 0) IncomeColor else ExpenseColor
        )
    }
}

@Composable
fun YearlyStatsContent(stats: YearlyStats) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        StatsCard(
            title = "本年收入",
            amount = stats.income,
            color = IncomeColor
        )
        Spacer(modifier = Modifier.height(12.dp))
        StatsCard(
            title = "本年支出",
            amount = stats.expense,
            color = ExpenseColor
        )
        Spacer(modifier = Modifier.height(12.dp))
        StatsCard(
            title = "本年结余",
            amount = stats.balance,
            color = if (stats.balance >= 0) IncomeColor else ExpenseColor
        )
    }
}

@Composable
fun StatsCard(
    title: String,
    amount: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = String.format("¥ %.2f", amount),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
