package com.ascomp.database_prac;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

public class MainActivity extends AppCompatActivity {

    EditText et_email, et_password;
    Button b_login, B_show;
    SharedPreferences prefer1;
    CheckBox checkBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_email=findViewById(R.id.et_email);
        et_password=findViewById(R.id.et_password);
        b_login=findViewById(R.id.b_login);
        checkBox=findViewById(R.id.checkBox);


        final Database1 db= new Database1(MainActivity.this);

        prefer1= getSharedPreferences("User", MODE_PRIVATE);
        et_email.setText(prefer1.getString("em",""));
        et_password.setText(prefer1.getString("pass", ""));

        if (prefer1.contains("em")){
            startActivity(new Intent(MainActivity.this, login1.class));
        }

        b_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email= et_email.getText().toString();
                final String password=et_password.getText().toString();
                final boolean check=checkBox.isChecked();

                ContentValues values=new ContentValues();
                values.put("name", email);
                values.put("password", password);

                db.insertData(values);

                startActivity(new Intent(MainActivity.this, login1.class));

/*                if(checkBox.isChecked()) {
                    prefer1.edit().putString("em", email).apply();
                    prefer1.edit().putString("pass", password).apply();
                }


                String url="http://192.168.10.6/android/login.php";

                Response.Listener listener=new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        Log.v("-----",response.toString());
                            try {
                            JSONObject object1 =new JSONObject(response.toString());
                            boolean b= object1.getBoolean("status");
                            String Data=object1.getString("body");
                            if (b== true){
                                Intent intent=new Intent(MainActivity.this,Feedback.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Login failed", Toast.LENGTH_LONG).show();
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
                        Log.v("-----","Error");
                    }
                };

                StringRequest request =new StringRequest(Request.Method.POST,url,listener,errorListener){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> param=new HashMap<>();
                        param.put("email", email);
                        param.put("password", password);
                        return param;
                    }
                };;
                Volleyton.getInstance(getApplicationContext()).addToRequestQueue(request);
            */
            }
        });

   //     Intent intent=new Intent(MainActivity.this,MyService1.class);
     //   startService(intent);
    }
}
