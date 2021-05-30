package com.example.otpnotifier;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final String PREF_USER_MOBILE_PHONE = "pref_user_mobile_phone";
    private static final int SMS_PERMISSION_CODE = 0;

    private EditText editTextPhone;
    private Button button;
    private TextView textView;

    private String mobilePhone;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!hasReadSmsPermission()) {
            showRequestPermissionsInfoAlertDialog();
        }
        button = findViewById(R.id.button);
        editTextPhone = findViewById(R.id.editTextPhone);
        textView = findViewById(R.id.textView2);

        mSharedPreferences = getSharedPreferences("user", 0);
        mobilePhone = mSharedPreferences.getString(PREF_USER_MOBILE_PHONE, "");
        if (!TextUtils.isEmpty(mobilePhone)) {
            editTextPhone.setText(mobilePhone);
            User.mobile = mobilePhone;
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                if (!hasValidPreConditions()) return;
                checkAndUpdateUserPrefNumber();

                String mobile = editTextPhone.getText().toString();
                if (mobile.length() != 10) {
                    textView.setTextColor(Color.parseColor("#DC143C"));
                    textView.setText("!!! Enter Valid Mobile Number !!!");
                } else {
                    textView.setTextColor(Color.parseColor("#9ACD32"));
                    textView.setText("Mobile Successfully Registered");
                    // startB(mobile);
                }
                //Toast.makeText(MainActivity.this, "Mobile Number Saved", Toast.LENGTH_LONG).show();
            }
        });


    }

    /**
     * Checks if stored SharedPreferences value needs updating and updates \o/
     */
    private void checkAndUpdateUserPrefNumber() {
        if (TextUtils.isEmpty(mobilePhone) && !mobilePhone.equals(editTextPhone.getText().toString())) {
            User.mobile = editTextPhone.getText().toString();
            mSharedPreferences
                    .edit()
                    .putString(PREF_USER_MOBILE_PHONE, editTextPhone.getText().toString())
                    .apply();
        }
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_conditional_sms:
//                if (!hasValidPreConditions()) return;
//                checkAndUpdateUserPrefNumber();
//
//                SmsHelper.sendDebugSms(String.valueOf(mNumberEditText.getText()), SmsHelper.SMS_CONDITION + " This SMS is conditional, Hello toast");
//                Toast.makeText(getApplicationContext(), R.string.toast_sending_sms, Toast.LENGTH_SHORT).show();
//                break;
//
//            case R.id.btn_normal_sms:
//                if (!hasValidPreConditions()) return;
//                checkAndUpdateUserPrefNumber();
//
//                SmsHelper.sendDebugSms(String.valueOf(mNumberEditText.getText()), "The broadcast should not show a toast for this");
//                Toast.makeText(getApplicationContext(), R.string.toast_sending_sms, Toast.LENGTH_SHORT).show();
//                break;
//        }
    }


    /**
     * Validates if the app has readSmsPermissions and the mobile phone is valid
     *
     * @return boolean validation value
     */
    private boolean hasValidPreConditions() {
        if (!hasReadSmsPermission()) {
            requestReadAndSendSmsPermission();
            return false;
        }

        return true;
    }

    /**
     * Optional informative alert dialog to explain the user why the app needs the Read/Send SMS permission
     */
    private void showRequestPermissionsInfoAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission");
        builder.setMessage("CoWIN SMS Read Permission Required");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                requestReadAndSendSmsPermission();
            }
        });
        builder.show();
    }

    /**
     * Runtime permission shenanigans
     */
    private boolean hasReadSmsPermission() {
        return ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestReadAndSendSmsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_SMS)) {
            Log.d(TAG, "shouldShowRequestPermissionRationale(), no permission requested");
            return;
        }
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS},
                SMS_PERMISSION_CODE);
    }
}