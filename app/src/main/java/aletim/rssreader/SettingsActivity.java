package aletim.rssreader;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Locale;

/**
 * Activity asetusten hallintaan ja nayttamiseen
 */
public class SettingsActivity extends AppCompatActivity {
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    /**
     * Luo asetusvalikon ja liittaa kieliasetukseen kuuntelijan
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Asetetaan kuuntelija kuuntelemaan kieliasetuksen muutosta
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if(key.equals("lang")) {
                    Locale locale = new Locale(sharedPreferences.getString("lang", "en"));
                    Configuration conf = new Configuration();
                    conf.locale = locale;
                    getBaseContext().getResources().updateConfiguration(conf, null);
                    System.out.println("set lang to: " + conf.locale.toString());
                    //Ladataan activity uudelleen, jotta saadaan ladattua uusi, kieltä vastaava strings.xml-tiedosto:
                    recreate();
                }
            }
        };
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(listener);

        //tarkastellaan asetuksia uudessa Fragmentissa
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();

    }


    /**
     * Fragment, joka nayttaa listan asetuksista
     */
    public static class SettingsFragment extends PreferenceFragment {

        public SettingsFragment() {}

        /**
         * Luo asetuslista preferences.xml-tiedoston perusteella
         * @param inflater
         * @param container
         * @param savedInstanceState
         * @return
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //Liitetaan preferences.xml tahan nakymaan
            addPreferencesFromResource(R.xml.preferences);
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_settings, container, false);
        }
    }
}
