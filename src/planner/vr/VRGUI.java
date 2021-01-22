package planner.vr;
import java.util.ArrayList;
import org.joml.Matrix4f;
import org.lwjgl.openvr.HmdMatrix34;
import org.lwjgl.openvr.TrackedDevicePose;
import simplelibrary.Sys;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorLevel;
public class VRGUI{
    public ArrayList<ArrayList<Integer>> buttonsWereDown = new ArrayList<>();
    public VRMenu menu;
    public <V extends VRMenu> V open(VRMenu menu){
        if(this.menu!=null){
            this.menu.onGUIClosed();
        }
        this.menu = menu;
        if(menu!=null)menu.onGUIOpened();
        return (V)menu;
    }
    public synchronized void render(TrackedDevicePose.Buffer tdpb){
        for(int i = 0; i<tdpb.limit(); i++){
            TrackedDevicePose tdp = tdpb.get(i);
            if(tdp.bDeviceIsConnected()&&tdp.bPoseIsValid()){
                while(buttonsWereDown.size()<=i)buttonsWereDown.add(new ArrayList<>());
                HmdMatrix34 m = tdp.mDeviceToAbsoluteTracking();
                onDeviceMoved(i, new Matrix4f(VRCore.convert(m)));
            }
        }
        if(menu!=null)menu.render(tdpb);
    }
    public void onKeyEvent(int device, int button, boolean pressed){
        while(buttonsWereDown.size()<=device)buttonsWereDown.add(new ArrayList<>());
        if(pressed)buttonsWereDown.get(device).add((Integer)button);
        else buttonsWereDown.get(device).remove((Integer)button);
        if(menu!=null)menu.keyEvent(device, button, pressed);
    }
    public synchronized void tick(){
        if(menu!=null){
            try{
                menu.tick();
            }catch(Throwable throwable){
                Sys.error(ErrorLevel.severe, "Could not tick VRGUI!", new RuntimeException(throwable), ErrorCategory.other);
            }
        }
    }
    private void onDeviceMoved(int device, Matrix4f matrix){
        if(menu!=null)menu.onDeviceMoved(device, matrix);
    }
}