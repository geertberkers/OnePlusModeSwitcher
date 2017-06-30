package geert.berkers.modeswitcher.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import geert.berkers.modeswitcher.R;

/**
 * Created by Geert Berkers.
 */
public class MyPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}