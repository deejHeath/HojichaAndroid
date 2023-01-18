package edu.plu.heathdj.hojicha;

import java.util.LinkedList;

public class MathEqns{
	public static double min(double x, double y) { if (x>y) return y; else return x; }
	public static double max(double x, double y) { if (x>y) return x; else return y; }

  public static int round(double x) {
	  if (x < 0) return -(int) Math.round(Math.abs(x));
	  return (int) Math.round(x);
  }
  public static double chop(double x) {
	  int n;
	  boolean neg=false;
	  if (x<0) {neg=true;x*=-1;}
	  if (x<1) {
		  n=(int)(Math.round(Math.pow(10,-Math.floor(Math.log10(x)))));
		  x*=(n*100.);
		  x=(double)(Math.round(x))/(n*100.);
	  }
	  else {
		  n=(int)(Math.round(Math.pow(10,Math.floor(Math.log10(x)))));
		  x*=(100./n);
		  x=(double)(Math.round(x))/(100./n);
	  }
	  if (neg) x*=-1;
	  return x;
  }
  public static double chop(double x, int b) {
	  int n;
	  boolean neg=false;
	  if (x<0) {neg=true;x*=-1;}
	  if (x<1) {
		  n=(int)(Math.round(Math.pow(10,-Math.floor(Math.log10(x)))));
		  x*=(n*Math.pow(10.,b));
		  x=(double)(Math.round(x))/(n*Math.pow(10.,b));
	  }
	  else {
		  n=(int)(Math.round(Math.pow(10,Math.floor(Math.log10(x)))));
		  x*=(Math.pow(10.,b)/n);
		  x=(double)(Math.round(x))/(Math.pow(10.,b)/n);
	  }
	  if (neg) x*=-1;
	  return x;
  }
  public static double norm(double[] v){
    return Math.sqrt(dotProduct(v,v));
  }
  public static double norm(double[] u,double[] v){
	  double[] w={0,0,0};
	  for (int i=0;i<3;i++) w[i]=u[i]-v[i];
	  return norm(w);
  }
  public static double determinant(double[] v0, double[] v1, double[] v2){
	  return v0[0]*(v1[1]*v2[2]-v2[1]*v1[2])-v0[1]*(v1[0]*v2[2]-v2[0]*v1[2])+v0[2]*(v1[0]*v2[1]-v2[0]*v1[1]);
  }
  public static void crossProduct(double[] v1, double[] v2, double[] normal) {
    normal[0]=v1[1]*v2[2]-v2[1]*v1[2];		
    normal[1]=v2[0]*v1[2]-v1[0]*v2[2];		
    normal[2]=v1[0]*v2[1]-v2[0]*v1[1];		
    normalize(normal);
  }
  public static void hypCrossProduct(double[] v1, double[] v2, double[] normal) {
    normal[0]=v1[1]*v2[2]-v2[1]*v1[2];		
    normal[1]=v2[0]*v1[2]-v1[0]*v2[2];
    normal[2]=v1[0]*v2[1]-v2[0]*v1[1];
    if (hypProduct(normal,normal)!=0) hypNormalize(normal);
  }
  public static void normalize(double[] v) {
	  double r = 0;
	  r = norm(v);
	  if (r != 0)
		  for (int i = 0; i < 3; i++) v[i] /= r;
  }
  public static void hypNormalize(double[] v) {
	  double r = hypProduct(v, v);
	  if (r > 0) r = Math.sqrt(r);
	  else r = Math.sqrt(-r);
	  if (r > 0) {
		  for (int i = 0; i < 3; i++) v[i] /= r;
	  }
  }
  
  public static double[] scalarProduct(double a, double[] v1){
    for(int i=0;i<3;i++) v1[i]*=a;
    return v1;
  }
  public static double[] addVec(double[] a, double[] b){
    double[] sum={0,0,0};
    sum[0]=a[0]+b[0];
    sum[1]=a[1]+b[1];
    sum[2]=a[2]+b[2];
    return sum;
  }
  public static double[] subVec(double[] a, double[] b){
    double[] diff={0,0,0};
    diff[0]=a[0]-b[0];
    diff[1]=a[1]-b[1];
    diff[2]=a[2]-b[2];
    return diff;
  }
  public static double dotProduct(double[] v1, double[] v2) {
    return v1[2]*v2[2]+v1[1]*v2[1]+v1[0]*v2[0];
  }
  public static double hypProduct(double[] v1, double[] v2) {
    return v1[0]*v2[0]+v1[1]*v2[1]-v1[2]*v2[2];
  }
//  public static void rotate(double w, double x, double y, double z, GeoConstruct A) {
//	  // this is only called from S & P geometries.
//  	double[] vector1={0,0,0};
//  	vector1[0]=(1-2*y*y-2*z*z)*A.getX()+
//  	               2*(x*y-w*z)*A.getY()+
//  				   2*(x*z+w*y)*A.getZ();
//  	vector1[1]=    2*(x*y+w*z)*A.getX()+
//  	           (1-2*x*x-2*z*z)*A.getY()+
//  				   2*(y*z-w*x)*A.getZ();
//  	vector1[2]=    2*(x*z-w*y)*A.getX()+
//  	               2*(y*z+w*x)*A.getY()+
//  			   (1-2*x*x-2*y*y)*A.getZ();
//  	A.setNewXYZ(vector1);
//  }
//    public static void transform(GeoConstruct a,HyperConstruct b,double[] ds,double[] dn){
//    // the fixed object is a, the type=point object is b, ds=dragStart, dn=dragNow
//	double[] a1={0,0,0}, n={0,0,0}, b1={0,0,0},
//	         dS={ds[0],ds[1],ds[2]},dN={dn[0],dn[1],dn[2]};
//	if (a.getType()==0) a.get(0).getXYZ(a1);
//	else a.getXYZ(a1);
//	b.getXYZ(b1);
//	if (a.getType()>=0) {// rotation: fixedObject = pt or circ
//	  hypTranslate(a1,dS,n);
//	  for (int i=0;i<2;i++) dS[i]=n[i];
//	  hypTranslate(a1,dN,n);
//	  for (int i=0;i<2;i++) dN[i]=n[i];
//	  dS[2]=0;			dN[2]=0;
//	  normalize(dS);	normalize(dN);
//	  n[0]=-dS[1];		n[1]=dS[0];
//	  double theta=Math.acos(dotProduct(dS,dN));
//	  double phi=0;
//	  for (int i=0;i<2;i++) phi+=Math.pow((Math.cos(theta)*dS[i]+Math.sin(theta)*n[i]-dN[i]),2);
//	  if (Math.abs(phi)>.0001) theta*=-1;
//	  hypTranslate(a1,b1,n);
//	  b1[0]=Math.cos(theta)*n[0]-Math.sin(theta)*n[1];
//	  b1[1]=Math.cos(theta)*n[1]+Math.sin(theta)*n[0];
//	  b1[2]=n[2];
//	  a1[0]*=-1;	a1[1]*=-1;
//	  hypTranslate(a1,b1,n);
//	  a1[0]*=-1;	a1[1]*=-1;
//	  b.setNewXYZ(n);
//	}
//	else  if (a.getType()!=GeoConstruct.SEGMENT && a.getType()!=GeoConstruct.RAY) {// translation: fixedObject = line
//	  boolean reflect=false;
//	  double[] b2={0,0,0},b3={0,0,0},pt={0,0,0},s1={0,0,0},n1={0,0,0};
//	  hypPerp(a1,dS,n);	hypLineIntLine(a1,n,s1);// s1 = proj of dS on a1
//	  hypPerp(a1,dN,n);	hypLineIntLine(a1,n,n1);// n1 = proj of dN on a1
//	  hypCrossProduct(dS,dN,n);					// n = line through dS and dN
//	  if (n[0]*n[0]+n[1]*n[1]+n[2]*n[2]==0) b.setNewXYZ(b1);
//	  else {
//	  if (hypLineIntLine(n,a1,pt) &&
//	      (Math.abs(acosh(hypProduct(dS,pt))+acosh(hypProduct(pt,dN))-acosh(hypProduct(dS,dN)))<.001
//		  ||
//		  Math.abs(acosh(hypProduct(s1,pt))+acosh(hypProduct(pt,n1))-acosh(hypProduct(s1,n1)))<.001))
//		reflect=true;
//	  hypPerp(a1,b1,n);				// n  = perp to a1 through b1
//	  hypLineIntLine(n,a1,b2);		// b2 = proj of b1 on a1
//	  hypTranslate(s1,b1,b2);		// b2 = b1 translated down dS
//	  hypTranslate(s1,n1,n);		// n  = dN translated down dS
//	  n[0]*=-1;		n[1]*=-1;
//	  s1[0]*=-1;	s1[1]*=-1;
//	  hypTranslate(n,b2,b3);		// b3 = b2 translated up n
//	  hypTranslate(s1,b3,b2);		// b2 = b3 translated up dS
//	  if (reflect) {
//	    hypPerp(a1,b2,b1); hypLineIntLine(a1,b1,b3);
//		hypTranslate(b3,b2,n);
//		n[0]*=-1; n[1]*=-1;
//		b3[0]*=-1;b3[1]*=-1;
//		hypTranslate(b3,n,b2);
//	  }
//	  b.setNewXYZ(b2);				// this is the translated (& reflected) point.
//	  }
//	}
//	else { // type == SEGMENT or RAY
//		LinkedList<GeoConstruct> tempList = new LinkedList<GeoConstruct>();
//		tempList.add(a.get(0));	tempList.add(a.get(1));
//		HyperLine theLine = new HyperLine(GeoConstruct.LINE,tempList);
//		theLine.update();	theLine.getNewXYZ(n);	theLine.setXYZ(n);
//		tempList.clear();
//		HyperPoint dragS = new HyperPoint(GeoConstruct.POINT,tempList,ds);
//		HyperPoint dragN = new HyperPoint(GeoConstruct.POINT,tempList,dn);
//		tempList.clear();	tempList.add(dragS);	tempList.add(dragN);
//		HyperSEGMENT theDrag = new HyperSEGMENT(GeoConstruct.SEGMENT,tempList);
//		theDrag.update();	theDrag.getNewXYZ(n);	theDrag.setXYZ(n);
//		HyperPoint iS0,iS1,iS2,iS3;
//		iS0=((HyperLine)theLine).intersect(0,(HyperLine)theDrag);
//		iS0.update();
//		iS1=((HyperLine)theLine).intersect(1,(HyperLine)theDrag);
//		iS1.update();
//		double[] a0={a.get(0).getX(),a.get(0).getY(),a.get(0).getZ()},
//				 b0={b.getX(),b.getY(),b.getZ()};
//		a.get(1).getXYZ(a1);	tempList.clear();
//		HyperPoint	e0 = new HyperPoint(GeoConstruct.POINT,tempList,a0),
//					e1 = new HyperPoint(GeoConstruct.POINT,tempList,a1);
//		tempList.clear();	tempList.add(e0);	tempList.add(b);
//		HyperCircle c0 = new HyperCircle(GeoConstruct.CIRCLE,tempList);
//		tempList.clear();	tempList.add(e1);	tempList.add(b);
//		HyperCircle c1 = new HyperCircle(GeoConstruct.CIRCLE,tempList);
//		iS2=((HyperCircle)c0).intersect(0,(HyperCircle)c1);
//		iS2.update(); iS2.getNewXYZ(n);
//		iS3=((HyperCircle)c0).intersect(1,(HyperCircle)c1);
//		iS3.update();
//		if (iS0.getValidNew() || iS1.getValidNew()) {
//			if (iS2.getValidNew()) {
//				if (norm(n,b0)>.00001) b.setNewXYZ(n);
//				else {
//					iS3.getNewXYZ(n); b.setNewXYZ(n);
//				}
//			}
//		}
//		else {
//			if (iS2.getValidNew()) {
//				if (norm(n,b0)<.00001) b.setNewXYZ(n);
//				else {
//					iS3.getNewXYZ(n); b.setNewXYZ(n);
//				}
//			}
//		}
//	}
//  }
  public static void hypPerp(double[] ln, double[] pt, double[] perp) {
    double sr=Math.sqrt(-(pt[1]*pt[1]*ln[0]*ln[0])+pt[2]*pt[2]*ln[0]*ln[0]+2*pt[0]*pt[1]*ln[0]*ln[1]-pt[0]*pt[0]*ln[1]*ln[1]+2*pt[0]*pt[2]*ln[0]*ln[2]+pt[0]*pt[0]*ln[2]*ln[2]+(pt[2]*ln[1]+pt[1]*ln[2])*(pt[2]*ln[1]+pt[1]*ln[2]));
	perp[2]=(pt[1]*ln[0]-pt[0]*ln[1])/sr;
	perp[1]=-(pt[2]*ln[0]+pt[0]*ln[2])/sr;
	perp[0]=(pt[2]*ln[1]+pt[1]*ln[2])/sr;
  }
  public static boolean hypParallel(double[] line, double[] point, double[] parallel, boolean bit) {
	  int x=1;
	  if (bit) x=-1;
	  if ((line[1]*line[1])!=(line[2]*line[2]) && line[2]!=0) {
		  double[] inftyVector = {0,0,0};
		  inftyVector[0]=1;
		  inftyVector[1]=(-line[0]*line[1]+x*line[2]*Math.sqrt(line[0]*line[0]+line[1]*line[1]-line[2]*line[2]))
		  		/(line[1]*line[1]-line[2]*line[2]);
		  inftyVector[2]=-line[0]/line[2]*inftyVector[0]-line[1]/line[2]*inftyVector[1];
		  MathEqns.hypCrossProduct(inftyVector,point,parallel);
		  MathEqns.hypNormalize(parallel);
		  return true;
		  // each line (on cup) has two limit vectors of infinite length.
		  // Their unit vectors (on cone) are the two inftyVectors. The
		  // normal vectors to the parallel lines are orthog to pt & infVector.
	  }
	  else return false;
  }
  public static boolean hypLineIntLine(double[] l1, double[] l2, double[] pt) {
    hypCrossProduct(l1,l2,pt);
    hypNormalize(pt);
    if (hypProduct(pt,pt)>=0 || norm(pt)>1e6) return false;
    else return true;
  }
  public static void hypTranslate(double[] ds, double[] v1, double[] v2) {
	// this is the hyperbolic translation from the point ds to the origin.
	// to go backwards, use (-ds[0],-ds[1],ds[2]) instead of ds.
	// v2=v1-ds
	if (ds[0]*ds[0]+ds[1]*ds[1]==0) {
	  for (int i=0;i<3;i++) v2[i]=v1[i];
	  return;
	}
    double[][] dsToOrigin={{(ds[0]*ds[0]*ds[2]+ds[1]*ds[1])/(ds[0]*ds[0]+ds[1]*ds[1]),
	                        (ds[0]*ds[1]*(ds[2]-1))/(ds[0]*ds[0]+ds[1]*ds[1]),-ds[0]},
	                       {(ds[0]*ds[1]*(ds[2]-1))/(ds[0]*ds[0]+ds[1]*ds[1]),
						    (ds[1]*ds[1]*ds[2]+ds[0]*ds[0])/(ds[0]*ds[0]+ds[1]*ds[1]),-ds[1]},
						   {-ds[0],-ds[1],ds[2]}};
	for (int i=0;i<3;i++)
	  v2[i]=v1[0]*dsToOrigin[0][i]+v1[1]*dsToOrigin[1][i]+v1[2]*dsToOrigin[2][i];
  }
  public static void sphTranslate(double[] ds, double[] v1, double[] v2) {
	// this is the spherical translation from the point ds to (0,0,1).
	// v2=v1-ds
	if (ds[0]*ds[0]+ds[1]*ds[1]==0) {
	  for (int i=0;i<3;i++) v2[i]=v1[i];
	  return;
	}
    double[] norm={0,0,0},temp={0,0,1};
	crossProduct(temp,ds,norm);
	double theta=Math.acos(MathEqns.dotProduct(temp,ds));
    double w=Math.cos(theta/2);
    for (int i=0;i<3;i++) norm[i]*=Math.sin(theta/2);
  	v2[0]=(1-2*norm[1]*norm[1]-2*norm[2]*norm[2])*v1[0]+
  	                2*(norm[0]*norm[1]-w*norm[2])*v1[1]+
  				    2*(norm[0]*norm[2]+w*norm[1])*v1[2];
  	v2[1]=			2*(norm[0]*norm[1]+w*norm[2])*v1[0]+
		  (1-2*norm[0]*norm[0]-2*norm[2]*norm[2])*v1[1]+
					2*(norm[1]*norm[2]-w*norm[0])*v1[2];
  	v2[2]=			2*(norm[0]*norm[2]-w*norm[1])*v1[0]+
					2*(norm[1]*norm[2]+w*norm[0])*v1[1]+
		  (1-2*norm[0]*norm[0]-2*norm[1]*norm[1])*v1[2];
  }
  
  public static void makeStandard(double[] vector) {
    if (vector[2]<0 || (vector[2]==0 && vector[1]<0) || 
	    (vector[2]==0 && vector[1]==0 && vector[0]<0))
	  for (int i=0;i<3;i++) vector[i]*=-1;
  }
  public static double acosh(double x) { return Math.log(x+Math.sqrt(x*x-1)); }
  
  public static double eucAngle(double[] a, double[] b, double[] c) {
    return Math.acos(dotProduct(subVec(a,b),subVec(c,b))/norm(subVec(a,b))/norm(subVec(c,b)))/Math.PI*180;
  }
  public static double hypAngle(double[] a, double[] b, double[] c) {
    double[] d={0,0,1};
	hypTranslate(b,a,d);
	for (int i=0;i<3;i++) a[i]=d[i];
	hypTranslate(b,c,d);
	for (int i=0;i<3;i++) c[i]=d[i];
	a[2]=0;
	c[2]=0;
	for (int i=0;i<3;i++) d[i]=0;
	return eucAngle(a,d,c);
  }
  public static double hypDistance(double[] a, double[] b) {
	  double epsilon = 0.000001;
	  double product=MathEqns.hypProduct(a,b);
	  if (Math.abs(a[0]-b[0])+Math.abs(a[1]-b[1])<epsilon) return 0;
	  else if (product<=-1) return acosh(-product);
	  else return 1024;
  }
  public static double sphAngle(double[] a, double[] b, double[] c) {
    double[] d={0,0,0};
	hypTranslate(b,a,d);
	for (int i=0;i<3;i++) a[i]=d[i];
	hypTranslate(b,c,d);
	for (int i=0;i<3;i++) c[i]=d[i];
	a[2]=0;
	c[2]=0;
	for (int i=0;i<3;i++) d[i]=0;
	return eucAngle(a,d,c);
  }
}
