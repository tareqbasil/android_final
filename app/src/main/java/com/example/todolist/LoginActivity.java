package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);
    }

    public void create(View view) {
        Intent f = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(f);
    }

    public void login_new(View view) {
        Intent e =  new Intent(LoginActivity.this, ListActivity.class);
        startActivity(e);
    }
}
