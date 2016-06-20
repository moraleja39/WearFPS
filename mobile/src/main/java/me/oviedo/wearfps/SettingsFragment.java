package me.oviedo.wearfps;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    //SharedPreferences.OnSharedPreferenceChangeListener listener = null;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);
        return textView;
    }*/

    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        /*if (key.equals("pref_wear_enabled")) {
            //Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();
            Preference pref = findPreference(key);
            Preference wear = findPreference("pref_wear_autostart");
            PreferenceCategory cat = (PreferenceCategory) findPreference("pref_cat");
            if (pref.isEnabled()) {
                cat.removePreference(wear);
            } else {
                cat.addPreference(wear);
            }
        }*/
    }

}
