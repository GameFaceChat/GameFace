/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.databinding.ToolbarBackBinding
import com.magnitudestudios.GameFace.ui.main.MainViewModel

class SettingsFragment : PreferenceFragmentCompat() {
    lateinit var toolbar: ToolbarBackBinding
    lateinit var mainViewModel: MainViewModel
    val TITLE = "Settings"
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        findPreference<Preference>("signOutPref")?.setOnPreferenceClickListener {
            mainViewModel.signOutUser()
            return@setOnPreferenceClickListener true
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mainViewModel = activity?.run { ViewModelProvider(this).get(MainViewModel::class.java) }!!
        val layout = super.onCreateView(inflater, container, savedInstanceState)
        val viewWithToolbar = LinearLayout(context)
        viewWithToolbar.orientation = LinearLayout.VERTICAL
        toolbar = ToolbarBackBinding.inflate(inflater, container, false)
        toolbar.title.text = TITLE
        viewWithToolbar.addView(toolbar.root)
        viewWithToolbar.addView(layout)
        return viewWithToolbar
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}