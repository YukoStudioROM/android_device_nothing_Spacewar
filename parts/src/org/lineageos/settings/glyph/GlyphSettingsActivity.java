/*
 * Copyright (C) 2022 The LineageOS Project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.settings.glyph;

import android.os.Bundle;

import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity;
import com.android.settingslib.R;

public class GlyphSettingsActivity extends CollapsingToolbarBaseActivity {

    private static final String TAG_GLYPH = "glyph";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(R.id.content_frame,
                new GlyphSettingsFragment(), TAG_GLYPH).commit();
    }
}
