import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import fcm.FCMService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
@Preview
fun App() {
    var token by remember { mutableStateOf("") }
    var lastResponse by remember { mutableStateOf("") }
    val responses = mutableStateListOf<Responses>()

    val scope = rememberCoroutineScope()
    MaterialTheme {
        LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {

            item {
                OutlinedTextField(token, onValueChange = {token = it})
            }


            item {
                Button(onClick = {
                    scope.launch{
                        if (token.isNotBlank())
                        sendMessage(token).collect {
                            lastResponse = it
                            responses.add(0, Responses(it, System.currentTimeMillis()))
                        }
                        else {
                            responses.add(0, Responses("token is empty", System.currentTimeMillis()))
                        }
                    }
                }) {
                    Text("sendMessage!")
                }
            }


            items(responses) {
                Card(modifier = Modifier.fillParentMaxWidth().padding(8.dp)) {
                    Column {
                        Text(it.test)
                        Text(convertTimestamp(it.time), fontWeight = FontWeight.Bold)
                    }

                }
            }

        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

data class Responses(val test: String, val time: Long)

fun sendMessage(token: String): Flow<String> {
    val fcmService = FCMService()
    fcmService.initializeFirebase()

    return fcmService.sendNotification(
        deviceToken = token,
        title = "Test Notification",
        body = "Hello from FCM!"
    )
}

fun convertTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
    return format.format(date)
}