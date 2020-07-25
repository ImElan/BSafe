package app.android.bsafe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity implements CustomAddAlertDialog.CustomAddAlertDialogListener,CustomEditAlertDialog.CustomEditDialogListener
{

    DatabaseHelper Contact_database;
    RecyclerView mRecycler;
    RecyclerViewAdapter mAdapter;
    private ArrayList<String> PhoneNumberList;
    private static final int RESULT_PICK_CONTACT = 6000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Toolbar mToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Your Contacts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Contact_database = new DatabaseHelper(ContactsActivity.this);
        PhoneNumberList = new ArrayList<>();

        mRecycler = findViewById(R.id.storage_recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setHasFixedSize(true);

        Cursor data = Contact_database.ViewData();
        while(data.moveToNext())
        {
            PhoneNumberList.add(data.getString(0));
        }

        mAdapter = new RecyclerViewAdapter(PhoneNumberList,ContactsActivity.this);
        mRecycler.setAdapter(mAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.add_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.add_id)
        {
            CharSequence[] option = new CharSequence[]{"1. Add From Contacts", "2. Add A New Number"};
            final AlertDialog.Builder builder = new AlertDialog.Builder(ContactsActivity.this);
            builder.setTitle("Choose Your Option!");
            builder.setItems(option, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == 0)
                    {
                        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
                    }
                    if (i == 1)
                    {
                       CustomAddAlertDialog customAddAlertDialog = new CustomAddAlertDialog();
                       customAddAlertDialog.show((ContactsActivity.this).getSupportFragmentManager(),"CUSTOM_EDIT_DIALOG");
                       customAddAlertDialog.setCancelable(false);
                    }
                }
            });
            builder.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be usign multiple startActivityForReslut
            switch (requestCode) {
                case RESULT_PICK_CONTACT :
                    Cursor cursor = null;
                    try {
                        String phoneNo = null;
                        String name = null;
                        Uri uri = data.getData();
                        cursor = getContentResolver().query(uri, null, null, null, null);
                        cursor.moveToFirst();

                        int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        phoneNo = cursor.getString(phoneIndex);

                        if(!TextUtils.isEmpty(phoneNo))
                        {
                            boolean insertData = Contact_database.AddData(phoneNo);
                            if(insertData)
                            {
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());
                                overridePendingTransition(0, 0);
                                Toast.makeText(ContactsActivity.this,"Contact Added Successfully",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(ContactsActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                            }
                        }
                        Log.d("PHONE_NUMBER", phoneNo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }
    }

    @Override
    public void ApplyAddDataText(String number) {
        if(!TextUtils.isEmpty(number))
        {
            boolean insertData = Contact_database.AddData(number);
            if(insertData)
            {
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
                Toast.makeText(ContactsActivity.this,"Contact Added Successfully",Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(ContactsActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
        }
        else
        {
            CustomAddAlertDialog customAddAlertDialog = new CustomAddAlertDialog();
            customAddAlertDialog.show((ContactsActivity.this).getSupportFragmentManager(),"CUSTOM_EDIT_DIALOG");
            customAddAlertDialog.setCancelable(false);
            Toast.makeText(ContactsActivity.this,"Field Should not be empty..!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void ApplyEditDataText(String phone_id,String phoneNumber) {
        if(!TextUtils.isEmpty(phoneNumber))
        {
            boolean result = Contact_database.EditData(phone_id,phoneNumber);
            if(result)
            {
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
                Toast.makeText(ContactsActivity.this,"Contact Updated Successfully",Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(ContactsActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(ContactsActivity.this,"Fields Should not be empty..!",Toast.LENGTH_SHORT).show();
        }
    }
}
