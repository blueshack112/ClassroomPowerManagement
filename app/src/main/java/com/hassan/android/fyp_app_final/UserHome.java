package com.hassan.android.fyp_app_final;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
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
import android.widget.TextView;
import android.widget.Toast;
// TODO: documentation
public class UserHome extends AppCompatActivity {

    //Main variables
    private String userType;
    private String userFirstNname;
    private String userLastNname;
    private String userDesignation;
    private String userID;

    //UI elements
    private LinearLayoutCompat fragmentHolder;
    private TextView usernameTextVeiw;
    private TextView userIDTextVeiw;
    private TextView userDesignationTextVeiw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        fragmentHolder = findViewById(R.id.main_fragment_space);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        userType = "";
        userFirstNname= "";
        userLastNname= "";
        userDesignation= "";
        userID = "";
        Intent mainIntnet = getIntent();
        userFirstNname = mainIntnet.getStringExtra("userFirstName");
        userLastNname = mainIntnet.getStringExtra("userLastName");
        userDesignation = mainIntnet.getStringExtra("userDesignation");
        userID = mainIntnet.getStringExtra("userID");

        //Set text boxes that will carry details
        usernameTextVeiw = findViewById(R.id.tv_user_name);
        userIDTextVeiw = findViewById(R.id.tv_user_ID);
        userDesignationTextVeiw = findViewById(R.id.tv_user_designation);

        //Fill Details
        usernameTextVeiw.setText(userFirstNname + " " + userLastNname);
        userIDTextVeiw.setText(userID);
        if (userDesignation.equals(MainActivity.TEACHER_DESIGNATION_ASST_PROF))
            userDesignationTextVeiw.setText("Assistant Professor");
        else if (userDesignation.equals(MainActivity.TEACHER_DESIGNATION_HOD))
            userDesignationTextVeiw.setText("Professor / HOD");

        //initialize fragment based on account type
        if (mainIntnet.getStringExtra("userType").equals(MainActivity.ACCOUNT_TYPE_TEACHER)) {
            FRGTeacherScheduleList temp = new FRGTeacherScheduleList();
            temp.setUserID(userID);
            transaction.add(R.id.main_fragment_space, temp);
            transaction.commit();
        } else if (mainIntnet.getStringExtra("userType").equals(MainActivity.ACCOUNT_TYPE_HOD)) {
            FRGHODTabPages temp = new FRGHODTabPages();
            temp.setUserID(userID);
            transaction.add(R.id.main_fragment_space, temp);
            transaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Intent menuIntent = getIntent();
        if (menuIntent.getStringExtra("userType").equals(MainActivity.ACCOUNT_TYPE_TEACHER))
            getMenuInflater().inflate(R.menu.teacher_action_menu, menu);
        else
            getMenuInflater().inflate(R.menu.user_action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout)
            onLogOutClicked();

        else if (userType == MainActivity.ACCOUNT_TYPE_TEACHER && item.getItemId() == R.id.request)
            Log.d("itwasgrabbed", "onOptionsItemSelected: ");
            onRequestClicked();
        return super.onOptionsItemSelected(item);
    }

    //functionality for request button
    public void onRequestClicked() {
        // Initialize the form dialog and add userID into its arguments
        DialogFragment formDialog = new ExtraRequestFormDialog();
        Bundle userIDBundle = new Bundle();
        userIDBundle.putString("userID", userID);
        formDialog.setArguments(userIDBundle);

        // Show the dialog
        formDialog.show(getSupportFragmentManager(), "formDialog");
        Log.d("formdebug", "Form shown...");
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
