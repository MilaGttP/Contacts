package com.example.final_contacts.adapters;

import static androidx.core.content.ContextCompat.startActivity;
import static com.example.final_contacts.tools.DBTools.ContactsNames.*;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_contacts.R;
import com.example.final_contacts.tools.DBTools;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactHolder> {
    private Cursor cursor;
    private Activity activity;
    private LayoutInflater inflater;
    private ContactDeleteListener contactDeleteListener;
    private AdapterInteractionListener smsListener;

    public ContactsAdapter(Cursor cursor, Activity activity) {
        this.cursor = cursor;
        this.activity = activity;
        this.inflater = activity.getLayoutInflater();
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_view_contacts_recycler, parent, false);
        return new ContactHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ContactHolder holder, int position) {
        if (cursor.moveToNext()) {
            holder.contactId = cursor.getInt(cursor.getColumnIndexOrThrow(_id.toString()));
            String nameVar = cursor.getString(cursor.getColumnIndexOrThrow(name.name()));
            String surnameVar = cursor.getString(cursor.getColumnIndexOrThrow(surname.name()));
            String patronymicVar = cursor.getString(cursor.getColumnIndexOrThrow(patronymic.name()));
            String phoneVar = cursor.getString(cursor.getColumnIndexOrThrow(phone_number.name()));

            holder.fullNameView.setText(nameVar + " " + surnameVar + " " + patronymicVar);

            holder.phoneNumberView.setText(
                cursor.getString(cursor.getColumnIndexOrThrow(phone_number.name()))
            );

            holder.itemView.setOnClickListener(v -> {
                if (smsListener != null) {
                    smsListener.sendSms(phoneVar, "Hello!");
                }
            });

            holder.itemView.setOnLongClickListener(v -> {
                showRecyclerPopup(v, holder.contactId, holder.itemView.getContext());
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    private void showRecyclerPopup(View view, int contactId, Context context) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.item_contact_recycler_popup, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (R.id.deletePopup == id) {
                if (contactDeleteListener != null) {
                    contactDeleteListener.onContactDeleted(contactId);
                }
                DBTools db = new DBTools(context);
                db.deleteContactById(contactId);
            }
            return true;
        });

        popupMenu.show();
    }

    public void setContactDeleteListener(ContactDeleteListener listener) {
        this.contactDeleteListener = listener;
    }

    public void setAdapterInteractionListener(AdapterInteractionListener listener) {
        this.smsListener = listener;
    }

    class ContactHolder extends RecyclerView.ViewHolder {
        int contactId;
        TextView fullNameView;
        TextView phoneNumberView;

        public ContactHolder(@NonNull View itemView) {
            super(itemView);
            fullNameView = itemView.findViewById(R.id.text1ItemContactsRecycler);
            phoneNumberView = itemView.findViewById(R.id.text2ItemContactsRecycler);
        }
    }

    public interface ContactDeleteListener {
        void onContactDeleted(int contactId);
    }

    public interface AdapterInteractionListener {
        void sendSms(String phoneNumber, String message);
    }
}
