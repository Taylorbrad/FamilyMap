package com.example.familymapclientmarktb;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import Client.DataCache;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }



    }



    @Override
    public boolean onOptionsItemSelected(MenuItem menu)
    {
        switch(menu.getItemId()) {
            case android.R.id.home:
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
        }
        return true;
    }



    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            setHasOptionsMenu(true);
            super.onCreate(savedInstanceState);
            Preference logout = getPreferenceScreen().findPreference("logout");

            logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    DataCache.getInstance().clear();

                    Intent mainIntent = new Intent(getActivity(), MainActivity.class);
                    startActivity(mainIntent);
                    return true;
                }
            });
        }


        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }
}