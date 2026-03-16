package fcm

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.FileInputStream
import kotlin.random.Random


class FCMService {

    fun initializeFirebase(filePath: String = "service-account-key2.json") {
        try {
            val serviceAccount = FileInputStream(filePath)

            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build()

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendNotification(deviceToken: String, title: String, body: String, channelId: String?): Flow<String> {
        return flow {
            try {
                // Создание сообщения
                val message = Message.builder()
                    .setToken(deviceToken)
                    .setNotification(
                        Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build()
                    )
                    .apply {
                        if (channelId?.isNotBlank() == true)
                            setAndroidConfig(
                                AndroidConfig.builder()
                                    .setNotification(
                                        AndroidNotification.builder()
                                            .setChannelId(channelId)
                                            .build()
                                    )
                                    .setPriority(AndroidConfig.Priority.HIGH)
                                    .build()
                            )

                    }.build()

                // Отправка сообщения
                val response = FirebaseMessaging.getInstance().send(message)
                emit("Успешно отправлено: $response")

            } catch (e: FirebaseMessagingException) {

                emit(e.httpResponse.content)
                e.printStackTrace()
            }
        }
    }


    fun sendNotificationWithCustomData(
        deviceToken: String,
        title: String,
        body: String,
        channelId: String?,
        isSilent: Boolean = false
    ): Flow<String> {

        return flow {
            try {
                val notificationID = Random.nextInt().toString()
                val collapseID = Random.nextInt().toString()

                val notificationMap: Map<String, String> = mapOf(
                    "a" to notificationID,
                    "e" to title,
                    "b" to if (isSilent) "1" else "0",
                    "g" to body,
                    "as" to (channelId ?: "")
                )
                val pushMap: Map<String, String> = mapOf(
                    "b" to "0",
                    "a" to collapseID,
                    "d" to Json.encodeToString(notificationMap)
                )

                val message = Message.builder()
                    .setToken(deviceToken)
                    .putData(
                        "yamp", Json.encodeToString(pushMap)
                    )
                    .build()


                val response = FirebaseMessaging.getInstance().send(message)
                emit("Успешно отправлено: $response")

            } catch (e: FirebaseMessagingException) {
                e.printStackTrace()
            }
        }


    }
}
