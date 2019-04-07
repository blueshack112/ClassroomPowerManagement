package com.hassan.android.fyp_app_final;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {

    //Variable Declaration
    private Button loginButton;
    private TextView idText, passwordText;
    private CheckBox rememberedCheck;


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
                String id = idText.getText().toString();
                String password = passwordText.getText().toString();
                if (id.equals("")) {
                    Toast.makeText(MainActivity.this, "Please enter an ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!id.equals("test-id")) {
                    Toast.makeText(MainActivity.this, "Invalid Id", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals("pass")) {
                    Toast.makeText(MainActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(MainActivity.this, "Correct Login and " + (rememberedCheck.isChecked()? "Remembered": "Not Remembered"), Toast.LENGTH_SHORT).show();
                Intent userScreenIntent = new Intent(MainActivity.this, UserHome.class);
                userScreenIntent.putExtra("userType", "teacher");
                startActivity(userScreenIntent);
            }
        });
    }
}
