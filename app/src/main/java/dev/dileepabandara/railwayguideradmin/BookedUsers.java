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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import dev.dileepabandara.railwayguideradmin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import io.paperdb.Paper;

public class BookedUsers extends AppCompatActivity {

    DatabaseReference reference;
    RecyclerView bookedRecycler;
    ArrayList<UsersFromDB> arrayList;
    UsersAdapter bookedTrainsAdapter;
    ProgressBar progressBarUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_booked_users);

        ActivityCompat.requestPermissions(BookedUsers.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS}, PackageManager.PERMISSION_GRANTED);

        Paper.init(BookedUsers.this);

        try {

            bookedRecycler = findViewById(R.id.bookedTrains_result_list);
            progressBarUsers = findViewById(R.id.progressBarUsers);

            bookedRecycler.setLayoutManager(new LinearLayoutManager(this));
            arrayList = new ArrayList<UsersFromDB>();

            //Get data from firebase
            progressBarUsers.setVisibility(View.VISIBLE);
            reference = FirebaseDatabase.getInstance().getReference().child("trains").child("4857").child("users");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            UsersFromDB p = dataSnapshot1.getValue(UsersFromDB.class);
                            arrayList.add(p);
                        }
                        bookedTrainsAdapter = new UsersAdapter(BookedUsers.this, arrayList);
                        bookedRecycler.setAdapter(bookedTrainsAdapter);
                        bookedTrainsAdapter.notifyDataSetChanged();
                        progressBarUsers.setVisibility(View.GONE);
                    } else {
                        progressBarUsers.setVisibility(View.GONE);
                        Toast.makeText(BookedUsers.this, "No Booked Tickets", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(BookedUsers.this, "No Booked Tickets", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
        }

    }

    public void onClickSendSMS(View view) {

        Paper.init(BookedUsers.this);
        final String latitude = Paper.book().read(PrevalentLocation.latitudeKey);
        final String longitude = Paper.book().read(PrevalentLocation.longitudeKey);
        final String city = Paper.book().read(PrevalentLocation.cityKey);

        //Send SMS
        try {
            String messageSend = "Hi!\n" +"Your train has arrived in " +city+ "\n" + latitude + "\n" + longitude + "\n" +"Thank You\n Railway Guider Team";
            String numberSend = "07*********";
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(numberSend, null, messageSend, null, null);

            Toast.makeText(BookedUsers.this, "Message Send", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(BookedUsers.this, "" + e, Toast.LENGTH_SHORT).show();
        }

    }
}
