package com.ascomp.database_prac;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class login1 extends AppCompatActivity {

    Button Signout, b_show;
    SharedPreferences prefer1;
    LinearLayout ll_table;
    Database1 db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);
        Signout=findViewById(R.id.signout);
        b_show= findViewById(R.id.b_show);
        ll_table=findViewById(R.id.ll_table);

        db= new Database1(login1.this);

        Signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefer1=getSharedPreferences("User", MODE_PRIVATE);
                prefer1.edit().remove("em").apply();
                prefer1.edit().remove("pass").apply();

                startActivity(new Intent(login1.this, MainActivity.class));
            }
        });
        b_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor cursor= Database1.getData();
                while ((cursor.moveToNext())){
                    String name=cursor.getString(1);
                    String password=cursor.getString(2);

                    TextView tv_name= new TextView(getBaseContext());
                    TextView tv_password= new TextView(getBaseContext());

                    tv_name.setText(name);
                    tv_password.setText(password);

                    ll_table.addView(tv_name);
                    ll_table.addView(tv_password);
                }
            }
        });
    }
}
