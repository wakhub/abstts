/**
 * Copyright (C) 2014 wak
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.wakhub.abstts.pref;

import android.content.SharedPreferences;
import android.util.Log;

import com.github.wakhub.abstts.AbsttsTextToSpeechService;

import java.util.Locale;

/**
 * Created by wak on 9/20/14.
 */
public class LanguagePrefItem {

    private static final String TAG = LanguagePrefItem.class.getSimpleName();

    private static final String KEY_PREFIX = "language_";

    private final Locale locale;
    private final String key;

    private final String keyForEnable;
    private final String keyForEngine;
    private final String keyForVolume;
    private final String keyForSpeechRate;
    private final String keyForPitch;
    private final String keyForPan;

    private boolean valueForEnable = true;
    private String valueForEngine = AbsttsTextToSpeechService.DEFAULT_ENGINES[0];
    private float valueForVolume = 1.f;
    private float valueForSpeechRate = 1.f;
    private float valueForPitch = 1.f;
    private float valueForPan = 0.f;

    public static boolean isEnabled(Locale locale, SharedPreferences preferences) {
        LanguagePrefItem prefItem = new LanguagePrefItem(locale);
        return prefItem.getValueForEnable();
    }

    public LanguagePrefItem(Locale locale) {
        this.locale = locale;

        key = String.format("%s%s", KEY_PREFIX, locale.getISO3Language());
        String itemKeyPrefix = key + "_";

        keyForEnable = itemKeyPrefix + "enable";
        keyForEngine = itemKeyPrefix + "engine";
        keyForVolume = itemKeyPrefix + "volume";
        keyForPitch = itemKeyPrefix + "pitch";
        keyForSpeechRate = itemKeyPrefix + "speech_rate";
        keyForPan = itemKeyPrefix + "pan";
    }

    @Override
    public String toString() {
        return String.format("LanguagePrefItem(%s:%s(%s) volume=%.2f, pitch=%.2f, speech_rate=%.2f, pan=%.2f)",
                locale.getDisplayName(), valueForEngine, valueForEnable ? "enabled" : "disabled",
                valueForVolume, valueForPitch, valueForSpeechRate, valueForPan);
    }

    public String getTitle() {
        if (valueForEnable) {
            return String.format("%s - %s", locale.getDisplayName(), valueForEngine);
        } else {
            return String.format("%s (disabled)", locale.getDisplayName());
        }
    }

    public void load(SharedPreferences preferences) {
        valueForEnable = preferences.getBoolean(keyForEnable, valueForEnable);
        valueForEngine = preferences.getString(keyForEngine, valueForEngine);
        valueForVolume = preferences.getFloat(keyForVolume, valueForVolume);
        valueForPan = preferences.getFloat(keyForPan, valueForPan);
        valueForPitch = preferences.getFloat(keyForPitch, valueForPitch);
        valueForSpeechRate = preferences.getFloat(keyForSpeechRate, valueForSpeechRate);
    }

    public void commit(SharedPreferences preferences) {
        preferences.edit()
                .putBoolean(keyForEnable, valueForEnable)
                .putString(keyForEngine, valueForEngine)
                .putFloat(keyForVolume, valueForVolume)
                .putFloat(keyForPan, valueForPan)
                .putFloat(keyForPitch, valueForPitch)
                .putFloat(keyForSpeechRate, valueForSpeechRate)
                .commit();
        Log.d(TAG, "commit: " + toString());
    }

    public String getKey() {
        return key;
    }

    public boolean getValueForEnable() {
        return valueForEnable;
    }

    public void setValueForEnable(boolean valueForEnable) {
        this.valueForEnable = valueForEnable;
    }

    public String getValueForEngine() {
        return valueForEngine;
    }

    public void setValueForEngine(String valueForEngine) {
        this.valueForEngine = valueForEngine;
    }

    public float getValueForVolume() {
        return valueForVolume;
    }

    public void setValueForVolume(float valueForVolume) {
        this.valueForVolume = valueForVolume;
    }

    public float getValueForSpeechRate() {
        return valueForSpeechRate;
    }

    public void setValueForSpeechRate(float valueForSpeechRate) {
        this.valueForSpeechRate = valueForSpeechRate;
    }

    public float getValueForPitch() {
        return valueForPitch;
    }

    public void setValueForPitch(float valueForPitch) {
        this.valueForPitch = valueForPitch;
    }

    public float getValueForPan() {
        return valueForPan;
    }

    public void setValueForPan(float valueForPan) {
        this.valueForPan = valueForPan;
    }
}