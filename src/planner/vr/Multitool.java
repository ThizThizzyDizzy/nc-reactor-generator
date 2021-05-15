package planner.vr;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.openvr.VR;
import planner.Core;
import simplelibrary.opengl.ModelStash;
import static simplelibrary.opengl.Renderer3D.*;
public class Multitool{
    public static Matrix4f editOffsetmatrix = new Matrix4f();
    static{
        Matrix4f rot = new Matrix4f();
        rot.setTranslation(0, 0, -.3375f);
        editOffsetmatrix.setRotationXYZ((float)Math.toRadians(10), 0, 0);
        editOffsetmatrix.mul(rot);
    }
    public int device;
    public float display = 0;
    public float leftGripper = 0;
    public float rightGripper = 0;
    public float gaugeAngle = 0;
    public float targetDisplay = 0;
    public float targetLeftGripper = 0;
    public float targetRightGripper = 0;
    public float targetGaugeAngle = 0;
    public float snappiness = .25f;
    private boolean a,b,trigger,trackpad;
    public String tooltip;
    public Multitool(){}
    public void render(){
        GL11.glPushMatrix();
        GL11.glScalef(1/32f, 1/32f, 1/32f);
        GL11.glRotated(-90, 0, 1, 0);
        GL11.glRotated(125, 0, 0, 1);
        GL11.glTranslated(-6, -6, 0);
        targetDisplay = tooltip==null?0:1;
        display = Core.getValueBetweenTwoValues(0, display, 1, targetDisplay, snappiness);
        leftGripper = Core.getValueBetweenTwoValues(0, leftGripper, 1, targetLeftGripper, snappiness);
        rightGripper = Core.getValueBetweenTwoValues(0, rightGripper, 1, targetRightGripper, snappiness);
        gaugeAngle = Core.getValueBetweenTwoValues(0, gaugeAngle, 1, targetGaugeAngle, snappiness);
        GL11.glColor3f(1, 1, 1);
        drawModel(ModelStash.instance.getModel("/textures/multitool/handle.obj"));
        GL11.glTranslated(-display, display, 0);
        drawModel(ModelStash.instance.getModel("/textures/multitool/display.obj"));
        GL11.glTranslated(display, -display, 0);
        GL11.glTranslated(leftGripper, -leftGripper, 0);
        drawModel(ModelStash.instance.getModel("/textures/multitool/left gripper.obj"));
        GL11.glTranslated(-leftGripper, leftGripper, 0);
        GL11.glTranslated(-rightGripper, rightGripper, 0);
        drawModel(ModelStash.instance.getModel("/textures/multitool/right gripper.obj"));
        GL11.glTranslated(rightGripper, -rightGripper, 0);
        GL11.glTranslated(9.5, 5.5, 0);
        GL11.glRotated(gaugeAngle, 0, 0, 1);
        drawModel(ModelStash.instance.getModel("/textures/multitool/gauge pointer.obj"));
        GL11.glRotated(-gaugeAngle, 0, 0, 1);
        GL11.glTranslated(-9.5, -5.5, 0);
        if(tooltip!=null){
            GL11.glPushMatrix();
            GL11.glTranslated(4, 10, (.5-1/128f));
            GL11.glRotated(-45, 0, 0, 1);
            Core.applyColor(Core.theme.getVRMultitoolTextColor());
            String[] txt = tooltip.split("\n");
            double textHeight = 1/5f;
            GL11.glScaled(1, -1, 1);
            if(this==VRCore.leftMultitool){
                GL11.glRotated(180, 0, 1, 0);
                GL11.glTranslated(-Math.sqrt(5), 0, 0);
            }
            int i = 0;
            for(String s : txt){
                do{
                    s = Core.drawTextWithWordWrap(0, textHeight*i, Math.sqrt(5), textHeight*(i+1), s);
                    i++;
                }while(s!=null&&!s.isEmpty());
            }
            GL11.glColor3f(1, 1, 1);
            GL11.glPopMatrix();
        }
        GL11.glColor4d(.75, 1, 1, .125);
        GL11.glTranslated(0, 0, 1);
        GL11.glScaled(1, -1, 1);
        drawRect(7, 4, 12, 7, 0);
        drawRect(8, 7, 11, 8, 0);
        drawRect(8, 3, 11, 4, 0);
        GL11.glScaled(1, -1, 1);
        GL11.glTranslated(0, 0, .5);
//        drawModel(ModelStash.instance.getModel("/textures/multitool/gauge face.obj"));
        GL11.glPopMatrix();
    }
    public void keyEvent(int button, boolean pressed){
        if(button==VR.EVRButtonId_k_EButton_IndexController_A)a = pressed;
        if(button==VR.EVRButtonId_k_EButton_IndexController_B)b = pressed;
        if(button==VR.EVRButtonId_k_EButton_SteamVR_Touchpad)trackpad = pressed;
        if(button==VR.EVRButtonId_k_EButton_SteamVR_Trigger)trigger = pressed;
        targetLeftGripper = targetRightGripper = trigger?1:0;
        float ang = 0;
        if(a&&b)ang = 90;
        else if(a)ang = 135;
        else if(b)ang = 45;
        targetGaugeAngle = (trackpad?-1:1)*ang+(trackpad?-45:0);
    }
}