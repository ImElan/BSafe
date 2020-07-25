package app.android.bsafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static android.Manifest.permission.SEND_SMS;

public class MainActivity extends AppCompatActivity implements CustomDialog.CustomDialogListener
{

    DatabaseHelper Contact_database;

    private TextView messageText;
    private Button sendSmsButton;
    private ImageView changeMessage;

    private String[] AppPermissions;
    private boolean mPermissionGranted = false;
    private static final int SMS_PERMISSION_CODE = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("B Safe");

        Contact_database = new DatabaseHelper(MainActivity.this);

        requestPermission();

        messageText = findViewById(R.id.message_id);
        sendSmsButton = findViewById(R.id.send_button);
        changeMessage = findViewById(R.id.edit_message);

        String initialMessage = restoreMessageData();
        messageText.setText(initialMessage);

        sendSmsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPermissionGranted) {
                    String message = messageText.getText().toString();
                    // GET ALL THE NUMBERS FROM DATABASE AND SEND
                    Cursor data = Contact_database.ViewData();
                    while(data.moveToNext())
                    {
//                        PhoneNumberList.add(data.getString(0));
                        String number = data.getString(0);
                        SendSms(message,number);
                    }

                    Toast.makeText(MainActivity.this,"Message Sent Successfully",Toast.LENGTH_SHORT).show();
                }
                else {
                    ActivityCompat.requestPermissions(MainActivity.this,AppPermissions,SMS_PERMISSION_CODE);
                }
            }
        });

        changeMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog customDialog = new CustomDialog();
                customDialog.show(getSupportFragmentManager(),"CUSTOM_DIALOG");
                customDialog.setCancelable(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.contact)
        {
            Intent ContactIntent = new Intent(MainActivity.this,ContactsActivity.class);
            startActivity(ContactIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void SendSms(String message, String number) {
        Log.d("SEND_SMS",message+":"+number);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number,null,message,null,null);
    }

    public void requestPermission()
    {
        AppPermissions = new String[]{Manifest.permission.SEND_SMS};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            mPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    AppPermissions,
                    SMS_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        mPermissionGranted = false;
        if (requestCode == SMS_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = true;
            }
        }
    }

    @Override
    public void ApplyText(String message) {
        saveMessageData(message);
        messageText.setText(message);
    }

    private String restoreMessageData()
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myMessagePref",MODE_PRIVATE);
        String message = pref.getString("message","Help");
        return  message;
    }

    private void saveMessageData(String message)
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myMessagePref",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("message",message);
        editor.commit();
    }
}
