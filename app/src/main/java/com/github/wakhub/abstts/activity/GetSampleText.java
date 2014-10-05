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
package com.github.wakhub.abstts.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.github.wakhub.abstts.App;

import java.util.Locale;

/**
 * Created by wak on 7/20/14.
 */
public class GetSampleText extends Activity {

    private static final String TAG = GetSampleText.class.getSimpleName();

    public static String getSampleTextForLocale(Locale locale) {
        if (locale.getISO3Language().equals(Locale.JAPANESE.getISO3Language())) {
            return "こんにちは、これは日本語のサンプルテキストです。";
        }
        if (locale.getISO3Language().equals(Locale.CHINESE.getISO3Language())) {
            return "您好，这是一个简单的文本为中国语言。";
        }
        if (locale.getISO3Language().equals(App.HINDI_LOCALE.getISO3Language())) {
            return "नमस्कार, इस हिन्दी के लिए एक नमूना पाठ है.";
        }
        if (locale.getISO3Language().equals(Locale.KOREAN.getISO3Language())) {
            return "안녕하세요, 한국 언어에 대한 샘플 텍스트입니다.";
        }
        if (locale.getISO3Language().equals(App.THAI_LOCALE.getISO3Language())) {
            return "สวัสดีนี้เป็นข้อความตัวอย่างสำหรับภาษาไทย";
        }
        if (locale.getISO3Language().equals(Locale.FRENCH.getISO3Language())) {
            return "Bonjour, ceci est un exemple de texte pour la langue française.";
        }
        if (locale.getISO3Language().equals(App.RUSSIAN_LOCALE.getISO3Language())) {
            return "Здравствуйте, это образец текста для французского языка.";
        }
        return "Hello, this is a sample text for English.";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        String language = i.getExtras().getString("language");
        String country = i.getExtras().getString("country");
        String variant = i.getExtras().getString("variant");

        Locale locale = new Locale(language);
        Intent data = new Intent();
        data.putExtra("sampleText", getSampleTextForLocale(locale));
        setResult(TextToSpeech.LANG_AVAILABLE, data);
        finish();
    }
}
