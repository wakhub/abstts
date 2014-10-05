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
package com.github.wakhub.abstts;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.speech.tts.SynthesisCallback;
import android.speech.tts.SynthesisRequest;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.github.wakhub.abstts.pref.LanguageDetectionPrefs;
import com.github.wakhub.abstts.pref.LanguagePrefItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

/**
 * NOTE: Don't execute  initializedTtsList.remove() . Only shutdown() is acceptable.
 * <p/>
 * Created by wak on 7/20/14.
 */
public class AbsttsTextToSpeechService extends android.speech.tts.TextToSpeechService {

    private static final String TAG = AbsttsTextToSpeechService.class.getSimpleName();

    public static final String KEY_PARAM_AUTO_DETECTION = App.PACKAGE_NAME + ":AUTO_DETECTION";

    public static final String AUTO_DETECTION_ENABLED = "1";

    public static final String AUTO_DETECTION_DISABLED = "0";

    public static final String[] DEFAULT_ENGINES = new String[]{
            RemoteTextToSpeech.ENGINE.GOOGLE_TTS,
            RemoteTextToSpeech.ENGINE.SAMSUNG_SMT,
            RemoteTextToSpeech.ENGINE.SVOX_PICO,
            RemoteTextToSpeech.ENGINE.SVOX_CLLASIC,
            RemoteTextToSpeech.ENGINE.IVONA_TTS,
            RemoteTextToSpeech.ENGINE.AQUESTALK,
            RemoteTextToSpeech.ENGINE.N2_TTS,
            RemoteTextToSpeech.ENGINE.DTALKER,
            RemoteTextToSpeech.ENGINE.DTALKER_DEMO,
            RemoteTextToSpeech.ENGINE.VAJA_TTS
    };

    public static final String[] LANGUAGES = new String[]{
            "eng", "fra", "jpn", "rus", "zh", "tha", "hin", "kor"
    };

    public static final String KEY_PARAM_RATE = "rate";

    public static final String KEY_PARAM_PITCH = "pitch";

    private ArrayList<RemoteTextToSpeech> initializedTtsList = new ArrayList<RemoteTextToSpeech>();

    private int numberOfInitializingTts = 0;

    private SharedPreferences preferences;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        initializedTtsList = new ArrayList<RemoteTextToSpeech>();
        super.onCreate();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        numberOfInitializingTts = DEFAULT_ENGINES.length;
        for (final String name : DEFAULT_ENGINES) {
            final RemoteTextToSpeech tts = new RemoteTextToSpeech(this, new OnInitListener(this, name), name);
            Log.d(TAG, "tts: " + tts.toString());
            initializedTtsList.add(tts);
        }
    }

    @Override
    protected int onIsLanguageAvailable(String language, String country, String variant) {
        Log.d(TAG, "onIsLanguageAvailable: " + language);
        if (isLanguageAvailableInternal(new Locale(language))) {
            return TextToSpeech.LANG_AVAILABLE;
        }
        return TextToSpeech.LANG_NOT_SUPPORTED;
    }

    @Override
    protected String[] onGetLanguage() {
        Log.d(TAG, "onGetLanguage");
        return LANGUAGES;
    }

    @Override
    protected int onLoadLanguage(String language, String country, String variant) {
        Log.d(TAG, "onLoadLanguage: " + language);
        return onIsLanguageAvailable(language, country, variant);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        for (RemoteTextToSpeech tts : initializedTtsList) {
            tts.stop();
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        for (RemoteTextToSpeech tts : initializedTtsList) {
            if (!tts.isShutdown()) {
                tts.shutdown();
            }
        }
    }

    @Override
    protected void onSynthesizeText(SynthesisRequest synthesisRequest, SynthesisCallback synthesisCallback) {
        Bundle paramsBundle = synthesisRequest.getParams();
        HashMap<String, String> params = new HashMap<String, String>();
        for (String key : Arrays.asList(
                TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
                TextToSpeech.Engine.KEY_PARAM_VOLUME,
                TextToSpeech.Engine.KEY_PARAM_PAN,
                TextToSpeech.Engine.KEY_PARAM_STREAM,
                KEY_PARAM_RATE,
                KEY_PARAM_PITCH,
                KEY_PARAM_AUTO_DETECTION)) {
            String value = String.valueOf(paramsBundle.get(key));
            if (value != null && !value.equals("null")) {
                params.put(key, value);
            }
        }

        Locale locale = new Locale(synthesisRequest.getLanguage());
        String textForSpeech = synthesisRequest.getText();

        Log.d(TAG, String.format("onSynthesize: text=%s, locale=%s, params=%s",
                synthesisRequest.getText(),
                locale.getDisplayName(),
                params.toString()));

        ArrayList<RemoteTextToSpeech> ttsList = new ArrayList<RemoteTextToSpeech>(initializedTtsList);

        String autoDetectionParam = params.get(KEY_PARAM_AUTO_DETECTION);

        LanguageDetectionPrefs languageDetectionPrefs = new LanguageDetectionPrefs();
        languageDetectionPrefs.load(preferences);
        if ((autoDetectionParam == null && languageDetectionPrefs.getValueForEnabled())
                || (autoDetectionParam != null && autoDetectionParam.equals(AUTO_DETECTION_ENABLED))) {
            Locale detectedLocale = LanguageDetector.detectLanguage(textForSpeech, this);
            Log.d(TAG, "Language is detected as " + detectedLocale.getDisplayName());
            locale = detectedLocale;
        }

        LanguagePrefItem languagePrefItem = new LanguagePrefItem(locale);
        languagePrefItem.load(preferences);
        if (!languagePrefItem.getValueForEnable()) {
            Log.d(TAG, locale.getDisplayName() + " has been disabled by preference");
            return;
        }

        RemoteTextToSpeech defaultTts = getRemoteTextToSpeech(languagePrefItem.getValueForEngine());
        if (defaultTts != null) {
            ttsList.add(0, defaultTts);
        }
//        Log.d(TAG, "ttsList: " + ttsList.toString());

        for (RemoteTextToSpeech tts : ttsList) {
            if (tts.isLanguageAvailable(locale) == TextToSpeech.LANG_AVAILABLE) {
                Log.d(TAG, locale.getDisplayName() + " is available on " + tts.getEngineName());
                tts.setLanguage(locale);

                LanguagePrefItem prefItem = new LanguagePrefItem(locale);
                prefItem.load(preferences);

                float volume = getVolume(params, prefItem);
                float pan = getPan(params, prefItem);
                float pitch = getPitch(params, prefItem);
                float speechRate = getSpeechRate(params, prefItem);

                Log.d(TAG, String.format("speak: locale=%s, volume=%.2f, pan=%.2f, pitch=%.2f, speechRate=%.2f",
                        locale.getDisplayName(), volume, pan, pitch, speechRate));

                params.put(TextToSpeech.Engine.KEY_PARAM_VOLUME, String.valueOf(volume));
                params.put(TextToSpeech.Engine.KEY_PARAM_PAN, String.valueOf(pan));
                tts.setPitch(pitch);
                tts.setSpeechRate(speechRate);
                tts.speak(textForSpeech, TextToSpeech.QUEUE_ADD, params, synthesisCallback);

                // TODO: QUEUE_ADD doesn't work if remove while block (Need investigation)
                while (tts.isSpeaking()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                }

                return;
            }
        }
        synthesisCallback.error();
        Log.d(TAG, locale.getDisplayName() + " is not available");
    }

    private float getVolume(HashMap<String, String> params, LanguagePrefItem prefItem) {
        if (!params.containsKey(TextToSpeech.Engine.KEY_PARAM_VOLUME)) {
            return prefItem.getValueForVolume();
        }
        String valueString = params.get(TextToSpeech.Engine.KEY_PARAM_VOLUME);
        if (valueString != null && !valueString.equals("null")) {
            return Float.valueOf(valueString);
        }
        return 100f;
    }

    private float getPan(HashMap<String, String> params, LanguagePrefItem prefItem) {
        if (!params.containsKey(TextToSpeech.Engine.KEY_PARAM_PAN)) {
            return prefItem.getValueForPan();
        }
        String valueString = params.get(TextToSpeech.Engine.KEY_PARAM_PAN);
        if (valueString != null && !valueString.equals("null")) {
            return Float.valueOf(valueString);
        }
        return 0.5f;
    }

    private float getSpeechRate(HashMap<String, String> params, LanguagePrefItem prefItem) {
        if (!params.containsKey(KEY_PARAM_RATE)) {
            return prefItem.getValueForSpeechRate();
        }
        String valueString = params.get(KEY_PARAM_RATE);
        if (valueString != null && !valueString.equals("null")) {
            return Float.valueOf(valueString) / 100.f;
        }
        return getDefaultSpeechRate() / 100.f;
    }

    private float getPitch(HashMap<String, String> params, LanguagePrefItem prefItem) {
        if (!params.containsKey(KEY_PARAM_PITCH)) {
            return prefItem.getValueForPitch();
        }
        String valueString = params.get(KEY_PARAM_PITCH);
        if (valueString != null && !valueString.equals("null")) {
            return Float.valueOf(valueString) / 100.f;
        }
        return getDefaultPitch() / 100.f;
    }

    private int getDefaultSpeechRate() {
        return getSecureSettingInt(Settings.Secure.TTS_DEFAULT_RATE, 100);
    }

    private int getDefaultPitch() {
        return getSecureSettingInt(Settings.Secure.TTS_DEFAULT_PITCH, 100);
    }

    private int getSecureSettingInt(String name, int defaultValue) {
        return Settings.Secure.getInt(getContentResolver(), name, defaultValue);
    }

    private RemoteTextToSpeech getRemoteTextToSpeech(String name) {
        for (RemoteTextToSpeech tts : initializedTtsList) {
            if (tts.getEngineName().equals(name) && !tts.isShutdown()) {
                return tts;
            }
        }
        return null;
    }

    private void afterInit() {
        numberOfInitializingTts--;
        if (numberOfInitializingTts == 0) {
            Log.d(TAG, "Finish initializing: " + initializedTtsList.toString());
        }
    }

    private boolean isLanguageAvailableInternal(Locale locale) {
        for (RemoteTextToSpeech tts : initializedTtsList) {
            if (tts.isLanguageAvailable(locale) == TextToSpeech.LANG_AVAILABLE) {
                return true;
            }
        }
        return false;
    }

    private static class OnInitListener implements TextToSpeech.OnInitListener {

        private final WeakReference<AbsttsTextToSpeechService> serviceRef;

        private final String engineName;

        private OnInitListener(AbsttsTextToSpeechService service, String engineName) {
            serviceRef = new WeakReference<AbsttsTextToSpeechService>(service);
            this.engineName = engineName;
        }

        @Override
        public void onInit(int status) {
            Log.d(TAG, "onInit: " + engineName + " status=" + status);
            AbsttsTextToSpeechService service = serviceRef.get();
            if (service == null) {
                return;
            }

            RemoteTextToSpeech tts = service.getRemoteTextToSpeech(engineName);
            Log.d(TAG, "tts = " + tts.toString());

            boolean isEngineExists = false;
            for (TextToSpeech.EngineInfo engineInfo : tts.getEngines()) {
                if (engineInfo.name.equals(tts.getEngineName())) {
                    Log.d(TAG, String.format("%s is exits", engineInfo.name));
                    isEngineExists = true;
                }
            }

            if (status == TextToSpeech.ERROR || !isEngineExists) {
                tts.shutdown();
                service.afterInit();
                return;
            }

            boolean enabled = false;
            for (String name : LANGUAGES) {
                Locale locale = new Locale(name);
                if (tts.isLanguageAvailable(locale) == TextToSpeech.LANG_AVAILABLE) {
                    enabled = true;
                }
            }
            if (!enabled) {
                tts.shutdown();
            }
            service.afterInit();
        }
    }
}
