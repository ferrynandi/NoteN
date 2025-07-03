package com.ferry.noten

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed // Import for LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog // For the edit dialog
import com.ferry.noten.ui.theme.NoteNTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun saveNotesToPreferences(context: Context, notes: List<String>) {
    val sharedPreferences = context.getSharedPreferences("NoteAppPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    val gson = Gson()
    val jsonNotes = gson.toJson(notes)
    editor.putString("notes_list", jsonNotes)
    editor.apply()
}

fun loadNotesFromPreferences(context: Context): List<String> {
    val sharedPreferences = context.getSharedPreferences("NoteAppPrefs", Context.MODE_PRIVATE)
    val gson = Gson()
    val jsonNotes = sharedPreferences.getString("notes_list", null)
    return if (jsonNotes != null) {
        val type = object : TypeToken<List<String>>() {}.type
        gson.fromJson(jsonNotes, type)
    } else {
        emptyList()
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoteNTheme {
                NoteApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteApp() {
    val context = LocalContext.current
    var notesData by remember { mutableStateOf(loadNotesFromPreferences(context)) }

    // State for Create/Update form
    var showFormDialog by remember { mutableStateOf(false) }
    var currentNoteInput by remember { mutableStateOf("") }
    var editingNoteIndex by remember { mutableStateOf<Int?>(null) } // To track if we are editing

    val onSaveNote: () -> Unit = {
        if (currentNoteInput.isNotBlank()) {
            val updatedNotes = notesData.toMutableList()
            if (editingNoteIndex != null) { // UPDATE
                updatedNotes[editingNoteIndex!!] = currentNoteInput
            } else { // CREATE
                updatedNotes.add(currentNoteInput)
            }
            notesData = updatedNotes.toList()
            saveNotesToPreferences(context, notesData)
            currentNoteInput = ""
            editingNoteIndex = null
            showFormDialog = false
        }
    }

    val onDeleteNote: (Int) -> Unit = { index ->
        val updatedNotes = notesData.toMutableList()
        updatedNotes.removeAt(index)
        notesData = updatedNotes.toList()
        saveNotesToPreferences(context, notesData)
    }

    val onEditNote: (Int) -> Unit = { index ->
        currentNoteInput = notesData[index]
        editingNoteIndex = index
        showFormDialog = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Aplikasi Catatan Sederhana") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                currentNoteInput = "" //biar input note baru dari awal
                editingNoteIndex = null
                showFormDialog = true
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Tambah Catatan")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Data tersimpan secara lokal",
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (notesData.isNotEmpty()) {
                OutlinedButton(
                    onClick = {
                        notesData = emptyList()
                        saveNotesToPreferences(context, notesData)
                    },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Hapus Semua Catatan", color = Color.Red)
                }
                Spacer(modifier = Modifier.height(10.dp))
            }


            Text(
                "Catatan Anda (${notesData.size}):",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // READ: Displaying the notes
            if (notesData.isEmpty()){
                Text("Belum ada catatan.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(notesData, key = { index, _ -> index }) { index, note ->
                        NoteItem(
                            noteText = note,
                            onEditClick = { onEditNote(index) },
                            onDeleteClick = { onDeleteNote(index) }
                        )
                    }
                }
            }
        }

        // CREATE / UPDATE Dialog
        if (showFormDialog) {
            EditNoteDialog(
                initialValue = currentNoteInput,
                onDismissRequest = {
                    showFormDialog = false
                    editingNoteIndex = null //reset edit
                    currentNoteInput = ""
                },
                onSave = onSaveNote,
                onValueChange = { currentNoteInput = it },
                isEditing = editingNoteIndex != null
            )
        }
    }
}

@Composable
fun NoteItem(
    noteText: String,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = noteText,
                modifier = Modifier.weight(1f)
            )
            Row {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit Catatan", tint = Color(0xFF6A1B9A))
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Filled.Delete, contentDescription = "Hapus Catatan", tint = Color.Red)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteDialog(
    initialValue: String,
    onDismissRequest: () -> Unit,
    onSave: () -> Unit,
    onValueChange: (String) -> Unit,
    isEditing: Boolean
) {
    var text by remember { mutableStateOf(initialValue) }

    // Update internal text when initialValue changes (e.g., when opening dialog for different notes)
    LaunchedEffect(initialValue) {
        text = initialValue
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(if (isEditing) "Edit Catatan" else "Tambah Catatan Baru", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = {
                        text = it
                        onValueChange(it)
                    },
                    label = { Text("Isi Catatan") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 5
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Batal")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        onSave()
                    }) {
                        Text("Simpan")
                    }
                }
            }
        }
    }
}