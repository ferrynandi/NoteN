data class NoteItem(val text: String, val timestamp: String)

@Composable
fun NoteApp() {
    val navController = rememberNavController()
    val notesData = remember { mutableStateListOf<NoteItem>() }

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(navController, notesData)
        }
        composable("detail/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull()
            val note = index?.let { notesData.getOrNull(it) }
            if (note != null) {
                DetailScreen(note) {
                    navController.popBackStack()
                }
            } else {
                Text("Catatan tidak ditemukan")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController, notesData: MutableList<NoteItem>) {
    var showForm by remember { mutableStateOf(false) }
    var noteInput by remember { mutableStateOf("") }

    val dateFormatter = remember {
        SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault())
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Buku Catatan Sederhana Ferry") })
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
                    text = "Data tersimpan selama app berjalan, klo keluar ilang",
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
                        onClick = {
                            if (notesData.isNotEmpty()) {
                                navController.navigate("detail/${notesData.lastIndex}")
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Detail Terakhir")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { notesData.clear() },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth()
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
                            val currentTime = dateFormatter.format(Date())
                            val newNote = NoteItem(noteInput, currentTime)
                            notesData.add(newNote)
                            noteInput = ""
                            showForm = false
                        }
                    }) {
                        Text("Simpan")
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                Text(
                    "📚 Catatan Anda (${notesData.size}):",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                )

                notesData.forEachIndexed { index, note ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                navController.navigate("detail/$index")
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Catatan #${index + 1}", color = Color(0xFF6A1B9A), fontWeight = FontWeight.Bold)
                            Text(note.text)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("🕒 ${note.timestamp}", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun DetailScreen(note: NoteItem, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("📋 Detail Catatan", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Isi Catatan:", fontWeight = FontWeight.SemiBold)
        Text(note.text, modifier = Modifier.padding(bottom = 16.dp))
        Text("🕒 Dibuat pada:", fontWeight = FontWeight.SemiBold)
        Text(note.timestamp)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onBack) {
            Text("Kembali")
        }
    }
}
