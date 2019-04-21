package com.hassan.android.fyp_app_final;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
        Intent menuIntent = getIntent();
        if (menuIntent.getStringExtra("userType").equals("teacher"))
            getMenuInflater().inflate(R.menu.teacher_action_menu, menu);
        else
            getMenuInflater().inflate(R.menu.user_action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout)
            onLogOutClicked();

        else if (item.getItemId()==R.id.request)
            onRequestClicked();
        return super.onOptionsItemSelected(item);
    }

    //functionality for request button
    public void onRequestClicked () {

    }

    //functionality for logout button
    public void onLogOutClicked() {
        AlertDialog.Builder logoutAlert = new AlertDialog.Builder(this);
        logoutAlert.setTitle("Confirm Logout");
        logoutAlert.setMessage("Are you sure you want to logout?");
        logoutAlert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent mainActivityIntent = new Intent(UserHome.this, MainActivity.class);
                startActivity(mainActivityIntent);
            }
        });
        logoutAlert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(UserHome.this, "you chose to not logout.", Toast.LENGTH_SHORT).show();
            }
        });
        logoutAlert.show();
    }
}
