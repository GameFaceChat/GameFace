package com.magnitudestudios.GameFace

import android.Manifest

object Constants {
    const val ALL_PERMISSIONS = 101
    const val STATE_COMPLETED = 200
    const val STATE_URL_FAILED = 201
    const val STATE_CONNECTED = 1
    const val STATE_FAILED = -1
    const val STATE_DISCONNECTED = -2
    val PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_WIFI_STATE
    )

    //Firebase paths
    const val ROOMS_PATH = "rooms"
    const val USERS_PATH = "users"

    //Firebase signalling keywords
    const val JOINED_KEY = "JOINED"
    const val LEFT_KEY = "LEFT"
    const val OFFER_KEY = "OFFER"
    const val ANSWER_KEY = "ANSWER"
    const val ICE_CANDIDATE_KEY = "ICECANDIDATE"
}