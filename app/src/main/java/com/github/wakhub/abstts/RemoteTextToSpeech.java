/**
 * Copyright (C) 2014 wak
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.wakhub.abstts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioFormat;
import android.os.Build;
import android.speech.tts.SynthesisCallback;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by wak on 9/20/14.
 */
public class RemoteTextToSpeech extends TextToSpeech {

    private static final String TAG = RemoteTextToSpeech.class.getSimpleName();

    public static final class ENGINE {
        public static final String AQUESTALK = "com.a_quest.aquestalka";
        public static final String DTALKER = "jp.co.createsystem";
        public static final String DTALKER_DEMO = "jp.co.createsystem.DTalkerTtsDemo";
        public static final String GOOGLE_TTS = "com.google.android.tts";
        public static final String IVONA_TTS = "com.ivona.tts";
        public static final String N2_TTS = "jp.kddilabs.n2tts";
        public static final String SAMSUNG_SMT = "com.samsung.SMT";
        public static final String SVOX_CLLASIC = "com.svox.classic";
        public static final String SVOX_PICO = "com.svox.pico";
        public static final String VAJA_TTS = "com.spt.tts.vaja";
    }

    private final String engineName;

    private WeakReference<SynthesisCallback> synthesisCallbackRef = new WeakReference<SynthesisCallback>(null);

    @Override
    public int stop() {
        Log.d(TAG, "stop");
        return super.stop();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public RemoteTextToSpeech(Context context, OnInitListener listener, String engineName) {
        super(context, listener, engineName);
        this.engineName = engineName;

//        setOnUtteranceCompletedListener(this);

        final WeakReference<RemoteTextToSpeech> ttsRef = new WeakReference<RemoteTextToSpeech>(this);

        // http://stackoverflow.com/questions/11409177/unable-to-detect-completion-of-tts-callback-android
        if (Build.VERSION.SDK_INT >= 15) {
            Log.d(TAG, "setOnUtteranceProgressListener");
            int result = setOnUtteranceProgressListener(new UtteranceProgressListener() {

                @Override
                public void onDone(String utteranceId) {
                    if (ttsRef.get() != null) {
                        ttsRef.get().onUtteranceDone(utteranceId);
                    }
                }

                @Override
                public void onError(String utteranceId) {
                    if (ttsRef.get() != null) {
                        ttsRef.get().onUtteranceError(utteranceId);
                    }
                }

                @Override
                public void onStart(String utteranceId) {
                    if (ttsRef.get() != null) {
                        ttsRef.get().onUtteranceStart(utteranceId);
                    }
                }
            });
            if (result != TextToSpeech.SUCCESS) {
                Log.d(TAG, "failed to add utterance progress listener");
            }
        } else {
            Log.d(TAG, "setOnUtteranceCompletedListener");
            int result = setOnUtteranceCompletedListener(new OnUtteranceCompletedListener() {
                @Override
                public void onUtteranceCompleted(String utteranceId) {
                    if (ttsRef.get() != null) {
                        ttsRef.get().onUtteranceDone(utteranceId);
                    }
                }
            });
            if (result != TextToSpeech.SUCCESS) {
                Log.e(TAG, "failed to add utterance completed listener");
            }
        }
    }

    @Override
    public String toString() {
        return String.format("%s (engineName=%s)", super.toString(), engineName);
    }

    private boolean shutdown = false;

    public boolean isShutdown() {
        return shutdown;
    }

    @Override
    public void shutdown() {
        Log.d(TAG, "shutdown: " + engineName);
        super.shutdown();
        shutdown = true;
    }

    public int speak(String text,
                     int queueMode,
                     HashMap<String, String> params,
                     SynthesisCallback synthesisCallback) {
        this.synthesisCallbackRef = new WeakReference<SynthesisCallback>(synthesisCallback);
        if (Build.VERSION.SDK_INT < 15) {
            onUtteranceStart(params.get(Engine.KEY_PARAM_UTTERANCE_ID));
        }
        int result = speak(text, queueMode, params);

        switch (result) {
            case TextToSpeech.SUCCESS:
                break;
            case TextToSpeech.ERROR:
                synchronized (this) {
                    synthesisCallback.error();
                }
                break;
        }
        return result;
    }

    private void onUtteranceDone(String utteranceId) {
        Log.d(TAG, "onUtteranceDone: " + utteranceId);
        // Handled by remote app
        if (synthesisCallbackRef.get() != null) {
            synchronized (synthesisCallbackRef.get()) {
                Log.d(TAG, "Call remote synthesisCallback.done(): " + this.synthesisCallbackRef.get());
                synthesisCallbackRef.get().done();
                synthesisCallbackRef = new WeakReference<SynthesisCallback>(null);
                // TODO: FBReader stopped with: com.github.wakhub.abstts W/PlaybackSynthesisRequest﹕ Duplicate call to done()
            }
        }
    }

    private void onUtteranceError(String utteranceId) {
        if (synthesisCallbackRef.get() != null) {
            Log.d(TAG, "Call remote synthesisCallback.error(): " + this.synthesisCallbackRef.get());
            // Handled by remote app
            synthesisCallbackRef.get().error();
        }
    }

    private void onUtteranceStart(String utteranceId) {
        if (synthesisCallbackRef.get() != null) {
            synchronized (synthesisCallbackRef.get()) {
                synthesisCallbackRef.get().start(16000, AudioFormat.ENCODING_DEFAULT, 1);
            }
        }
    }

    public String getEngineName() {
        return engineName;
    }
}
