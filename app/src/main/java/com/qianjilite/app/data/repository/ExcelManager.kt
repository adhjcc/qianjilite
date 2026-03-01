package com.qianjilite.app.data.repository

import android.content.Context
import android.os.Environment
import com.qianjilite.app.data.model.Transaction
import com.qianjilite.app.data.model.TransactionType
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ExcelManager(private val context: Context) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    private val fileNameFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    fun exportToExcel(transactions: List<Transaction>, context: Context): Result<File> {
        return try {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("账单")

            val headerStyle = workbook.createCellStyle().apply {
                val font = workbook.createFont().apply {
                    bold = true
                }
                setFont(font)
                fillForegroundColor = IndexedColors.LIGHT_BLUE.index
                fillPattern = FillPatternType.SOLID_FOREGROUND
            }

            val headerRow = sheet.createRow(0)
            val headers = listOf("ID", "类型", "金额", "分类", "备注", "时间", "年份", "月份", "日期", "小时", "分钟")
            headers.forEachIndexed { index, header ->
                headerRow.createCell(index).apply {
                    setCellValue(header)
                    cellStyle = headerStyle
                }
            }

            transactions.forEachIndexed { index, transaction ->
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(transaction.id.toDouble())
                row.createCell(1).setCellValue(if (transaction.type == TransactionType.EXPENSE) "支出" else "收入")
                row.createCell(2).setCellValue(transaction.amount)
                row.createCell(3).setCellValue(transaction.category)
                row.createCell(4).setCellValue(transaction.note)
                row.createCell(5).setCellValue(dateFormat.format(Date(transaction.timestamp)))
                row.createCell(6).setCellValue(transaction.year.toDouble())
                row.createCell(7).setCellValue(transaction.month.toDouble())
                row.createCell(8).setCellValue(transaction.day.toDouble())
                row.createCell(9).setCellValue(transaction.hour.toDouble())
                row.createCell(10).setCellValue(transaction.minute.toDouble())
            }

            sheet.setColumnWidth(0, 2000)
            sheet.setColumnWidth(1, 3000)
            sheet.setColumnWidth(2, 5000)
            sheet.setColumnWidth(3, 4000)
            sheet.setColumnWidth(4, 8000)
            sheet.setColumnWidth(5, 6000)

            val exportDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            if (exportDir != null && !exportDir.exists()) {
                exportDir.mkdirs()
            }
            val fileName = "qianji_export_${fileNameFormat.format(Date())}.xlsx"
            val file = if (exportDir != null) File(exportDir, fileName) else File(context.filesDir, fileName)

            FileOutputStream(file).use { outputStream ->
                workbook.write(outputStream)
            }
            workbook.close()

            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun importFromExcel(file: File): Result<List<Transaction>> {
        return try {
            val transactions = mutableListOf<Transaction>()
            
            FileInputStream(file).use { inputStream ->
                val workbook = XSSFWorkbook(inputStream)
                val sheet = workbook.getSheetAt(0)
                
                for (i in 1..sheet.lastRowNum) {
                    val row = sheet.getRow(i) ?: continue
                    
                    val id = row.getCell(0)?.numericCellValue?.toLong() ?: 0
                    val typeStr = row.getCell(1)?.stringCellValue ?: "支出"
                    val type = if (typeStr == "收入") TransactionType.INCOME else TransactionType.EXPENSE
                    val amount = row.getCell(2)?.numericCellValue ?: 0.0
                    val category = row.getCell(3)?.stringCellValue ?: ""
                    val note = row.getCell(4)?.stringCellValue ?: ""
                    val timestampStr = row.getCell(5)?.stringCellValue
                    val timestamp = timestampStr?.let { 
                        dateFormat.parse(it)?.time ?: System.currentTimeMillis()
                    } ?: System.currentTimeMillis()
                    val year = row.getCell(6)?.numericCellValue?.toInt() ?: Calendar.getInstance().get(Calendar.YEAR)
                    val month = row.getCell(7)?.numericCellValue?.toInt() ?: 1
                    val day = row.getCell(8)?.numericCellValue?.toInt() ?: 1
                    val hour = row.getCell(9)?.numericCellValue?.toInt() ?: 0
                    val minute = row.getCell(10)?.numericCellValue?.toInt() ?: 0
                    
                    transactions.add(
                        Transaction(
                            id = id,
                            type = type,
                            amount = amount,
                            category = category,
                            note = note,
                            timestamp = timestamp,
                            year = year,
                            month = month,
                            day = day,
                            hour = hour,
                            minute = minute
                        )
                    )
                }
                workbook.close()
            }
            
            Result.success(transactions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
