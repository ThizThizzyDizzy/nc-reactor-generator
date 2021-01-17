package planner.vr;
import java.util.ArrayList;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.openvr.TrackedDevicePose;
public class VRMenu{
    public VRGUI gui;
    public VRMenu parent;
    public ArrayList<VRMenuComponent> components = new ArrayList<>();
    public VRMenu(VRGUI gui, VRMenu parent){
        this.gui = gui;
        this.parent = parent;
    }
    public <V extends VRMenuComponent> V add(V component){
        components.add(component);
        component.gui = gui;
        component.parent = this;
        component.onAdded();
        return component;
    }
    public void tick(){
        for(int i = components.size()-1; i>=0; i--){
            if(i<components.size()) components.get(i).tick();
        }
    }
    public void render(TrackedDevicePose.Buffer tdpb){
        renderBackground();
        for(VRMenuComponent component : components){
            component.render(tdpb);
        }
        renderForeground();
    }
    public void renderBackground(){}
    public void renderForeground(){}
    public void onGUIOpened(){}
    public void onGUIClosed(){}
    public void keyEvent(int device, int button, boolean pressed){
        for(VRMenuComponent c : components){
            if(c.isDeviceOver.contains(device)){
                c.keyEvent(device, button, pressed);
            }
        }
    }
    public void onDeviceMoved(int device, Matrix4f matrix){
        Vector3f pos = matrix.getTranslation(new Vector3f());
        float x = pos.x;
        float y = pos.y;
        float z = pos.z;
        for(VRMenuComponent component : components){
            Vector3f p = VRCore.convertPoint(x, y, z, component.x, component.y, component.z, component.xRot, component.yRot, component.zRot);
            Matrix4f newMatrix = new Matrix4f(matrix);
            newMatrix.setTranslation(p.x, p.y, p.z);
            if(VRCore.isPointWithinBox(x, y, z, component.x, component.y, component.z, component.width, component.height, component.depth, component.xRot, component.yRot, component.zRot)){
                component.onDeviceMoved(device, newMatrix);
            }else{
                component.onDeviceMovedElsewhere(device, newMatrix);
            }
        }
    }
}