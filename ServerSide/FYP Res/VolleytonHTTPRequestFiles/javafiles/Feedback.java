package com.ascomp.database_prac;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class Feedback extends AppCompatActivity {
    EditText et_name, et_email,et_feedback;
    Button login_button;
    Dialog dialog1;
    public static final String channelId="channel0";
    NotificationManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loign);
        et_name=findViewById(R.id.et_name);
        et_email=findViewById(R.id.et_email);
        et_feedback=findViewById(R.id.et_feedback);
        login_button=findViewById(R.id.login_button);
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT>=26)
            createNotificationChannel();

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = et_name.getText().toString();
                final String email = et_email.getText().toString();
                final String feedback = et_feedback.getText().toString();
                showLoader();
                String url = "http://192.168.10.6/android/sendfeedback.php";

                Response.Listener listener1 = new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        Log.v("-----", response.toString());
                        dialog1.dismiss();
                        showPushNote();
                        try {
                            JSONObject object2 = new JSONObject(response.toString());
                            boolean b = object2.getBoolean("status");
                            String Data = object2.getString("message");
                            if (b == true) {
                                Toast.makeText(getApplicationContext(), "sent", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                Response.ErrorListener errorListener1 = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog1.dismiss();
                        Log.v("-----", "ERROR");
                    }
                };
                StringRequest request = new StringRequest(Request.Method.POST, url, listener1, errorListener1) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> param = new HashMap<>();
                        param.put("name", name);
                        param.put("email", email);
                        param.put("feedback", feedback);
                        return param;
                    }
                };
                Volleyton.getInstance(getApplicationContext()).addToRequestQueue(request);
            }
        });
    }

    private void showLoader(){
        dialog1=new Dialog(Feedback.this);
        dialog1.setContentView(R.layout.dialog);
        dialog1.setCancelable(false);
        dialog1.show();
    }

    private void showPushNote(){
        NotificationCompat.Builder builder=null;
        if(Build.VERSION.SDK_INT>=26)
            builder= new NotificationCompat.Builder(getBaseContext(),channelId);
        else
            builder= new NotificationCompat.Builder(getBaseContext());

        builder.setContentText("feedback uploaded");
        builder.setContentTitle("Facebook");
        builder.setSmallIcon(R.drawable.fb);
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        Notification note= builder.build();
        manager.notify(0,note);
    }
    public void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=26){
            NotificationChannel channel=new NotificationChannel(channelId,"DB_prac",manager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
    }
}
