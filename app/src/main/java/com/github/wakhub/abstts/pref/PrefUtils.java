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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.github.wakhub.abstts.AbsttsTextToSpeechService;
import com.github.wakhub.abstts.App;

import java.lang.ref.WeakReference;
import java.util.Locale;

/**
 * Created by wak on 9/20/14.
 */
public class PrefUtils {

    private final static String TAG = PrefUtils.class.getSimpleName();

    public final static String KEY_SYSTEM_VERSION_CODE = "system_version_code";

    private final WeakReference<Context> contextRef;

    private final SharedPreferences preferences;

    public PrefUtils(Context context) {
        contextRef = new WeakReference<Context>(context);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        int versionCode = App.getPackageInfo(context).versionCode;
        int prefVersionCode = preferences.getInt(KEY_SYSTEM_VERSION_CODE, 0);
        Log.d(TAG, String.format("versionCode=%d, prefVersionCode=%d", versionCode, prefVersionCode));
        if (prefVersionCode < versionCode) {
            if (prefVersionCode == 0) {
                newInstall(versionCode);
            } else {
                versionUp(versionCode);
            }
        }
    }

    private void newInstall(int versionCode) {
        Log.d(TAG, "newInstall: " + versionCode);
        preferences.edit().putInt(KEY_SYSTEM_VERSION_CODE, versionCode).commit();
        initValues();
    }

    private void versionUp(int versionCode) {
        Log.d(TAG, "versionUp: " + versionCode);
        preferences.edit().putInt(KEY_SYSTEM_VERSION_CODE, versionCode).commit();
        initValues();
    }

    private void initValues() {
        LanguageDetectionPrefs languageDetectionPrefs = new LanguageDetectionPrefs();
        languageDetectionPrefs.commit(preferences);
        for (String language : AbsttsTextToSpeechService.LANGUAGES) {
            LanguagePrefItem prefItem = new LanguagePrefItem(new Locale(language));
            prefItem.commit(preferences);
        }
    }
}
