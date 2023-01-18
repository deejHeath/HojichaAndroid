package edu.plu.heathdj.hojicha;

import static edu.plu.heathdj.hojicha.HojiView.resetNumberOfMeasures;
import static edu.plu.heathdj.hojicha.HojiView.decrementNumberOfMeasures;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Path;
import java.io.ByteArrayOutputStream;
import java.util.LinkedList;

public class MainActivity<model> extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    public static final String modelNumber="";
    Button measureButton;
    Button createButton;
    Button shareButton;
    Button clearLastButton;
    Button clearAllButton;
    Button infoButton;
    TextView infoLabel;
    Spinner modelSpinner;
    //TextView infoXLabel;
    String[] actionText={"Create or move POINTS", "Swipe between POINTS to create midpoint","Select 2 OBJECTS to create their intersection","Swipe from LINE to POINT to reflect","Swipe from CIRCLE to POINT to invert", "Swipe between POINTS to create segment", "Swipe between POINTS to create ray","Swipe between POINTS to create line","Swipe from LINE to POINT to create ⊥ line","Swipe from LINE to POINT to create || line","Select 3 POINTS to create bisector","Swipe between POINTS to create circle"};
    String[] measureText={"Select 2 POINTS to measure distance","Select 3 POINTS to measure angle","Select 3 POINTS to measure area of triangle","Measure circumference of CIRCLE","Measure area of CIRCLE", "Measure sum of 2 MEASURES","Measure difference of 2 MEASURES","Measure product of 2 MEASURES","Measure ratio of 2 MEASURES","Select OBJECT to hide","Show/hide label of OBJECT","Swipe to translate"};
    //public static int model=0;
    static int whatToDo = 7;
    public static LinkedList<Construct> linkedList = new LinkedList();
    static int model=0;
    final int makePoints=0, makeMidpoint=1, makeIntersections=2, foldPoints=3, invertPoints=4;
    final int makeSegments=5, makeRays=6, makeLines=7, makePerps=8, makeParallels=9;
    final int makeBisectors=10, makeCircles=11;
    final int measureDistance=20, measureAngle=21, measureTriArea=22;
    final int measureCircumference=23, measureCircArea=24;
    final int measureSum=25, measureDifference=26, measureProduct=27, measureRatio=28;
    final int hideObject=29, toggleLabel=30, scaleEverything=31;
    public static final int POINT = 1, PTonLINE = 2, PTonCIRCLE = 3, MIDPOINT = 4;
    public static final int LINEintLINE = 5, FOLDedPT = 6;
    public static final int CIRCintCIRC0 = 8,CIRCintCIRC1 = 9, LINEintCIRC0 = 10, LINEintCIRC1 = 11;
    public static final int HIDDENthing = 17;
    public static final int DISTANCE = 20, ANGLE = 21, TriAREA=22, CIRCUMFERENCE=23, CircAREA=24;
    public static final int SUM = 25, DIFFERENCE = 26, PRODUCT = 27, RATIO = 28;
    public static final int CIRCLE = 0;
    public static final int LINE = -1, PERP = -2, PARALLEL0 = -3, PARALLEL1 = -4;
    public static final int BISECTOR = -5, SEGMENT = -11, RAY = -12;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        measureButton = findViewById(R.id.measureBtn);
        createButton = findViewById(R.id.createBtn);
        shareButton = findViewById(R.id.shareBtn);
        clearLastButton = findViewById(R.id.clearLastBtn);
        clearAllButton = findViewById(R.id.clearAllBtn);
        infoButton = findViewById(R.id.infoBtn);
        infoLabel = findViewById(R.id.infoLbl);
        modelSpinner = findViewById(R.id.modelSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.models,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modelSpinner.setAdapter(adapter);

        //infoXLabel = findViewById(R.id.infoXLbl);
        measureButton.setOnClickListener(this);
        createButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);
        clearLastButton.setOnClickListener(this);
        clearAllButton.setOnClickListener(this);
        infoButton.setOnClickListener(this);
        modelSpinner.setOnItemSelectedListener(this);
        infoLabel.setBackgroundColor(Color.WHITE);
        //infoXLabel.setBackgroundColor(Color.WHITE);
        android.content.Intent intent = getIntent();
        whatToDo=intent.getIntExtra(CreateActivity.createNumber,7);
        whatToDo=intent.getIntExtra(MeasureActivity.measureNumber,7);
        if (whatToDo < 14) {
            infoLabel.setText(actionText[whatToDo]);
            //infoXLabel.setText(actionText[whatToDo]);
        } else {
            infoLabel.setText(measureText[whatToDo-20]);
            //infoXLabel.setText(measureText[whatToDo-20]);
        }
    }
    public static int getWhatToDo() {
        return whatToDo;
    }
    @Override
    public void onClick(View view) {
        View HojiView = (View) findViewById(R.id.rect);
        switch (view.getId()) {
            case R.id.createBtn:
                openCreateActivity();
                break;
            case R.id.measureBtn:
                openMeasureActivity();
                break;
            case R.id.shareBtn:
                // View to BitMap
                Bitmap b  = getScreenShot(getWindow().getDecorView().findViewById(R.id.rect));

                //BitMap to Parsable Uri (needs write permissions)
                String pathofBmp = MediaStore.Images.Media.insertImage(getContentResolver(), b,"title", null);
                Uri bmpUri = Uri.parse(pathofBmp);

                //Share the image
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                shareIntent.setType("image/jpeg");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "send"));
                break;
            case R.id.clearLastBtn:
                doClearLastStuff();
                HojiView.invalidate();
                break;
            case R.id.clearAllBtn:
                resetNumberOfMeasures();
                linkedList.clear();
                resetWhatToDo();
                HojiView.invalidate();
                break;
            case R.id.infoBtn:
                openInfoActivity();
                break;
            default:
                break;
        }
    }

    public void openCreateActivity() {
        android.content.Intent intent = new android.content.Intent(this,CreateActivity.class);
        startActivity(intent);
    }

    public void openMeasureActivity() {
        android.content.Intent intent = new android.content.Intent(this,MeasureActivity.class);
        startActivity(intent);
    }

    public void openInfoActivity() {
        android.content.Intent intent = new android.content.Intent(this,InfoActivity.class);
        startActivity(intent);
    }

    public void doClearLastStuff() {
        if (linkedList.size()>0) {
            if (linkedList.getLast().type >= DISTANCE) {
                decrementNumberOfMeasures();
            }
            if (linkedList.getLast().type==HIDDENthing) {
                for(int i=0;i<linkedList.getLast().parent.size();i++) {
                    linkedList.getLast().parent.get(i).isShown=true;
                }
            }
            if (linkedList.size() > 1) {
                if (linkedList.getLast().type == CIRCintCIRC1 || linkedList.getLast().type == LINEintCIRC1 || linkedList.getLast().type==PARALLEL1) {
                    linkedList.removeLast();         // since there were two created at once
                }
            }
            linkedList.removeLast();
        }
        if (linkedList.size()<2) {
            resetWhatToDo();
        }
    }
    public void resetWhatToDo() {
        whatToDo = makeLines;
        infoLabel.setText(actionText[whatToDo]);
        //infoXLabel.setText(actionText[whatToDo]);
    }

    public static Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (model != 0) {
            modelSpinner.setSelection(model);
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLUE);
        model=i;
        View Hojiview = findViewById(R.id.rect);
        Hojiview.invalidate();
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
