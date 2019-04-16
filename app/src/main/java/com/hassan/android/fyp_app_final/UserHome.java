package com.hassan.android.fyp_app_final;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class UserHome extends AppCompatActivity {
    private String userType;
    private LinearLayoutCompat fragmentHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        fragmentHolder = findViewById(R.id.main_fragment_space);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        userType = "";
        Intent mainIntnet = getIntent();
        if (mainIntnet.getStringExtra("userType").equals("teacher")) {
            Toast.makeText(this, "Entered", Toast.LENGTH_SHORT).show();
            Fragment temp = new FRGTeacherScheduleList();
            transaction.add(R.id.main_fragment_space, temp);
            transaction.commit();
        } else if (mainIntnet.getStringExtra("userType").equals("hod")) {
            Toast.makeText(this, "Entered", Toast.LENGTH_SHORT).show();
            Fragment temp = new FRGHODTabPages();
            transaction.add(R.id.main_fragment_space, temp);
            transaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_action_menu, menu);
        return true;
    }
}
