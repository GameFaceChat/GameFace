/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.pojo.UserInfo

import androidx.annotation.NonNull
import com.google.firebase.database.Exclude

data class User(
        @JvmField
        @NonNull
        var uid: String = "",

        @JvmField
        var created: Any? = null,

        @JvmField
        var devicesID: HashMap<String, Boolean> = HashMap(),

        @JvmField
        var friendRequests: HashMap<String, FriendRequest> = HashMap(),

        @JvmField
        var friendRequestsSent: HashMap<String, FriendRequest> = HashMap(),

        @JvmField
        var friends: Map<String, Friend> = HashMap()


) {
        @Exclude
        fun getCreated(): Long? {
                return if (created is Long) {
                        created as Long?
                } else {
                        null
                }
        }
}