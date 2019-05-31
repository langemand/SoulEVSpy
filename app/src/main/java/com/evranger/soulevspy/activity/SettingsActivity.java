package com.evranger.soulevspy.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.evranger.soulevspy.fragment.ClientPreferencesFragment;

/**
 *
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new ClientPreferencesFragment())
                .commit();
    }
}