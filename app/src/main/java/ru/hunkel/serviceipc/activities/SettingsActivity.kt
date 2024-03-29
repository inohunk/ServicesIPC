package ru.hunkel.serviceipc.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import ru.hunkel.servicesipc.R
import utils.KEY_GPS_INTERVAL

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.button_settings, SettingsFragment())
            .commit()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val syncSummaryProvider = Preference.SummaryProvider<EditTextPreference> { preference ->
                preference.text.toString()
            }
            findPreference<EditTextPreference>(KEY_GPS_INTERVAL)?.summaryProvider = syncSummaryProvider
        }
    }
}