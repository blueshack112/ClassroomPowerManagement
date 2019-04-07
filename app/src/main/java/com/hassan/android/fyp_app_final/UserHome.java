package com.hassan.android.fyp_app_final;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class UserHome extends AppCompatActivity {
    private String userType;
    private LinearLayoutCompat fragmentHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        fragmentHolder = findViewById(R.id.main_fragment_space);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        userType = "";
        Intent mainIntnet = getIntent();
        if (mainIntnet.getStringExtra("userType").equals("teacher")) {
            Toast.makeText(this, "Entered", Toast.LENGTH_SHORT).show();
            Log.d("Check", "checked");
            Fragment temp = new TeacherScheduleList();
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
