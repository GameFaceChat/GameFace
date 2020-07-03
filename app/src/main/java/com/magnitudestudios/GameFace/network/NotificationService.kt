/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.network


import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import com.magnitudestudios.GameFace.pojo.VideoCall.SendCall
import com.magnitudestudios.GameFace.repository.FirebaseHelper
import com.magnitudestudios.GameFace.ui.calling.IncomingCall
import com.magnitudestudios.GameFace.ui.main.MainActivity
import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.random.Random

class NotificationService : FirebaseMessagingService() {
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        try {

            serviceScope.launch {
                if (Firebase.auth.currentUser != null) {
                    FirebaseHelper.updateDeviceToken(token)
                }
            }
        } catch (e: Exception) {
            Log.e("ERROR", "ON NEW DEVICE TOKEN", e)
        }

        Log.e("--NEW TOKEN--", token)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        for (a in p0.data) {
            Log.e("Got Data", "${a.key} : ${a.value}")
        }
        if (!validateMessage(p0)) return
        when (p0.data["type"]) {
            "CALL" -> receiveCall(p0.data)
            "NOTIFICATION" -> showNotification(p0.data)
        }
    }

    private fun receiveCall(data: Map<String, String>) {
        val fullScreenIntent = Intent(this, IncomingCall::class.java).apply {
            putExtra(SendCall::roomID.name, data["roomID"])
            putExtra(Profile::username.name, data["fromUsername"])
            putExtra(Profile::name.name, data["fromName"])
            putExtra(Profile::uid.name, data["fromUID"])
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder = NotificationCompat.Builder(this, getString(R.string.calling_notification_ID))
                .setSmallIcon(R.drawable.logo_simple_rainbow)
                .setContentTitle("Incoming Video Call")
                .setContentText("From: "+ data["fromUsername"])
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setVibrate(Constants.VIBRATE_PATTERN)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
        with(NotificationManagerCompat.from(this)) {
            notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
        }
    }

    private fun showNotification(data: Map<String, String>) {
        val pendingIntent = PendingIntent.getActivity(this, 1, Intent(this, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationBuilder = NotificationCompat.Builder(this, getString(R.string.friends_notification_ID))
                .setSmallIcon(R.drawable.logo_simple_rainbow)
                .setContentTitle(data["title"])
                .setContentText(data["body"])
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
        }
    }

    private fun validateMessage(message: RemoteMessage): Boolean {
        if (message.data.isEmpty() || !message.data.containsKey("type")) return false
        else if (Firebase.auth.currentUser == null) return false
        else if (!message.data.containsKey("toUID") || message.data["toUID"] != Firebase.auth.currentUser?.uid) return false
        return true
    }

    override fun onMessageSent(p0: String) {
        super.onMessageSent(p0)
    }
}