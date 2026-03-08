package com.qianjilite.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qianjilite.app.data.model.Category
import com.qianjilite.app.data.model.CategoryIcons
import com.qianjilite.app.data.model.Transaction
import com.qianjilite.app.data.model.TransactionType
import com.qianjilite.app.ui.theme.ExpenseColor
import com.qianjilite.app.ui.theme.IncomeColor

@Composable
fun TransactionItem(
    transaction: Transaction,
    onEdit: (Transaction) -> Unit,
    onDelete: (Transaction) -> Unit,
    isSelected: Boolean = false,
    onSelectChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onSelectChange != null) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = onSelectChange,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (transaction.type == TransactionType.EXPENSE) 
                            ExpenseColor.copy(alpha = 0.1f) 
                        else 
                            IncomeColor.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = CategoryIcons.getIconForCategory(transaction.category, transaction.type),
                    contentDescription = null,
                    tint = if (transaction.type == TransactionType.EXPENSE) ExpenseColor else IncomeColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.category,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp
                )
                if (transaction.note.isNotEmpty()) {
                    Text(
                        text = transaction.note,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
                Text(
                    text = String.format("%02d:%02d", transaction.hour, transaction.minute),
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Text(
                text = "${if (transaction.type == TransactionType.EXPENSE) "-" else "+"}${String.format("%.2f", transaction.amount)}",
                color = if (transaction.type == TransactionType.EXPENSE) ExpenseColor else IncomeColor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            if (onSelectChange == null) {
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "编辑",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("编辑") },
                            onClick = {
                                showMenu = false
                                onEdit(transaction)
                            },
                            leadingIcon = { Icon(Icons.Default.Edit, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("删除") },
                            onClick = {
                                showMenu = false
                                onDelete(transaction)
                            },
                            leadingIcon = { Icon(Icons.Default.Delete, null, tint = ExpenseColor) }
                        )
                    }
                }
            }
        }
    }
}
