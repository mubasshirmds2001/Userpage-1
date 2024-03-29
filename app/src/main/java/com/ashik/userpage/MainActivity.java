package com.ashik.userpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ashik.userpage.utility.NetworkChangeListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.grpc.ManagedChannelProvider;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment;
    private BroadcastReceiver broadcastReceiver;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID = "";
    private String name, phone, email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        broadcastReceiver = new NetworkChangeListener();
        homeFragment = new HomeFragment();

        bottomNavigationView = findViewById(R.id.bottom_nav);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container,homeFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                switch(item.getItemId()){
                    case R.id.home:
                        selectedFragment = new HomeFragment();
                        break;

                    case R.id.bookings:
                        selectedFragment = new BookingFragment();
                        break;

                    case R.id.profile:
                        selectedFragment = new ProfileFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,selectedFragment).commit();
                return true;
            }
        });

        saveData();

    }

    private void saveData() {

            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
            reference = FirebaseDatabase.getInstance().getReference("Users");
            if (user != null){
                userID = user.getUid();
            }

            reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userData = snapshot.getValue(User.class);

                    if (userData != null){
                        name = userData.name;
                        phone = userData.phone;
                        email = userData.email;

                        SharedPreferences.Editor editor = getSharedPreferences("userInfo", MODE_PRIVATE).edit();
                        editor.putString("name", name);
                        editor.putString("phone", phone);
                        editor.putString("email", email);
                        editor.apply();
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            });


        }


    @Override
    protected void onStart() {
        try {
            IntentFilter filter = new IntentFilter();
            registerReceiver(broadcastReceiver, filter);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        super.onStart();
    }


    @Override
    protected void onStop() {
        try {
            unregisterReceiver(broadcastReceiver);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        super.onStop();
    }



}


