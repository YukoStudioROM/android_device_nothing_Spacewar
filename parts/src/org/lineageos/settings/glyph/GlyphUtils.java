/*
 * Copyright (C) 2022 The LineageOS Project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.settings.glyph;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.provider.Settings;

import androidx.preference.PreferenceManager;

import org.lineageos.settings.util.FileUtils;

public class GlyphUtils {

    public static void startService(Context context) {
        context.startServiceAsUser(new Intent(context, GlyphService.class), UserHandle.CURRENT);
    }

    public static void enableGlyph(Context context, boolean enable) {
        Settings.Secure.putInt(context.getContentResolver(), "glyph_enable", enable ? 1 : 0);
    }

    public static boolean isGlyphEnabled(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "glyph_enable", 1) != 0;
    }

    public static boolean isGlyphChargingMeterEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                "glyph_charging_meter", false);
    }

    public static int getGlyphBrightness(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(
                "glyph_brightness", /* default brightness */ 1365);
    }

    public static void writeLedById(int id, int brightness) {
        FileUtils.writeLine("/sys/class/leds/aw210xx_led/single_led_br",
                id + " " + brightness);
    }
}
