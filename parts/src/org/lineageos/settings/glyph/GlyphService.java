/*
 * Copyright (C) 2022 The LineageOS Project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.settings.glyph;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class GlyphService extends Service {

    private static final boolean DEBUG = false;
    private static final String TAG = "GlyphService";

    private static final int LED_DISPLAY = 5000;
    private static final int LED_LIGHT_DELAY = 25;
    private static final int[] HORSE_RACE_LEDS = {13, 11, 9, 12, 10, 14, 15, 8};

    private BatteryManager mBatteryManager;
    private boolean mGlyphChargingMeterEnabled;
    private int mGlyphBrightness;
    private boolean mGlyphPlaying;
    private LedHandler mHandler;

    @Override
    public void onCreate() {
        if (DEBUG) Log.d(TAG, "Creating service");

        mBatteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
        mHandler = new LedHandler(Looper.getMainLooper());

        IntentFilter powerStateFilter = new IntentFilter();
        powerStateFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        powerStateFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(mPowerStateReceiver, powerStateFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mGlyphChargingMeterEnabled = GlyphUtils.isGlyphEnabled(this)
                && GlyphUtils.isGlyphChargingMeterEnabled(this);
        mGlyphBrightness = GlyphUtils.getGlyphBrightness(this);
        if (DEBUG) {
            Log.d(TAG, "Starting service");
            Log.d(TAG, "mGlyphChargingMeterEnabled = " + mGlyphChargingMeterEnabled);
            Log.d(TAG, "mGlyphBrightness = " + mGlyphBrightness);
        }

        if (!mGlyphChargingMeterEnabled) {
            onPowerDisconnected();
        }
        if (mBatteryManager.isCharging()) {
            onPowerConnected();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "Destroying service");
        onPowerDisconnected();
        unregisterReceiver(mPowerStateReceiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void onPowerConnected() {
        if (!mGlyphChargingMeterEnabled || mGlyphPlaying) return;
        mGlyphPlaying = true;
        Message message = Message.obtain(mHandler, LedHandler.MSG_LIGHT_LED, 0);
        mHandler.sendMessageDelayed(message, LED_LIGHT_DELAY);
    }

    private void onPowerDisconnected() {
        if (mGlyphPlaying) {
            mHandler.removeMessages(LedHandler.MSG_LIGHT_LED);
            mHandler.removeMessages(LedHandler.MSG_EXTINCT_LED);
            extinctAllLeds();
        }
        mGlyphPlaying = false;
    }

    private void extinctLed(int id) {
        if (DEBUG) Log.d(TAG, "Extinct " + id + ", " + System.currentTimeMillis());
        GlyphUtils.writeLedById(HORSE_RACE_LEDS[id], 0);
        if (id != 0) {
            Message message = Message.obtain(mHandler, LedHandler.MSG_EXTINCT_LED, id - 1);
            mHandler.sendMessageDelayed(message, LED_LIGHT_DELAY);
        }
    }

    private void lightLed(int id) {
        if (DEBUG) Log.d(TAG, "Light " + id + ", " + System.currentTimeMillis());
        int batteryCapacity =
                mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        int ledLength = (int) Math.round(batteryCapacity / 12.5);
        GlyphUtils.writeLedById(HORSE_RACE_LEDS[id], mGlyphBrightness);
        if (ledLength > id + 1) {
            Message message = Message.obtain(mHandler, LedHandler.MSG_LIGHT_LED, id + 1);
            mHandler.sendMessageDelayed(message, LED_LIGHT_DELAY);
        } else {
            Message message = Message.obtain(mHandler, LedHandler.MSG_EXTINCT_LED, id);
            mHandler.sendMessageDelayed(message, LED_DISPLAY);
        }
    }

    private void extinctAllLeds() {
        for (int i = HORSE_RACE_LEDS.length - 1; i >= 0; i--) {
            GlyphUtils.writeLedById(HORSE_RACE_LEDS[i], 0);
        }
    }

    private BroadcastReceiver mPowerStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
                onPowerConnected();
            } else if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
                onPowerDisconnected();
            }
        }
    };

    private class LedHandler extends Handler {

        private static final int MSG_LIGHT_LED = 1;
        private static final int MSG_EXTINCT_LED = 2;

        public LedHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LIGHT_LED:
                    lightLed((int) msg.obj);
                    break;
                case MSG_EXTINCT_LED:
                    extinctLed((int) msg.obj);
                    break;
            }
        }
    }
}
