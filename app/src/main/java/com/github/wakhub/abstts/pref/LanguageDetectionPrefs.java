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

/**
 * Created by wak on 10/5/14.
 */
public class LanguageDetectionPrefs {

    public static final String KEY_ENABLED = "language_detection_enabled";
    public static final String KEY_HIRAGANA_KATAKANA_AS_JAPANESE = "language_detection_hiragana_katakana_as_japanese";
    public static final String KEY_CHINESE_CHARACTERS_AS_JAPANESE = "language_detection_chinese_characters_as_japanese";
    public static final String KEY_CHINESE_CHARACTERS_AS_CHINESE = "language_detection_chinese_characters_as_chinese";
    public static final String KEY_HINDI_ALPHABET_AS_HINDI = "language_detection_hindi_alphabet_as_hindi";
    public static final String KEY_HANGEUL_AS_KOREAN = "language_detection_hangeul_as_korean";
    public static final String KEY_THAI_ALPHABET_AS_THAI = "language_detection_thai_alphabet_as_thai";
    public static final String KEY_ACCENT_CODES_AS_FRENCH = "language_detection_accent_codes_as_french";
    public static final String KEY_CYRILLIC_ALPHABET_AS_RUSSIAN = "language_detection_cyrillic_alphabet_as_russian";

    private boolean valueForEnabled = true;
    private boolean valueForHiraganaKatakanaAsJapanese = true;
    private boolean valueForChineseCharacterAsJapanese = true;
    private boolean valueForChineseCharacterAsChinese = false;
    private boolean valueForHindiAlphabetAsHindi = true;
    private boolean valueForHangeulAsKorean = true;
    private boolean valueForThaiAlphabetAsThai = true;
    private boolean valueForAccentCodesAsFrench = true;
    private boolean valueForCyrillicAlphabetAsRussian = true;

    @Override
    public String toString() {
        return super.toString();
    }

    public void load(SharedPreferences preferences) {
        valueForEnabled = preferences.getBoolean(KEY_ENABLED, valueForEnabled);
        valueForHiraganaKatakanaAsJapanese =
                preferences.getBoolean(KEY_HIRAGANA_KATAKANA_AS_JAPANESE, valueForHiraganaKatakanaAsJapanese);
        valueForChineseCharacterAsJapanese =
                preferences.getBoolean(KEY_CHINESE_CHARACTERS_AS_JAPANESE, valueForChineseCharacterAsJapanese);
        valueForChineseCharacterAsChinese =
                preferences.getBoolean(KEY_CHINESE_CHARACTERS_AS_CHINESE, valueForChineseCharacterAsChinese);
        valueForHindiAlphabetAsHindi =
                preferences.getBoolean(KEY_HINDI_ALPHABET_AS_HINDI, valueForHindiAlphabetAsHindi);
        valueForHangeulAsKorean =
                preferences.getBoolean(KEY_HANGEUL_AS_KOREAN, valueForHangeulAsKorean);
        valueForThaiAlphabetAsThai =
                preferences.getBoolean(KEY_THAI_ALPHABET_AS_THAI, valueForThaiAlphabetAsThai);
        valueForAccentCodesAsFrench =
                preferences.getBoolean(KEY_ACCENT_CODES_AS_FRENCH, valueForAccentCodesAsFrench);
        valueForCyrillicAlphabetAsRussian =
                preferences.getBoolean(KEY_CYRILLIC_ALPHABET_AS_RUSSIAN, valueForCyrillicAlphabetAsRussian);
    }

    public void commit(SharedPreferences preferences) {
        preferences.edit()
                .putBoolean(KEY_ENABLED, valueForEnabled)
                .putBoolean(KEY_HIRAGANA_KATAKANA_AS_JAPANESE, valueForHiraganaKatakanaAsJapanese)
                .putBoolean(KEY_CHINESE_CHARACTERS_AS_JAPANESE, valueForChineseCharacterAsJapanese)
                .putBoolean(KEY_CHINESE_CHARACTERS_AS_CHINESE, valueForChineseCharacterAsChinese)
                .putBoolean(KEY_HINDI_ALPHABET_AS_HINDI, valueForHindiAlphabetAsHindi)
                .putBoolean(KEY_HANGEUL_AS_KOREAN, valueForHangeulAsKorean)
                .putBoolean(KEY_THAI_ALPHABET_AS_THAI, valueForThaiAlphabetAsThai)
                .putBoolean(KEY_ACCENT_CODES_AS_FRENCH, valueForAccentCodesAsFrench)
                .putBoolean(KEY_CYRILLIC_ALPHABET_AS_RUSSIAN, valueForCyrillicAlphabetAsRussian)
                .commit();
    }

    public boolean getValueForEnabled() {
        return valueForEnabled;
    }

    public void setValueForEnabled(boolean valueForEnabled) {
        this.valueForEnabled = valueForEnabled;
    }

    public boolean getValueForHiraganaKatakanaAsJapanese() {
        return valueForHiraganaKatakanaAsJapanese;
    }

    public void setValueForHiraganaKatakanaAsJapanese(boolean valueForHiraganaKatakanaAsJapanese) {
        this.valueForHiraganaKatakanaAsJapanese = valueForHiraganaKatakanaAsJapanese;
    }

    public boolean getValueForChineseCharacterAsJapanese() {
        return valueForChineseCharacterAsJapanese;
    }

    public void setValueForChineseCharacterAsJapanese(boolean valueForChineseCharacterAsJapanese) {
        this.valueForChineseCharacterAsJapanese = valueForChineseCharacterAsJapanese;
    }

    public boolean getValueForChineseCharacterAsChinese() {
        return valueForChineseCharacterAsChinese;
    }

    public void setValueForChineseCharacterAsChinese(boolean valueForChineseCharacterAsChinese) {
        this.valueForChineseCharacterAsChinese = valueForChineseCharacterAsChinese;
    }

    public boolean getValueForHindiAlphabetAsHindi() {
        return valueForHindiAlphabetAsHindi;
    }

    public void setValueForHindiAlphabetAsHindi(boolean valueForHindiAlphabetAsHindi) {
        this.valueForHindiAlphabetAsHindi = valueForHindiAlphabetAsHindi;
    }

    public boolean getValueForHangeulAsKorean() {
        return valueForHangeulAsKorean;
    }

    public void setValueForHangeulAsKorean(boolean valueForHangeulAsKorean) {
        this.valueForHangeulAsKorean = valueForHangeulAsKorean;
    }

    public boolean getValueForThaiAlphabetAsThai() {
        return valueForThaiAlphabetAsThai;
    }

    public void setValueForThaiAlphabetAsThai(boolean valueForThaiAlphabetAsThai) {
        this.valueForThaiAlphabetAsThai = valueForThaiAlphabetAsThai;
    }

    public boolean getValueForAccentCodesAsFrench() {
        return valueForAccentCodesAsFrench;
    }

    public void setValueForAccentCodesAsFrench(boolean valueForAccentCodesAsFrench) {
        this.valueForAccentCodesAsFrench = valueForAccentCodesAsFrench;
    }

    public boolean getValueForCyrillicAlphabetAsRussian() {
        return valueForCyrillicAlphabetAsRussian;
    }

    public void setValueForCyrillicAlphabetAsRussian(boolean valueForCyrillicAlphabetAsRussian) {
        this.valueForCyrillicAlphabetAsRussian = valueForCyrillicAlphabetAsRussian;
    }
}
