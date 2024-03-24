package com.example.final_contacts;

import static com.example.final_contacts.adapters.ContactsAdapter.*;
import static com.example.final_contacts.tools.SettingsTools.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.final_contacts.adapters.ContactsAdapter;
import com.example.final_contacts.databinding.ActivityMainBinding;
import com.example.final_contacts.tools.DBTools;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ContactDeleteListener, AdapterInteractionListener {
    ActivityMainBinding binding;

    ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                DBTools helper = new DBTools(MainActivity.this);
                Cursor cursor = helper.findAllContactsCursor();

                if (result.getResultCode() == RESULT_OK) {
                    ContactsAdapter adapter = new ContactsAdapter(cursor, this);
                    adapter.setContactDeleteListener(this);
                    binding.contactsRecycler.setAdapter(adapter);
                }
                recreate();
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadSettings(this);
        setTheme(settings().theme);
        setLocale(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        DBTools db = new DBTools(this);
        Cursor cursor = db.findAllContactsCursor();
        ContactsAdapter adapter = new ContactsAdapter(cursor, this);
        adapter.setContactDeleteListener(this);
        adapter.setAdapterInteractionListener(this);
        binding.contactsRecycler.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (R.id.createNewContactMenu == id) {
            Intent intent = new Intent(this, ContactActivity.class);
            activityLauncher.launch(intent);
        } else if (R.id.lightThemeMenu == id) {
            settings().theme = R.style.Theme_Light;
            saveSettings(this);
            recreate();
        } else if (R.id.darkThemeMenu == id) {
            settings().theme = R.style.Theme_Dark;
            saveSettings(this);
            recreate();
        } else if (R.id.englishLocaleMenu == id) {
            settings().locale = Locale.ENGLISH.toString();
            saveSettings(this);
            recreate();
        } else if (R.id.ukrainianLocaleMenu == id) {
            settings().locale = "uk";
            saveSettings(this);
            recreate();
        } else if (R.id.spanishLocaleMenu == id) {
            settings().locale = "es";
            saveSettings(this);
            recreate();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onContactDeleted(int contactId) {
        recreate();
    }

    @Override
    public void sendSms(String phoneNumber, String message) {
        String smsTo = "smsto:" + phoneNumber;
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(smsTo));
        intent.putExtra("sms_body", message);
        startActivity(intent);
    }
}