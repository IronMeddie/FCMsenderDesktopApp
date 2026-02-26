package fcm

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.FileInputStream


class FCMService {

    fun initializeFirebase(filePath :String = "service-account-key2.json") {
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

    fun sendNotification(deviceToken: String, title: String, body: String): Flow<String> {
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
//                .putData("custom_key", "custom_value") // Дополнительные данные
                    .setAndroidConfig(
                        AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .build()
                    )
                    .build()

                // Отправка сообщения
                val response = FirebaseMessaging.getInstance().send(message)
                emit("Успешно отправлено: $response")

            } catch (e: FirebaseMessagingException) {

                emit(e.httpResponse.content)
                e.printStackTrace()
            }
        }
    }

    // Отправка multicast сообщения (нескольким устройствам)
    fun sendMulticastNotification(deviceTokens: List<String>, title: String, body: String) {
        try {
            val message = MulticastMessage.builder()
                .addAllTokens(deviceTokens)
                .setNotification(
                    Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build()
                )
                .build()

            val response = FirebaseMessaging.getInstance().sendMulticast(message)
            println("Успешно отправлено: ${response.successCount}")
            println("Ошибок: ${response.failureCount}")

        } catch (e: FirebaseMessagingException) {
            e.printStackTrace()
        }
    }
}
