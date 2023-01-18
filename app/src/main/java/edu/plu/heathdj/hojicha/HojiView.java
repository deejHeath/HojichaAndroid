package edu.plu.heathdj.hojicha;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.expandable.ExpandableWidgetHelper;

import java.util.LinkedList;

import java.util.LinkedList;

public class HojiView extends View {
    int model;
    Construct potentialClick;
    LinkedList<Construct> clickedList = new LinkedList();
    LinkedList<Integer> clickedIndex = new LinkedList();
    double[] firstTouch;
    boolean activeConstruct = false,newPoint=false,newFirstPoint=false,firstMove=true;
    double touchSense = 48.0;
    static int numberOfMeasures = 1;
    final int makePoints=0, makeMidpoint=1, makeIntersections=2, foldPoints=3;
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
    double lastAngle=0.0;
    double[] lastVector={0,0};

    public HojiView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void onDraw(Canvas canvas){
        int x=canvas.getWidth(),y=canvas.getHeight();
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setTextSize(45);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(4);
        switch (MainActivity.model) {
            case 0:
                //canvas.drawText("Weierstrass", (float) (0.75*x), 55f, paint);
                break;
            case 1:
                //canvas.drawText("Poincaré", (float) (0.81*x), 55f, paint);
                paint.setColor(Color.rgb(128,0,128));
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(x/2,y/2,Math.max(x,y)/2,paint);
                break;
            case 2:
                //canvas.drawText("Klein", (float) (0.88*x), 55f, paint);
                paint.setColor(Color.rgb(0,128,128));
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(x/2,y/2,Math.min(x,y)/2,paint);
                break;
            case 3:
                //canvas.drawText("Half plane", (float) (0.78*x), 55f, paint);
                paint.setColor(Color.rgb(255,0,255));
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawLine(0,y-2,x,y-2,paint);
                break;
        }

        for (int i=0;i<MainActivity.linkedList.size();i++) {
            if ((MainActivity.linkedList.get(i).type==CircAREA && MainActivity.linkedList.get(i).isReal && MainActivity.linkedList.get(i).isShown) ||
                    (MainActivity.linkedList.get(i).type==TriAREA && MainActivity.linkedList.get(i).isReal && MainActivity.linkedList.get(i).isShown)) {
                if (clickedIndex.contains(MainActivity.linkedList.get(i).index)) {
                    MainActivity.linkedList.get(i).draw(canvas, paint, true);
                } else {
                    MainActivity.linkedList.get(i).draw(canvas, paint, false);
                }
            }
        }
        for (int i=0;i<MainActivity.linkedList.size();i++) {
            if (MainActivity.linkedList.get(i).type<=0 && MainActivity.linkedList.get(i).isReal && MainActivity.linkedList.get(i).isShown) {
                if (clickedIndex.contains(MainActivity.linkedList.get(i).index)) {
                    MainActivity.linkedList.get(i).draw(canvas, paint, true);
                } else {
                    MainActivity.linkedList.get(i).draw(canvas, paint, false);
                }
            }
        }
        for (int i=0;i<MainActivity.linkedList.size();i++) {
            if (MainActivity.linkedList.get(i).type>0 && MainActivity.linkedList.get(i).isReal && MainActivity.linkedList.get(i).isShown &&
            MainActivity.linkedList.get(i).type != CircAREA && MainActivity.linkedList.get(i).type != TriAREA) {
                if (clickedIndex.contains(MainActivity.linkedList.get(i).index)) {
                    MainActivity.linkedList.get(i).draw(canvas, paint, true);
                } else {
                    MainActivity.linkedList.get(i).draw(canvas, paint, false);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int whatToDo=MainActivity.getWhatToDo();
        double[] location = {event.getX(),event.getY()};
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                firstTouch=location;
                activeConstruct=false;
                newPoint=false;
                newFirstPoint=false;
                firstMove=true;
                switch(whatToDo){
                    case makePoints:
                        getPointOrMeasure(location);
                        if (!activeConstruct) {
                            newPoint = true;
                            getLineOrCircle(location);
                        }
                        if (!activeConstruct) {
                            LinkedList<Construct> tempList = new LinkedList();
                            MainActivity.linkedList.add(new Point(tempList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                            setActiveConstruct(MainActivity.linkedList.size() - 1);
                        } else {
                            if (clickedList.get(0).type < 0) {
                            MainActivity.linkedList.add(new PointOnLine(clickedList, location, MainActivity.linkedList.size(),getWidth(),getHeight()));
                            setActiveConstruct(MainActivity.linkedList.size()-1);
                            } else if (clickedList.get(0).type == 0) {
                            MainActivity.linkedList.add(new PointOnCircle(clickedList, location, MainActivity.linkedList.size(),getWidth(), getHeight()));
                            setActiveConstruct(MainActivity.linkedList.size()-1);
                            } else {
                                // clickedList.get(0) is a point or measure
                            }
                        }
                    break;
                    case makeLines:
                    case makeSegments:
                    case makeRays:
                    case makeMidpoint:
                    case makeCircles:
                        getPointOrLineOrCircle(location);
                        if (activeConstruct) {
                            if (clickedList.get(0).type<0) {
                                newFirstPoint = true;
                                MainActivity.linkedList.add(new PointOnLine(clickedList,location, MainActivity.linkedList.size(),getWidth(),getHeight()));
                                setActiveConstruct(MainActivity.linkedList.size() - 1);
                                clickedList.removeFirst();
                                clickedIndex.removeFirst();
                            } else if (clickedList.get(0).type==0) {
                                newFirstPoint=true;
                                MainActivity.linkedList.add(new PointOnCircle(clickedList, location, MainActivity.linkedList.size(),getWidth(),getHeight()));
                                setActiveConstruct(MainActivity.linkedList.size()-1);
                                clickedList.removeFirst();
                                clickedIndex.removeFirst();
                            }
                        } else {
                            newFirstPoint = true;
                            MainActivity.linkedList.add(new Point(new LinkedList<Construct>(),location,MainActivity.linkedList.size(),getWidth(),getHeight()));
                            setActiveConstruct(MainActivity.linkedList.size()-1);
                        }
                        break;
                    case makeIntersections:
                        getLineOrCircle(location);
                        if (!activeConstruct) {
                            potentialClick = null;
                        }
                        break;
                    case makeBisectors:
                    case measureDistance:
                    case measureAngle:
                    case measureTriArea:
                        getPoint(location);
                        if (!activeConstruct) {
                            potentialClick = null;
                        }
                        break;
                    case foldPoints:
                    case makePerps:
                    case makeParallels:
                        getLine(location);
                        if (!activeConstruct) {
                            potentialClick = null;
                        }
                        break;
                    case measureRatio:
                    case measureSum:
                    case measureProduct:
                    case measureDifference:
                        getMeasure(location);
                        if (!activeConstruct) {
                            potentialClick = null;
                        }
                        break;
                    case measureCircArea:
                    case measureCircumference:
                        getCircle(location);
                        if (!activeConstruct) {
                            potentialClick = null;
                        }
                        break;
                    case hideObject:
                    case toggleLabel:
                        getPointOrLineOrCircle(location);
                        if (!activeConstruct) {
                            potentialClick = null;
                        }
                        break;
                    case scaleEverything:
                        lastVector[0] = location[0];
                        lastVector[1] = location[1];
                        break;
                    default:
                        break;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                switch(whatToDo) {
                    case makePoints:
                        if (!newPoint) {
                            clickedList.get(0).update(location);
                        } else { // otherwise the point is new, and we can do what we like with it.
                            clearAllPotentials();
                            getLineOrCircle(location);
                            if (activeConstruct) {
                                if (clickedList.get(0).type < 0) {
                                    MainActivity.linkedList.removeLast();
                                    MainActivity.linkedList.add(new PointOnLine(clickedList, location,
                                            MainActivity.linkedList.size(), getWidth(), getHeight()));
                                    setActiveConstruct(MainActivity.linkedList.size() - 1);
                                } else {
                                    MainActivity.linkedList.removeLast();
                                    MainActivity.linkedList.add(new PointOnCircle(clickedList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                    setActiveConstruct(MainActivity.linkedList.size() - 1);
                                }
                            } else {
                                MainActivity.linkedList.removeLast();
                                LinkedList<Construct> tempList = new LinkedList();
                                MainActivity.linkedList.add(new Point(tempList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                setActiveConstruct(MainActivity.linkedList.size() - 1);
                            }
                        }
                        break;
                    case makeLines:
                    case makeSegments:
                    case makeRays:
                    case makeMidpoint:
                    case makeCircles:
                        if (firstMove) {
                            firstMove = false;
                        } else {
                            MainActivity.linkedList.removeLast(); // remove temporary segment
                            clickedList.removeLast();
                            clickedIndex.removeLast();
                            if (newPoint) {
                                MainActivity.linkedList.removeLast(); // remove temporary point
                                clickedList.removeLast();
                                clickedIndex.removeLast();
                                newPoint = false;
                            }
                        }
                        activeConstruct = false;
                        while (clickedList.size() > 1) {
                            clickedList.removeLast();
                            clickedIndex.removeLast();
                        }
                        getPointOrLineOrCircle(location);
                        if (activeConstruct) {
                            LinkedList<Construct> temp = new LinkedList();
                            temp.add(clickedList.getLast());
                            if (clickedList.getLast().type > 0) {
                                newPoint = false;
                            } else if (clickedList.getLast().type < 0) {
                                newPoint = true;
                                MainActivity.linkedList.add(new PointOnLine(temp, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                clickedList.remove(1);
                                clickedIndex.remove(1);
                                setActiveConstruct(MainActivity.linkedList.size() - 1);
                            } else {
                                newPoint = true;
                                MainActivity.linkedList.add(new PointOnCircle(temp, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                clickedList.remove(1);
                                clickedIndex.remove(1);
                                setActiveConstruct(MainActivity.linkedList.size() - 1);
                            }
                        } else {
                            newPoint = true;
                            MainActivity.linkedList.add(new Point(new LinkedList<Construct>(),
                                    location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                            setActiveConstruct(MainActivity.linkedList.size() - 1);
                        }
                        switch (whatToDo) {
                            case makeLines:
                                MainActivity.linkedList.add(new Line(clickedList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                break;
                            case makeCircles:
                                MainActivity.linkedList.add(new Circle(clickedList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                break;
                            case makeSegments:
                                MainActivity.linkedList.add(new Segment(clickedList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                break;
                            case makeRays:
                                MainActivity.linkedList.add(new Ray(clickedList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                break;
                            case makeMidpoint:
                                MainActivity.linkedList.add(new Midpoint(clickedList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                break;
                        }
                        setActiveConstruct(MainActivity.linkedList.size() - 1);
                        break;
                    case makeIntersections:
                        getRidOfActivesThatAreTooFar(location);
                        if (!activeConstruct) {
                            getLineOrCircle(location);
                        }
                        break;
                    case foldPoints:
                    case makePerps:
                    case makeParallels:
                        if (firstMove) {
                            firstMove = false;
                        } else if (clickedList.size() > 0) {
                            MainActivity.linkedList.removeLast(); // remove temporary segment
                            if (whatToDo == makeParallels) {
                                MainActivity.linkedList.removeLast();
                            }
                            clickedList.removeLast();
                            clickedIndex.removeLast();
                            if (newPoint) {
                                MainActivity.linkedList.removeLast(); // remove temporary point
                                clickedList.removeLast();
                                clickedIndex.removeLast();
                                newPoint = false;
                            }
                        }
                        activeConstruct = false;
                        while (clickedList.size() > 1) {
                            clickedList.removeLast();
                            clickedIndex.removeLast();
                        }
                        if (clickedList.size() == 1) {
                            getPointOrLineOrCircleAllowingRepeatConstructions(location);
                            if (activeConstruct) {
                                if (clickedList.getLast().type > 0) {
                                    newPoint = false;
                                } else if (clickedList.getLast().type < 0) {
                                    newPoint = true;
                                    LinkedList<Construct> temp = new LinkedList();
                                    temp.add(clickedList.getLast());
                                    MainActivity.linkedList.add(new PointOnLine(temp,
                                            location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                    clickedList.remove(1);
                                    clickedIndex.remove(1);
                                    setActiveConstruct(MainActivity.linkedList.size() - 1);
                                } else {
                                    newPoint = true;
                                    LinkedList<Construct> temp = new LinkedList();
                                    temp.add(clickedList.getLast());
                                    MainActivity.linkedList.add(new PointOnCircle(temp, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                    clickedList.remove(1);
                                    clickedIndex.remove(1);
                                    setActiveConstruct(MainActivity.linkedList.size() - 1);
                                }
                            } else {
                                newPoint = true;
                                LinkedList<Construct> temp = new LinkedList();
                                MainActivity.linkedList.add(new Point(temp, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                setActiveConstruct(MainActivity.linkedList.size() - 1);
                            }
                            LinkedList<Construct> newList = new LinkedList();
                            newList.add(clickedList.get(1));
                            newList.add(clickedList.get(0));
                            switch (whatToDo) {
                                case makePerps:
                                    MainActivity.linkedList.add(new PerpLine(newList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                    break;
                                case makeParallels:
                                    MainActivity.linkedList.add(new Parallel0(newList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                    setActiveConstruct(MainActivity.linkedList.size() - 1);
                                    MainActivity.linkedList.add(new Parallel1(newList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                    break;
                                case foldPoints:
                                    MainActivity.linkedList.add(new FoldedPoint(newList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                    break;
                            }
                            setActiveConstruct(MainActivity.linkedList.size() - 1);
                        }
                        break;
                    case makeBisectors:
                    case measureDistance:
                    case measureAngle:
                    case measureTriArea:
                        getRidOfActivesThatAreTooFar(location);
                        if (!activeConstruct) {
                            getPoint(location);
                        }
                        break;
                    case measureCircArea:
                        getRidOfActivesThatAreTooFar(location);
                        if (!activeConstruct) {
                            getCircle(location);
                        }
                        break;
                    case measureRatio:
                    case measureSum:
                    case measureDifference:
                    case measureProduct:
                        getRidOfActivesThatAreTooFar(location);
                        if (!activeConstruct) {
                            getMeasure(location);
                        }
                        break;
                    case hideObject:
                    case toggleLabel:
                        getRidOfActivesThatAreTooFar(location);
                        if (!activeConstruct) {
                            getPointOrLineOrCircle(location);
                        }
                        break;
                    case scaleEverything:
                        double[] newVector = {location[0], location[1]};
                        LinkedList<Construct> tempList = new LinkedList();
                        Point temp0 = new Point(tempList, lastVector, 0, getWidth(), getHeight());
                        Point temp1 = new Point(tempList, newVector, 1, getWidth(), getHeight());
                        double[] u = {0, 0, 0}, v = {0, 0, 0}, w = {0, 0, 0};
                        for (int i = 0; i < MainActivity.linkedList.size(); i++) {
                            if (MainActivity.linkedList.get(i).type > 0 && MainActivity.linkedList.get(i).type <= PTonCIRCLE) {
                                temp0.getXYZ(u);
                                MainActivity.linkedList.get(i).getXYZ(v);
                                MathEqns.hypTranslate(u, v, w); // w=v-u
                                temp1.getXYZ(u);
                                u[0] = -u[0];
                                u[1] = -u[1];
                                MathEqns.hypTranslate(u, w, v); // v=w+u
                                MainActivity.linkedList.get(i).setXYZ(v);
                            }
                        }
                        lastVector[0] = newVector[0];
                        lastVector[1] = newVector[1];
                        break;
                    default:
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                switch(whatToDo) {
                    case makePoints:
                        if (!newPoint) {
                            clickedList.get(0).update(location);
                        } else { // otherwise the point is new, and we can do what we like with it.
                            clearAllPotentials();
                            getLineOrCircle(location);
                            if (activeConstruct) {
                                if (clickedList.get(0).type < 0) {
                                    MainActivity.linkedList.removeLast();
                                    MainActivity.linkedList.add(new PointOnLine(clickedList, location,
                                            MainActivity.linkedList.size(), getWidth(), getHeight()));
                                    setActiveConstruct(MainActivity.linkedList.size() - 1);
                                } else {
                                    MainActivity.linkedList.removeLast();
                                    MainActivity.linkedList.add(new PointOnCircle(clickedList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                    setActiveConstruct(MainActivity.linkedList.size() - 1);
                                }
                            } else {
                                MainActivity.linkedList.removeLast();
                                LinkedList<Construct> tempList = new LinkedList();
                                MainActivity.linkedList.add(new Point(tempList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                setActiveConstruct(MainActivity.linkedList.size() - 1);
                            }
                        }
                        clearAllPotentials();
                        break;
                    case makeLines:
                    case makeSegments:
                    case makeRays:
                    case makeMidpoint:
                    case makeCircles:
                        if (!firstMove) {
                            if (MainActivity.linkedList.getLast().type <= 0) {
                                MainActivity.linkedList.removeLast(); // remove temporary segment/ray etc.
                                clickedList.removeLast();
                                clickedIndex.removeLast();
                            }
                            if (newPoint) {
                                MainActivity.linkedList.removeLast(); // remove temporary point
                                clickedList.removeLast();
                                clickedIndex.removeLast();
                                newPoint = false;
                            }
                        }
                        activeConstruct = false;
                        while (clickedList.size() > 1) {
                            clickedList.removeLast();
                            clickedIndex.removeLast();
                        }
                        getPointOrLineOrCircle(location);
                        if (activeConstruct) {
                            LinkedList<Construct> temp = new LinkedList();
                            temp.add(clickedList.getLast());
                            if (clickedList.getLast().type > 0) {
                                newPoint = false;
                            } else if (clickedList.getLast().type < 0) {
                                newPoint = true;
                                MainActivity.linkedList.add(new PointOnLine(temp, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                clickedList.remove(1);
                                clickedIndex.remove(1);
                                setActiveConstruct(MainActivity.linkedList.size() - 1);
                            } else {
                                newPoint = true;
                                MainActivity.linkedList.add(new PointOnCircle(temp, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                clickedList.remove(1);
                                clickedIndex.remove(1);
                                setActiveConstruct(MainActivity.linkedList.size() - 1);
                            }
                        } else {
                            newPoint = true;
                            MainActivity.linkedList.add(new Point(new LinkedList<Construct>(), location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                            setActiveConstruct(MainActivity.linkedList.size() - 1);
                        }
                        if (whatToDo != makeRays && whatToDo != makeCircles) {
                            arrangeClickedObjectsByIndex();
                        }
                        if (clickedList.get(0).distance(location) > 0.01) {
                            boolean alreadyExists = false;
                            for (int i = 0; i < MainActivity.linkedList.size(); i++) {
                                if ((MainActivity.linkedList.get(i).type == LINE && whatToDo == makeLines) || (MainActivity.linkedList.get(i).type == SEGMENT && whatToDo == makeSegments) || (MainActivity.linkedList.get(i).type == RAY && whatToDo == makeRays) || (MainActivity.linkedList.get(i).type == CIRCLE && whatToDo == makeCircles) || (MainActivity.linkedList.get(i).type == MIDPOINT && whatToDo == makeMidpoint)) {
                                    if (MainActivity.linkedList.get(i).parent.get(0).index == clickedList.get(0).index && MainActivity.linkedList.get(i).parent.get(1).index == clickedList.get(1).index) {
                                        alreadyExists = true;
                                        MainActivity.linkedList.get(i).isShown = true;
                                    }
                                }
                            }
                            if (!alreadyExists) {
                                switch (whatToDo) {
                                    case makeSegments:
                                        MainActivity.linkedList.add(new Segment(clickedList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                        break;
                                    case makeLines:
                                        MainActivity.linkedList.add(new Line(clickedList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                        //MainActivity.linkedList.getLast().update(getWidth(), getHeight());
                                        break;
                                    case makeRays:
                                        MainActivity.linkedList.add(new Ray(clickedList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                        break;
                                    case makeMidpoint:
                                        MainActivity.linkedList.add(new Midpoint(clickedList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                        break;
                                    case makeCircles:
                                        MainActivity.linkedList.add(new Circle(clickedList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                        break;
                                }
                            }
                        } else if (newPoint) {
                            MainActivity.linkedList.removeLast();
                            if (newFirstPoint) {
                                MainActivity.linkedList.removeLast();
                            }
                        }
                        clearAllPotentials();
                        clearActives();
                        break;
                    case makeIntersections:
                        getRidOfActivesThatAreTooFar(location);
                        clearActives();
                        getRidOfDuplicates();
                        if (clickedList.size() == 2) {
                            arrangeClickedObjectsByIndex();
                            boolean alreadyExists = false;
                            for (int i = 0; i < MainActivity.linkedList.size(); i++) {
                                if (MainActivity.linkedList.get(i).type == LINEintLINE) {
                                    LineIntLine temp = (LineIntLine) MainActivity.linkedList.get(i);
                                    if (!alreadyExists) {
                                        if (temp.parent.get(0).index == clickedList.get(0).index && temp.parent.get(1).index == clickedList.get(1).index) {
                                            alreadyExists = true;
                                            MainActivity.linkedList.get(i).isShown = true;
                                            clearAllPotentials();
                                        }
                                    }
                                } else if (MainActivity.linkedList.get(i).type == LINEintCIRC0) {
                                    LineIntCirc0 temp = (LineIntCirc0) MainActivity.linkedList.get(i);
                                    if ((temp.parent.get(0).index == clickedList.get(0).index && temp.parent.get(1).index == clickedList.get(1).index) ||
                                            (temp.parent.get(0).index == clickedList.get(1).index && temp.parent.get(1).index == clickedList.get(0).index)) {
                                        alreadyExists = true;
                                        MainActivity.linkedList.get(i).isShown = true;
                                        MainActivity.linkedList.get(i + 1).isShown = true;
                                        clearAllPotentials();
                                    }
                                } else if (MainActivity.linkedList.get(i).type == CIRCintCIRC0) {
                                    CircIntCirc0 temp = (CircIntCirc0) MainActivity.linkedList.get(i);
                                    if (temp.parent.get(0).index == clickedList.get(0).index && temp.parent.get(1).index == clickedList.get(1).index) {
                                        alreadyExists = true;
                                        MainActivity.linkedList.get(i).isShown = true;
                                        MainActivity.linkedList.get(i + 1).isShown = true;
                                        clearAllPotentials();
                                    }
                                }
                            }
                            if (!alreadyExists) {
                                if (clickedList.get(0).type * clickedList.get(1).type > 0) {
                                    MainActivity.linkedList.add(new LineIntLine(clickedList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                } else if (clickedList.get(0).type != CIRCLE || clickedList.get(1).type != CIRCLE) {
                                    if (clickedList.get(0).type == CIRCLE) {
                                        clickedList.add(clickedList.get(0));
                                        clickedList.remove(0); // line 0th, circle 1st
                                    }
                                    MainActivity.linkedList.add(new LineIntCirc0(clickedList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                    clickedList.clear();
                                    clickedList.add(MainActivity.linkedList.getLast());
                                    MainActivity.linkedList.add(new LineIntCirc1(clickedList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                } else {
                                    MainActivity.linkedList.add(new CircIntCirc0(clickedList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                    clickedList.clear();
                                    clickedList.add(MainActivity.linkedList.getLast());
                                    MainActivity.linkedList.add(new CircIntCirc1(clickedList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                }
                                clearAllPotentials();
                            }
                        }
                        break;
                    case makePerps:
                    case makeParallels:
                    case foldPoints:
                        if (firstMove) {
                            firstMove = false;
                            if (MainActivity.linkedList.getLast().type <= 0) {
                                MainActivity.linkedList.removeLast(); // remove temporary segment
                                clickedList.removeLast();
                                clickedIndex.removeLast();
                            }
                            if (newPoint) {
                                MainActivity.linkedList.removeLast(); // remove temporary point
                                clickedList.removeLast();
                                clickedIndex.removeLast();
                                newPoint = false;
                            }
                        }
                        activeConstruct = false;
                        while (clickedList.size() > 1) {
                            clickedList.removeLast();
                            clickedIndex.removeLast();
                        }
                        if (clickedList.size() == 1) {
                            getPointOrLineOrCircleAllowingRepeatConstructions(location);
                            if (activeConstruct) {
                                if (clickedList.getLast().type > 0) {
                                    newPoint = false;
                                } else if (clickedList.getLast().type < 0) {
                                    newPoint = true;
                                    LinkedList<Construct> temp = new LinkedList();
                                    temp.add(clickedList.getLast());
                                    MainActivity.linkedList.add(new PointOnLine(temp, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                    clickedList.remove(1);
                                    clickedIndex.remove(1);
                                    setActiveConstruct(MainActivity.linkedList.size() - 1);
                                } else {
                                    newPoint = true;
                                    LinkedList<Construct> temp = new LinkedList();
                                    temp.add(clickedList.getLast());
                                    MainActivity.linkedList.add(new PointOnCircle(temp, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                    clickedList.remove(1);
                                    clickedIndex.remove(1);
                                    setActiveConstruct(MainActivity.linkedList.size() - 1);
                                }
                            } else {
                                newPoint = true;
                                LinkedList<Construct> temp = new LinkedList();
                                MainActivity.linkedList.add(new Point(temp, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                setActiveConstruct(MainActivity.linkedList.size() - 1);
                            }
                            boolean alreadyExists = false;
                            if (clickedList.size() == 2) {
                                if (whatToDo == makeParallels && clickedList.get(1).type == PTonLINE) {
                                    if (clickedList.get(1).parent.get(0).index == clickedList.get(0).index) {
                                        alreadyExists = true;
                                    }
                                }
                                for (int i = 0; i < MainActivity.linkedList.size(); i++) {
                                    if (!alreadyExists && ((whatToDo == makePerps && MainActivity.linkedList.get(i).type == PERP) || (whatToDo == foldPoints && MainActivity.linkedList.get(i).type == FOLDedPT))) {
                                        if (MainActivity.linkedList.get(i).parent.get(0).index == clickedList.get(1).index && MainActivity.linkedList.get(i).parent.get(1).index == clickedList.get(0).index) {
                                            alreadyExists = true;
                                            MainActivity.linkedList.get(i).isShown = true;
                                        }
                                    }
                                    if (!alreadyExists && (whatToDo == makeParallels && MainActivity.linkedList.get(i).type == PARALLEL0)) {
                                        if (MainActivity.linkedList.get(i).parent.get(0).index == clickedList.get(1).index && MainActivity.linkedList.get(i).parent.get(1).index == clickedList.get(0).index) {
                                            alreadyExists = true;
                                            MainActivity.linkedList.get(i).isShown = true;
                                            MainActivity.linkedList.get(i + 1).isShown = true;
                                        }
                                    }
                                }
                            }
                            LinkedList<Construct> newList = new LinkedList();
                            newList.add(clickedList.get(1));
                            newList.add(clickedList.get(0));
                            if (!alreadyExists) {
                                switch (whatToDo) {
                                    case makePerps:
                                        MainActivity.linkedList.add(new PerpLine(newList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                        break;
                                    case makeParallels:
                                        MainActivity.linkedList.add(new Parallel0(newList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                        MainActivity.linkedList.add(new Parallel1(newList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                        break;
                                    default:
                                        MainActivity.linkedList.add(new FoldedPoint(newList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                        break;
                                }
                            }
                            if (newPoint && alreadyExists) {
                                MainActivity.linkedList.removeLast();
                            }
                        }
                        clearAllPotentials();
                        clearActives();
                        break;
                    case makeBisectors:
                        getRidOfActivesThatAreTooFar(location);
                        clearActives();
                        getRidOfDuplicates();
                        if (clickedList.size() == 3) {
                            clickedList.addFirst(clickedList.get(1));
                            clickedList.remove(2);
                            if (clickedList.get(1).index > clickedList.get(2).index) {
                                clickedList.add(clickedList.get(1));
                                clickedList.remove(1);
                            }
                            boolean alreadyExists = false;
                            for (int i = 0; i < MainActivity.linkedList.size(); i++) {
                                if (MainActivity.linkedList.get(i).type == BISECTOR) {
                                    Bisector temp = (Bisector) MainActivity.linkedList.get(i);
                                    if (!alreadyExists) {
                                        if (temp.parent.get(0).index == clickedList.get(0).index && temp.parent.get(1).index == clickedList.get(1).index && temp.parent.get(2).index == clickedList.get(2).index) {
                                            alreadyExists = true;
                                            MainActivity.linkedList.get(i).isShown = true;
                                            clearAllPotentials();
                                        }
                                    }
                                }
                            }
                            if (!alreadyExists) {
                                MainActivity.linkedList.add(new Bisector(clickedList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                clearAllPotentials();
                            }
                        }
                        break;
                    case measureAngle:
                        getRidOfActivesThatAreTooFar(location);
                        clearActives();
                        getRidOfDuplicates();
                        if (clickedList.size() == 3) {
                            if (clickedList.get(0).index > clickedList.get(2).index) {
                                clickedList.add(0, clickedList.getLast());
                                clickedList.removeLast();
                                clickedList.add(clickedList.get(1));
                                clickedList.remove(1);
                            }
                            boolean alreadyExists = false;
                            for (int i = 0; i < MainActivity.linkedList.size(); i++) {
                                if (MainActivity.linkedList.get(i).type == ANGLE) {
                                    Angle temp = (Angle) MainActivity.linkedList.get(i);
                                    if (!alreadyExists) {
                                        if (temp.parent.get(0).index == clickedList.get(0).index && temp.parent.get(1).index == clickedList.get(1).index && temp.parent.get(2).index == clickedList.get(2).index) {
                                            alreadyExists = true;
                                            MainActivity.linkedList.get(i).isShown = true;
                                            clearAllPotentials();
                                        }
                                    }
                                }
                            }
                            if (!alreadyExists) {
                                MainActivity.linkedList.add(new Angle(clickedList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                double[] vector = {24, 55 * numberOfMeasures +20 };
                                numberOfMeasures += 1;
                                MainActivity.linkedList.getLast().update(vector);
                                clearAllPotentials();
                            }
                        }
                        break;
                    case measureTriArea:
                        getRidOfActivesThatAreTooFar(location);
                        clearActives();
                        getRidOfDuplicates();
                        if (clickedList.size() == 3) {
                            arrangeClickedObjectsByIndex();
                            boolean alreadyExists = false;
                            for (int i = 0; i < MainActivity.linkedList.size(); i++) {
                                if (MainActivity.linkedList.get(i).type == TriAREA) {
                                    TriArea temp = (TriArea) MainActivity.linkedList.get(i);
                                    if (!alreadyExists) {
                                        if (temp.parent.get(0).index == clickedList.get(0).index && temp.parent.get(1).index == clickedList.get(1).index && temp.parent.get(2).index == clickedList.get(2).index) {
                                            alreadyExists = true;
                                            MainActivity.linkedList.get(i).isShown = true;
                                            clearAllPotentials();
                                        }
                                    }
                                }
                            }
                            if (!alreadyExists) {
                                MainActivity.linkedList.add(new TriArea(clickedList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                double[] vector = {24, 55 * numberOfMeasures};
                                numberOfMeasures += 1;
                                MainActivity.linkedList.getLast().update(vector);
                                clearAllPotentials();
                            }
                        }
                        break;
                    case measureCircArea:
                        getRidOfActivesThatAreTooFar(location);
                        clearActives();
                        if (clickedList.size() == 1) {
                            boolean alreadyExists = false;
                            for (int i = 0; i < MainActivity.linkedList.size(); i++) {
                                if (MainActivity.linkedList.get(i).type == CircAREA) {
                                    CircArea temp = (CircArea) MainActivity.linkedList.get(i);
                                    if (!alreadyExists) {
                                        if (temp.parent.get(0).index == clickedList.get(0).index) {
                                            alreadyExists = true;
                                            MainActivity.linkedList.get(i).isShown = true;
                                            clearAllPotentials();
                                        }
                                    }
                                }
                            }
                            if (!alreadyExists) {
                                MainActivity.linkedList.add(new CircArea(clickedList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                double[] vector = {24, 55 * numberOfMeasures};
                                numberOfMeasures += 1;
                                MainActivity.linkedList.getLast().update(vector);
                                clearAllPotentials();
                            }
                        }
                        break;
                    case measureCircumference:
                        getRidOfActivesThatAreTooFar(location);
                        clearActives();
                        if (clickedList.size() == 1) {
                            boolean alreadyExists = false;
                            for (int i = 0; i < MainActivity.linkedList.size(); i++) {
                                if (MainActivity.linkedList.get(i).type == CIRCUMFERENCE) {
                                    Circumference temp = (Circumference) MainActivity.linkedList.get(i);
                                    if (!alreadyExists) {
                                        if (temp.parent.get(0).index == clickedList.get(0).index) {
                                            alreadyExists = true;
                                            MainActivity.linkedList.get(i).isShown = true;
                                            clearAllPotentials();
                                        }
                                    }
                                }
                            }
                            if (!alreadyExists) {
                                MainActivity.linkedList.add(new Circumference(clickedList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                double[] vector = {24, 55 * numberOfMeasures};
                                numberOfMeasures += 1;
                                MainActivity.linkedList.getLast().update(vector);
                                clearAllPotentials();
                            }
                        }
                        break;
                    case measureDistance:
                        getRidOfActivesThatAreTooFar(location);
                        clearActives();
                        getRidOfDuplicates();
                        if (clickedList.size() == 2) {
                            arrangeClickedObjectsByIndex();
                            boolean alreadyExists = false;
                            for (int i = 0; i < MainActivity.linkedList.size(); i++) {
                                if (MainActivity.linkedList.get(i).type == DISTANCE) {
                                    Distance temp = (Distance) MainActivity.linkedList.get(i);
                                    if (!alreadyExists) {
                                        if (temp.parent.get(0).index == clickedList.get(0).index && temp.parent.get(1).index == clickedList.get(1).index) {
                                            alreadyExists = true;
                                            MainActivity.linkedList.get(i).isShown = true;
                                            clearAllPotentials();
                                        }
                                    }
                                }
                            }
                            if (!alreadyExists) {
                                MainActivity.linkedList.add(new Distance(clickedList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                                double[] vector = {24, 55 * numberOfMeasures};
                                numberOfMeasures += 1;
                                MainActivity.linkedList.getLast().update(vector);
                                clearAllPotentials();
                            }
                        }
                        break;
                    case measureSum:
                        getRidOfActivesThatAreTooFar(location);
                        clearActives();
                        getRidOfDuplicates();
                        if (clickedList.size()==2) {
                            arrangeClickedObjectsByIndex();
                            boolean alreadyExists = false;
                            for (int i = 0; i < MainActivity.linkedList.size(); i++) {
                                if (MainActivity.linkedList.get(i).type == SUM) {
                                    Sum temp = (Sum) MainActivity.linkedList.get(i);
                                    if (!alreadyExists) {
                                        if (temp.parent.get(0).index == clickedList.get(0).index && temp.parent.get(1).index == clickedList.get(1).index) {
                                            alreadyExists = true;
                                            MainActivity.linkedList.get(i).isShown = true;
                                            clearAllPotentials();
                                        }
                                    }
                                }
                            }
                            if (!alreadyExists) {
                                MainActivity.linkedList.add(new Sum(clickedList, location, MainActivity.linkedList.size(),getWidth(),getHeight()));
                                double[] vector = {24, 55 * numberOfMeasures};
                                numberOfMeasures += 1;
                                MainActivity.linkedList.getLast().update(vector);
                                clearAllPotentials();
                            }
                        }
                        break;
                    case measureProduct:
                        getRidOfActivesThatAreTooFar(location);
                        clearActives();
                        getRidOfDuplicates();
                        if (clickedList.size()==2) {
                            arrangeClickedObjectsByIndex();
                            boolean alreadyExists = false;
                            for (int i = 0; i < MainActivity.linkedList.size(); i++) {
                                if (MainActivity.linkedList.get(i).type == PRODUCT) {
                                    Product temp = (Product) MainActivity.linkedList.get(i);
                                    if (!alreadyExists) {
                                        if (temp.parent.get(0).index == clickedList.get(0).index && temp.parent.get(1).index == clickedList.get(1).index) {
                                            alreadyExists = true;
                                            MainActivity.linkedList.get(i).isShown = true;
                                            clearAllPotentials();
                                        }
                                    }
                                }
                            }
                            if (!alreadyExists) {
                                MainActivity.linkedList.add(new Product(clickedList, location, MainActivity.linkedList.size(),getWidth(),getHeight()));
                                double[] vector = {24, 55 * numberOfMeasures};
                                numberOfMeasures += 1;
                                MainActivity.linkedList.getLast().update(vector);
                                clearAllPotentials();
                            }
                        }
                        break;
                    case measureDifference:
                        getRidOfActivesThatAreTooFar(location);
                        clearActives();
                        getRidOfDuplicates();
                        if (clickedList.size()==2) {
                            boolean alreadyExists = false;
                            for (int i = 0; i < MainActivity.linkedList.size(); i++) {
                                if (MainActivity.linkedList.get(i).type == DIFFERENCE) {
                                    Difference temp = (Difference) MainActivity.linkedList.get(i);
                                    if (!alreadyExists) {
                                        if (temp.parent.get(0).index == clickedList.get(0).index && temp.parent.get(1).index == clickedList.get(1).index) {
                                            alreadyExists = true;
                                            MainActivity.linkedList.get(i).isShown = true;
                                            clearAllPotentials();
                                        }
                                    }
                                }
                            }
                            if (!alreadyExists) {
                                MainActivity.linkedList.add(new Difference(clickedList, location, MainActivity.linkedList.size(),getWidth(),getHeight()));
                                double[] vector = {24, 55 * numberOfMeasures};
                                numberOfMeasures += 1;
                                MainActivity.linkedList.getLast().update(vector);
                                clearAllPotentials();
                            }
                        }
                        break;
                    case measureRatio:
                        getRidOfActivesThatAreTooFar(location);
                        clearActives();
                        getRidOfDuplicates();
                        if (clickedList.size()==2) {
                            boolean alreadyExists = false;
                            for (int i = 0; i < MainActivity.linkedList.size(); i++) {
                                if (MainActivity.linkedList.get(i).type == RATIO) {
                                    Ratio temp = (Ratio) MainActivity.linkedList.get(i);
                                    if (!alreadyExists) {
                                        if (temp.parent.get(0).index == clickedList.get(0).index && temp.parent.get(1).index == clickedList.get(1).index) {
                                            alreadyExists = true;
                                            MainActivity.linkedList.get(i).isShown = true;
                                            clearAllPotentials();
                                        }
                                    }
                                }
                            }
                            if (!alreadyExists) {
                                MainActivity.linkedList.add(new Ratio(clickedList, location, MainActivity.linkedList.size(),getWidth(),getHeight()));
                                double[] vector = {24, 55 * numberOfMeasures};
                                numberOfMeasures += 1;
                                MainActivity.linkedList.getLast().update(vector);
                                clearAllPotentials();
                            }
                        }
                        break;
                    case hideObject:
                        getRidOfActivesThatAreTooFar(location);
                        if (!activeConstruct) {
                            potentialClick = null;
                            activeConstruct = false;
                        }
                        if (clickedList.size() == 1) {
                            MainActivity.linkedList.get(clickedIndex.get(0)).isShown = false;
                            if (MainActivity.linkedList.getLast().type == HIDDENthing) {
                                MainActivity.linkedList.getLast().parent.add(clickedList.get(0));
                            } else {
                                MainActivity.linkedList.add(new HiddenThing(clickedList, location, MainActivity.linkedList.size(), getWidth(), getHeight()));
                            }
                            clearAllPotentials();
                        }
                        break;
                    case toggleLabel:
                        getRidOfActivesThatAreTooFar(location);
                        if (!activeConstruct) {
                            potentialClick = null;
                            activeConstruct = false;
                        }
                        if (clickedList.size() == 1) {
                            MainActivity.linkedList.get(clickedIndex.get(0)).showLabel = !MainActivity.linkedList.get(clickedIndex.get(0)).showLabel;
                            clearAllPotentials();
                        }
                        break;
                    case scaleEverything:
                        double[] newVector = {location[0], location[1]};
                        LinkedList<Construct> tempList = new LinkedList();
                        Point temp0 = new Point(tempList, lastVector, 0, getWidth(), getHeight());
                        Point temp1 = new Point(tempList, newVector, 1, getWidth(), getHeight());
                        double[] u = {0, 0, 0}, v = {0, 0, 0}, w = {0, 0, 0};
                        for (int i = 0; i < MainActivity.linkedList.size(); i++) {
                            if (MainActivity.linkedList.get(i).type > 0 && MainActivity.linkedList.get(i).type <= PTonCIRCLE) {
                                temp0.getXYZ(u);
                                MainActivity.linkedList.get(i).getXYZ(v);
                                MathEqns.hypTranslate(u, v, w); // w=v-u
                                temp1.getXYZ(u);
                                u[0] = -u[0];
                                u[1] = -u[1];
                                MathEqns.hypTranslate(u, w, v); // v=w+u
                                MainActivity.linkedList.get(i).setXYZ(v);
                            }
                        }
                        lastVector[0] = 0;
                        lastVector[1] = 0;
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        for (int i=0;i<MainActivity.linkedList.size();i++) {
            //if (MainActivity.linkedList.get(i).type<DISTANCE) {
                double[] vector = {0, 0};
                MainActivity.linkedList.get(i).getScreen(vector);
                MainActivity.linkedList.get(i).update(vector);
            //}
        }
        invalidate();
        return true;
    }

    public void arrangeClickedObjectsByIndex() {
        if (clickedIndex.get(0)>clickedIndex.get(1)) {
            clickedList.add(2,clickedList.get(0));
            clickedList.removeFirst();
            clickedIndex.add(2,clickedIndex.get(0));
            clickedIndex.removeFirst();
        }
        if (clickedList.size()>=3) {
            if (clickedIndex.get(0)>clickedIndex.get(2)) {
                clickedList.add(2,clickedList.get(0));
                clickedList.add(1,clickedList.get(3));
                clickedList.removeFirst();
                clickedList.removeLast();
                clickedIndex.add(2,clickedIndex.get(0));
                clickedIndex.add(1,clickedIndex.get(3));
                clickedIndex.removeFirst();
                clickedIndex.removeLast();
            }
            if (clickedIndex.get(1)>clickedIndex.get(2)) {
                clickedList.add(clickedList.get(1));
                clickedList.remove(1);
                clickedIndex.add(clickedIndex.get(1));
                clickedIndex.remove(1);
            }
        }
    }
    public void getRidOfActivesThatAreTooFar(double[] location) {
        if (activeConstruct) {
            if (potentialClick.distance(location)>touchSense) {
                clearLastPotential();
            }
        }
    }
    public void getRidOfDuplicates() {
        if (clickedList.size()>1) {
            if (clickedIndex.get(0)==clickedIndex.get(1)) {
                clearLastPotential();
            }
            if (clickedList.size()==3) {
                if (clickedIndex.get(0)==clickedIndex.get(2) || clickedIndex.get(1)==clickedIndex.get(2)) {
                    clearLastPotential();
                }
            }
        }
    }
    public void getMeasure(double[] location) {
        for(int i=0;i<MainActivity.linkedList.size();i++) {
            if (MainActivity.linkedList.get(i).distance(location)<touchSense && !clickedIndex.contains(i) && !activeConstruct && MainActivity.linkedList.get(i).isShown && MainActivity.linkedList.get(i).isReal) {
                if (MainActivity.linkedList.get(i).type>=DISTANCE) {
                    setActiveConstruct(i);
                }
            }
        }
    }
    public void getPoint(double[] location) {
        for(int i=0;i<MainActivity.linkedList.size();i++) {
            if (MainActivity.linkedList.get(i).distance(location)<touchSense && !clickedIndex.contains(i) && !activeConstruct && MainActivity.linkedList.get(i).isShown && MainActivity.linkedList.get(i).isReal) {
                if (MainActivity.linkedList.get(i).type>0 && MainActivity.linkedList.get(i).type<DISTANCE) {
                    setActiveConstruct(i);
                }
            }
        }
    }
    public void getPointOrMeasure(double[] location) {
        for(int i=0;i<MainActivity.linkedList.size();i++) {
            if (MainActivity.linkedList.get(i).distance(location)<touchSense && !clickedIndex.contains(i) && !activeConstruct && MainActivity.linkedList.get(i).isShown && MainActivity.linkedList.get(i).isReal) {
                if ((MainActivity.linkedList.get(i).type>0 && MainActivity.linkedList.get(i).type<MIDPOINT) || MainActivity.linkedList.get(i).type>=DISTANCE) {
                    setActiveConstruct(i);
                }
            }
        }
    }
    public void getPointOrLineOrCircle(double[] location) {
        getPoint(location);
        if (!activeConstruct) {
            getLineOrCircle(location);
        }
    }
    public void getPointOrLineOrCircleAllowingRepeatConstructions(double[] location) {
        getPoint(location);
        if (!activeConstruct) {
            for (int i=0;i<MainActivity.linkedList.size();i++) {
                if (MainActivity.linkedList.get(i).distance(location)<touchSense && !activeConstruct && MainActivity.linkedList.get(i).isShown && MainActivity.linkedList.get(i).isReal) {
                    if (MainActivity.linkedList.get(i).type <= 0) {
                        setActiveConstruct(i);
                    }
                }
            }
        }
    }
    public void getLineOrCircle(double[] location) {
        for(int i=0;i<MainActivity.linkedList.size();i++) {
            if (MainActivity.linkedList.get(i).distance(location) < touchSense && !clickedIndex.contains(i) && !activeConstruct && MainActivity.linkedList.get(i).isShown && MainActivity.linkedList.get(i).isReal) {
                if (MainActivity.linkedList.get(i).type <= 0) {
                    setActiveConstruct(i);
                }
            }
        }
    }
    public void getLine(double[] location) {
        for(int i=0;i<MainActivity.linkedList.size();i++) {
            if (MainActivity.linkedList.get(i).distance(location) < touchSense && !clickedIndex.contains(i) && !activeConstruct && MainActivity.linkedList.get(i).isShown && MainActivity.linkedList.get(i).isReal) {
                if (MainActivity.linkedList.get(i).type < 0) {
                    setActiveConstruct(i);
                }
            }
        }
    }
    public void getCircle(double[] location) {
        for(int i=0;i<MainActivity.linkedList.size();i++) {
            if (MainActivity.linkedList.get(i).distance(location) < touchSense && !clickedIndex.contains(i) && !activeConstruct && MainActivity.linkedList.get(i).isShown && MainActivity.linkedList.get(i).isReal) {
                if (MainActivity.linkedList.get(i).type == 0) {
                    setActiveConstruct(i);
                }
            }
        }
    }
    public void setActiveConstruct(int i) {
        activeConstruct=true;
        potentialClick=MainActivity.linkedList.get(i);
        clickedList.add(MainActivity.linkedList.get(i));
        clickedIndex.add(i);
    }
    public void clearActives() {
        if (activeConstruct) {
            potentialClick=null;
            activeConstruct=false;
        }
    }
    public void clearLastPotential() {
        clearActives();
        clickedList.removeLast();
        clickedIndex.removeLast();
    }
    public void clearAllPotentials() {
        clearActives();
        clickedIndex.clear();
        clickedList.clear();
    }
    public static void decrementNumberOfMeasures() {
        numberOfMeasures-=1;
    }
    public static void resetNumberOfMeasures() { numberOfMeasures=1; }
}

