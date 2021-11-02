package net.ncplanner.plannerator.planner;
import org.joml.Matrix4f;
import org.joml.Matrix4x3f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.openvr.HmdMatrix34;
import org.lwjgl.openvr.HmdMatrix44;
/**
 * An abstraction layer for math operations between java and other platforms.
 * Also provides extra functionality used within the planner
 * @author Thiz
 */
public class MathUtil{
    public static String percent(double n, int digits){
        double fac = pow(10, digits);
        double d = (round(n*fac*100)/(double)round(fac));
        return (digits==0?round(d):d)+"%";
    }
    public static String round(double n, int digits){
        double fac = pow(10, digits);
        double d = round(n*fac)/(double)round(fac);
        return (digits==0?round(d):d)+"";
    }
    public static double pow(double a, double b){
        return Math.pow(a, b);
    }
    public static double exp(double a){
        return Math.exp(a);
    }
    public static long round(double d){
        return Math.round(d);
    }
    public static int round(float a){
        return Math.round(a);
    }
    public static double max(double a, double b){
        return Math.max(a, b);
    }
    public static float max(float a, float b){
        return Math.max(a, b);
    }
    public static int max(int a, int b){
        return Math.max(a, b);
    }
    public static long max(long a, long b){
        return Math.max(a, b);
    }
    public static double min(double a, double b){
        return Math.min(a, b);
    }
    public static float min(float a, float b){
        return Math.min(a, b);
    }
    public static int min(int a, int b){
        return Math.min(a, b);
    }
    public static long min(long a, long b){
        return Math.min(a, b);
    }
    public static double pi(){
        return Math.PI;
    }
    public static double toRadians(double degrees){
        return Math.toRadians(degrees);
    }
    public static double toDegrees(double radians){
        return Math.toDegrees(radians);
    }
    public static double sin(double a){
        return Math.sin(a);
    }
    public static double cos(double a){
        return Math.cos(a);
    }
    public static double log(double a){
        return Math.log(a);
    }
    public static int logBase(int base, int n){
        return (int)(log(n)/log(base));
    }
    public static double getValueBetweenTwoValues(double pos1, double val1, double pos2, double val2, double pos){
        if(pos1>pos2){
            return getValueBetweenTwoValues(pos2, val2, pos1, val1, pos);
        }
        double posDiff = pos2-pos1;
        double percent = pos/posDiff;
        double valDiff = val2-val1;
        return percent*valDiff+val1;
    }
    public static float getValueBetweenTwoValues(float pos1, float val1, float pos2, float val2, float pos){
        if(pos1>pos2){
            return getValueBetweenTwoValues(pos2, val2, pos1, val1, pos);
        }
        float posDiff = pos2-pos1;
        float percent = pos/posDiff;
        float valDiff = val2-val1;
        return percent*valDiff+val1;
    }
    public static double[] rotatePoint(double pointX, double pointY, double degrees, double originX, double originY){
        double rX = pointX-originX, rY = pointY-originY;//Find relative coordinates; easier to rotate around the origin (0, 0) than any other point.
        double rad = degrees*(Math.PI/180);//Angle in radians
        double sin = Math.sin(rad), cos = Math.cos(rad);//Compute the sine and cosine, only two trig operations we need
        //2x2 Rotation matrix:  [Cos(A), -Sin(A)]
        //                      [Sin(A), Cos(A)]
        //Then, with matrix multiplication, our point becomes:
        return new double[]{cos*rX+sin*rY+originX, cos*rY-sin*rX+originY};//Matrix multiplication on the relative coordinates; add origin coords back in before return.
    }
    public static float[] rotatePoint(float pointX, float pointY, float degrees, float originX, float originY){
        float rX = pointX-originX, rY = pointY-originY;//Find relative coordinates; easier to rotate around the origin (0, 0) than any other point.
        double rad = degrees*(Math.PI/180);//Angle in radians
        double sin = Math.sin(rad), cos = Math.cos(rad);//Compute the sine and cosine, only two trig operations we need
        //2x2 Rotation matrix:  [Cos(A), -Sin(A)]
        //                      [Sin(A), Cos(A)]
        //Then, with matrix multiplication, our point becomes:
        return new float[]{(float)(cos*rX+sin*rY+originX), (float)(cos*rY-sin*rX+originY)};//Matrix multiplication on the relative coordinates; add origin coords back in before return.
    }
    public static boolean isPointWithinBox(double x, double y, double z, double x0, double y0, double z0, double width, double height, double depth, double xRot, double yRot, double zRot){
        Vector3f p = convertPointInverted(x, y, z, x0, y0, z0, xRot, yRot, zRot);
        return p.x>0&&p.y>0&&p.z>0&&p.x<width&&p.y<height&&p.z<depth;
    }
    public static double distance(Vector3f v1, Vector3d v2){//I know one's float and one's double... don't worry about it
        return Math.sqrt(Math.pow(v1.x-v2.x, 2)+Math.pow(v1.y-v2.y, 2)+Math.pow(v1.z-v2.z, 2));
    }
    //VR
    public static Matrix4f convertHmdMatrix(HmdMatrix44 m){
        return new Matrix4f(m.m(0), m.m(1), m.m(2), m.m(3), m.m(4), m.m(5), m.m(6), m.m(7), m.m(8), m.m(9), m.m(10), m.m(11), m.m(12), m.m(13), m.m(14), m.m(15));//no convert
    }
    public static Matrix4x3f convertHmdMatrix(HmdMatrix34 m){
        return new Matrix4x3f(m.m(0), m.m(4), m.m(8), m.m(1), m.m(5), m.m(9), m.m(2), m.m(6), m.m(10), m.m(3), m.m(7), m.m(11));//converts to column-major
//        return new Matrix4x3f(m.m(0), m.m(1), m.m(2), m.m(3), m.m(4), m.m(5), m.m(6), m.m(7), m.m(8), m.m(9), m.m(10), m.m(11));//no convert
    }
    public static Vector3f convertPoint(double x, double y, double z, double x0, double y0, double z0, double xRot, double yRot, double zRot){
        x+=x0;
        y+=y0;
        z+=z0;
        double[] xy = rotatePoint(x, y, zRot, x0, y0);
        x = xy[0];
        y = xy[1];
        double[] yz = rotatePoint(y, z, xRot, y0, z0);
        y = yz[0];
        z = yz[1];
        double[] xz = rotatePoint(x, z, yRot, x0, z0);
        x = xz[0];
        z = xz[1];
        return new Vector3f((float)x, (float)y, (float)z);
    }
    public static Vector3f convertPointInverted(double x, double y, double z, double x0, double y0, double z0, double xRot, double yRot, double zRot){
        double[] xz = rotatePoint(x, z, -yRot, x0, z0);
        x = xz[0];
        z = xz[1];
        double[] yz = rotatePoint(y, z, -xRot, y0, z0);
        y = yz[0];
        z = yz[1];
        double[] xy = rotatePoint(x, y, -zRot, x0, y0);
        x = xy[0];
        y = xy[1];
        x-=x0;
        y-=y0;
        z-=z0;
        return new Vector3f((float)x, (float)y, (float)z);
    }
    public static long nanoTime(){
        return System.nanoTime();
    }
    public static boolean isPrime(int n){
        if(n<=1)return false;for(int i = 2; i<n; i++){
            if(n%i==0)return false;
        }
        return true;
    }
    public static int nextPrime(int n, int step){
        if(n<=1)return 2;
        if(isPrime(n+step))return n+step;
        return nextPrime(n+step, step);
    }
}