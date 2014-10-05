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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.github.wakhub.abstts.pref.LanguageDetectionPrefs;
import com.github.wakhub.abstts.pref.LanguagePrefItem;

import java.util.Locale;

/**
 * Created by wak on 7/21/14.
 */
public class LanguageDetector {

    private static final String TAG = LanguageDetector.class.getSimpleName();

    private static final String HIRAGANA_KATAKANA_TEST = ".*[\\p{InHiragana}\\p{InKatakana}]+.*";
    private static final String CHINESE_CHARACTERS_TEST = ".*[\\p{InCJKUnifiedIdeographs}]+.*";
    private static final String HANGEUL_TEST = ".*[가-힣]+.*";
    private static final String THAI_TEST = ".*[\\p{InThai}]+.*";
    private static final String HINDI_TEST = ".*[\\p{InDevanagari}]+.*";
    private static final String ACCENT_CODES_TEST = ".*[ÉÀÈÙÂÊÎÔÛËÏÜÇŒÆéàèùâêîôûëïüçœæ]+.*";
    private static final String CYRILLIC_TEST = ".*[\\p{InCyrillic}]+.*";

    public static Locale detectLanguage(String text, Context context) {
        Log.d(TAG, "detectLanguage: " + text);
        try {
            return detectLanguageInternal(text, context);
        } catch (Exception e) {
            Log.d(TAG, "Detection error: " + e.toString(), e);
            return Locale.ENGLISH;
        }
    }

    private static Locale detectLanguageInternal(String text, Context context) {
        String[] split = text.split("\\n");
        text = split[0].trim();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

        LanguageDetectionPrefs languageDetectionPrefs = new LanguageDetectionPrefs();
        languageDetectionPrefs.load(preferences);

        if (!languageDetectionPrefs.getValueForEnabled()) {
            return Locale.ENGLISH;
        }

        boolean japaneseEnabled = LanguagePrefItem.isEnabled(Locale.JAPANESE, preferences);
        boolean chineseEnabled = LanguagePrefItem.isEnabled(Locale.CHINESE, preferences);
        boolean hindiEnabled = LanguagePrefItem.isEnabled(App.HINDI_LOCALE, preferences);
        boolean koreanEnabled = LanguagePrefItem.isEnabled(Locale.KOREAN, preferences);
        boolean thaiEnabled = LanguagePrefItem.isEnabled(App.THAI_LOCALE, preferences);
        boolean russianEnabled = LanguagePrefItem.isEnabled(App.RUSSIAN_LOCALE, preferences);
        boolean frenchEnabled = LanguagePrefItem.isEnabled(Locale.FRANCE, preferences);

        if (languageDetectionPrefs.getValueForHiraganaKatakanaAsJapanese()
                && text.matches(HIRAGANA_KATAKANA_TEST)
                && japaneseEnabled) {
            return Locale.JAPANESE;
        }
        if (languageDetectionPrefs.getValueForChineseCharacterAsJapanese()
                && text.matches(CHINESE_CHARACTERS_TEST)
                && japaneseEnabled) {
            return Locale.JAPANESE;
        }

        if (languageDetectionPrefs.getValueForChineseCharacterAsChinese()
                && text.matches(CHINESE_CHARACTERS_TEST)
                && chineseEnabled) {
            return Locale.CHINESE;
        }

        if (languageDetectionPrefs.getValueForHindiAlphabetAsHindi()
                && text.matches(HINDI_TEST)
                && hindiEnabled) {
            return App.HINDI_LOCALE;
        }

        if (languageDetectionPrefs.getValueForHangeulAsKorean()
                && text.matches(HANGEUL_TEST)
                && koreanEnabled) {
            return Locale.KOREAN;
        }

        if (languageDetectionPrefs.getValueForThaiAlphabetAsThai()
                && text.matches(THAI_TEST)
                && thaiEnabled) {
            return App.THAI_LOCALE;
        }

        if (languageDetectionPrefs.getValueForAccentCodesAsFrench()
                && text.matches(ACCENT_CODES_TEST)
                && frenchEnabled) {
            return Locale.FRANCE;
        }

        if (languageDetectionPrefs.getValueForCyrillicAlphabetAsRussian()
                && text.matches(CYRILLIC_TEST)
                && russianEnabled) {
            return App.RUSSIAN_LOCALE;
        }
        return Locale.ENGLISH;
    }
}
