package aletim.rssreader;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsActivity extends AppCompatActivity {
    public static final String PREFERENCE_FILE = "preferences.xml";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //tarkastellaan asetuksia uudessa Fragmentissa
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }


    public static class SettingsFragment extends PreferenceFragment {


        public SettingsFragment() {
            // Required empty public constructor
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //Liitetään preferences.xml tähän näkymään
            addPreferencesFromResource(R.xml.preferences);

            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_settings, container, false);
        }
    }
}
