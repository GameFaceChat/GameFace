/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.calling

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.bases.BasePermissionsActivity
import com.magnitudestudios.GameFace.databinding.ActivityIncomingCallBinding
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import com.magnitudestudios.GameFace.pojo.VideoCall.Member
import com.magnitudestudios.GameFace.repository.SessionRepository
import com.magnitudestudios.GameFace.ui.main.MainActivity

class IncomingCall : BasePermissionsActivity() {
    private lateinit var bind: ActivityIncomingCallBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deleteNotification()
        bind = ActivityIncomingCallBinding.inflate(layoutInflater)
        setContentView(bind.root)

        if (!intent.hasExtra(Constants.ROOM_ID_KEY) || !intent.hasExtra(Constants.ROOM_MEMBERS_KEY)) finish()
        else if (Firebase.auth.currentUser == null) finish()

        val roomID = intent.getStringExtra(Constants.ROOM_ID_KEY)!!

        val memberProfiles = try {
            Gson().fromJson(intent.getStringExtra(Constants.ROOM_MEMBERS_KEY), object : TypeToken<List<Profile>>() {}.type) as List<Profile>
        } catch (e: Exception) {
            Log.e("INCOMING CALL", "Error upon deserialize JSON: "+ intent.getStringExtra(Constants.ROOM_MEMBERS_KEY), e)
            finish()
            ArrayList<Profile>()
        }

        Glide.with(this)
                .load(memberProfiles[0].profilePic)
                .placeholder(R.drawable.profile_placeholder)
                .error(R.drawable.ic_user_placeholder)
                .circleCrop()
                .into(bind.profilePic)

        bind.usernames.text = memberProfiles.joinToString(",") { it.username }

        bind.denyCall.setOnClickListener {
            SessionRepository.denyCall(Firebase.auth.currentUser!!.uid, roomID)
            finish()
        }

        bind.acceptCall.setOnClickListener {
            SessionRepository.acceptCall(Firebase.auth.currentUser!!.uid, roomID)
            val toMainActivity = Intent(this, MainActivity::class.java)
            toMainActivity.putExtra(Constants.ROOM_ID_KEY, roomID)
            toMainActivity.putExtra(Constants.CALL_KEY, "true")
            startActivity(toMainActivity)
            finish()
        }
    }

    private fun deleteNotification() {
        with (NotificationManagerCompat.from(this)) { cancel(Constants.INCOMING_CALL_ID) }
    }
}