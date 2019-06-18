package com.hassan.android.fyp_app_final;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
@TargetApi(26)
public class MainActivity extends AppCompatActivity {

    //Variable Declaration
    private Button loginButton;
    private TextView idText, passwordText;
    private CheckBox rememberedCheck;

    //Connection URL
    public static final String URL = "192.168.8.101";

    //Account Types
    public static final String ACCOUNT_TYPE_TEACHER = "Teacher";
    public static final String ACCOUNT_TYPE_HOD = "HOD";
    public static final String ACCOUNT_TYPE_QMD = "QMD";

    //Designations
    public static final String TEACHER_DESIGNATION_ASST_PROF = "asst_prof";
    public static final String TEACHER_DESIGNATION_HOD = "HOD";
    public static final String TEACHER_DESIGNATION_QMD = "QMD";

    //Class schedule slots
    public static final LocalTime SLOT_TIMINGS_1 = LocalTime.parse("08:30:00");
    public static final LocalTime SLOT_TIMINGS_2 = LocalTime.parse("09:30:00");
    public static final LocalTime SLOT_TIMINGS_3 = LocalTime.parse("10:30:00");
    public static final LocalTime SLOT_TIMINGS_4 = LocalTime.parse("11:30:00");
    public static final LocalTime SLOT_TIMINGS_5 = LocalTime.parse("13:10:00");
    public static final LocalTime SLOT_TIMINGS_6 = LocalTime.parse("14:10:00");
    public static final LocalTime SLOT_TIMINGS_7 = LocalTime.parse("15:05:00");
    public static final LocalTime SLOT_TIMINGS_END = LocalTime.parse("16:00:00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginButton = findViewById(R.id.button_login);
        idText = findViewById(R.id.tin_id);
        passwordText = findViewById(R.id.tin_password);
        rememberedCheck = findViewById(R.id.check_remember_me);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "Authenticating",
                        "Verifying Credentials. Please wait...", true);
                final String userId = idText.getText().toString();
                final String userPass = passwordText.getText().toString();
                String url = "http://" + URL + "/AreebaFYP/auth.php";

                //Setting up response handler
                Response.Listener listener = new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        boolean passwordCorrect = false;
                        String accountType = "";
                        try {
                            Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                            JSONObject authResponse = new JSONObject(response.toString());
                            boolean userFound = authResponse.getBoolean("idFound");

                            //Check if user ID is correct
                            if (userFound) {
                                passwordCorrect = authResponse.getBoolean("passwordCorrect");
                                accountType = authResponse.getString("permissionLevel");
                            } else {
                                dialog.cancel();
                                //Show error dialog for wrong user ID
                                new AlertDialog.Builder(MainActivity.this).setTitle("Incorrect ID!")
                                        .setMessage("The ID was incorrect. Try Again!")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .show();
                                return;
                            }

                            //Check password and account types
                            if (passwordCorrect) {
                                dialog.cancel();
                                if (accountType.equals(ACCOUNT_TYPE_TEACHER)) {
                                    Intent userScreenIntent = new Intent(MainActivity.this, UserHome.class);
                                    userScreenIntent.putExtra("userType", ACCOUNT_TYPE_TEACHER);
                                    userScreenIntent.putExtra("userID", userId);
                                    userScreenIntent.putExtra("userFirstName", authResponse.getString("holderFirstName"));
                                    userScreenIntent.putExtra("userLastName", authResponse.getString("holderLastName"));
                                    userScreenIntent.putExtra("userDesignation", authResponse.getString("holderDesignation"));
                                    startActivity(userScreenIntent);
                                } else if (accountType.equals(ACCOUNT_TYPE_HOD)) {
                                    Intent userScreenIntent = new Intent(MainActivity.this, UserHome.class);
                                    userScreenIntent.putExtra("userType", ACCOUNT_TYPE_HOD);
                                    userScreenIntent.putExtra("userID", userId);
                                    userScreenIntent.putExtra("userFirstName", authResponse.getString("holderFirstName"));
                                    userScreenIntent.putExtra("userLastName", authResponse.getString("holderLastName"));
                                    userScreenIntent.putExtra("userDesignation", authResponse.getString("holderDesignation"));
                                    startActivity(userScreenIntent);
                                } else if (accountType.equals(ACCOUNT_TYPE_QMD)) {
                                    //Show error dialog for wrong account type
                                    new AlertDialog.Builder(MainActivity.this).setTitle("Wrong Account Type!")
                                            .setMessage("QMD accounts are not available on the Android app!")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            })
                                            .show();
                                }
                            } else {
                                dialog.cancel();
                                if (accountType.equals(ACCOUNT_TYPE_QMD)) {
                                    //Show error dialog for wrong account type
                                    new AlertDialog.Builder(MainActivity.this).setTitle("Wrong Account Type!")
                                            .setMessage("QMD accounts are not available on the Android app!")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            })
                                            .show();
                                    return;
                                } else {
                                    //Show error dialog for wrong password
                                    new AlertDialog.Builder(MainActivity.this).setTitle("Incorrect Password!")
                                            .setMessage("The password was incorrect. Try again!")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            })
                                            .show();
                                    return;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("XXXXXXXXXXXXXXXXXX", error.getLocalizedMessage());
                    }
                };

                //Initialize request string with GET method
                StringRequest request = new StringRequest(Request.Method.POST, url, listener, errorListener) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> param = new HashMap<>();
                        //Put user ID and password in data set
                        param.put("userID", userId);
                        param.put("userPass", userPass);
                        return param;
                    }
                };
                //Execute request
                Volleyton.getInstance(getApplicationContext()).addToRequestQueue(request);
                dialog.cancel();
            }
        });
    }

    //Get Current Day of week
    public static int getCurrentDayOfWeek () {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1;
    }

    //Get current slot
    public static int getCurrentSlot () {
        LocalTime currentTime = LocalTime.now();
        if (currentTime.isAfter(SLOT_TIMINGS_1) && currentTime.isBefore(SLOT_TIMINGS_2)) {
            return 1;
        } else if (currentTime.isAfter(SLOT_TIMINGS_2) && currentTime.isBefore(SLOT_TIMINGS_3)) {
            return 2;
        } else if (currentTime.isAfter(SLOT_TIMINGS_3) && currentTime.isBefore(SLOT_TIMINGS_4)) {
            return 3;
        } else if (currentTime.isAfter(SLOT_TIMINGS_4) && currentTime.isBefore(SLOT_TIMINGS_5)) {
            return 4;
        } else if (currentTime.isAfter(SLOT_TIMINGS_5) && currentTime.isBefore(SLOT_TIMINGS_6)) {
            return 5;
        } else if (currentTime.isAfter(SLOT_TIMINGS_6) && currentTime.isBefore(SLOT_TIMINGS_7)) {
            return 6;
        } else if (currentTime.isAfter(SLOT_TIMINGS_7) && currentTime.isBefore(SLOT_TIMINGS_END)) {
            return 7;
        } else if (currentTime.isAfter(SLOT_TIMINGS_END) || currentTime.isBefore(SLOT_TIMINGS_1)) {
            return -1;
        } else {
            return 0;
        }
    }
}
