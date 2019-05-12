package com.hassan.android.fyp_app_final;

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

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //Variable Declaration
    private Button loginButton;
    private TextView idText, passwordText;
    private CheckBox rememberedCheck;

    public static final String ACCOUNT_TYPE_TEACHER = "Teacher";
    public static final String ACCOUNT_TYPE_HOD = "HOD";
    public static final String ACCOUNT_TYPE_QMD = "QMD";


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
                String url="http://192.168.18.11:5555/AreebaFYP/auth.php";

                //Setting up response handler
                Response.Listener listener=new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        boolean passwordCorrect = false;
                        String accountType = "";
                        try {
                            Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            Log.d("XXXXXXXXXXXXXXXXXXXXXXX", response.toString());
                            JSONObject authResponse =new JSONObject(response.toString());
                            boolean userFound = authResponse.getBoolean("idFound");

                            //Check if user ID is correct
                            if (userFound) {
                                Log.d("ZZZZZZZZZZZZZZZZZZ", Boolean.toString(authResponse.getBoolean("passwordCorrect")));
                                passwordCorrect = authResponse.getBoolean("passwordCorrect");
                                accountType = authResponse.getString("permissionLevel");
                            } else {
                                dialog.cancel();
                                //Show error dialog for wrong user ID
                                new AlertDialog.Builder(MainActivity.this).setTitle("Incorrect ID!")
                                        .setMessage("The ID was incorrect. Try Again!")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {}})
                                        .show();
                                return;
                            }

                            //Check password and account types
                            if (passwordCorrect) {
                                dialog.cancel();
                                if (accountType.equals(ACCOUNT_TYPE_TEACHER)) {
                                    Intent userScreenIntent = new Intent(MainActivity.this, UserHome.class);
                                    userScreenIntent.putExtra("userType", ACCOUNT_TYPE_TEACHER);
                                    startActivity(userScreenIntent);
                                } else if (accountType.equals(ACCOUNT_TYPE_HOD)) {
                                    Intent userScreenIntent = new Intent(MainActivity.this, UserHome.class);
                                    userScreenIntent.putExtra("userType", ACCOUNT_TYPE_HOD);
                                    startActivity(userScreenIntent);
                                } else if (accountType.equals(ACCOUNT_TYPE_QMD)){
                                    //Show error dialog for wrong account type
                                    new AlertDialog.Builder(MainActivity.this).setTitle("Wrong Account Type!")
                                            .setMessage("QMD accounts are not available on the Android app!")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {}})
                                            .show();
                                }
                            } else {
                                dialog.cancel();
                                if (accountType.equals( ACCOUNT_TYPE_QMD)) {
                                    //Show error dialog for wrong account type
                                    new AlertDialog.Builder(MainActivity.this).setTitle("Wrong Account Type!")
                                            .setMessage("QMD accounts are not available on the Android app!")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {}})
                                            .show();
                                    return;
                                } else {
                                    //Show error dialog for wrong password
                                    new AlertDialog.Builder(MainActivity.this).setTitle("Incorrect Password!")
                                            .setMessage("The password was incorrect. Try again!")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {}})
                                            .show();
                                    return;
                                }
                            }
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                };
                Response.ErrorListener errorListener=new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("XXXXXXXXXXXXXXXXXX",error.getLocalizedMessage());
                    }
                };

                //Initialize request string with GET method
                StringRequest request =new StringRequest(Request.Method.POST,url,listener,errorListener){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Log.d("XXXXXXXXXXXXXXXXXXXX", userId+"\t"+userPass);

                        Map<String, String> param=new HashMap<>();
                        //Put user ID and password in data set
                        param.put("userID", userId);
                        param.put("userPass", userPass);return param;
                    }
                };
                //Execute request
                Volleyton.getInstance(getApplicationContext()).addToRequestQueue(request);
                dialog.cancel();
            }

        });
    }
}
