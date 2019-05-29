package com.example.lishanxin.commonuse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.lishanxin.commonuse.process.MessengerActivity;
import com.example.lishanxin.commonuse.process.aidlprocess.AidlClientActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void showProcessActivity(View view) {
        Intent intent = new Intent(this, MessengerActivity.class);
        startActivity(intent);
    }

    public void showAIDLProcessActivity(View view) {
        Intent intent = new Intent(this, AidlClientActivity.class);
        startActivity(intent);
    }
}
