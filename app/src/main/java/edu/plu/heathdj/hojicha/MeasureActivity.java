package edu.plu.heathdj.hojicha;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MeasureActivity extends AppCompatActivity implements View.OnClickListener {
    Button[] measureButton = new Button[14];
    public static final String measureNumber="";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);
        measureButton[0] = (Button)findViewById(R.id.button20);
        measureButton[1] = (Button)findViewById(R.id.button21);
        measureButton[2] = (Button)findViewById(R.id.button22);
        measureButton[3] = (Button)findViewById(R.id.button23);
        measureButton[4] = (Button)findViewById(R.id.button24);
        measureButton[5] = (Button)findViewById(R.id.button25);
        measureButton[6] = (Button)findViewById(R.id.button26);
        measureButton[7] = (Button)findViewById(R.id.button27);
        measureButton[8] = (Button)findViewById(R.id.button28);
        measureButton[9] = (Button)findViewById(R.id.button29);
        measureButton[10] = (Button)findViewById(R.id.button30);
        measureButton[11] = (Button)findViewById(R.id.button31);
        for(int i=0;i<=11;i++) measureButton[i].setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int whatToDo;
        switch (view.getId()) {
            case R.id.button20:
                whatToDo = 20;
                break;
            case R.id.button21:
                whatToDo = 21;
                break;
            case R.id.button22:
                whatToDo = 22;
                break;
            case R.id.button23:
                whatToDo = 23;
                break;
            case R.id.button24:
                whatToDo = 24;
                break;
            case R.id.button25:
                whatToDo = 25;
                break;
            case R.id.button26:
                whatToDo = 26;
                break;
            case R.id.button27:
                whatToDo = 27;
                break;
            case R.id.button28:
                whatToDo = 28;
                break;
            case R.id.button29:
                whatToDo = 29;
                break;
            case R.id.button30:
                whatToDo = 30;
                break;
            case R.id.button31:
                whatToDo = 31;
                break;
            default:
                whatToDo = 31;
                break;
        }
        android.content.Intent intent = new android.content.Intent(this,MainActivity.class);
        intent.putExtra(measureNumber,whatToDo);
        startActivity(intent);
    }
}
