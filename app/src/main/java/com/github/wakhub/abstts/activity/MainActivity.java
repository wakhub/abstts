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
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.wakhub.abstts.AbsttsTextToSpeechService;
import com.github.wakhub.abstts.App;
import com.github.wakhub.abstts.R;
import com.github.wakhub.abstts.pref.LanguagePrefItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String AUTO_DETECTION = "Auto Detection";

    private EditText editText;
    private Button speechButton;
    private Spinner languagesSpinner;
    private CheckBox languageUseDefaultCheckBox;
    private SeekBar volumeSeekBar;
    private Button volumeResetButton;
    private SeekBar panSeekBar;
    private Button panResetButton;
    private SeekBar rateSeekBar;
    private Button rateResetButton;
    private SeekBar pitchSeekBar;
    private Button pitchResetButton;

    private SharedPreferences preferences;

    private TextToSpeech tts;

    private LanguagePrefItem currentLanguagePrefItem = null;

    private void tieDefaultValues() {
        if (currentLanguagePrefItem == null) {
            untieDefaultValues();
            return;
        }
        Log.d(TAG, currentLanguagePrefItem.toString());
        volumeSeekBar.setProgress((int) (currentLanguagePrefItem.getValueForVolume() * 100.f));
        volumeSeekBar.setEnabled(false);

        panSeekBar.setProgress((int) ((currentLanguagePrefItem.getValueForPan() + 1.f) * 50.f));
        panSeekBar.setEnabled(false);

        rateSeekBar.setProgress((int) (currentLanguagePrefItem.getValueForSpeechRate() * 10.f));
        rateSeekBar.setEnabled(false);

        pitchSeekBar.setProgress((int) (currentLanguagePrefItem.getValueForPitch() * 10.f));
        pitchSeekBar.setEnabled(false);
    }

    private void untieDefaultValues() {
        volumeSeekBar.setEnabled(true);
        panSeekBar.setEnabled(true);
        rateSeekBar.setEnabled(true);
        pitchSeekBar.setEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.edit_text);

        languagesSpinner = (Spinner) findViewById(R.id.languages_spinner);
        final List<String> languages = new ArrayList<String>();
        languages.add(AUTO_DETECTION);
        languages.addAll(Arrays.asList(AbsttsTextToSpeechService.LANGUAGES));
        languagesSpinner.setAdapter(new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                languages));
        languagesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String language = languages.get(position);
                if (language.equals(AUTO_DETECTION)) {
                    currentLanguagePrefItem = null;
                    untieDefaultValues();
                    return;
                }
                Locale locale = new Locale(language);
                String sampleText = GetSampleText.getSampleTextForLocale(locale);
                editText.setText(sampleText);
                currentLanguagePrefItem = new LanguagePrefItem(locale);
                currentLanguagePrefItem.load(preferences);
                if (languageUseDefaultCheckBox.isChecked()) {
                    tieDefaultValues();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // pass
            }
        });

        languageUseDefaultCheckBox = (CheckBox) findViewById(R.id.languages_use_default);
        languageUseDefaultCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tieDefaultValues();
                } else {
                    untieDefaultValues();
                }
            }
        });

        volumeSeekBar = (SeekBar) findViewById(R.id.volume_seek_bar);

        volumeResetButton = (Button) findViewById(R.id.volume_reset_button);
        volumeResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLanguagePrefItem != null) {
                    volumeSeekBar.setProgress((int) (currentLanguagePrefItem.getValueForVolume() * 100.f));
                }
            }
        });

        panSeekBar = (SeekBar) findViewById(R.id.pan_seek_bar);
        panResetButton = (Button) findViewById(R.id.pan_reset_button);
        panResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLanguagePrefItem != null) {
                    panSeekBar.setProgress((int) ((currentLanguagePrefItem.getValueForPan() + 1.f) * 50.f));
                }
            }
        });

        rateSeekBar = (SeekBar) findViewById(R.id.rate_seek_bar);
        rateResetButton = (Button) findViewById(R.id.rate_reset_button);
        rateResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLanguagePrefItem != null) {
                    rateSeekBar.setProgress((int) (currentLanguagePrefItem.getValueForSpeechRate() * 10.f));
                }
            }
        });

        pitchSeekBar = (SeekBar) findViewById(R.id.pitch_seek_bar);
        pitchResetButton = (Button) findViewById(R.id.pitch_reset_button);
        pitchResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLanguagePrefItem != null) {
                    pitchSeekBar.setProgress((int) (currentLanguagePrefItem.getValueForPitch() * 10.f));
                }
            }
        });

        speechButton = (Button) findViewById(R.id.speech_button);
        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speech();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        if (currentLanguagePrefItem != null) {
            currentLanguagePrefItem.load(preferences);
            if (languageUseDefaultCheckBox.isChecked()) {
                tieDefaultValues();
            }
        }
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                Log.d(TAG, "onInit: " + status);
            }
        }, App.PACKAGE_NAME);

    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        tts.shutdown();
    }


    private void speech() {
        HashMap<String, String> params = new HashMap<String, String>();
        String language = (String) languagesSpinner.getSelectedItem();
        if (language.equals(AUTO_DETECTION)) {
            tts.setLanguage(Locale.ENGLISH);
            params.put(AbsttsTextToSpeechService.KEY_PARAM_AUTO_DETECTION,
                    AbsttsTextToSpeechService.AUTO_DETECTION_ENABLED);
        } else {
            Locale locale = new Locale(language);
            if (tts.isLanguageAvailable(locale) != TextToSpeech.LANG_AVAILABLE) {
                String text = locale.getDisplayName() + " is not available";
                Toast.makeText(this, text, Toast.LENGTH_SHORT);
                return;
            }
            tts.setLanguage(locale);
            params.put(AbsttsTextToSpeechService.KEY_PARAM_AUTO_DETECTION,
                    AbsttsTextToSpeechService.AUTO_DETECTION_DISABLED);
        }

        params.put(TextToSpeech.Engine.KEY_PARAM_VOLUME,
                String.valueOf(String.valueOf((float) volumeSeekBar.getProgress() / 100.f)));

        params.put(TextToSpeech.Engine.KEY_PARAM_PAN,
                String.valueOf(((float) panSeekBar.getProgress() - 50) / 50.f));

        params.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
                String.valueOf(AudioManager.STREAM_MUSIC));

        tts.setPitch(Math.max(0.1f, pitchSeekBar.getProgress() / 10.f));
        tts.setSpeechRate(Math.max(0.1f, rateSeekBar.getProgress() / 10.f));

        int result = tts.speak(editText.getText().toString(), TextToSpeech.QUEUE_ADD, params);
        if (result != TextToSpeech.SUCCESS) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }
}
