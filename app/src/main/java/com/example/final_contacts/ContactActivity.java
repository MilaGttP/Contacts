package com.example.final_contacts;

import static com.example.final_contacts.tools.SettingsTools.loadSettings;
import static com.example.final_contacts.tools.SettingsTools.setLocale;
import static com.example.final_contacts.tools.SettingsTools.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.final_contacts.data.Contact;
import com.example.final_contacts.databinding.ActivityContactBinding;
import com.example.final_contacts.tools.DBTools;

public class ContactActivity extends AppCompatActivity {
    ActivityContactBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadSettings(this);
        setTheme(settings().theme);
        setLocale(this);

        binding = ActivityContactBinding.inflate(getLayoutInflater());

        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (R.id.saveContactMenu == id) {
            String name = binding.nameEditText.getText().toString();
            String surname = binding.surnameEditText.getText().toString();
            String patronymic = binding.patronymicEditText.getText().toString();
            String phoneNumber = binding.phoneEditText.getText().toString();
            String description = binding.descriptionEditText.getText().toString();

            if (name.isEmpty() || surname.isEmpty() || patronymic.isEmpty() || phoneNumber.isEmpty() || description.isEmpty()) {
                return super.onOptionsItemSelected(item);
            }

            DBTools db = new DBTools(this);
            Contact contact = new Contact(name, surname, patronymic, phoneNumber, description);
            Log.d("TAG", "onOptionsItemSelected: contact " + contact.toString());
            db.saveContact(contact);
            setResult(RESULT_OK);
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}