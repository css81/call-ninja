package com.sschoi.callninja.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.sschoi.callninja.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnRegisterCallerId = findViewById(R.id.btn_register_callerid);
        btnRegisterCallerId.setOnClickListener(v -> registerCallerIdApp());
    }

    private void registerCallerIdApp() {
        TelecomManager telecomManager = (TelecomManager) getSystemService(TELECOM_SERVICE);

        if (telecomManager != null &&
                !getPackageName().equals(telecomManager.getDefaultDialerPackage())) {

            Intent intent = new Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER);
            intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME,
                    getPackageName());
            startActivity(intent);
        }
    }
}
