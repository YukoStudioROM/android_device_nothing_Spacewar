/*
 * Copyright (C) 2022 The LineageOS Project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.settings.glyph;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;

import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;

import org.lineageos.settings.R;

public class GlyphSettingsFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener, OnMainSwitchChangeListener {

    private MainSwitchPreference mSwitchBar;
    private SeekBarPreference mBrightnessPreference;
    private SwitchPreference mGlyphChargingMeterPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.glyph_settings);

        boolean isGlyphEnabled = GlyphUtils.isGlyphEnabled(getActivity());

        mSwitchBar = findPreference("glyph_enable");
        mSwitchBar.setChecked(isGlyphEnabled);
        mSwitchBar.addOnSwitchChangeListener(this);

        mBrightnessPreference = findPreference("glyph_brightness");
        mBrightnessPreference.setOnPreferenceChangeListener(this);

        mGlyphChargingMeterPreference = findPreference("glyph_charging_meter");
        mGlyphChargingMeterPreference.setOnPreferenceChangeListener(this);

        updatePreferences();
    }

    private void updatePreferences() {
        boolean isGlyphEnabled = GlyphUtils.isGlyphEnabled(getActivity());
        mBrightnessPreference.setEnabled(isGlyphEnabled);
        mGlyphChargingMeterPreference.setEnabled(isGlyphEnabled);
    }

    @Override
    public void onSwitchChanged(Switch switchView, boolean isChecked) {
        GlyphUtils.enableGlyph(getActivity(), isChecked);
        updatePreferences();
        GlyphUtils.startService(getActivity());
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        GlyphUtils.startService(getActivity());
        return true;
    }
}
