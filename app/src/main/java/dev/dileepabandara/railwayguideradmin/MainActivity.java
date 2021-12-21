/*
   --------------------------------------
      Developed by
      Dileepa Bandara
      https://dileepabandara.github.io
      contact.dileepabandara@gmail.com
      ©dileepabandara.dev
      2020
   --------------------------------------
*/

package dev.dileepabandara.railwayguideradmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import dev.dileepabandara.railwayguideradmin.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    TextInputLayout username, password;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);

        //Request permissions from user
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS}, PackageManager.PERMISSION_GRANTED);

    }

    public void log(View view) {
        try {
            progressBar.setVisibility(View.VISIBLE);
            if (username.getEditText().getText().toString().equals("4857") && password.getEditText().getText().toString().equals("4857")) {
                progressBar.setVisibility(View.GONE);
                username.getEditText().setText("");
                password.getEditText().setText("");
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            } else {
                Snackbar.make(view, "Can't access. Invalid login data", Snackbar.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "" + e, Toast.LENGTH_SHORT).show();
        }

    }
}
