package com.ferry.noten

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                NoteApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteApp() {
    var showForm by remember { mutableStateOf(false) }
    var noteInput by remember { mutableStateOf("") }
    var notesData by remember { mutableStateOf(listOf<String>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📓 Buku Catatan Sederhana") }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Data tersimpan selama app berjalan",
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { showForm = !showForm },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Tambah Catatan")
                    }
                    Button(
                        onClick = { },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Detail Screen")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { notesData = emptyList() },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text("Hapus Semua Catatan", color = Color(0xFF6A1B9A))
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (showForm) {
                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it },
                        label = { Text("Isi Catatan") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        if (noteInput.isNotBlank()) {
                            notesData = notesData + noteInput
                            noteInput = ""
                            showForm = false
                        }
                    }) {
                        Text("Simpan")
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }

                Text("📚 Catatan Anda (${notesData.size}):", fontWeight = FontWeight.Bold, textAlign = TextAlign.Start)

                notesData.forEachIndexed { index, note ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Catatan #${index + 1}", color = Color(0xFF6A1B9A), fontWeight = FontWeight.Bold)
                            Text(note)
                        }
                    }
                }
            }
        }
    )
}