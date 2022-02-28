package net.ncplanner.plannerator.planner.vr;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.legacyobj.model.loader.OBJLoader;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.MathUtil;
import org.joml.Matrix4f;
import org.lwjgl.openvr.VR;
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
    public void render(Renderer renderer){
        Matrix4f model = new Matrix4f().scale(1/32f, 1/32f, 1/32f)
                .rotate((float)MathUtil.toRadians(-90), 0, 1, 0)
                .rotate((float)MathUtil.toRadians(125), 0, 0, 1)
                .translate(-6, -6, 0);
        renderer.pushModel(model);
        targetDisplay = tooltip==null?0:1;
        display = MathUtil.getValueBetweenTwoValues(0, display, 1, targetDisplay, snappiness);
        leftGripper = MathUtil.getValueBetweenTwoValues(0, leftGripper, 1, targetLeftGripper, snappiness);
        rightGripper = MathUtil.getValueBetweenTwoValues(0, rightGripper, 1, targetRightGripper, snappiness);
        gaugeAngle = MathUtil.getValueBetweenTwoValues(0, gaugeAngle, 1, targetGaugeAngle, snappiness);
        renderer.setWhite();
        renderer.drawModel(OBJLoader.getModel("/textures/multitool/handle.obj"));
        renderer.pushModel(new Matrix4f().translate(-display, display, 0));
        renderer.drawModel(OBJLoader.getModel("/textures/multitool/display.obj"));
        renderer.popModel();
        renderer.pushModel(new Matrix4f().translate(leftGripper, -leftGripper, 0));
        renderer.drawModel(OBJLoader.getModel("/textures/multitool/left gripper.obj"));
        renderer.popModel();
        renderer.pushModel(new Matrix4f().translate(-rightGripper, rightGripper, 0));
        renderer.drawModel(OBJLoader.getModel("/textures/multitool/right gripper.obj"));
        renderer.popModel();
        renderer.pushModel(new Matrix4f().translate(9.5f, 5.5f, 0).rotate((float)Math.toRadians(gaugeAngle), 0, 0, 1));
        renderer.drawModel(OBJLoader.getModel("/textures/multitool/gauge pointer.obj"));
        renderer.popModel();
        renderer.resetModelMatrix();
        if(tooltip!=null){
            renderer.setColor(Core.theme.getVRMultitoolTextColor());
            String[] txt = tooltip.split("\n");
            float textHeight = 1/5f;
            if(this==VRCore.leftMultitool)renderer.pushModel(new Matrix4f()
                    .translate(4, 10, (.5f-1/128f))
                    .rotate((float)MathUtil.toRadians(-45), 0, 0, 1)
                    .scale(1, -1, 1)
                    .rotate((float)Math.toRadians(180), 0, 1, 0)
                    .translate(-(float)Math.sqrt(5), 0, 0));
            else renderer.pushModel(new Matrix4f()
                    .translate(4, 10, (.5f-1/128f))
                    .rotate((float)MathUtil.toRadians(-45), 0, 0, 1)
                    .scale(1, -1, 1));
            
            if(this==VRCore.leftMultitool){
            }
            int i = 0;
            for(String s : txt){
                do{
                    s = renderer.drawTextWithWordWrap(0, textHeight*i, (float)Math.sqrt(5), textHeight*(i+1), s);
                    i++;
                }while(s!=null&&!s.isEmpty());
            }
            renderer.popModel();
            renderer.setWhite();
        }
        renderer.resetModelMatrix();
        renderer.setColor(.75f, 1, 1, .125f);
        renderer.fillRect(7, 4, 12, 7);
        renderer.fillRect(8, 7, 11, 8);
        renderer.fillRect(8, 3, 11, 4);
        renderer.popModel();
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