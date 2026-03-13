package fcm

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.FileInputStream


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
                                    .setNotification(AndroidNotification.builder()
                                        .setChannelId(channelId)
                                        .build())
                                    .setPriority(AndroidConfig.Priority.HIGH)
                                    .build())

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
        channelId: String?
    ): Flow<String> {

        return flow {
            try {
                val message = Message.builder()
                    .setToken(deviceToken)
                    .putData("a", "myMessage")
                    .putData("d", "https://")
                    .putData("t", title)
                    .putData("b", body)
                    .setAndroidConfig(
                        AndroidConfig.builder()
                            .apply {
                                if (channelId?.isNotEmpty() == true)
                                    setNotification(
                                        AndroidNotification.builder()
                                            .setChannelId(channelId)
                                            .build()

                                    )
                            }
                            .setPriority(AndroidConfig.Priority.HIGH).build()
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
