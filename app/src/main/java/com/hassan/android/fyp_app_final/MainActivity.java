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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
//TODO: Proper documentation
@TargetApi(26)
public class MainActivity extends AppCompatActivity {

    //Variable Declaration
    private Button loginButton;
    private TextView idText, passwordText;
    private TextView forgotPassword;

    //Connection URL 192.168.18.56:4444
    public static final String URL = "192.168.18.4";

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
        forgotPassword = findViewById(R.id.tv_forgot_password);

        //TODO: Remove this debug part
        idText.setText("1001");
        passwordText.setText("Hamdard123");

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder accountIDResetBuilder = new AlertDialog.Builder(MainActivity.this);
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_enter_id, null);
                final TextView accountIdReset = view.findViewById(R.id.tv_account_id);
                accountIDResetBuilder.setTitle("Enter your account ID");
                accountIDResetBuilder.setView(view);
                accountIDResetBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                accountIDResetBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        final String accountID = accountIdReset.getText().toString();
                        String url = "http://" + URL + "/AreebaFYP/checkEmail.php";
                        Response.Listener listener = new Response.Listener() {
                            @Override
                            public void onResponse(Object response) {
                                Log.d("XXXXXXXXXXXXXXXXXX", response.toString());
                                boolean userIdCorrect = false;
                                try {
                                    JSONObject authResponse = new JSONObject(response.toString());
                                    boolean idFound = authResponse.getBoolean("idFound");
                                    boolean emailSent = authResponse.getBoolean("emailSent");

                                    //Check if user ID is correct
                                    if (idFound) {
                                        if (emailSent) {
                                            Toast.makeText(MainActivity.this, "The password has been sent to your email address. Kindly Check it.", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        } else {
                                            Toast.makeText(MainActivity.this, "Could not send the email due to some difficulties.", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    } else {
                                        Toast.makeText(MainActivity.this, "The account ID you entered was incorrect, make sure you are entering a valid account ID.", Toast.LENGTH_SHORT).show();
                                    }
                                    dialog.dismiss();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        Response.ErrorListener errorListener = new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("XXXXXXXXXXXXXXXXXX", "Error");
                                error.printStackTrace();
                            }
                        };

                        //Initialize request string with POST method
                        StringRequest request = new StringRequest(Request.Method.POST, url, listener, errorListener) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> param = new HashMap<>();
                                //Put user ID and password in data set
                                param.put("userID", accountID);
                                return param;
                            }
                        };
                        //Execute request
                        request.setRetryPolicy(new RetryPolicy() {
                            @Override
                            public int getCurrentTimeout() {
                                return 50000;
                            }

                            @Override
                            public int getCurrentRetryCount() {
                                return 50000;
                            }

                            @Override
                            public void retry(VolleyError error) throws VolleyError {

                            }
                        });

                        Volleyton.getInstance(getApplicationContext()).addToRequestQueue(request);
                    }
                });
                AlertDialog dialog = accountIDResetBuilder.create();
                dialog.show();
            }
        });

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
                        String localizedMessage = error.getLocalizedMessage();
                        if (localizedMessage != null)
                            Log.v("XXXXXXXXXXXXXXXXXX", error.getLocalizedMessage());
                        else
                            Toast.makeText(MainActivity.this, "Check you internet connection and try again.", Toast.LENGTH_LONG).show();
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

    /**
     * Funciton that return the day of week such that monday is 1 and friday is 5
     * @return int: 1-5
     */
    public static int getCurrentDayOfWeek() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * Function that return day of week such that monday is 0 and friday is 4
     * @return int: 0-4
     */
    public static int getCurrentDayOfWeekAsIndex() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2;
    }

    /**
     * Function that calculates which slot it is and returns as an int
     * @return int: 0-7: 0 is no slot while 1-7 are slots
     */
    public static int getCurrentSlot() {
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
