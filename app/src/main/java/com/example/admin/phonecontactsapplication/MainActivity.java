package com.example.admin.phonecontactsapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.admin.phonecontactsapplication.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 10;
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    List<Contact> contactList = new ArrayList<>();
    private MyItemListAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById( R.id.rvRecyclerView );
        layoutManager = new LinearLayoutManager( this );
        adapter = new MyItemListAdapter( contactList );
        recyclerView.setLayoutManager( layoutManager );
        recyclerView.setAdapter(adapter);

        checkPermission();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void retrievePhoneContacts() {
        //get the content provider's universal resource identifier

        Uri ContentURI = ContactsContract.Contacts.CONTENT_URI;
        String DISPAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Cursor cursor = getContentResolver().query( ContentURI, null, null, null, DISPAY_NAME + " ASC" );

        int hasPhone = 0;

        while( cursor.moveToNext() ) {
            String contactName = cursor.getString( cursor.getColumnIndex( DISPAY_NAME ));
            hasPhone = Integer.parseInt( cursor.getString( cursor.getColumnIndex( HAS_PHONE_NUMBER )));

            if( hasPhone > 0 ) {
                Log.d(TAG, "retrievePhoneContacts: " + contactName);

                Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String number = ContactsContract.CommonDataKinds.Phone.NUMBER;

                String[] projection = new String[] { number };
                String selection = DISPAY_NAME + "=?";
                String[] selectionArgs = new String[] { contactName };

                Cursor phoneCursor = getContentResolver().query( phoneUri, projection,
                        selection, selectionArgs, null );

                List<String> phoneNumbers = new ArrayList<>();

                while( phoneCursor.moveToNext() ) {
                    String phoneNumber = phoneCursor.getString( phoneCursor.getColumnIndex( number ));
                    Log.d(TAG, "retrievePhoneContacts: " + phoneNumber);
                    phoneNumbers.add( phoneNumber );
                }

                contactList.add( new Contact( contactName, phoneNumbers));
            }

            adapter.notifyDataSetChanged();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void checkPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "onCreate: Dose not have permission");

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                showRequestRationale();

            } else {

                // No explanation needed, we can request the permission.
                requestContactsPermission();
            }
        } else {
            Log.d(TAG, "onCreate: Permission already granted");
            retrievePhoneContacts();
        }
    }

    private void showRequestRationale() {
        Log.d(TAG, "onCreate: Should show rationale");
        // Show an explanation to the user *asynchronously* -- don't block
        // this thread waiting for the user's response! After the user
        // sees the explanation, try again to request the permission.

        AlertDialog alertDialog = new AlertDialog.Builder( this )
                .setTitle( "Explanation" )
                .setMessage( "Please allow this permission to read your contacts." )
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestContactsPermission();
                    }
                })
                .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, "Think Again", Toast.LENGTH_SHORT).show();
                    }
                })
                .create();

        alertDialog.show();
    }

    private void requestContactsPermission() {
        Log.d(TAG, "onCreate: Requesting Permission");

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_CONTACTS},
                MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
        // app-defined int constant. The callback method gets the
        // result of the request.
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted.
                    Log.d(TAG, "onRequestPermissionsResult: Permission Granted");
                    retrievePhoneContacts();

                } else {

                    // permission denied.
                    // Disable the functionality that depends on this permission.

                    Log.d(TAG, "onRequestPermissionsResult: Permission Denied");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

    }
}
