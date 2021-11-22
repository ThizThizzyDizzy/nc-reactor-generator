package net.ncplanner.plannerator.planner.vr;
import java.nio.IntBuffer;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.planner.MathUtil;
import org.joml.Matrix4f;
import org.lwjgl.openvr.HmdMatrix34;
import org.lwjgl.openvr.TrackedDevicePose;
import static org.lwjgl.openvr.VR.ETrackedControllerRole_TrackedControllerRole_LeftHand;
import static org.lwjgl.openvr.VR.ETrackedControllerRole_TrackedControllerRole_RightHand;
import static org.lwjgl.openvr.VR.ETrackedDeviceProperty_Prop_ControllerRoleHint_Int32;
import org.lwjgl.openvr.VRSystem;
public class VRGUI{
    public ArrayList<ArrayList<Integer>> buttonsWereDown = new ArrayList<>();
    public VRMenu menu;
    public <V extends VRMenu> V open(VRMenu menu){
        if(this.menu!=null){
            this.menu.onClosed();
        }
        this.menu = menu;
        if(menu!=null)menu.onOpened();
        return (V)menu;
    }
    public synchronized void render(Renderer renderer, TrackedDevicePose.Buffer tdpb, double deltaTime){
        for(int i = 0; i<tdpb.limit(); i++){
            TrackedDevicePose tdp = tdpb.get(i);
            if(tdp.bDeviceIsConnected()&&tdp.bPoseIsValid()){
                while(buttonsWereDown.size()<=i)buttonsWereDown.add(new ArrayList<>());
                HmdMatrix34 m = tdp.mDeviceToAbsoluteTracking();
                onDeviceMoved(i, new Matrix4f(MathUtil.convertHmdMatrix(m)).mul(Multitool.editOffsetmatrix));
            }
        }
        if(menu!=null)menu.render(renderer, tdpb, deltaTime);
    }
    public void onKeyEvent(int device, int button, boolean pressed){
        IntBuffer pError = IntBuffer.allocate(1);
        int role = VRSystem.VRSystem_GetInt32TrackedDeviceProperty(device, ETrackedDeviceProperty_Prop_ControllerRoleHint_Int32, pError);
        if(role==ETrackedControllerRole_TrackedControllerRole_LeftHand||role==ETrackedControllerRole_TrackedControllerRole_RightHand){
            while(buttonsWereDown.size()<=device)buttonsWereDown.add(new ArrayList<>());
            if(pressed)buttonsWereDown.get(device).add((Integer)button);
            else buttonsWereDown.get(device).remove((Integer)button);
            if(menu!=null)menu.keyEvent(device, button, pressed);
            if(role==ETrackedControllerRole_TrackedControllerRole_LeftHand)VRCore.leftMultitool.keyEvent(button, pressed);
            if(role==ETrackedControllerRole_TrackedControllerRole_RightHand)VRCore.rightMultitool.keyEvent(button, pressed);
        }
    }
    private void onDeviceMoved(int device, Matrix4f matrix){
        if(menu!=null)menu.onDeviceMoved(device, matrix);
    }
}