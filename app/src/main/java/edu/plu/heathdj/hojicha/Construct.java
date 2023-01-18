package edu.plu.heathdj.hojicha;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.LinkedList;

class CGPoint {
    double x, y;
    public CGPoint(double xx, double yy) {
        x=xx;
        y=yy;
    }
}

abstract class Construct {
    String[] character = {"A","B","C","D","E","F","G","H","J","K","L","M","N","P","Q","R","S","T","U","V","W","X","Y","Z"};
    public static final int POINT = 1, PTonLINE = 2, PTonCIRCLE = 3, MIDPOINT = 4;
    public static final int LINEintLINE = 5, FOLDedPT = 6;
    public static final int CIRCintCIRC0 = 8,CIRCintCIRC1 = 9, LINEintCIRC0 = 10, LINEintCIRC1 = 11;
    public static final int HIDDENthing = 17;
    public static final int DISTANCE = 20, ANGLE = 21, TriAREA=22, CIRCUMFERENCE=23, CircAREA=24;
    public static final int SUM = 25, DIFFERENCE = 26, PRODUCT = 27, RATIO = 28;
    public static final int CIRCLE = 0;
    public static final int LINE = -1, PERP = -2, PARALLEL0 = -3, PARALLEL1 = -4;
    public static final int BISECTOR = -5, SEGMENT = -11, RAY = -12;
    public static float width=0, height=0;
    protected double x=0,y=0,z=0;
    protected LinkedList<Construct> parent = new LinkedList<Construct>();
    public int type;
    public int index=-1;
    public double value = -1;
    protected boolean isShown=true, showLabel=false, isReal=true;
    double epsilon = 0.000001;
    String textString="";
    float strokeWidth=5;
    public double[] pt0={1,1,1.732},pt1={1,-1,1.732},oldPT0={1,1,1.732},oldPT1={1,-1,1.732};

    public Construct(LinkedList<Construct> ancestor, double[] vector, int ID, float xx, float yy){
        for (int i=0;i<ancestor.size();i++) {
            parent.addLast(ancestor.get(i));
        }
        width=xx;
        height=yy;
        index=ID;
        textString = character[index % 24] + Integer.toString(index / 24);
        update(vector);
    }
    public boolean getValid() { return isReal; }
    public Construct get(int i){if(parent.size()>i) return parent.get(i); else return null;}
    public abstract double distance(double[] vector);
    public void getXYZ(double[] vector) {vector[0]=x; vector[1]=y; vector[2]=z;}
    public void setXYZ(double[] vector) {x=vector[0]; y=vector[1]; z=vector[2];}
    public abstract void getScreen(double[] vector);
    public void setValid(boolean x) { isReal=x;}
    public abstract void draw(Canvas canvas, Paint paint, boolean isNew);
    public abstract void update(double[] vector);
    //public double chop(double x) { return (double)((int)(100*x+.5))/100.0; }
    public void screenToXYZ(double[] screen, double[] xyz) {
        if (screen[1]<0) screen[1]=0;
        if (screen[1]>height) screen[1]=height;
        float temp = Math.min(width / 2, height / 2);
        double xx = (screen[0] - width / 2) / temp, yy = (screen[1] - height / 2) / temp;
        double denom=0;
        switch (MainActivity.model) {
            case 0: // Weierstrass model
                xyz[0] = xx;
                xyz[1] = yy;
                xyz[2] = Math.sqrt(1.0 + x * x + y * y);
                break;
            case 1: // Poincare model
                temp = (float) Math.max(width, height) / 2;
                xx = (screen[0] - width / 2) / temp;
                yy = (screen[1] - height / 2) / temp;
                while (xx * xx + yy * yy >= .99) {
                    denom = Math.sqrt(xx * xx + yy * yy + .1);
                    xx /= denom;
                    yy /= denom;
                }
                denom = 1 - xx * xx - yy * yy;
                xyz[0] = xx * 2 / denom;
                xyz[1] = yy * 2 / denom;
                xyz[2] = (1 + xx * xx + yy * yy) / denom;
                break;
            case 2: // Klein model
                while (xx * xx + yy * yy >= .99) {
                    denom = Math.sqrt(xx * xx + yy * yy + .1);
                    xx /= denom;
                    yy /= denom;
                }
                denom = Math.sqrt(1 - xx * xx - yy * yy);
                xyz[0] = xx / denom;
                xyz[1] = yy / denom;
                xyz[2] = 1 / denom;
                break;
            default: // half plane model
                yy = (screen[1] - height) / temp;
                if (yy > height) yy = 2 * height - yy;
                double xxx = 2 * xx / (xx * xx + (yy - 1) * (yy - 1));
                double yyy = (1 - xx * xx - yy * yy) / (xx * xx + (yy - 1) * (yy - 1));
                denom = 1 - xxx * xxx - yyy * yyy;
                xyz[0] = xxx * 2 / denom;
                xyz[1] = yyy * 2 / denom;
                xyz[2] = (1 + xxx * xxx + yyy * yyy) / denom;
                break;
        }
    }
    public void xyzToScreen(double[] xyz, double[] screen) {
        float temp = Math.min(width/2,height/2);
        switch(MainActivity.model) {
            case 0: // weierstrass model
                screen[0]=temp*xyz[0]+width/2;
                screen[1]=temp*xyz[1]+height/2;
                break;
            case 1: // poincare model
                temp = (float) Math.max(width,height)/2;
                screen[0]=temp*xyz[0]/(1+xyz[2])+width/2;
                screen[1]=temp*xyz[1]/(1+xyz[2])+height/2;
                break;
            case 2: // klein model
                screen[0]=temp*xyz[0]/xyz[2]+width/2;
                screen[1]=temp*xyz[1]/xyz[2]+height/2;
                break;
            default: // half plane model
                double xx=xyz[0]/(1+xyz[2]);
                double yy=xyz[1]/(1+xyz[2]);
                double xxx=2*xx/(xx*xx+(yy+1)*(yy+1));
                double yyy=(xx*xx+yy*yy-1)/(xx*xx+(yy+1)*(yy+1));
                screen[0]=xxx*temp+width/2;
                screen[1]=height+yyy*temp;
                break;
        }

    }
} // end class

class Point extends Construct {
    public Point(LinkedList<Construct> ancestor, double[] vector, int ID, float xx, float yy) {
        super(ancestor, vector, ID, xx, yy);
        type=POINT;
        update(vector);
    }
    @Override
    public void draw(Canvas canvas, Paint paint, boolean isNew) {
        paint.setStrokeWidth(strokeWidth);
        double[] screenPT={0,0};
        getScreen(screenPT);
        if (isNew) {
            paint.setColor(Color.RED);
        } else {
            paint.setColor(Color.YELLOW);
        }
        if (type <= PTonCIRCLE) {
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
        } else {
            paint.setStyle(Paint.Style.STROKE);
        }
        canvas.drawCircle((float) (screenPT[0]), (float) (screenPT[1]), 3 * strokeWidth, paint);
        if (showLabel) {
            paint.setTextSize(45);
            canvas.drawText(textString, (float) (screenPT[0] + 36), (float) (screenPT[1] + 45), paint);
            // +"("+Double.toString(MathEqns.chop(x,1))+","+Double.toString(MathEqns.chop(y,1))+","+Double.toString(MathEqns.chop(z,1))+")"
        }
    }
    public double distance(double[] vector){
        double[] screenPT={0,0};
        getScreen(screenPT);
        return Math.sqrt(Math.pow(screenPT[0]-vector[0],2)+Math.pow(screenPT[1]-vector[1],2));
    }
    @Override
    public void update(double[] vector) {
        if (type < DISTANCE) {
            double[] xyz = {0,0,0};
            screenToXYZ(vector, xyz);
            x=xyz[0];
            y=xyz[1];
            z=xyz[2];
        }
    }
    @Override
    public void getScreen(double[] vector) {
        double[] xyz={x,y,z};
        xyzToScreen(xyz,vector);
    }
}
class PointOnLine extends Point {
    public PointOnLine(LinkedList<Construct> ancestor, double[] vector, int ID, float xx, float yy) {
        super(ancestor, vector, ID, xx, yy);
        type = PTonLINE;
        update(vector);
    }
    @Override
    public void update(double[] vector) {
        isReal = parent.get(0).isReal;
        if (isReal) {
            LinkedList<Construct> tempList = new LinkedList<>();
            Point temp0 = new Point(tempList, vector, 0, width, height);
            tempList.add(temp0);
            tempList.add(parent.get(0));
            PerpLine temp1 = new PerpLine(tempList, vector, 1, width, height);
            tempList.removeFirst();
            tempList.add(temp1);
            LineIntLine temp2 = new LineIntLine(tempList, vector, 2, width, height);
            x = temp2.x;
            y = temp2.y;
            z = temp2.z;
            if (parent.get(0).type == SEGMENT || parent.get(0).type == RAY) {
                // make sure between endpoints or make it an endpoint
                double[] v0 = {0, 0, 0}, v1 = {0, 0, 0}, v2 = {0, 0, 0}, nm = {0, 0, 0};
                v0[0] = x;
                v0[1] = y;
                v0[2] = z;
                parent.get(0).get(0).getXYZ(v1);
                parent.get(0).get(1).getXYZ(v2);
                MathEqns.hypCrossProduct(v1, v2, nm);
                double a = nm[0], b = nm[1], c = nm[2];
                double s1 = Math.sqrt(a * a + b * b), s2 = Math.sqrt(a * a + b * b - c * c);
                // finding the "natural" parameter endpoints t1 and t2.
                double t0 = Math.log(v0[2] * s2 / s1 + Math.sqrt(v0[2] * v0[2] * s2 * s2 / s1 / s1 - 1)); // point on line
                double t1 = Math.log(v1[2] * s2 / s1 + Math.sqrt(v1[2] * v1[2] * s2 * s2 / s1 / s1 - 1)); // endpoint 1
                double t2 = Math.log(v2[2] * s2 / s1 + Math.sqrt(v2[2] * v2[2] * s2 * s2 / s1 / s1 - 1)); // endpoint 2
                // since the inverse hyperbolic cosine is double-valued, we do
                // the next few steps to make sure we have the correct values
                if ((Math.abs((-a * c * Math.cosh(t0) / s2 + b * Math.sinh(t0)) / s1 - v0[0]) > epsilon && Math.abs(v1[1] - v2[1]) > epsilon)
                        || (Math.abs((-b * c * Math.cosh(t0) / s2 - a * Math.sinh(t0)) / s1 - v0[1]) > epsilon && Math.abs(v1[0] - v2[0]) > epsilon))
                    t0 = Math.log(v0[2] * s2 / s1 - Math.sqrt(v0[2] * v0[2] * s2 * s2 / s1 / s1 - 1));
                if ((Math.abs((-a * c * Math.cosh(t1) / s2 + b * Math.sinh(t1)) / s1 - v1[0]) > epsilon && Math.abs(v1[1] - v2[1]) > epsilon)
                        || (Math.abs((-b * c * Math.cosh(t1) / s2 - a * Math.sinh(t1)) / s1 - v1[1]) > epsilon && Math.abs(v1[0] - v2[0]) > epsilon))
                    t1 = Math.log(v1[2] * s2 / s1 - Math.sqrt(v1[2] * v1[2] * s2 * s2 / s1 / s1 - 1));
                if ((Math.abs((-a * c * Math.cosh(t2) / s2 + b * Math.sinh(t2)) / s1 - v2[0]) > epsilon && Math.abs(v1[1] - v2[1]) > epsilon)
                        || (Math.abs((-b * c * Math.cosh(t2) / s2 - a * Math.sinh(t2)) / s1 - v2[1]) > epsilon && Math.abs(v1[0] - v2[0]) > epsilon))
                    t2 = Math.log(v2[2] * s2 / s1 - Math.sqrt(v2[2] * v2[2] * s2 * s2 / s1 / s1 - 1));
                if (parent.get(0).type == SEGMENT) {
                    if (t1 < t2) {
                        if (t0 < t1) {
                            t0 = t1;
                        } else if (t2 < t0) {
                            t0 = t2;
                        }
                    } else {
                        if (t0 < t2) {
                            t0 = t2;
                        } else if (t1 < t0) {
                            t0 = t1;
                        }
                    }
                } else { // its a ray
                    if (t1 < t2) {
                        if (t0 < t1) {
                            t0 = t1;
                        }
                    } else {
                        if (t0 < t2) {
                            t0 = t2;
                        }
                    }
                }
                x = (-a * c / s2 * Math.cosh(t0) + b * Math.sinh(t0)) / s1;
                y = (-b * c / s2 * Math.cosh(t0) - a * Math.sinh(t0)) / s1;
                z = Math.sqrt(1.0 + x * x + y * y);
            }
        }
    }
}
class Line extends Construct {
    public Line(LinkedList<Construct> ancestor, double[] vector, int ID, float xx, float yy) {
        super(ancestor, vector, ID, xx, yy);
        type = LINE;
        update(vector);
    }
    @Override
    public double distance(double[] vector) {
        double[] v1={0,0,0},v2={0,0,0},n1={0,0,0},n2={0,0,0};
        screenToXYZ(vector, v1);
        LinkedList<Construct> tempList = new LinkedList();
        Construct temp0 = new Point(tempList,vector,0,width,height);
        tempList.add(temp0);
        tempList.add(this);
        Construct temp1 = new PerpLine(tempList,vector,1,width,height);
        tempList.removeFirst();
        tempList.add(temp1);
        Construct temp2 = new LineIntLine(tempList,vector,2,width,height);
        double[] screenPT={0,0};
        temp2.getScreen(screenPT);
        return Math.sqrt(Math.pow(screenPT[0]-vector[0],2)+Math.pow(screenPT[1]-vector[1],2));
    }
    @Override
    public void draw(Canvas canvas, Paint paint, boolean isNew) {
        if (isNew) paint.setColor(Color.RED);
        else paint.setColor(Color.LTGRAY);
        paint.setStrokeWidth(strokeWidth);
        double[] v1 = {0, 0, 0}, v2 = {0, 0, 0}, nm = {0, 0, 0};
        // first point, second point, normal
        parent.get(0).getXYZ(v1);
        parent.get(1).getXYZ(v2);
        float temp = Math.min(width/2,height/2);
        getXYZ(nm);
        double	a=nm[0],	b=nm[1],	c=nm[2];
        double s1=Math.sqrt(a*a+b*b),	s2=Math.sqrt(a*a+b*b-c*c);
        for (int i=-343; i<343; i++) {
            v1[0]=(-a*c/s2*Math.cosh(Math.tan(i/219.))+b*Math.sinh(Math.tan(i/219.)))/s1;
            v1[1]=(-b*c/s2*Math.cosh(Math.tan(i/219.))-a*Math.sinh(Math.tan(i/219.)))/s1;
            v1[2]=s1/s2*Math.cosh(Math.tan(i/219.));
            v2[0]=(-a*c/s2*Math.cosh(Math.tan((i+1)/219.))+b*Math.sinh(Math.tan((i+1)/219.)))/s1;
            v2[1]=(-b*c/s2*Math.cosh(Math.tan((i+1)/219.))-a*Math.sinh(Math.tan((i+1)/219.)))/s1;
            v2[2]=s1/s2*Math.cosh(Math.tan((i+1)/219.));
            if (i==0 && showLabel) {
                paint.setTextSize(45);
                canvas.drawText(textString, (float) (temp * v1[0] + width/2 + 36), (float) (temp * v1[1] + height/2 + 45), paint);
            }
            double[] scrn1={0,0},scrn2={0,0};
            xyzToScreen(v1,scrn1);
            xyzToScreen(v2,scrn2);
            canvas.drawLine((float) (scrn1[0]), (float) (scrn1[1]), (float) (scrn2[0]), (float) (scrn2[1]), paint);
        }

    }
    @Override
    public void update(double[] vector) {
        isReal = parent.get(0).isReal && parent.get(1).isReal;
        if (isReal) {
            double[] v1 = {0, 0, 0}, v2 = {0, 0, 0}, nm = {0, 0, 0};
            parent.get(0).getXYZ(v1);
            parent.get(1).getXYZ(v2);
            MathEqns.crossProduct(v1, v2, nm);
            MathEqns.hypNormalize(nm);
            x = nm[0];
            y = nm[1];
            z = nm[2];
            // so XYZ for line is the normal vector on skirt.
        }
    }
    @Override
    public void getScreen(double[] vector) {
        parent.get(0).getScreen(vector);
    }
}

class LineIntLine extends Point {
    public LineIntLine(LinkedList<Construct> ancestor, double[] vector, int ID, float xx, float yy) {
        super(ancestor, vector, ID, xx, yy);
        type = LINEintLINE;
        update(vector);
    }
    @Override
    public void update(double[] vector) {
        double[] v1={0,0,0},v2={0,0,0},nm={0,0,0};
        if (parent.get(0).getValid() && parent.get(1).getValid()) {
            parent.get(0).getXYZ(v1); // normal 1
            parent.get(1).getXYZ(v2); // normal 2
            setValid(MathEqns.hypLineIntLine(v1,v2,nm));
            if (nm[2]<0) for(int i=0;i<3;i++) nm[i]*=-1;
            x=nm[0];y=nm[1];z=nm[2];
            for(int i=0;i<2;i++) {
                if (parent.get(i).type==SEGMENT) {
                    parent.get(i).get(0).getXYZ(v1);
                    parent.get(i).get(1).getXYZ(v2);
                    if (MathEqns.hypDistance(v1,nm)+MathEqns.hypDistance(nm,v2)-MathEqns.hypDistance(v1,v2)>epsilon) {
                        setValid(false);
                    }
                } else if (parent.get(i).type==RAY) {
                    parent.get(i).get(0).getXYZ(v1);
                    parent.get(i).get(1).getXYZ(v2);
                    if (MathEqns.hypDistance(nm,v1)+MathEqns.hypDistance(v1,v2)-MathEqns.hypDistance(nm,v2)<epsilon) {
                        setValid(false);
                    }
                }
            }
        }
        else isReal=false;
    }
}
class PerpLine extends Line {
    public PerpLine(LinkedList<Construct> ancestor, double[] vector, int ID, float xx, float yy) {
        super(ancestor, vector, ID, xx, yy);
        type = PERP;
        update(vector);
    }
    @Override
    public void update(double[] vector) {
        isReal = parent.get(0).isReal && parent.get(1).isReal;
        if (isReal) {
            double[] ln = {0, 0, 0}, pt = {0, 0, 0}, v2 = {0, 0, 0};
            parent.get(0).getXYZ(pt);
            parent.get(1).getXYZ(ln);
            MathEqns.hypPerp(ln, pt, v2);
            x = v2[0];
            y = v2[1];
            z = v2[2];
        }
    }
}
class Parallel0 extends Line {
    public Parallel0(LinkedList<Construct> ancestor, double[] vector, int ID, float xx, float yy) {
        super(ancestor, vector, ID, xx, yy);
        type = PARALLEL0;
        update(vector);
    }
    @Override
    public void update(double[] vector) {
        isReal = parent.get(0).isReal && parent.get(1).isReal;
        if (isReal) {
            double[] pt = {0, 0, 0}, ln = {0, 0, 0}, nm = {0, 0, 0};
            parent.get(0).getXYZ(pt);
            parent.get(1).getXYZ(ln);
            setValid(MathEqns.hypParallel(ln, pt, nm, false));
            x = nm[0];
            y = nm[1];
            z = nm[2];
        }
    }
}
class Parallel1 extends Line {
    public Parallel1(LinkedList<Construct> ancestor, double[] vector, int ID, float xx, float yy) {
        super(ancestor, vector, ID, xx, yy);
        type = PARALLEL1;
        update(vector);
    }
    @Override
    public void update(double[] vector) {
        isReal = parent.get(0).isReal && parent.get(1).isReal;
        if (isReal) {
            double[] pt = {0, 0, 0}, ln = {0, 0, 0}, nm = {0, 0, 0};
            parent.get(0).getXYZ(pt);
            parent.get(1).getXYZ(ln);
            setValid(MathEqns.hypParallel(ln, pt, nm, true));
            x = nm[0];
            y = nm[1];
            z = nm[2];
        }
    }
}

class Circle extends Construct {
    public Circle(LinkedList<Construct> ancestor, double[] vector, int ID, float xx, float yy) {
        super(ancestor, vector, ID, xx, yy);
        type = CIRCLE;
        update(vector);
    }
    @Override
    public void draw(Canvas canvas, Paint paint, boolean isNew) {
        double[] axis = {0, 0, 0}, pt = {0, 0, 0}, pTranslate = {0, 0, 0},
                v1 = {0, 0, 0}, v2 = {0, 0, 0},
                u1 = {0, 0, 0}, u2 = {0, 0, 0};
        if (isNew) {
            paint.setColor(Color.RED);

        } else {
            paint.setColor(Color.BLUE);
        }
        parent.get(0).getXYZ(axis);
        parent.get(1).getXYZ(pt);
        MathEqns.hypTranslate(axis, pt, pTranslate);
        axis[0] *= -1;
        axis[1] *= -1;
        double r = Math.sqrt(pTranslate[0] * pTranslate[0] + pTranslate[1] * pTranslate[1]);
        int multiplier = MathEqns.round(MathEqns.min(MathEqns.max(r , 1), 256));
        float temp = Math.min(width/2,height/2);
        for (int i = 0; i < 44 * multiplier; i++) {
            v1[0] = r * Math.cos(i / (7. * multiplier));
            v2[0] = r * Math.cos((i + 1) / (7. * multiplier));
            v1[1] = r * Math.sin(i / (7. * multiplier));
            v2[1] = r * Math.sin((i + 1) / (7. * multiplier));
            v1[2] = Math.sqrt(1 + v1[0] * v1[0] + v1[1] * v1[1]);
            v2[2] = Math.sqrt(1 + v1[0] * v1[0] + v1[1] * v1[1]);
            MathEqns.hypTranslate(axis, v1, u1);
            MathEqns.hypTranslate(axis, v2, u2);
            paint.setStrokeWidth(strokeWidth);
            double[] scrn1={0,0},scrn2={0,0};
            xyzToScreen(u1,scrn1);
            xyzToScreen(u2,scrn2);
            canvas.drawLine((float) (scrn1[0]), (float) (scrn1[1]), (float) (scrn2[0]), (float) (scrn2[1]), paint);
            if (i == 6 && showLabel) {
                if (isNew) {
                    paint.setColor(Color.RED);

                } else {
                    paint.setColor(Color.argb(255, 64, 64, 255));
                }
                paint.setTextSize(45);
                canvas.drawText(textString, (float) (scrn1[0] + 36), (float) (scrn1[1] + 45), paint);
                if (isNew) {
                    paint.setColor(Color.RED);

                } else {
                    paint.setColor(Color.BLUE);
                }
            }
        }
    }
    @Override
    public void update(double[] vector) {
        isReal = parent.get(0).isReal && parent.get(1).isReal;
//        if (isReal) {
//            x = parent.get(0).x;
//            y = parent.get(0).y;
//            z = parent.get(0).z;
//        }
    }
    @Override
    public double distance(double[] mouse){
        LinkedList<Construct> tempList = new LinkedList<>();
        tempList.add(this);
        PointOnCircle temp0 = new PointOnCircle(tempList,mouse,0,width,height);
        return temp0.distance(mouse);
    }
    @Override
    public void getScreen(double[] vector) {
        float temp = Math.min(width / 2, height / 2);
        vector[0] = temp * x + width / 2;
        vector[1] = temp * y + height / 2;
    }
}

class PointOnCircle extends Point {
    public PointOnCircle(LinkedList<Construct> ancestor, double[] vector, int ID, float xx, float yy) {
        super(ancestor, vector, ID, xx, yy);
        type = PTonCIRCLE;
        update(vector);
    }
    @Override
    public void update(double[] vector) {
        isReal = parent.get(0).isReal;
        if (isReal) {
            double[] axis = {0, 0, 0}, point = {0, 0, 0}, touch = {0, 0, 0};
            screenToXYZ(vector,touch);
            parent.get(0).get(0).getXYZ(axis);
            parent.get(0).get(1).getXYZ(point);
            double[] vec1 = {0, 0, 0}, vec2 = {0, 0, 0};
            MathEqns.hypTranslate(axis, point, vec1);
            MathEqns.hypTranslate(axis, touch, vec2);
            double multiplier = Math.sqrt((Math.pow(vec1[0], 2) + Math.pow(vec1[1], 2)) / (Math.pow(vec2[0], 2) + Math.pow(vec2[1], 2)));
            vec2[0] *= multiplier;
            vec2[1] *= multiplier;
            vec2[2] = Math.sqrt(1.0 + vec2[0] * vec2[0] + vec2[1] * vec2[1]);
            for (int i = 0; i < 2; i++) axis[i] *= -1;
            MathEqns.hypTranslate(axis, vec2, touch);
            x = touch[0];
            y = touch[1];
            z = touch[2];
        }
    }
}

class LineIntCirc0 extends Point {
    public boolean pt1IsReal=true;

    public LineIntCirc0(LinkedList<Construct> ancestor, double[] vector, int ID, float xx, float yy) {
        super(ancestor, vector, ID, xx, yy);
        type = LINEintCIRC0;
        update(vector);
    }
    public void draw(Canvas canvas, Paint paint, boolean isNew) {super.draw(canvas, paint, isNew);}
    @Override
    public void update(double[] vector) {
        isReal = parent.get(0).isReal && parent.get(1).isReal;
        if (isReal) {
            double[] cc = {0, 0, 0}, cp = {0, 0, 0}, ln = {0, 0, 0};
            parent.get(0).getXYZ(ln); // parent 0 is a line, it's normal vector is ln
            parent.get(1).parent.get(0).getXYZ(cc);// parent 1 is a circle, its parent 0 is circCntr
            parent.get(1).parent.get(1).getXYZ(cp);// parent 1 is a circle, its parent 1 is circPt
            setValid(calculateHypCL(cc, cp, ln, pt0, true));
            calculateHypCL(cc, cp, ln, pt1, false);
            // now we need to calculate distance between pt0 & oldPT0 + pt1 & oldPT1
            // vs pt0 & oldPT1 + pt1 & oldPT0, to see if the points need to be swapped.
            double dist0 = MathEqns.hypDistance(pt0,oldPT0)+MathEqns.hypDistance(pt1,oldPT1);
            double dist1 = MathEqns.hypDistance(pt1,oldPT0)+MathEqns.hypDistance(pt0,oldPT1);
            if (dist0 > dist1) {
                for (int i = 0; i < 3; i++) {
                    cc[i] = pt0[i];
                    pt0[i] = pt1[i];
                    pt1[i] = cc[i];
                }
            }
            for (int i = 0; i < 3; i++) {
                oldPT0[i] = pt0[i];
                oldPT1[i] = pt1[i];
            }
            x = pt0[0];
            y = pt0[1];
            z = pt0[2];

            if (parent.get(0).type==SEGMENT) {
                double[] v1={0,0,0},v2={0,0,0};
                parent.get(0).get(0).getXYZ(v1);
                parent.get(0).get(1).getXYZ(v2);
                if (MathEqns.hypDistance(v1, pt0) + MathEqns.hypDistance(pt0, v2) - MathEqns.hypDistance(v1, v2) > epsilon) {
                    setValid(false);
                }
                if (MathEqns.hypDistance(v1, pt1) + MathEqns.hypDistance(pt1, v2) - MathEqns.hypDistance(v1, v2) > epsilon) {
                    pt1IsReal=false;
                } else pt1IsReal=true;
            } else if (parent.get(0).type==RAY) {
                double[] v1={0,0,0},v2={0,0,0};
                parent.get(0).get(0).getXYZ(v1);
                parent.get(0).get(1).getXYZ(v2);
                if (MathEqns.hypDistance(pt0, v1) + MathEqns.hypDistance(v1, v2) - MathEqns.hypDistance(pt0, v2) < epsilon) {
                    setValid(false);
                }
                if (MathEqns.hypDistance(pt1, v1) + MathEqns.hypDistance(v1, v2) - MathEqns.hypDistance(pt1, v2) < epsilon) {
                    pt1IsReal=false;
                } else pt1IsReal=true;
            }

        }
    }
    public boolean calculateHypCL(double[] u, double[] v, double[] w, double[] x, boolean bit) {
        double discriminant=(u[1]*w[0]-u[0]*w[1])*(u[1]*w[0]-u[0]*w[1])*(u[2]*u[2]*((-1+v[2]*v[2])*w[0]*w[0]+(-1+v[2]*v[2])*w[1]*w[1]-v[2]*v[2]*w[2]*w[2])+u[1]*u[1]*((1+v[1]*v[1])*w[0]*w[0]-w[2]*w[2]+v[1]*v[1]*(w[1]*w[1]-w[2]*w[2]))+u[0]*u[0]*(w[1]*w[1]-w[2]*w[2]+v[0]*v[0]*(w[0]*w[0]+w[1]*w[1]-w[2]*w[2]))-2*u[0]*u[2]*(w[0]*w[2]+v[0]*v[2]*(w[0]*w[0]+w[1]*w[1]-w[2]*w[2]))+2*u[1]*(u[0]*(-(w[0]*w[1])+v[0]*v[1]*(w[0]*w[0]+w[1]*w[1]-w[2]*w[2]))-u[2]*(w[1]*w[2]+v[1]*v[2]*(w[0]*w[0]+w[1]*w[1]-w[2]*w[2]))));
        if (discriminant>=0) {//circ with ctr u & pt v intersects line with normal w (Hyp only)
            double sr=Math.sqrt(discriminant); if (bit) sr*=-1;
            x[0]=-((u[0]*u[1]*u[1]*v[0]*w[0]*w[0]*w[1]+u[1]*u[1]*u[1]*v[1]*w[0]*w[0]*w[1]-u[1]*u[1]*u[2]*v[2]*w[0]*w[0]*w[1]-2*u[0]*u[0]*u[1]*v[0]*w[0]*w[1]*w[1]-2*u[0]*u[1]*u[1]*v[1]*w[0]*w[1]*w[1]+2*u[0]*u[1]*u[2]*v[2]*w[0]*w[1]*w[1]+u[0]*u[0]*u[0]*v[0]*w[1]*w[1]*w[1]+u[0]*u[0]*u[1]*v[1]*w[1]*w[1]*w[1]-u[0]*u[0]*u[2]*v[2]*w[1]*w[1]*w[1]+u[0]*u[1]*u[2]*v[0]*w[0]*w[0]*w[2]+u[1]*u[1]*u[2]*v[1]*w[0]*w[0]*w[2]-u[1]*u[2]*u[2]*v[2]*w[0]*w[0]*w[2]-u[0]*u[0]*u[2]*v[0]*w[0]*w[1]*w[2]-u[0]*u[1]*u[2]*v[1]*w[0]*w[1]*w[2]+u[0]*u[2]*u[2]*v[2]*w[0]*w[1]*w[2]+u[0]*u[0]*u[1]*v[0]*w[0]*w[2]*w[2]+u[0]*u[1]*u[1]*v[1]*w[0]*w[2]*w[2]-u[0]*u[1]*u[2]*v[2]*w[0]*w[2]*w[2]-u[0]*u[0]*u[0]*v[0]*w[1]*w[2]*w[2]-u[0]*u[0]*u[1]*v[1]*w[1]*w[2]*w[2]+u[0]*u[0]*u[2]*v[2]*w[1]*w[2]*w[2]+u[2]*w[1]*sr+u[1]*w[2]*sr)/((u[1]*w[0]-u[0]*w[1])*(-(u[2]*u[2]*(w[0]*w[0]+w[1]*w[1]))-2*u[0]*u[2]*w[0]*w[2]-2*u[1]*w[1]*(u[0]*w[0]+u[2]*w[2])+u[1]*u[1]*(w[0]*w[0]-w[2]*w[2])+u[0]*u[0]*(w[1]*w[1]-w[2]*w[2]))));
            x[1]=(u[0]*u[1]*u[1]*v[0]*w[0]*w[0]*w[0]+u[1]*u[1]*u[1]*v[1]*w[0]*w[0]*w[0]-u[1]*u[1]*u[2]*v[2]*w[0]*w[0]*w[0]-2*u[0]*u[0]*u[1]*v[0]*w[0]*w[0]*w[1]-2*u[0]*u[1]*u[1]*v[1]*w[0]*w[0]*w[1]+2*u[0]*u[1]*u[2]*v[2]*w[0]*w[0]*w[1]+u[0]*u[0]*u[0]*v[0]*w[0]*w[1]*w[1]+u[0]*u[0]*u[1]*v[1]*w[0]*w[1]*w[1]-u[0]*u[0]*u[2]*v[2]*w[0]*w[1]*w[1]-u[0]*u[1]*u[2]*v[0]*w[0]*w[1]*w[2]-u[1]*u[1]*u[2]*v[1]*w[0]*w[1]*w[2]+u[1]*u[2]*u[2]*v[2]*w[0]*w[1]*w[2]+u[0]*u[0]*u[2]*v[0]*w[1]*w[1]*w[2]+u[0]*u[1]*u[2]*v[1]*w[1]*w[1]*w[2]-u[0]*u[2]*u[2]*v[2]*w[1]*w[1]*w[2]-u[0]*u[1]*u[1]*v[0]*w[0]*w[2]*w[2]-u[1]*u[1]*u[1]*v[1]*w[0]*w[2]*w[2]+u[1]*u[1]*u[2]*v[2]*w[0]*w[2]*w[2]+u[0]*u[0]*u[1]*v[0]*w[1]*w[2]*w[2]+u[0]*u[1]*u[1]*v[1]*w[1]*w[2]*w[2]-u[0]*u[1]*u[2]*v[2]*w[1]*w[2]*w[2]+u[2]*w[0]*sr+u[0]*w[2]*sr)/((u[1]*w[0]-u[0]*w[1])*(-(u[2]*u[2]*(w[0]*w[0]+w[1]*w[1]))-2*u[0]*u[2]*w[0]*w[2]-2*u[1]*w[1]*(u[0]*w[0]+u[2]*w[2])+u[1]*u[1]*(w[0]*w[0]-w[2]*w[2])+u[0]*u[0]*(w[1]*w[1]-w[2]*w[2])));
            x[2]=(u[0]*u[2]*v[0]*w[0]*w[0]+u[1]*u[2]*v[1]*w[0]*w[0]-u[2]*u[2]*v[2]*w[0]*w[0]+u[0]*u[2]*v[0]*w[1]*w[1]+u[1]*u[2]*v[1]*w[1]*w[1]-u[2]*u[2]*v[2]*w[1]*w[1]+u[0]*u[0]*v[0]*w[0]*w[2]+u[0]*u[1]*v[1]*w[0]*w[2]-u[0]*u[2]*v[2]*w[0]*w[2]+u[0]*u[1]*v[0]*w[1]*w[2]+u[1]*u[1]*v[1]*w[1]*w[2]-u[1]*u[2]*v[2]*w[1]*w[2]+sr)/(-(u[2]*u[2]*(w[0]*w[0]+w[1]*w[1]))-2*u[0]*u[2]*w[0]*w[2]-2*u[1]*w[1]*(u[0]*w[0]+u[2]*w[2])+u[1]*u[1]*(w[0]*w[0]-w[2]*w[2])+u[0]*u[0]*(w[1]*w[1]-w[2]*w[2]));
            return true;
        }
        else return false;
    }
}
class LineIntCirc1 extends Point {  // ancestor: LineIntCirc0.
    public LineIntCirc1(LinkedList<Construct> ancestor, double[] vector, int ID, float xx, float yy) {
        super(ancestor, vector, ID, xx, yy);
        type = LINEintCIRC1;
        update(vector);
    }
    public void draw(Canvas canvas, Paint paint, boolean isNew) {super.draw(canvas, paint, isNew);}
    @Override
    public void update(double[] vector) {
        LineIntCirc0 mommy = (LineIntCirc0) parent.get(0);
        x = mommy.pt1[0];
        y = mommy.pt1[1];
        z = mommy.pt1[2];
        isReal = mommy.pt1IsReal;
    }
}
class CircIntCirc0 extends Point {
    public CircIntCirc0(LinkedList<Construct> ancestor, double[] vector, int ID, float xx, float yy) {
        super(ancestor, vector, ID, xx, yy);
        type = CIRCintCIRC0;
        update(vector);
    }
    public void draw(Canvas canvas, Paint paint, boolean isNew) {super.draw(canvas, paint, isNew);}
    @Override
    public void update(double[] vector) {
        isReal = parent.get(0).isReal && parent.get(1).isReal;
        if (isReal) {
            double[] t = {0, 0, 0}, u = {0, 0, 0}, v = {0, 0, 0}, w = {0, 0, 0};
            parent.get(0).parent.get(0).getXYZ(t); // parent 0 is a circle, its parent 0 is circCntr
            parent.get(0).parent.get(1).getXYZ(u); // parent 0 is a circle, its parent 1 is circPt
            parent.get(1).parent.get(0).getXYZ(v); // parent 1 is a circle, its parent 0 is circCntr
            parent.get(1).parent.get(1).getXYZ(w); // parent 1 is a circle, its parent 1 is circPt
            setValid(calculateHypCC(t, u, v, w, pt0, true));
            calculateHypCC(t, u, v, w, pt1, false);
            // now we need to calculate distance between pt0 & oldPT0 + pt1 & oldPT1
            // vs pt0 & oldPT1 + pt1 & oldPT0, to see if the points need to be swapped.
            double dist0 = MathEqns.hypDistance(pt0,oldPT0)+MathEqns.hypDistance(pt1,oldPT1);
            double dist1 = MathEqns.hypDistance(pt0,oldPT1)+MathEqns.hypDistance(pt1,oldPT0);
            if (dist0 > dist1) {
                for (int i = 0; i < 3; i++) {
                    t[i] = pt0[i];
                    pt0[i] = pt1[i];
                    pt1[i] = t[i];
                }
            }
            for (int i = 0; i < 3; i++) {
                oldPT0[i] = pt0[i];
                oldPT1[i] = pt1[i];
            }
            x = pt0[0];
            y = pt0[1];
            z = pt0[2];
        }
    }
    public boolean calculateHypCC(double[] t, double[] u, double[] v, double[] w, double[] x, boolean bit) {
        double discriminant=(t[1]*v[0]-t[0]*v[1])*(t[1]*v[0]-t[0]*v[1])*(t[2]*t[2]*(v[0]*v[0]*(-1+u[2]*u[2]-w[0]*w[0])+v[1]*v[1]*(-1+u[2]*u[2]-w[1]*w[1])-v[2]*v[2]*(u[2]-w[2])*(u[2]-w[2])+2*v[1]*v[2]*w[1]*(-u[2]+w[2])-2*v[0]*w[0]*(u[2]*v[2]+v[1]*w[1]-v[2]*w[2]))+t[0]*t[0]*(-v[2]*v[2]+u[0]*u[0]*(v[0]*v[0]+v[1]*v[1]-v[2]*v[2])+v[0]*v[0]*w[0]*w[0]+v[1]*v[1]*(1+w[1]*w[1])-2*v[0]*v[2]*w[0]*w[2]+v[2]*v[2]*w[2]*w[2]+2*v[1]*w[1]*(v[0]*w[0]-v[2]*w[2])-2*u[0]*v[0]*(v[0]*w[0]+v[1]*w[1]-v[2]*w[2]))+t[1]*t[1]*(-v[2]*v[2]+u[1]*u[1]*(v[1]*v[1]-v[2]*v[2])+v[0]*v[0]*(1+u[1]*u[1]+w[0]*w[0])+v[1]*v[1]*w[1]*w[1]-2*v[1]*v[2]*w[1]*w[2]+v[2]*v[2]*w[2]*w[2]+2*u[1]*v[1]*(-(v[1]*w[1])+v[2]*w[2])-2*v[0]*w[0]*(u[1]*v[1]-v[1]*w[1]+v[2]*w[2]))-2*t[0]*t[2]*(-(v[0]*(v[2]+u[2]*v[0]*w[0]+u[2]*v[1]*w[1]-u[2]*v[2]*w[2]))+u[0]*(u[2]*(v[0]*v[0]+v[1]*v[1]-v[2]*v[2])+v[2]*(-(v[0]*w[0])-v[1]*w[1]+v[2]*w[2])))+2*t[1]*(t[0]*(-(v[0]*(v[1]+u[1]*v[0]*w[0]+u[1]*v[1]*w[1]-u[1]*v[2]*w[2]))+u[0]*(u[1]*(v[0]*v[0]+v[1]*v[1]-v[2]*v[2])-v[1]*(v[0]*w[0]+v[1]*w[1]-v[2]*w[2])))+t[2]*(v[1]*(v[2]+u[2]*v[0]*w[0]+u[2]*v[1]*w[1]-u[2]*v[2]*w[2])+u[1]*(-(u[2]*(v[0]*v[0]+v[1]*v[1]-v[2]*v[2]))+v[2]*(v[0]*w[0]+v[1]*w[1]-v[2]*w[2])))));
        if (discriminant>=0) { //circ with ctr t & pt u intersects circ with ctr v & pt w (Hyp only)
            double sr=Math.sqrt(discriminant); if (bit) sr*=-1;
            x[0]=(-(t[0]*t[1]*t[1]*u[0]*v[0]*v[0]*v[1])-t[1]*t[1]*t[1]*u[1]*v[0]*v[0]*v[1]+t[1]*t[1]*t[2]*u[2]*v[0]*v[0]*v[1]+2*t[0]*t[0]*t[1]*u[0]*v[0]*v[1]*v[1]+2*t[0]*t[1]*t[1]*u[1]*v[0]*v[1]*v[1]-2*t[0]*t[1]*t[2]*u[2]*v[0]*v[1]*v[1]-t[0]*t[0]*t[0]*u[0]*v[1]*v[1]*v[1]-t[0]*t[0]*t[1]*u[1]*v[1]*v[1]*v[1]+t[0]*t[0]*t[2]*u[2]*v[1]*v[1]*v[1]+t[0]*t[1]*t[2]*u[0]*v[0]*v[0]*v[2]+t[1]*t[1]*t[2]*u[1]*v[0]*v[0]*v[2]-t[1]*t[2]*t[2]*u[2]*v[0]*v[0]*v[2]-t[0]*t[0]*t[2]*u[0]*v[0]*v[1]*v[2]-t[0]*t[1]*t[2]*u[1]*v[0]*v[1]*v[2]+t[0]*t[2]*t[2]*u[2]*v[0]*v[1]*v[2]-t[0]*t[0]*t[1]*u[0]*v[0]*v[2]*v[2]-t[0]*t[1]*t[1]*u[1]*v[0]*v[2]*v[2]+t[0]*t[1]*t[2]*u[2]*v[0]*v[2]*v[2]+t[0]*t[0]*t[0]*u[0]*v[1]*v[2]*v[2]+t[0]*t[0]*t[1]*u[1]*v[1]*v[2]*v[2]-t[0]*t[0]*t[2]*u[2]*v[1]*v[2]*v[2]+t[1]*t[1]*t[1]*v[0]*v[0]*v[0]*w[0]-t[1]*t[2]*t[2]*v[0]*v[0]*v[0]*w[0]-2*t[0]*t[1]*t[1]*v[0]*v[0]*v[1]*w[0]+t[0]*t[2]*t[2]*v[0]*v[0]*v[1]*w[0]+t[0]*t[0]*t[1]*v[0]*v[1]*v[1]*w[0]+t[0]*t[1]*t[2]*v[0]*v[0]*v[2]*w[0]-t[0]*t[0]*t[2]*v[0]*v[1]*v[2]*w[0]+t[1]*t[1]*t[1]*v[0]*v[0]*v[1]*w[1]-t[1]*t[2]*t[2]*v[0]*v[0]*v[1]*w[1]-2*t[0]*t[1]*t[1]*v[0]*v[1]*v[1]*w[1]+t[0]*t[2]*t[2]*v[0]*v[1]*v[1]*w[1]+t[0]*t[0]*t[1]*v[1]*v[1]*v[1]*w[1]+t[0]*t[1]*t[2]*v[0]*v[1]*v[2]*w[1]-t[0]*t[0]*t[2]*v[1]*v[1]*v[2]*w[1]-t[1]*t[1]*t[1]*v[0]*v[0]*v[2]*w[2]+t[1]*t[2]*t[2]*v[0]*v[0]*v[2]*w[2]+2*t[0]*t[1]*t[1]*v[0]*v[1]*v[2]*w[2]-t[0]*t[2]*t[2]*v[0]*v[1]*v[2]*w[2]-t[0]*t[0]*t[1]*v[1]*v[1]*v[2]*w[2]-t[0]*t[1]*t[2]*v[0]*v[2]*v[2]*w[2]+t[0]*t[0]*t[2]*v[1]*v[2]*v[2]*w[2]-t[2]*v[1]*sr+t[1]*v[2]*sr)/((t[1]*v[0]-t[0]*v[1])*(-(t[2]*t[2]*(v[0]*v[0]+v[1]*v[1]))+2*t[0]*t[2]*v[0]*v[2]+2*t[1]*v[1]*(-(t[0]*v[0])+t[2]*v[2])+t[1]*t[1]*(v[0]*v[0]-v[2]*v[2])+t[0]*t[0]*(v[1]*v[1]-v[2]*v[2])));
            x[1]=(t[0]*t[1]*t[1]*u[0]*v[0]*v[0]*v[0]+t[1]*t[1]*t[1]*u[1]*v[0]*v[0]*v[0]-t[1]*t[1]*t[2]*u[2]*v[0]*v[0]*v[0]-2*t[0]*t[0]*t[1]*u[0]*v[0]*v[0]*v[1]-2*t[0]*t[1]*t[1]*u[1]*v[0]*v[0]*v[1]+2*t[0]*t[1]*t[2]*u[2]*v[0]*v[0]*v[1]+t[0]*t[0]*t[0]*u[0]*v[0]*v[1]*v[1]+t[0]*t[0]*t[1]*u[1]*v[0]*v[1]*v[1]-t[0]*t[0]*t[2]*u[2]*v[0]*v[1]*v[1]+t[0]*t[1]*t[2]*u[0]*v[0]*v[1]*v[2]+t[1]*t[1]*t[2]*u[1]*v[0]*v[1]*v[2]-t[1]*t[2]*t[2]*u[2]*v[0]*v[1]*v[2]-t[0]*t[0]*t[2]*u[0]*v[1]*v[1]*v[2]-t[0]*t[1]*t[2]*u[1]*v[1]*v[1]*v[2]+t[0]*t[2]*t[2]*u[2]*v[1]*v[1]*v[2]-t[0]*t[1]*t[1]*u[0]*v[0]*v[2]*v[2]-t[1]*t[1]*t[1]*u[1]*v[0]*v[2]*v[2]+t[1]*t[1]*t[2]*u[2]*v[0]*v[2]*v[2]+t[0]*t[0]*t[1]*u[0]*v[1]*v[2]*v[2]+t[0]*t[1]*t[1]*u[1]*v[1]*v[2]*v[2]-t[0]*t[1]*t[2]*u[2]*v[1]*v[2]*v[2]-t[0]*t[1]*t[1]*v[0]*v[0]*v[0]*w[0]+2*t[0]*t[0]*t[1]*v[0]*v[0]*v[1]*w[0]-t[1]*t[2]*t[2]*v[0]*v[0]*v[1]*w[0]-t[0]*t[0]*t[0]*v[0]*v[1]*v[1]*w[0]+t[0]*t[2]*t[2]*v[0]*v[1]*v[1]*w[0]+t[1]*t[1]*t[2]*v[0]*v[0]*v[2]*w[0]-t[0]*t[1]*t[2]*v[0]*v[1]*v[2]*w[0]-t[0]*t[1]*t[1]*v[0]*v[0]*v[1]*w[1]+2*t[0]*t[0]*t[1]*v[0]*v[1]*v[1]*w[1]-t[1]*t[2]*t[2]*v[0]*v[1]*v[1]*w[1]-t[0]*t[0]*t[0]*v[1]*v[1]*v[1]*w[1]+t[0]*t[2]*t[2]*v[1]*v[1]*v[1]*w[1]+t[1]*t[1]*t[2]*v[0]*v[1]*v[2]*w[1]-t[0]*t[1]*t[2]*v[1]*v[1]*v[2]*w[1]+t[0]*t[1]*t[1]*v[0]*v[0]*v[2]*w[2]-2*t[0]*t[0]*t[1]*v[0]*v[1]*v[2]*w[2]+t[1]*t[2]*t[2]*v[0]*v[1]*v[2]*w[2]+t[0]*t[0]*t[0]*v[1]*v[1]*v[2]*w[2]-t[0]*t[2]*t[2]*v[1]*v[1]*v[2]*w[2]-t[1]*t[1]*t[2]*v[0]*v[2]*v[2]*w[2]+t[0]*t[1]*t[2]*v[1]*v[2]*v[2]*w[2]+t[2]*v[0]*sr-t[0]*v[2]*sr)/((t[1]*v[0]-t[0]*v[1])*(-(t[2]*t[2]*(v[0]*v[0]+v[1]*v[1]))+2*t[0]*t[2]*v[0]*v[2]+2*t[1]*v[1]*(-(t[0]*v[0])+t[2]*v[2])+t[1]*t[1]*(v[0]*v[0]-v[2]*v[2])+t[0]*t[0]*(v[1]*v[1]-v[2]*v[2])));
            x[2]=(t[0]*t[2]*u[0]*v[0]*v[0]+t[1]*t[2]*u[1]*v[0]*v[0]-t[2]*t[2]*u[2]*v[0]*v[0]+t[0]*t[2]*u[0]*v[1]*v[1]+t[1]*t[2]*u[1]*v[1]*v[1]-t[2]*t[2]*u[2]*v[1]*v[1]-t[0]*t[0]*u[0]*v[0]*v[2]-t[0]*t[1]*u[1]*v[0]*v[2]+t[0]*t[2]*u[2]*v[0]*v[2]-t[0]*t[1]*u[0]*v[1]*v[2]-t[1]*t[1]*u[1]*v[1]*v[2]+t[1]*t[2]*u[2]*v[1]*v[2]-t[0]*t[2]*v[0]*v[0]*w[0]-t[1]*t[2]*v[0]*v[1]*w[0]+t[0]*t[0]*v[0]*v[2]*w[0]+t[1]*t[1]*v[0]*v[2]*w[0]-t[0]*t[2]*v[0]*v[1]*w[1]-t[1]*t[2]*v[1]*v[1]*w[1]+t[0]*t[0]*v[1]*v[2]*w[1]+t[1]*t[1]*v[1]*v[2]*w[1]+t[0]*t[2]*v[0]*v[2]*w[2]+t[1]*t[2]*v[1]*v[2]*w[2]-t[0]*t[0]*v[2]*v[2]*w[2]-t[1]*t[1]*v[2]*v[2]*w[2]+sr)/(-(t[2]*t[2]*(v[0]*v[0]+v[1]*v[1]))+2*t[0]*t[2]*v[0]*v[2]+2*t[1]*v[1]*(-(t[0]*v[0])+t[2]*v[2])+t[1]*t[1]*(v[0]*v[0]-v[2]*v[2])+t[0]*t[0]*(v[1]*v[1]-v[2]*v[2]));
            return true;
        }
        else return false;
    }
}
class CircIntCirc1 extends Point {  // ancestor: CircIntCirc0.
    public CircIntCirc1(LinkedList<Construct> ancestor, double[] vector, int ID, float xx, float yy) {
        super(ancestor, vector, ID, xx, yy);
        type = CIRCintCIRC1;
        update(vector);
    }
    public void draw(Canvas canvas, Paint paint, boolean isNew) {super.draw(canvas, paint, isNew);}
    @Override
    public void update(double[] vector) {
        isReal = parent.get(0).isReal;
        if (isReal) {
            CircIntCirc0 mommy = (CircIntCirc0) parent.get(0);
            x = mommy.pt1[0];
            y = mommy.pt1[1];
            z = mommy.pt1[2];
        }
    }
}
class Segment extends Line {  // ancestor: point, point
    public Segment(LinkedList<Construct> ancestor, double[] vector, int ID, float xx, float yy) {
        super(ancestor, vector, ID, xx, yy);
        type = SEGMENT;
        update(vector);
    }
    @Override
    public void draw(Canvas canvas, Paint paint, boolean isNew) {
        if (isNew) {
            paint.setColor(Color.RED);
        } else {
            paint.setColor(Color.LTGRAY);
        }
        paint.setStrokeWidth(strokeWidth);
        double[] v1 = {0, 0, 0}, v2 = {0, 0, 0}, nm = {0, 0, 0};
        parent.get(0).getXYZ(v1);
        parent.get(1).getXYZ(v2);
        MathEqns.hypCrossProduct(v1, v2, nm);
        double a = nm[0], b = nm[1], c = nm[2];
        double s1 = Math.sqrt(a * a + b * b), s2 = Math.sqrt(a * a + b * b - c * c);
        // finding the "natural" parameter endpoints t1 and t2.
        double t1 = Math.log(v1[2] * s2 / s1 + Math.sqrt(v1[2] * v1[2] * s2 * s2 / s1 / s1 - 1));
        double t2 = Math.log(v2[2] * s2 / s1 + Math.sqrt(v2[2] * v2[2] * s2 * s2 / s1 / s1 - 1));
        // since the inverse hyperbolic cosine is double-valued, we do
        // the next few steps to make sure we have the correct values
        if ((Math.abs((-a * c * Math.cosh(t1) / s2 + b * Math.sinh(t1)) / s1 - v1[0]) > epsilon && Math.abs(v1[1] - v2[1]) > epsilon)
                || (Math.abs((-b * c * Math.cosh(t1) / s2 - a * Math.sinh(t1)) / s1 - v1[1]) > epsilon && Math.abs(v1[0] - v2[0]) > epsilon))
            t1 = Math.log(v1[2] * s2 / s1 - Math.sqrt(v1[2] * v1[2] * s2 * s2 / s1 / s1 - 1));
        if ((Math.abs((-a * c * Math.cosh(t2) / s2 + b * Math.sinh(t2)) / s1 - v2[0]) > epsilon && Math.abs(v1[1] - v2[1]) > epsilon)
                || (Math.abs((-b * c * Math.cosh(t2) / s2 - a * Math.sinh(t2)) / s1 - v2[1]) > epsilon && Math.abs(v1[0] - v2[0]) > epsilon))
            t2 = Math.log(v2[2] * s2 / s1 - Math.sqrt(v2[2] * v2[2] * s2 * s2 / s1 / s1 - 1));
        if (t2 < t1) {
            double temp = t1;
            t1 = t2;
            t2 = temp;
        }
        for (double i = t1; i < t2 - (t2 - t1) / 64; i += (t2 - t1) / 32) {
            v1[0] = (-a * c / s2 * Math.cosh(i) + b * Math.sinh(i)) / s1;
            v1[1] = (-b * c / s2 * Math.cosh(i) - a * Math.sinh(i)) / s1;
            v1[2] = s1 / s2 * Math.cosh(i);
            v2[0] = (-a * c / s2 * Math.cosh(i + (t2 - t1) / 32) + b * Math.sinh(i + (t2 - t1) / 32)) / s1;
            v2[1] = (-b * c / s2 * Math.cosh(i + (t2 - t1) / 32) - a * Math.sinh(i + (t2 - t1) / 32)) / s1;
            v2[2] = s1 / s2 * Math.cosh(i + (t2 - t1) / 32);

            double[] scrn1={0,0},scrn2={0,0};
            xyzToScreen(v1,scrn1);
            xyzToScreen(v2,scrn2);
            canvas.drawLine((float) (scrn1[0]), (float) (scrn1[1]), (float) (scrn2[0]), (float) (scrn2[1]), paint);

            if (i >= (t2 + t1) / 2 && i < (t2 + t1) / 2 + (t2 - t1) / 32 && showLabel) {
                paint.setTextSize(45);
                canvas.drawText(textString, (float) (scrn1[0] + 36), (float) (scrn1[1] + 45), paint);
            }
        }
    }
}
class Ray extends Line {  // ancestor: point, point
    public Ray(LinkedList<Construct> ancestor, double[] vector, int ID, float xx, float yy) {
        super(ancestor, vector, ID, xx, yy);
        type = RAY;
        update(vector);
    }
    @Override
    public void draw(Canvas canvas, Paint paint, boolean isNew) {
        if (isNew) {
            paint.setColor(Color.RED);
        } else {
            paint.setColor(Color.LTGRAY);
        }
        paint.setStrokeWidth(strokeWidth);
        double[] v1 = {0, 0, 0}, v2 = {0, 0, 0}, nm = {0, 0, 0};
        parent.get(0).getXYZ(v1);
        parent.get(1).getXYZ(v2);
        MathEqns.hypCrossProduct(v1, v2, nm);

        parent.get(0).getXYZ(v1);
        parent.get(1).getXYZ(v2);
        MathEqns.hypCrossProduct(v1,v2,nm);
        double	a=nm[0],	b=nm[1],	c=nm[2];
        double s1=Math.sqrt(a*a+b*b),	s2=Math.sqrt(a*a+b*b-c*c);
        // finding the "natural" parameter endpoints t1 and t2.
        double t1=Math.log(v1[2]*s2/s1+Math.sqrt(v1[2]*v1[2]*s2*s2/s1/s1-1));
        double u2=Math.log(v2[2]*s2/s1+Math.sqrt(v2[2]*v2[2]*s2*s2/s1/s1-1));
        // since the inverse hyperbolic cosine is double-valued, we do
        // the next few steps to make sure we have the correct values
        if ((Math.abs((-a*c*Math.cosh(t1)/s2+b*Math.sinh(t1))/s1-v1[0])>.00001 && Math.abs(v1[1]-v2[1])>.00001)
                || (Math.abs((-b*c*Math.cosh(t1)/s2-a*Math.sinh(t1))/s1-v1[1])>.00001 && Math.abs(v1[0]-v2[0])>.00001))
            t1=Math.log(v1[2]*s2/s1-Math.sqrt(v1[2]*v1[2]*s2*s2/s1/s1-1));
        if ((Math.abs((-a*c*Math.cosh(u2)/s2+b*Math.sinh(u2))/s1-v2[0])>.00001 && Math.abs(v1[1]-v2[1])>.00001)
                || (Math.abs((-b*c*Math.cosh(u2)/s2-a*Math.sinh(u2))/s1-v2[1])>.00001 && Math.abs(v1[0]-v2[0])>.00001))
            u2=Math.log(v2[2]*s2/s1-Math.sqrt(v2[2]*v2[2]*s2*s2/s1/s1-1));
        double t2;
        if (u2<t1) t2=-10; // since we want to go out to infinity, 
        else t2=10;        // choose the second endpoint really big (e^10)
        for (double i=t1; i<t2-(t2-t1)/64; i+=(t2-t1)/256) {
            v1[0]=(-a*c/s2*Math.cosh(i)+b*Math.sinh(i))/s1;
            v1[1]=(-b*c/s2*Math.cosh(i)-a*Math.sinh(i))/s1;
            v1[2]=s1/s2*Math.cosh(i);
            v2[0]=(-a*c/s2*Math.cosh(i+(t2-t1)/256)+b*Math.sinh(i+(t2-t1)/256))/s1;
            v2[1]=(-b*c/s2*Math.cosh(i+(t2-t1)/256)-a*Math.sinh(i+(t2-t1)/256))/s1;
            v2[2]=s1/s2*Math.cosh(i+(t2-t1)/256);

            double[] scrn1={0,0},scrn2={0,0};
            xyzToScreen(v1,scrn1);
            xyzToScreen(v2,scrn2);
            canvas.drawLine((float) (scrn1[0]), (float) (scrn1[1]), (float) (scrn2[0]), (float) (scrn2[1]), paint);
            if (i>=(u2+t1)/2 && i<(u2+t1)/2+(t2-t1)/256 && showLabel) {
                paint.setTextSize(45);
                canvas.drawText(textString, (float) (scrn1[0] + 36), (float) (scrn1[1] + 45), paint);
            }
        }
    }
}

class Midpoint extends Point {
    public Midpoint(LinkedList<Construct> ancestor, double[] vector, int ID, float xx, float yy) {
        super(ancestor, vector, ID, xx, yy);
        type = MIDPOINT;
        update(vector);
    }
    @Override
    public void update(double[] vector) {
        isReal = parent.get(0).isReal && parent.get(1).isReal;
        if (isReal) {
            double[] v1 = {0, 0, 0}, v2 = {0, 0, 0};
            LinkedList<Construct> tempList = new LinkedList<Construct>();
            parent.get(0).getXYZ(v1);
            parent.get(1).getXYZ(v2);
            xyzToScreen(v1,vector);
            Point p1 = new Point(tempList, vector, 1, width, height);
            xyzToScreen(v2,vector);
            Point p2 = new Point(tempList, vector, 2, width, height);
            tempList.clear();
            tempList.add(p1);
            tempList.add(p2);
            Line l1 = new Line(tempList, vector, 3, width, height);
            Circle a = new Circle(tempList, vector, 4, width, height);
            tempList.clear();
            tempList.add(p2);
            tempList.add(p1);
            Circle b = new Circle(tempList, vector, 5, width, height);
            tempList.clear();
            tempList.add(a);
            tempList.add(b);
            CircIntCirc0 i1 = new CircIntCirc0(tempList, vector, 6, width, height);
            tempList.clear();
            tempList.add(i1);
            CircIntCirc1 i2 = new CircIntCirc1(tempList, vector, 7, width, height);
            tempList.clear();
            tempList.add(i1);
            tempList.add(i2);
            Line l2 = new Line(tempList, vector, 8, width, height);
            tempList.clear();
            tempList.add(l1);
            tempList.add(l2);
            LineIntLine i3 = new LineIntLine(tempList, vector, 9, width, height);
            i3.getXYZ(v1);
            x = v1[0];
            y = v1[1];
            z = v1[2];
        }
    }
}
class FoldedPoint extends Point {
    public FoldedPoint(LinkedList<Construct> ancestor, double[] vector, int ID, float xx, float yy) {
        super(ancestor, vector, ID, xx, yy);
        type = FOLDedPT;
        update(vector);
    }
    @Override
    public void update(double[] vector) {
        isReal = parent.get(0).isReal && parent.get(1).isReal;
        if (isReal) {
            LinkedList<Construct> tempList = new LinkedList<>();
            tempList.add(parent.get(0));
            tempList.add(parent.get(1));
            PerpLine temp0 = new PerpLine(tempList, vector, 0, width, height);
            tempList.remove(0);
            tempList.add(temp0);
            LineIntLine temp1 = new LineIntLine(tempList, vector, 1, width, height);
            tempList.clear();
            tempList.add(temp1);
            tempList.add(parent.get(0));
            Circle temp2 = new Circle(tempList, vector, 2, width, height);
            tempList.clear();
            tempList.add(temp0);
            tempList.add(temp2);
            LineIntCirc0 temp3 = new LineIntCirc0(tempList, vector, 3, width, height);
            if (Math.sqrt(Math.pow(parent.get(0).x - temp3.x, 2) + Math.pow(parent.get(0).y - temp3.y, 2)) < epsilon) {
                tempList.clear();
                tempList.add(temp3);
                LineIntCirc1 temp4 = new LineIntCirc1(tempList, vector, 4, width, height);
                x = temp4.x;
                y = temp4.y;
                z = temp4.z;
            } else {
                x = temp3.x;
                y = temp3.y;
                z = temp3.z;
            }
        }
    }
}
class Bisector extends Line { // three points: vertex, point, point
    public Bisector(LinkedList<Construct> ancestor, double[] vector, int ID, float xx, float yy) {
        super(ancestor, vector, ID, xx, yy);
        type = BISECTOR;
        update(vector);
    }
    @Override
    public void update(double[] vector) {
        isReal = parent.get(0).isReal && parent.get(1).isReal && parent.get(2).isReal;
        if (isReal) {
            double[] aa = {0, 0, 0}, bb = {0, 0, 0}, cc = {0, 0, 0}, mp = {0, 0, 0};
//            double temp = Math.min(width/2,height/2);
//            double[] xyz={x,y,z};
//            xyzToScreen(xyz,vector);
////            vector[0]=x*temp+width/2;
////            vector[1]=y*temp+height/2;
            parent.get(0).getXYZ(bb);
            parent.get(1).getXYZ(aa);
            parent.get(2).getXYZ(cc);
            LinkedList<Construct> tempList = new LinkedList<Construct>();
            tempList.add(parent.get(0)); tempList.add(parent.get(1));
            Line temp0 = new Line(tempList,vector,0,width,height);
            if (MathEqns.hypDistance(aa,bb)+MathEqns.hypDistance(bb,cc)-MathEqns.hypDistance(aa,cc) < epsilon) { // if the points are collinear
                tempList.clear();
                tempList.add(parent.get(0));
                tempList.add(temp0);
                PerpLine temp1 = new PerpLine(tempList,vector,1,width,height);
                x=temp1.x; y=temp1.y; z=temp1.z;
            } else if (MathEqns.hypDistance(aa,cc)+MathEqns.hypDistance(cc,bb)-MathEqns.hypDistance(aa,bb) < epsilon || MathEqns.hypDistance(bb,aa)+MathEqns.hypDistance(aa,cc)-MathEqns.hypDistance(bb,cc)< epsilon) {
                x=temp0.x; y=temp0.y; z=temp0.z;
            } else {
                tempList.clear();
                tempList.add(parent.get(0));
                tempList.add(parent.get(2));
                Circle temp1 = new Circle(tempList, vector, 1, width, height);
                tempList.clear();
                tempList.add(temp0);
                tempList.add(temp1);
                LineIntCirc0 temp2 = new LineIntCirc0(tempList, vector, 2, width, height);
                tempList.clear();
                tempList.add(temp2);
                LineIntCirc1 temp3 = new LineIntCirc1(tempList, vector, 3, width, height);
                temp2.getXYZ(bb);
                temp3.getXYZ(cc);
                tempList.clear();
                tempList.add(parent.get(2));
                if (MathEqns.hypDistance(aa, bb) < MathEqns.hypDistance(aa, cc)) {
                    tempList.add(temp2);
                } else {
                    tempList.add(temp3);
                }
                Midpoint temp4 = new Midpoint(tempList, vector, 4, width, height);
                tempList.clear();
                tempList.add(parent.get(0));
                tempList.add(temp4);
                Line temp5 = new Line(tempList, vector, 5, width, height);
                x = temp5.x; y = temp5.y; z = temp5.z;
            }
        }
    }
}
class Measure extends Point {
    public Measure(LinkedList<Construct> ancestor, double[] vector, int index, float xx, float yy) {
        super(ancestor, vector, index, xx, yy);      // point
    }
    @Override
    public void draw(Canvas canvas, Paint paint, boolean isNew) {
        paint.setStyle(Paint.Style.STROKE);
        if (isNew) {
            paint.setColor(Color.RED);
        } else {
            paint.setColor(Color.WHITE);
        }
        paint.setStrokeWidth(strokeWidth);
        canvas.drawCircle((float) (x), (float) (y), 3 * strokeWidth, paint);
        if (isNew) {
            paint.setColor(Color.RED);
        } else {
            paint.setColor(Color.LTGRAY);
        }
        String string = textString + " ≈ "+Double.toString(Math.round(10000*(value)+0.3)/10000.0);
        paint.setTextSize(42);
        canvas.drawText(string,(float)(x + 30), (float)(y+15),paint);
    }
    @Override
    public void getScreen(double[] vector) {
        vector[0]=x;
        vector[1]=y;
    }
    @Override
    public double distance(double[] v) {
        return Math.sqrt((v[0]-x)*(v[0]-x)+(v[1]-y)*(v[1]-y));
    }
    public int toRed(double x) {
        double temp = x-Math.floor(x);
        if (temp<1.0/3.0) {
            return (int)(765*(1.0/3.0-temp));
        } else if (temp>2.0/3.0) {
            return (int)(765*(temp-2.0/3.0));
        } else return 0;
    }
    public int toGreen(double x) {
        double temp = x-Math.floor(x);
        if (temp<1.0/3.0) {
            return (int)(765*(temp));
        } else if (temp>=1.0/3.0 && temp<2.0/3.0) {
            return (int)(765*(2.0/3.0-temp));
        } else return 0;
    }
    public int toBlue(double x) {
        double temp = x-Math.floor(x);
        if (temp>1.0/3.0 && temp<=2.0/3.0) {
            return (int)(765*(temp-1.0/3.0));
        } else if (temp>2.0/3.0) {
            return (int)(765*(1-temp));
        } else return 0;
    }
}
class Distance extends Measure {       // parents: point, point (for unit distance), or
    public Distance(LinkedList<Construct> ancestor, double[] vector, int index, float xx, float yy) {
        super(ancestor, vector, index, xx, yy);
        type = DISTANCE;
        update(vector);
        parent.get(0).showLabel=true;
        parent.get(1).showLabel=true;
        showLabel=false;
        textString += " : d(" + character[parent.get(0).index%24] + Integer.toString(parent.get(0).index/24) + "," + character[parent.get(1).index%24] + Integer.toString(parent.get(1).index/24) + ")";
    }
    @Override
    public void update(double[] vector) {
        boolean parentsAllReal = true;
        isReal = parent.get(0).isReal && parent.get(1).isReal;
        if (isReal) {
            double[] v1={0,0,0},v2={0,0,0};
            parent.get(0).getXYZ(v1); parent.get(1).getXYZ(v2);
            value = MathEqns.hypDistance(v1,v2);
            x=vector[0]; y=vector[1]; z=0;
        }
    }
}
class Angle extends Measure {
    public Angle(LinkedList<Construct> ancestor, double[] vector, int index, float xx, float yy) {
        super(ancestor, vector, index, xx, yy);
        type = ANGLE;
        update(vector);
        parent.get(0).showLabel=true;
        parent.get(1).showLabel=true;
        parent.get(2).showLabel=true;
        showLabel=false;
        textString = character[index%24] + Integer.toString(index/24) + " : ∠(" + character[parent.get(0).index%24] + Integer.toString(parent.get(0).index/24) + "," + character[parent.get(1).index%24] + Integer.toString(parent.get(1).index/24) + "," + character[parent.get(2).index%24] + Integer.toString(parent.get(2).index/24) + ")";
    }
    public void update(double[] vector) {
        isReal = parent.get(0).isReal && parent.get(1).isReal && parent.get(2).isReal;
        if (isReal) {
            double[] a={0,0,0},b={0,0,0},c={0,0,0};
            get(0).getXYZ(a);		get(1).getXYZ(b);		get(2).getXYZ(c);
            value=MathEqns.hypAngle(a,b,c);
            x=vector[0];y=vector[1];
        }
    }
}
class TriArea extends Measure {
    public TriArea(LinkedList<Construct> ancestor, double[] vector, int index, float xx, float yy) {
        super(ancestor, vector, index, xx, yy);
        type = TriAREA;
        update(vector);
        parent.get(0).showLabel=true;
        parent.get(1).showLabel=true;
        parent.get(2).showLabel=true;
        showLabel=false;
        textString = character[index%24] + Integer.toString(index/24) + " : Δ(" + character[parent.get(0).index%24] + Integer.toString(parent.get(0).index/24) + "," + character[parent.get(1).index%24] + Integer.toString(parent.get(1).index/24) + "," + character[parent.get(2).index%24] + Integer.toString(parent.get(2).index/24) + ")";
    }
    public void update(double[] vector) {
        isReal = parent.get(0).isReal && parent.get(1).isReal && parent.get(2).isReal;
        if (isReal) {
            double[] a={0,0,0},b={0,0,0},c={0,0,0};
            get(0).getXYZ(a);		get(1).getXYZ(b);		get(2).getXYZ(c);
            double angle0=MathEqns.hypAngle(a,b,c);
            get(0).getXYZ(a);		get(1).getXYZ(b);		get(2).getXYZ(c);
            double angle1=MathEqns.hypAngle(b,c,a);
            get(0).getXYZ(a);		get(1).getXYZ(b);		get(2).getXYZ(c);
            double angle2=MathEqns.hypAngle(c,a,b);
            value=Math.PI*(180.0-angle0-angle1-angle2)/180;
            x=vector[0];y=vector[1];
        }
    }
    @Override
    public void draw(Canvas canvas, Paint paint, boolean isNew) {
        double hue = (3 * (double) index) / 22.0;
        if (isNew) {
            paint.setColor(Color.RED);
        } else {
            paint.setColor(Color.argb(255, toRed(hue), toGreen(hue), toBlue(hue)));
        }
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle((float) (x), (float) (y), 3 * strokeWidth, paint);
        if (isNew) {
            paint.setColor(Color.RED);
        } else {
            paint.setColor(Color.LTGRAY);
        }
        String string = textString + " ≈ " + Double.toString(Math.round(10000*(value)+0.3)/10000.0);
        paint.setTextSize(42);
        canvas.drawText(string,(float)(x + 30), (float)(y+15),paint);

        if (isNew) {
            paint.setColor(Color.argb(160, toRed(hue), toGreen(hue), toBlue(hue)));
        } else {
            paint.setColor(Color.argb(96, toRed(hue), toGreen(hue), toBlue(hue)));
        }
        double[] v1 = {0, 0, 0}, v2 = {0, 0, 0}, nm = {0, 0, 0};
        paint.setStyle(Paint.Style.FILL);
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        parent.get(0).getXYZ(v1);
        double[] scrn={0,0};
        xyzToScreen(v1,scrn);
        path.moveTo((float) (scrn[0]), (float) (scrn[1]));
        for (int j = 0; j < 3; j++) {
            parent.get(j).getXYZ(v1);
            parent.get((j + 1) % 3).getXYZ(v2);
            MathEqns.hypCrossProduct(v1, v2, nm);
            double a = nm[0], b = nm[1], c = nm[2];
            double s1 = Math.sqrt(a * a + b * b), s2 = Math.sqrt(a * a + b * b - c * c);
            // finding the "natural" parameter endpoints t1 and t2.
            double t1 = Math.log(v1[2] * s2 / s1 + Math.sqrt(v1[2] * v1[2] * s2 * s2 / s1 / s1 - 1));
            double t2 = Math.log(v2[2] * s2 / s1 + Math.sqrt(v2[2] * v2[2] * s2 * s2 / s1 / s1 - 1));
            // since the inverse hyperbolic cosine is double-valued, we do
            // the next few steps to make sure we have the correct values
            if ((Math.abs((-a * c * Math.cosh(t1) / s2 + b * Math.sinh(t1)) / s1 - v1[0]) > epsilon && Math.abs(v1[1] - v2[1]) > epsilon)
                    || (Math.abs((-b * c * Math.cosh(t1) / s2 - a * Math.sinh(t1)) / s1 - v1[1]) > epsilon && Math.abs(v1[0] - v2[0]) > epsilon))
                t1 = Math.log(v1[2] * s2 / s1 - Math.sqrt(v1[2] * v1[2] * s2 * s2 / s1 / s1 - 1));
            if ((Math.abs((-a * c * Math.cosh(t2) / s2 + b * Math.sinh(t2)) / s1 - v2[0]) > epsilon && Math.abs(v1[1] - v2[1]) > epsilon)
                    || (Math.abs((-b * c * Math.cosh(t2) / s2 - a * Math.sinh(t2)) / s1 - v2[1]) > epsilon && Math.abs(v1[0] - v2[0]) > epsilon))
                t2 = Math.log(v2[2] * s2 / s1 - Math.sqrt(v2[2] * v2[2] * s2 * s2 / s1 / s1 - 1));
            v1[0] = (-a * c / s2 * Math.cosh(t1) + b * Math.sinh(t1)) / s1;
            v1[1] = (-b * c / s2 * Math.cosh(t1) - a * Math.sinh(t1)) / s1;
            if (t2 < t1) {
                for (double i = t1 + (t2 - t1) / 32; i >= t1; i += (t2 - t1) / 32) {
                    v1[0] = (-a * c / s2 * Math.cosh(i) + b * Math.sinh(i)) / s1;
                    v1[1] = (-b * c / s2 * Math.cosh(i) - a * Math.sinh(i)) / s1;
                    v1[2] = s1 / s2 * Math.cosh(i);
                    xyzToScreen(v1,scrn);
                    path.lineTo((float) (scrn[0]), (float) (scrn[1]));
                }
            } else {
                for (double i = t1 + (t2 - t1) / 32; i <= t2; i += (t2 - t1) / 32) {
                    v1[0] = (-a * c / s2 * Math.cosh(i) + b * Math.sinh(i)) / s1;
                    v1[1] = (-b * c / s2 * Math.cosh(i) - a * Math.sinh(i)) / s1;
                    v1[2] = s1 / s2 * Math.cosh(i);
                    xyzToScreen(v1,scrn);
                    path.lineTo((float) (scrn[0]), (float) (scrn[1]));
                }
            }
        }
        path.close();
        canvas.drawPath(path, paint);
    }
}
class HiddenThing extends Construct { // parent: any single
    public HiddenThing(LinkedList<Construct> ancestor, double[] point, int number,float width,float height) {
        super(ancestor, point, number,width,height);
        type = HIDDENthing;
        index = number;
    }
    @Override
    public double distance(double[] vector) {return 1024;}
    @Override
    public void getScreen(double[] vector) {}
    @Override
    public void draw(Canvas canvas, Paint paint, boolean isNew) {}
    @Override
    public void update(double[] vector) {}
}
class Ratio extends Distance {
    public Ratio(LinkedList<Construct> ancestor, double[] point, int number,float width,float height) {
        super(ancestor, point, number,width,height);
        type = RATIO;
        index = number;
        update(point);
        showLabel = false;
        textString = character[index % 24] + Integer.toString(index / 24) + " : " + character[parent.get(0).index % 24] + Integer.toString(parent.get(0).index / 24) + " / " + character[parent.get(1).index % 24] + Integer.toString(parent.get(1).index / 24);
    }
    public void update(double[] point) {
        if (parent.get(0).isReal && parent.get(1).isReal && Math.abs(parent.get(1).value) > epsilon) {
            isReal = true;
            x=point[0];y=point[1];
            value = parent.get(0).value / parent.get(1).value;
        } else {
            isReal = false;
        }
    }
}
class Product extends Distance {
    public Product(LinkedList<Construct> ancestor, double[] point, int number,float width,float height) {
        super(ancestor, point, number,width,height);
        type = PRODUCT;
        index = number;
        update(point);
        showLabel = false;
        textString = character[index % 24] + Integer.toString(index / 24) + " : " + character[parent.get(0).index % 24] + Integer.toString(parent.get(0).index / 24) + " ⋅ " + character[parent.get(1).index % 24] + Integer.toString(parent.get(1).index / 24);
    }
    public void update(double[] point) {
        if (parent.get(0).isReal && parent.get(1).isReal) {
            isReal = true;
            x=point[0];y=point[1];
            value = parent.get(0).value * parent.get(1).value;
        } else {
            isReal = false;
        }
    }
}
class Sum extends Distance {
    public Sum(LinkedList<Construct> ancestor, double[] point, int number,float width,float height) {
        super(ancestor, point, number,width,height);
        type = SUM;
        index = number;
        update(point);
        showLabel = false;
        textString = character[index % 24] + Integer.toString(index / 24) + " : " + character[parent.get(0).index % 24] + Integer.toString(parent.get(0).index / 24) + " + " + character[parent.get(1).index % 24] + Integer.toString(parent.get(1).index / 24);
    }
    public void update(double[] point) {
        if (parent.get(0).isReal && parent.get(1).isReal) {
            isReal = true;
            x=point[0];y=point[1];
            value = parent.get(0).value + parent.get(1).value;
        } else {
            isReal = false;
        }
    }
}
class Difference extends Distance {
    public Difference(LinkedList<Construct> ancestor, double[] point, int number,float width,float height) {
        super(ancestor, point, number,width,height);
        update(point);
        type = DIFFERENCE;
        index = number;
        showLabel = false;
        textString = character[index % 24] + Integer.toString(index / 24) + " : " + character[parent.get(0).index % 24] + Integer.toString(parent.get(0).index / 24) + " - " + character[parent.get(1).index % 24] + Integer.toString(parent.get(1).index / 24);
    }
    public void update(double[] point) {
        if (parent.get(0).isReal && parent.get(1).isReal) {
            isReal = true;
            x=point[0];y=point[1];
            value = parent.get(0).value - parent.get(1).value;
        } else {
            isReal = false;
        }
    }
}

class CircArea extends Measure {
    public CircArea(LinkedList<Construct> ancestor, double[] point, int number,float width,float height) {
        super(ancestor, point, number,width,height);
        update(point);
        parent.get(0).showLabel=true;
        type = CircAREA;
        index = number;
        showLabel = false;
        textString = character[index % 24] + Integer.toString(index / 24) + " : ⦿(" + character[parent.get(0).index % 24] + Integer.toString(parent.get(0).index / 24) + ")";
    }
    @Override
    public void update(double[] point) {
        double[] v1={0,0,0},v2={0,0,0};
        isReal = parent.get(0).isReal;
        x=point[0]; y=point[1];
        parent.get(0).parent.get(0).getXYZ(v1);
        parent.get(0).parent.get(1).getXYZ(v2);
        double radius = MathEqns.hypDistance(v1, v2);
        value = 4 * Math.PI * Math.pow(Math.sinh(radius / 2), 2);
    }
    @Override
    public void draw(Canvas canvas, Paint paint, boolean isNew) {
        double hue = (3 * (double) index) / 22.0;
        if (isNew) {
            paint.setColor(Color.RED);
        } else {
            paint.setColor(Color.argb(255, toRed(hue), toGreen(hue), toBlue(hue)));
        }
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle((float) (x), (float) (y), 3 * strokeWidth, paint);
        if (isNew) {
            paint.setColor(Color.RED);
        } else {
            paint.setColor(Color.LTGRAY);
        }
        String string = textString + " ≈ " + Double.toString(Math.round(10000*(value)+0.3)/10000.0);
        paint.setTextSize(42);
        canvas.drawText(string,(float)(x + 30), (float)(y+15),paint);

        if (isNew) {
            paint.setColor(Color.argb(160, toRed(hue), toGreen(hue), toBlue(hue)));
        } else {
            paint.setColor(Color.argb(96, toRed(hue), toGreen(hue), toBlue(hue)));
        }
        double[] v1 = {0, 0, 0}, nm = {0, 0, 0};
        paint.setStyle(Paint.Style.FILL);
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        //float temp = Math.min(width / 2, height / 2);
        double[] axis={0,0,0},pt={0,0,0},pTranslate={0,0,0},u1={0,0,0},u2={0,0,0};
        parent.get(0).parent.get(0).getXYZ(axis);
        parent.get(0).parent.get(1).getXYZ(pt);
        MathEqns.hypTranslate(axis, pt, pTranslate);
        axis[0] *= -1;
        axis[1] *= -1;
        double r = Math.sqrt(pTranslate[0] * pTranslate[0] + pTranslate[1] * pTranslate[1]);
        int multiplier = MathEqns.round(MathEqns.min(MathEqns.max(r, 1), 256));
        v1[0] = r * 1;
        v1[1] = 0;
        v1[2] = Math.sqrt(1 + v1[0] * v1[0] + v1[1] * v1[1]);
        MathEqns.hypTranslate(axis, v1, u1);
        double[] scrn={0,0};
        xyzToScreen(u1,scrn);
        path.moveTo((float)(scrn[0]), (float)(scrn[1]));
        for (int i = 1; i <= 44 * multiplier; i++) {
            v1[0] = r * Math.cos(i / (7. * multiplier));
            v1[1] = r * Math.sin(i / (7. * multiplier));
            v1[2] = Math.sqrt(1 + v1[0] * v1[0] + v1[1] * v1[1]);
            MathEqns.hypTranslate(axis, v1, u1);
            xyzToScreen(u1,scrn);
            path.lineTo((float)(scrn[0]), (float)(scrn[1]));
        }
        path.close();
        canvas.drawPath(path, paint);
    }
}

class Circumference extends Measure {
    public Circumference(LinkedList<Construct> ancestor, double[] point, int number,float width,float height) {
        super(ancestor, point, number,width,height);
        update(point);
        parent.get(0).showLabel=true;
        type = CIRCUMFERENCE;
        index = number;
        showLabel = false;
        textString = character[index % 24] + Integer.toString(index / 24) + " : C(" + character[parent.get(0).index % 24] + Integer.toString(parent.get(0).index / 24) + ")";
    }
    @Override
    public void update(double[] point) {
        double[] v1 = {0, 0, 0}, v2 = {0, 0, 0};
        isReal = parent.get(0).isReal;
        x = point[0];
        y = point[1];
        parent.get(0).parent.get(0).getXYZ(v1);
        parent.get(0).parent.get(1).getXYZ(v2);
        double radius = MathEqns.hypDistance(v1, v2);
        value = 2 * Math.PI * Math.sinh(radius);
    }
}









