package com.votelimes.memtask.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.votelimes.memtask.databinding.ActivityContactBinding;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.votelimes.memtask.R;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ContactActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    public static final int RESULT_RETURN_ITEMS = 40941680;
    public static final String RESULT_KEY = "40944330";
    SearchView searchView;
    private ActivityContactBinding binding;
    private List<ContactModel> contacts;
    private List<ContactModel> contactsBase;
    ContactAdapter adapter;
    List<Long> cid;
    ListView listView;

    final ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                setup();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityContactBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Serializable data = getIntent().getSerializableExtra("Contacts");
        if(data != null){
            cid = (List<Long>) data;
        }

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        Drawable drawable = getDrawable(R.drawable.ic_round_arrow_back_24);
        drawable.setTint(getColor(R.color.secondary));

        toolbar.setBackgroundColor(getColor(R.color.backgroundSecondary));
        toolbar.setTitleTextColor(getColor(R.color.toolbarTitle));
        toolbar.setSubtitleTextColor(getColor(R.color.toolbarIcons));
        setSupportActionBar(toolbar);

        toolbar.getMenu().findItem(R.id.action_search).setVisible(true);
        toolbar.setNavigationIcon(drawable);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        FloatingActionButton fab = findViewById(R.id.contact_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_INSERT,
                        ContactsContract.Contacts.CONTENT_URI);
                activityLauncher.launch(intent);
            }
        });

        ExtendedFloatingActionButton fabSave = findViewById(R.id.contact_fab_save);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onExit();
            }
        });
        setup();
    }

    private void addItem(){

    }

    public List<ContactModel> getContacts(Context ctx) {
        List<ContactModel> list = new ArrayList<>();
        ContentResolver contentResolver = ctx.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                Cursor cursorInfo = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(ctx.getContentResolver(),
                        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id)));

                Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id));
                Uri pURI = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

                Bitmap photo = null;
                if (inputStream != null) {
                    photo = BitmapFactory.decodeStream(inputStream);
                }
                while (cursorInfo.moveToNext()) {
                    ContactModel info = new ContactModel();
                    info.id = id;
                    info.name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

                    Cursor emailCur = contentResolver.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (emailCur.moveToNext()) {
                        info.email = emailCur.getString(emailCur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.DATA));
                        break;
                    }
                    info.mobileNumber = cursorInfo.getString(cursorInfo.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    info.photo = photo;
                    info.photoURI= pURI;
                    list.add(info);
                }

                cursorInfo.close();

            }
            cursor.close();
        }
        return list;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        filterItems(newText);
        setup();
        return false;
    }

    public class ContactModel {
        public String id;
        public boolean checked;
        public String name;
        public String email;
        public String mobileNumber;
        public Bitmap photo;
        public Uri photoURI;
    }

    public class ContactAdapter extends ArrayAdapter<ContactModel> {
        public ContactAdapter(Context context, List<ContactModel> contacts) {
            super(context, 0, contacts);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            ContactModel contact = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater
                        .from(getContext())
                        .inflate(R.layout.contact_layout, parent, false);
            }
            // Lookup view for data population
            CheckBox cChecked = (CheckBox) convertView.findViewById(R.id.contact_check);
            TextView cName = (TextView) convertView.findViewById(R.id.contact_name);
            TextView cNumber = (TextView) convertView.findViewById(R.id.contact_number);
            TextView cEmail = (TextView) convertView.findViewById(R.id.contact_email);
            // Populate the data into the template view using the data object

            cChecked.setChecked(contact.checked);
            cName.setText(contact.name);
            cNumber.setText(contact.mobileNumber);
            cEmail.setText(contact.email);

            cChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    contact.checked = b;
                }
            });
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MaterialAlertDialogBuilder optionsDialog = new MaterialAlertDialogBuilder(view.getContext())
                            .setTitle("Выберите действие")
                            .setItems(R.array.contact_dialog, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    switch (i) {
                                        // Звонок
                                        case 0:
                                            if(contact.mobileNumber != null && contact.mobileNumber.length() > 0) {
                                                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                                callIntent.setData(Uri.parse("tel:" + contact.mobileNumber));
                                                startActivity(callIntent);
                                            }
                                            else{
                                                Toast.makeText(ContactActivity.this, "Не указан номер", Toast.LENGTH_SHORT);
                                            }
                                            break;

                                        case 1: // Копировать телефон
                                            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                            ClipData clip = ClipData.newPlainText("Номер телефона", contact.mobileNumber);
                                            clipboard.setPrimaryClip(clip);
                                            break;
                                        case 2: // Копировать почту
                                            ClipboardManager clipboard2 = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                            ClipData clip2 = ClipData.newPlainText("Почта", contact.email);
                                            clipboard2.setPrimaryClip(clip2);
                                            break;
                                        case 3: // Удалить
                                            Log.d("", "");
                                            break;
                                    }
                                }
                            });
                    optionsDialog.show();
                }
            });


            // Return the completed view to render on screen
            return convertView;
        }
    }

    private void setup(){
        contacts = getContacts(getApplicationContext());
        contactsBase = contacts;

        if(cid != null) {
            contacts.forEach(contact -> {
                cid.forEach(ids -> {
                    if(ids == Long.parseLong(contact.id)){
                        contact.checked = true;
                    }
                });
            });
        }

        adapter = new ContactAdapter(this, contacts);
        listView = findViewById(R.id.contacts_scroll);
        listView.setAdapter(adapter);
    }

    private void filterItems(String filter){
        if(filter != null && filter.length() > 0) {
            contacts = contactsBase
                    .stream()
                    .filter(item -> item.name.contains(filter))
                    .collect(Collectors.toList());
        }
    }

    public void onExit(){
        AtomicReference<String> resultIDs = new AtomicReference<>("");
        contactsBase.forEach(item -> {
            if(item.checked){
                resultIDs.set(resultIDs + item.id + ",");
            }
        });
        Intent intent = new Intent();
        intent.putExtra(RESULT_KEY, resultIDs.get());
        setResult(RESULT_RETURN_ITEMS, intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        //Get SearchView through MenuItem
        searchView = (SearchView) searchItem.getActionView();
        searchItem.setVisible(true);
        searchView.setOnQueryTextListener(this);
        return true;
    }
}