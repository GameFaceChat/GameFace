/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.profile

import androidx.lifecycle.*
import com.magnitudestudios.GameFace.callbacks.RoomCallback
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import com.magnitudestudios.GameFace.repository.FirebaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ProfileViewModel : ViewModel() {
    val friendProfiles = MutableLiveData<List<Profile>>()
    private val queryString = MutableLiveData<String>()
    val searchResultsFriends = Transformations.switchMap(queryString) { query -> getFilteredFriendProfiles(query) }

    val requestUIDs = MutableLiveData<List<String>>()

    val requestProfiles = Transformations.switchMap(requestUIDs) {
        liveData {
            emit(FirebaseHelper.getUserProfilesByUID(it))
        }
    }

    private fun getFilteredFriendProfiles(query: String): LiveData<List<Profile>> {
        return Transformations.map(friendProfiles) { input: List<Profile>? -> input?.filter { query.isEmpty() || it.username.startsWith(query) || it.name.contains(query) } }
    }

    fun getFriendProfiles(uids: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            friendProfiles.postValue(FirebaseHelper.getUserProfilesByUID(uids))
        }
    }
    
    fun setRequestUIDs(uids: List<String>) {
        requestUIDs.value = uids
    }

    fun setQueryFriend(query: String) {
        queryString.value = query
    }

    fun acceptFriendRequest(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseHelper.acceptFriendRequest(uid)
        }
    }

    fun denyFriendRequest(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseHelper.deleteFriendRequest(uid, true)
        }
    }

    fun callUser(userProfile: Profile, toUID: String, url: String, callback: RoomCallback) {
//        viewModelScope.launch {
//            val roomID = SessionHelper.createRoom(callback, userProfile.uid)
//            HTTPRequest.callUser(url, SendCall(userProfile, toUID, roomID))
//        }

    }
}