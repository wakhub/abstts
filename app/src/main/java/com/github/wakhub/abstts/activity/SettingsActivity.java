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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.github.wakhub.abstts.AbsttsTextToSpeechService;
import com.github.wakhub.abstts.R;
import com.github.wakhub.abstts.pref.LanguagePrefItem;
import com.github.wakhub.abstts.pref.PrefUtils;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class SettingsActivity extends PreferenceActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private static final int VOICE_DATA_CHECK_CODE = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        new PrefUtils(this);

        Intent intent = new Intent();
        intent.setClass(this, CheckTtsData.class);
        startActivityForResult(intent, VOICE_DATA_CHECK_CODE);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return GeneralSettingsFragment.class.getName().equals(fragmentName)
                || LanguageSettingsFragment.class.getName().equals(fragmentName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        Log.d(TAG, "onBuildHeaders");
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    public static class GeneralSettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.d(GeneralSettingsFragment.class.getSimpleName(), "onCreate: " + getPreferenceManager().getSharedPreferencesName());
            addPreferencesFromResource(R.xml.preferences_general);
            getActivity().setTitle("General Settings");
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class LanguageSettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.d(LanguageSettingsFragment.class.getSimpleName(), "onCreate: " + getPreferenceManager().getSharedPreferencesName());
            getActivity().setTitle("Language Settings");
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

            PreferenceScreen root = getPreferenceManager().createPreferenceScreen(getActivity());

            setPreferenceScreen(root);

            for (String language : AbsttsTextToSpeechService.LANGUAGES) {
                LanguagePreference preference = new LanguagePreference(getActivity(), null, language);
                root.addPreference(preference);
            }
        }
    }

    public static class LanguagePreference extends DialogPreference {

        private final WeakReference<Activity> activityRef;

        private final LanguagePrefItem prefItem;

        private Button resetButton;
        private Spinner engineSpinner;
        private CheckBox enabledCheckBox;
        private SeekBar volumeSeekBar;
        private SeekBar rateSeekBar;
        private SeekBar pitchSeekBar;
        private SeekBar panSeekBar;

        public LanguagePreference(Activity activity, AttributeSet attrs, String language) {
            super(activity, attrs);
            activityRef = new WeakReference<Activity>(activity);
            prefItem = new LanguagePrefItem(new Locale(language));
            setKey(prefItem.getKey());
        }

        void reload() {
            String title = prefItem.getTitle();
            setTitle(title);
            setDialogTitle(title);
        }

        void resetValues() {
            engineSpinner.setSelection(0);
            enabledCheckBox.setChecked(true);
            volumeSeekBar.setProgress(100);
            rateSeekBar.setProgress(10);
            pitchSeekBar.setProgress(10);
            panSeekBar.setProgress(50);
        }

        @Override
        protected View onCreateDialogView() {
            reload();
            Context context = getContext();
            if (activityRef.get() == null) {
                return null;
            }

            ScrollView view = (ScrollView) activityRef.get().getLayoutInflater().inflate(R.layout.dialog_language_preference, null);

            resetButton = (Button) view.findViewById(R.id.reset_button);
            resetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetValues();
                }
            });

            engineSpinner = (Spinner) view.findViewById(R.id.engine_spinner);
            List<String> engines = Arrays.asList(AbsttsTextToSpeechService.DEFAULT_ENGINES);
            engineSpinner.setAdapter(new ArrayAdapter<String>(
                    context,
                    android.R.layout.simple_spinner_dropdown_item,
                    engines));
            for (int i = 0; i < engines.size(); i++) {
                if (engines.get(i).equals(prefItem.getValueForEngine())) {
                    engineSpinner.setSelection(i);
                }
            }

            enabledCheckBox = (CheckBox) view.findViewById(R.id.enabled_check_box);
            enabledCheckBox.setChecked(prefItem.getValueForEnable());

            volumeSeekBar = (SeekBar) view.findViewById(R.id.volume_seek_bar);
            volumeSeekBar.setProgress((int) (prefItem.getValueForVolume() * 100.f));

            rateSeekBar = (SeekBar) view.findViewById(R.id.rate_seek_bar);
            rateSeekBar.setProgress((int) (prefItem.getValueForSpeechRate() * 10.f));

            pitchSeekBar = (SeekBar) view.findViewById(R.id.pitch_seek_bar);
            pitchSeekBar.setProgress((int) (prefItem.getValueForSpeechRate() * 10.f));

            panSeekBar = (SeekBar) view.findViewById(R.id.pan_seek_bar);
            panSeekBar.setProgress((int) ((prefItem.getValueForPan() + 1.f) * 50.f));

            return view;
        }

        @Override
        protected void onBindView(View view) {
            Log.d(TAG, "onBindView");
            super.onBindView(view);
            reload();
        }

        @Override
        protected void onAttachedToHierarchy(PreferenceManager preferenceManager) {
            super.onAttachedToHierarchy(preferenceManager);
            prefItem.load(preferenceManager.getSharedPreferences());
            reload();
        }

        @Override
        protected void onDialogClosed(boolean positiveResult) {
            super.onDialogClosed(positiveResult);
            if (!positiveResult) {
                return;
            }

            reload();

            prefItem.setValueForEnable(enabledCheckBox.isChecked());
            prefItem.setValueForEngine((String) engineSpinner.getSelectedItem());
            prefItem.setValueForVolume(volumeSeekBar.getProgress() / 100.f);
            prefItem.setValueForPitch(pitchSeekBar.getProgress() / 10.f);
            prefItem.setValueForSpeechRate(rateSeekBar.getProgress() / 10.f);
            prefItem.setValueForPan((panSeekBar.getProgress() - 50) / 50.f);
            prefItem.commit(getSharedPreferences());

            reload();
        }
    }
}
