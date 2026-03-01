package com.qianjilite.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.qianjilite.app.ui.screens.MainScreen
import com.qianjilite.app.ui.theme.QianJiTheme
import com.qianjilite.app.viewmodel.TransactionViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QianJiTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel: TransactionViewModel = viewModel()
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }
}
