package edu.plu.heathdj.hojicha;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener {
    Button returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        returnButton = (Button)findViewById(R.id.returnButton);
        returnButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        android.content.Intent intent = new android.content.Intent(this,MainActivity.class);
        startActivity(intent);
    }
}