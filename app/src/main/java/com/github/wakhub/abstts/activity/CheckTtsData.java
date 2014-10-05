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

import com.github.wakhub.abstts.AbsttsTextToSpeechService;

import java.util.ArrayList;

/**
 * Created by wak on 7/20/14.
 */
public class CheckTtsData extends Activity {

    private final String TAG = CheckTtsData.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        ArrayList<String> available = new ArrayList<String>();
        ArrayList<String> unavailable = new ArrayList<String>();
        for (String language : AbsttsTextToSpeechService.LANGUAGES) {
            available.add(language);
        }

        Intent data = new Intent();
        data.putStringArrayListExtra("availableVoices", available);
        data.putStringArrayListExtra("unavailableVoices", unavailable);
        setResult(TextToSpeech.Engine.CHECK_VOICE_DATA_PASS, data);
        finish();
    }
}
