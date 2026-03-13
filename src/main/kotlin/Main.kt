import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
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
    var title by remember { mutableStateOf("Test Notification") }
    var body by remember { mutableStateOf("Hello from FCM!") }
    var channelId by remember { mutableStateOf("") }
    var lastResponse by remember { mutableStateOf("") }
    val responses = mutableStateListOf<Responses>()

    val scope = rememberCoroutineScope()
    MaterialTheme {
        LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {


            item {
                Text("token")
                OutlinedTextField(token, onValueChange = { token = it })
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text("title")
                OutlinedTextField(title, onValueChange = { title = it })
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Text("body")
                OutlinedTextField(body, onValueChange = { body = it })
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Text("channelID")
                OutlinedTextField(channelId, onValueChange = { channelId = it })
                Spacer(modifier = Modifier.height(16.dp))
            }



            item {
                Row {
                    Button(onClick = {
                        scope.launch {
                            if (token.isNotBlank())
                                sendMessage(token, title, body, channelId).collect {
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
                    Spacer(Modifier.width(16.dp))
                    Button(onClick = {
                        scope.launch {
                            if (token.isNotBlank())
                                sendMessageWithCustomDate(token, title, body, channelId).collect {
                                    lastResponse = it
                                    responses.add(0, Responses(it, System.currentTimeMillis()))
                                }
                            else {
                                responses.add(0, Responses("token is empty", System.currentTimeMillis()))
                            }
                        }
                    }) {
                        Text("send message with custom data!")
                    }
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

fun sendMessage(deviceToken: String, title: String, body: String, channelId: String): Flow<String> {
    val fcmService = FCMService()
    fcmService.initializeFirebase()

    return fcmService.sendNotification(
        deviceToken = deviceToken,
        title = title,
        body = body,
        channelId
    )
}

fun sendMessageWithCustomDate(deviceToken: String, title: String, body: String, channelId: String): Flow<String> {
    val fcmService = FCMService()
    fcmService.initializeFirebase()

    return fcmService.sendNotificationWithCustomData(
        deviceToken = deviceToken,
        title = title,
        body = body,
        channelId
    )
}

fun convertTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
    return format.format(date)
}