/*
* Copyright (C) 2016 The OmniROM Project
* Copyright (C) 2020 The Android Ice Cold Project
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/
package com.aicp.device;

import android.content.ContentResolver;
import android.content.Context;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import android.database.ContentObserver;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Button;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;

public class VibratorStrengthPreference extends Preference implements
        SeekBar.OnSeekBarChangeListener {

    private SeekBar mSeekBar;
    private int mOldStrength;
    private int mMinValue;
    private int mMaxValue;
    private Vibrator mVibrator;

    private static final boolean DEBUG = false;
    private static final String TAG = "VibratorStrengthPreference";
    private static final String FILE_LEVEL = "/sys/devices/virtual/timed_output/vibrator/voltage_level";
    private static final long testVibrationPattern[] = {0,250};
    public static final String SETTINGS_KEY = DeviceSettings.KEY_SETTINGS_PREFIX + DeviceSettings.KEY_VIBSTRENGTH;
    public static final String DEFAULT_VALUE = "1800";

    public VibratorStrengthPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        // from input/misc/pm8xxx-vibrator.c
	// #define VIB_MAX_LEVEL_mV	(3100)
        // #define VIB_MIN_LEVEL_mV	(1200)
        mMinValue = 1200;
        mMaxValue = 3100;

        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        setLayoutResource(R.layout.preference_seek_bar);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        mOldStrength = Integer.parseInt(getValue(getContext()));
        mSeekBar = (SeekBar) holder.findViewById(R.id.seekbar);
        mSeekBar.setMax(mMaxValue - mMinValue);
        mSeekBar.setProgress(mOldStrength - mMinValue);
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    public static boolean isSupported() {
        return Utils.fileWritable(FILE_LEVEL);
    }

    public static String getValue(Context context) {
        Log.i(TAG,"reading sysfs file: "+FILE_LEVEL);
        String val = Utils.getFileValueVibrator(FILE_LEVEL, DEFAULT_VALUE);
        return val;
    }

    private void setValue(String newValue, boolean withFeedback) {
        Utils.writeValue(FILE_LEVEL, newValue);
        Settings.System.putString(getContext().getContentResolver(), SETTINGS_KEY, newValue);
        if (withFeedback) {
            mVibrator.vibrate(testVibrationPattern, -1);
        }
    }

    public static void restore(Context context) {
        if (!isSupported()) {
            return;
        }
        String storedValue = Settings.System.getString(context.getContentResolver(), SETTINGS_KEY);
	if (DEBUG) Log.d(TAG,"restore value:"+storedValue);
        if (storedValue == null) {
            storedValue = DEFAULT_VALUE;
        }
	if (DEBUG) Log.d(TAG,"restore file:"+FILE_LEVEL);
        Utils.writeValue(FILE_LEVEL, storedValue);
    }

    public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromTouch) {
        setValue(String.valueOf(progress + mMinValue), true);
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        // NA
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        // NA
    }
}

