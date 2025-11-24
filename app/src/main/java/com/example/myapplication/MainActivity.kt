package com.example.myapplication

// ---------- Imports ----------
import android.Manifest
import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

// ---------- Colors & Globals ----------
private val Background = Color(0xFFF7F7F9)
private val CardColor = Color.White
private val Accent = Color(0xFF0A84FF) // modern blue
private val Subtle = Color(0xFF8E8E93)
private val Negative = Color(0xFFB00020)

private var recorder: MediaRecorder? = null

// ---------- Activity ----------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppRoot()
        }
    }
}

// ---------- App Root & Theme ----------
@Composable
fun AppRoot() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Accent,
            background = Background,
            surface = CardColor,
            onSurface = Color.Black
        )
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = Background) {
            AppNavigation()
        }
    }
}

// ---------- Navigation ----------
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "start") {
        composable("start") { ScreenScaffold(navController, title = "Welcome", showBack = false) { StartScreen(navController) } }
        composable("noise_test") { ScreenScaffold(navController, title = "Noise Test") { NoiseTestScreen(navController) } }
        composable("task_selection") { ScreenScaffold(navController, title = "Tasks") { TaskSelectionScreen(navController) } }
        composable("text_reading") { ScreenScaffold(navController, title = "Text Reading") { TextReadingScreen(navController) } }
        composable("image_description") { ScreenScaffold(navController, title = "Image Description") { ImageDescriptionScreen(navController) } }
        composable("photo_capture") { ScreenScaffold(navController, title = "Photo Capture") { PhotoCaptureScreen(navController) } }
        composable("task_history") { ScreenScaffold(navController, title = "Task History") { TaskHistoryScreen(navController) } }
    }
}

// ---------- Scaffold with Top App Bar ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenScaffold(
    navController: NavHostController,
    title: String,
    showBack: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    if (showBack) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = CardColor)
            )
        },
        containerColor = Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }
    }
}

// ---------- Small UI Components ----------
@Composable
fun BigCard(title: String? = null, subtitle: String? = null, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardColor)
            .padding(18.dp)
    ) {
        title?.let { Text(it, fontSize = 20.sp, fontWeight = FontWeight.SemiBold) }
        subtitle?.let {
            Spacer(Modifier.height(6.dp))
            Text(it, fontSize = 14.sp, color = Subtle)
            Spacer(Modifier.height(10.dp))
        }
        content()
    }
}

@Composable
fun ModernButton(onClick: () -> Unit, text: String, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Color.White),
        modifier = modifier.height(50.dp)
    ) {
        Text(text)
    }
}

// ---------- Start Screen ----------
@Composable
fun StartScreen(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BigCard(title = "Let's start with a Sample Task for practice.", subtitle = "Pehele hum ek sample task karte hain.") {
            Text("Practice small tasks to test camera and audio pipeline.", color = Subtle)
            Spacer(Modifier.height(14.dp))
            ModernButton(onClick = { navController.navigate("noise_test") }, text = "Start Sample Task", modifier = Modifier.fillMaxWidth())
        }
        Spacer(Modifier.height(18.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            ModernButton(onClick = { navController.navigate("task_selection") }, text = "Go to Tasks", modifier = Modifier.weight(1f))
            Spacer(Modifier.width(12.dp))
            ModernButton(onClick = { navController.navigate("task_history") }, text = "History", modifier = Modifier.weight(1f))
        }
    }
}

// ---------- Noise Test ----------
@Composable
fun NoiseTestScreen(navController: NavHostController) {
    var isTesting by remember { mutableStateOf(false) }
    var currentDb by remember { mutableStateOf(0) }
    var result by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    BigCard(title = "Noise Test") {
        Text("Measure ambient noise. This demo simulates readings.", color = Subtle)
        Spacer(Modifier.height(12.dp))
        Text("$currentDb dB", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(progress = (currentDb / 60f).coerceIn(0f, 1f), modifier = Modifier.fillMaxWidth().height(8.dp))
        Spacer(Modifier.height(10.dp))
        ModernButton(onClick = {
            if (isTesting) return@ModernButton
            isTesting = true; result = null
            scope.launch {
                repeat(25) {
                    currentDb = (20..65).random()
                    delay(120)
                }
                isTesting = false
                result = if (currentDb < 40) "Good to proceed" else "Please move to a quieter place"
            }
        }, text = if (isTesting) "Testing..." else "Start Test", enabled = !isTesting, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(10.dp))
        result?.let { Text(it, color = if (it.contains("Good")) Color(0xFF0A8F3D) else Negative) }

        Spacer(Modifier.height(10.dp))
        if (result == "Good to proceed") ModernButton(onClick = { navController.navigate("task_selection") }, text = "Continue", modifier = Modifier.fillMaxWidth())
    }
}

// ---------- Task Selection ----------
@Composable
fun TaskSelectionScreen(navController: NavHostController) {
    BigCard(title = "Choose a Task", subtitle = "Pick one to continue") {
        Column {
            ModernButton(onClick = { navController.navigate("text_reading") }, text = "Text Reading", modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            ModernButton(onClick = { navController.navigate("image_description") }, text = "Image Description", modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            ModernButton(onClick = { navController.navigate("photo_capture") }, text = "Photo Capture", modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            ModernButton(onClick = { navController.navigate("task_history") }, text = "View History", modifier = Modifier.fillMaxWidth())
        }
    }
}

// ---------- Text Reading Screen ----------
@Composable
fun TextReadingScreen(navController: NavHostController) {

    val context = LocalContext.current
    var textToRead by remember { mutableStateOf("Loading text...") }

    var isRecording by remember { mutableStateOf(false) }
    var duration by remember { mutableStateOf(0) }
    var audioPath by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isValid by remember { mutableStateOf(false) }

    // Checkboxes
    var noNoise by remember { mutableStateOf(false) }
    var noMistakes by remember { mutableStateOf(false) }
    var noErrors by remember { mutableStateOf(false) }

    // Player
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var playProgress by remember { mutableStateOf(0f) }

    val scope = rememberCoroutineScope()

    // Load text
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val api = URL("https://dummyjson.com/products/1").readText()
                textToRead = JSONObject(api).getString("description")
            } catch (_: Exception) {
                textToRead = "Failed to load text."
            }
        }
    }

    // Timer for press-hold recording
    LaunchedEffect(isRecording) {
        if (isRecording) {
            duration = 0
            while (isRecording) {
                delay(1000)
                duration++
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Text Reading Task", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        BigCard {

            Text(textToRead, color = Subtle)
            Spacer(Modifier.height(12.dp))

            Text("Read the passage aloud in your native language.")

            Spacer(Modifier.height(12.dp))

            // RECORD BUTTON
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isRecording) Color(0xFFFFE5E5) else Color(0xFFF1F1F3))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                audioPath = startPressHoldRecording(context)
                                isRecording = true
                                duration = 0
                                errorMessage = null
                                isValid = false

                                tryAwaitRelease()
                                isRecording = false

                                when {
                                    duration < 10 -> errorMessage = "Recording too short (min 10s)"
                                    duration > 20 -> errorMessage = "Recording too long (max 20s)"
                                    else -> {
                                        errorMessage = null
                                        isValid = true
                                    }
                                }

                                stopPressHoldRecording()
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isRecording) "Recording…" else "Hold to Record",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(10.dp))
            Text("Duration: $duration sec", color = Subtle)
            errorMessage?.let { Text(it, color = Negative) }

            // CHECKBOXES
            if (isValid) {
                Spacer(Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(noNoise, { noNoise = it })
                    Text("No background noise")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(noMistakes, { noMistakes = it })
                    Text("No mistakes while reading")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(noErrors, { noErrors = it })
                    Text("Beech me koi galti nahi hai")
                }
            }

            // PLAYBACK + SUBMIT
            if (audioPath != null && isValid) {
                Spacer(Modifier.height(16.dp))

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(Color(0xFFF3F3F6)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(14.dp)) {

                        Text("Playback", fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = playProgress,
                            modifier = Modifier.fillMaxWidth().height(6.dp)
                        )

                        Spacer(Modifier.height(12.dp))

                        // Play/Stop
                        ModernButton(
                            onClick = {
                                if (!isPlaying) {
                                    mediaPlayer = playAudio(audioPath!!) {
                                        isPlaying = false
                                        playProgress = 0f
                                    }
                                    isPlaying = true

                                    scope.launch {
                                        while (isPlaying) {
                                            mediaPlayer?.let {
                                                playProgress = it.currentPosition / it.duration.toFloat()
                                            }
                                            delay(100)
                                        }
                                    }
                                } else {
                                    mediaPlayer?.stop()
                                    mediaPlayer?.release()
                                    mediaPlayer = null
                                    isPlaying = false
                                    playProgress = 0f
                                }
                            },
                            text = if (isPlaying) "Stop" else "Play",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // SUBMIT BUTTON — ONLY WHEN CHECKBOXES ✓ ALL TRUE
                Button(
                    onClick = {
                        saveTextReadingTask(context, textToRead, audioPath!!, duration)
                        navController.navigate("task_selection")
                    },
                    enabled = noNoise && noMistakes && noErrors,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Submit", fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                }

                Spacer(Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        audioPath = null
                        isValid = false
                        noNoise = false
                        noMistakes = false
                        noErrors = false
                        duration = 0
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Text("Record Again")
                }
            }
        }
    }
}


// ---------- Image Description Screen ----------
@Composable
fun ImageDescriptionScreen(navController: NavHostController) {
    val context = LocalContext.current
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var audioPath by remember { mutableStateOf<String?>(null) }
    var duration by remember { mutableStateOf(0) }
    var isRecording by remember { mutableStateOf(false) }
    var isValid by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (!granted) Toast.makeText(context, "Microphone required", Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val api = URL("https://dummyjson.com/products/14").readText()
                val json = JSONObject(api)
                val arr = json.getJSONArray("images")
                imageUrl = arr.getString(0)
            } catch (_: Exception) { imageUrl = null }
        }
    }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            duration = 0
            while (isRecording) {
                delay(1000)
                duration++
            }
        }
    }

    BigCard(title = "Image Description") {
        if (imageUrl != null) {
            AsyncImage(model = imageUrl, contentDescription = null, modifier = Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(12.dp)))
        } else {
            Box(modifier = Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF0F0F2)), contentAlignment = Alignment.Center) {
                Text("Loading image...", color = Subtle)
            }
        }

        Spacer(Modifier.height(12.dp))

        Box(modifier = Modifier.height(110.dp).fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFFF8F8FA)).pointerInput(Unit) {
            detectTapGestures(onPress = {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                delay(120)
                audioPath = startPressHoldRecording(context)
                isRecording = true
                tryAwaitRelease()
                isRecording = false

                when {
                    duration < 10 -> { errorMessage = "Recording too short (min 10 s)"; isValid = false; stopPressHoldRecording() }
                    duration > 20 -> { errorMessage = "Recording too long (max 20 s)"; isValid = false; stopPressHoldRecording() }
                    else -> { errorMessage = null; isValid = true; stopPressHoldRecording() }
                }
            })
        }, contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(if (isRecording) "Recording..." else "Hold to describe the image")
                Spacer(Modifier.height(8.dp))
                Text("Duration: ${duration}s", color = Subtle)
            }
        }

        Spacer(Modifier.height(12.dp))
        errorMessage?.let { Text(it, color = Negative) }

        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            ModernButton(onClick = {
                if (isValid && audioPath != null && imageUrl != null) {
                    saveImageDescriptionTask(context, imageUrl!!, audioPath!!, duration)
                    Toast.makeText(context, "Saved image description task", Toast.LENGTH_SHORT).show()
                    navController.navigate("task_selection")
                } else {
                    Toast.makeText(context, "Record valid audio first", Toast.LENGTH_SHORT).show()
                }
            }, text = "Submit", modifier = Modifier.weight(1f))

            Spacer(Modifier.width(12.dp))

            ModernButton(onClick = {
                audioPath?.let { try { File(it).delete() } catch (_: Exception) {} }
                audioPath = null; duration = 0; isValid = false; errorMessage = null
            }, text = "Reset", modifier = Modifier.weight(1f))
        }
    }
}

// ---------- Photo Capture (capture + description text required; audio optional) ----------
@Composable
fun PhotoCaptureScreen(navController: NavHostController) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasPermission by remember { mutableStateOf(false) }
    var imagePath by remember { mutableStateOf<String?>(null) }
    var description by remember { mutableStateOf("") }

    // Optional audio
    var audioPath by remember { mutableStateOf<String?>(null) }
    var isRecording by remember { mutableStateOf(false) }
    var duration by remember { mutableStateOf(0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        hasPermission =
            it[Manifest.permission.CAMERA] == true &&
                    it[Manifest.permission.RECORD_AUDIO] == true
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
        )
    }

    if (!hasPermission) {
        BigCard(title = "Permissions Required") {
            Text("Camera + Microphone required", color = Subtle)
            Spacer(Modifier.height(8.dp))
            ModernButton(
                onClick = {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO
                        )
                    )
                },
                text = "Grant Permissions", modifier = Modifier.fillMaxWidth()
            )
        }
        return
    }

    // Camera setup
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    val previewView = remember { PreviewView(context) }
    val future = remember { ProcessCameraProvider.getInstance(context) }

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            val provider = future.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()

            provider.unbindAll()
            provider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture
            )
        }
    }

    Column(Modifier.fillMaxSize().padding(20.dp)) {

        Text("Photo Capture Task", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(12.dp))

        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxWidth().height(340.dp).clip(RoundedCornerShape(12.dp))
        )

        Spacer(Modifier.height(12.dp))

        ModernButton(
            onClick = {
                val file = File(context.getExternalFilesDir(null), "photo_${System.currentTimeMillis()}.jpg")
                val out = ImageCapture.OutputFileOptions.Builder(file).build()

                imageCapture?.takePicture(out, ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(result: ImageCapture.OutputFileResults) {
                            imagePath = file.absolutePath
                        }
                        override fun onError(exc: ImageCaptureException) {
                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                        }
                    })
            },
            text = "Capture Photo",
            modifier = Modifier.fillMaxWidth()
        )

        if (imagePath != null) {

            Spacer(Modifier.height(12.dp))
            AsyncImage(
                model = File(imagePath!!),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(12.dp))
            )

            Spacer(Modifier.height(12.dp))
            Text("Describe the photo:")
            Spacer(Modifier.height(6.dp))

            // FIXED → smooth typing
            TextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Write description…") }
            )

            Spacer(Modifier.height(16.dp))

            // Optional mic
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F1F3))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                audioPath = startPressHoldRecording(context)
                                isRecording = true
                                errorMessage = null
                                duration = 0

                                tryAwaitRelease()
                                isRecording = false

                                when {
                                    duration < 10 -> errorMessage = "Recording too short (min 10s)"
                                    duration > 20 -> errorMessage = "Recording too long (max 20s)"
                                }

                                stopPressHoldRecording()
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(if (isRecording) "Recording…" else "Hold to record (optional)")
            }

            Spacer(Modifier.height(8.dp))
            Text("Duration: $duration sec", color = Subtle)
            errorMessage?.let { Text(it, color = Negative) }

            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                OutlinedButton(
                    onClick = {
                        imagePath = null
                        description = ""
                        audioPath = null
                        duration = 0
                        errorMessage = null
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Retake")
                }

                Button(
                    onClick = {
                        savePhotoCaptureTask(
                            context,
                            imagePath!!,
                            audioPath,
                            duration,
                            description.trim()
                        )
                        navController.navigate("task_selection")
                    },
                    enabled = description.trim().isNotEmpty(),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Submit")
                }
            }
        }
    }
}


// ---------- Task History ----------
@Composable
fun TaskHistoryScreen(navController: NavHostController) {
    val context = LocalContext.current
    var tasks by remember { mutableStateOf<List<JSONObject>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val file = File(context.filesDir, "tasks.json")
            if (!file.exists()) { tasks = emptyList(); isLoading = false; return@withContext }
            try {
                val arr = JSONArray(file.readText())
                val list = mutableListOf<JSONObject>()
                for (i in 0 until arr.length()) list.add(arr.getJSONObject(i))
                tasks = list.reversed()
            } catch (_: Exception) { tasks = emptyList() }
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            BigCard { Text("Loading...") }
            return
        }
        if (tasks.isEmpty()) {
            BigCard { Text("No tasks yet. Complete one and it will appear here.") }
            return
        }

        // Header: total tasks + total recording duration
        val totalTasks = tasks.size
        var totalDurationSec = 0
        tasks.forEach { totalDurationSec += it.optInt("duration_sec", 0) }
        val totalDurationText = "${totalDurationSec}s"

        BigCard {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column { Text("Total Tasks", fontWeight = FontWeight.SemiBold); Text("$totalTasks", color = Subtle) }
                Column { Text("Total Recording", fontWeight = FontWeight.SemiBold); Text(totalDurationText, color = Subtle) }
            }
        }

        Spacer(Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            items(tasks) { obj -> TaskHistoryItemCompact(obj) }
        }
    }
}

@Composable
fun TaskHistoryItemCompact(task: JSONObject) {
    val type = task.optString("task_type", "unknown").replace('_', ' ').replaceFirstChar { it.uppercaseChar() }
    val ts = task.optLong("timestamp", System.currentTimeMillis())
    val df = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(ts))
    val durationSec = task.optInt("duration_sec", 0)

    Card(modifier = Modifier.fillMaxWidth().padding(8.dp), shape = RoundedCornerShape(10.dp)) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(type, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(6.dp))
                Text(df, color = Subtle)
                Spacer(Modifier.height(4.dp))
                if (durationSec > 0) Text("Duration: ${durationSec}s", color = Subtle)
            }
            // thumbnail or chevron
            val imagePath = task.optString("image_path", null)
            if (imagePath.isNullOrBlank()) {
                Text("›", fontSize = 22.sp, color = Subtle)
            } else {
                AsyncImage(model = File(imagePath), contentDescription = null, modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)))
            }
        }
    }
}

// ---------- Save Task Helpers ----------
fun saveTextReadingTask(context: Context, text: String, audioPath: String, duration: Int) {
    val file = File(context.filesDir, "tasks.json")
    val newTask = JSONObject().apply {
        put("task_type", "text_reading")
        put("text", text)
        put("audio_path", audioPath)
        put("duration_sec", duration)
        put("timestamp", System.currentTimeMillis())
    }
    saveTaskToFile(file, newTask)
}

fun saveImageDescriptionTask(context: Context, imageUrl: String, audioPath: String, duration: Int) {
    val file = File(context.filesDir, "tasks.json")
    val newTask = JSONObject().apply {
        put("task_type", "image_description")
        put("image_url", imageUrl)
        put("audio_path", audioPath)
        put("duration_sec", duration)
        put("timestamp", System.currentTimeMillis())
    }
    saveTaskToFile(file, newTask)
}

fun savePhotoCaptureTask(
    context: Context,
    imagePath: String,
    audioPath: String?,
    duration: Int,
    description: String
) {
    val file = File(context.filesDir, "tasks.json")

    val newTask = JSONObject().apply {
        put("task_type", "photo_capture")
        put("image_path", imagePath)
        put("description", description)
        if (!audioPath.isNullOrBlank()) {
            put("audio_path", audioPath)
            put("duration_sec", duration)
        } else {
            put("audio_path", JSONObject.NULL)
            put("duration_sec", 0)
        }
        put("timestamp", System.currentTimeMillis())
    }

    val arr = if (file.exists()) JSONArray(file.readText()) else JSONArray()
    arr.put(newTask)
    file.writeText(arr.toString())
}

fun saveTaskToFile(file: File, obj: JSONObject) {
    val arr = if (file.exists()) try { JSONArray(file.readText()) } catch (_: Exception) { JSONArray() } else JSONArray()
    arr.put(obj)
    file.writeText(arr.toString())
}

// ---------- Audio helpers ----------
fun startPressHoldRecording(context: Context): String {
    val file = File(context.getExternalFilesDir(null), "audio_${System.currentTimeMillis()}.m4a")
    recorder = MediaRecorder().apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        setOutputFile(file.absolutePath)
        try { prepare(); start() } catch (e: Exception) { e.printStackTrace() }
    }
    return file.absolutePath
}

fun stopPressHoldRecording() {
    try { recorder?.stop() } catch (_: Exception) {}
    try { recorder?.release() } catch (_: Exception) {}
    recorder = null
}

fun playAudio(path: String, onComplete: () -> Unit): MediaPlayer {
    return MediaPlayer().apply {
        setDataSource(path)
        prepare()
        start()
        setOnCompletionListener { onComplete() }
    }
}
