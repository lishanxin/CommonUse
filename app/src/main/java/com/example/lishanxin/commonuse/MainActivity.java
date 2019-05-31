package com.example.lishanxin.commonuse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.lishanxin.commonuse.process.MessengerActivity;
import com.example.lishanxin.commonuse.process.aidlprocess.AidlClientActivity;

public class MainActivity extends AppCompatActivity {

    TextView textView;

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.showTest);
    }

    public void showProcessActivity(View view) {
        Intent intent = new Intent(this, MessengerActivity.class);
        startActivity(intent);
    }

    public void showAIDLProcessActivity(View view) {
        Intent intent = new Intent(this, AidlClientActivity.class);
        startActivity(intent);
    }

    public void showNdkTest(View view) {
        if (textView != null)
        textView.setText(stringFromJNI());
    }

    public native String  stringFromJNI();
}
