package com.bumpr.bumpr;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;

public class FragmentPreferences extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
        setContentView(R.layout.activity_fragment_preferences);
        RangeSeekBar<Integer> rangeSeekBar = new RangeSeekBar<Integer>(this);
        // Set the range
        rangeSeekBar.setRangeValues(15, 90);
        rangeSeekBar.setSelectedMinValue(20);
        rangeSeekBar.setSelectedMaxValue(88);
        ImageView image = (ImageView) rangeSeekBar;

        // Add to layout
        ImageView layout = (ImageView) findViewById(R.id.imageView);
        layout = image;


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int c = preferences.getInt("seekBar",0);
        Log.d("myTag", "C" + Integer.toString(c));
    }


    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref_general);
            Preference myPref = (Preference) findPreference("example_switch");
            myPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    //Do stuff
                    boolean b = (Boolean) newValue;
                    Log.d("myTag", Boolean.toString(b));
                    return true;
                }
            });
            Preference myPref1 = (Preference) findPreference("seekBar");
            myPref1.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    //Do stuff
                    int b = (Integer) newValue;
                    Log.d("myTag", Integer.toString(b));
                    return true;
                }
            });
            Preference myPref2 = (Preference) findPreference("example_list");
            myPref2.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    //Do stuff
                    String b = (String) newValue;
                    Log.d("myTag", b);
                    return true;
                }
            });
        }
    }

}