package net.ncplanner.plannerator.planner.vr;
import java.util.HashSet;
import java.util.Set;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.planner.MathUtil;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.openvr.TrackedDevicePose;
public abstract class VRMenuComponent extends VRMenu{
    public Color color = Color.WHITE;
    public Set<Integer> isDeviceOver = new HashSet<>();
    public float width;
    public float height;
    public float depth;
    public float x;
    public float y;
    public float z;
    public float xRot;
    public float yRot;
    public float zRot;
    public String tooltip;
    public VRMenuComponent(float x, float y, float z, float width, float height, float depth, float rx, float ry, float rz){
        super(null, null);
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.xRot = rx;
        this.yRot = ry;
        this.zRot = rz;
    }
    public void draw(Renderer renderer, TrackedDevicePose.Buffer tdpb){
        if(color!=Color.WHITE){
            renderer.setColor(color);
        }
        renderComponent(renderer, tdpb);
        if(color!=Color.WHITE){
            renderer.setWhite();
        }
    }
    public void onAdded(){
        gui = parent.gui;
        for(VRMenuComponent c : components){
            c.onAdded();
        }
    }
    public abstract void renderComponent(Renderer renderer, TrackedDevicePose.Buffer tdpb);
    public VRMenuComponent setColor(Color color){
        this.color = color;
        return this;
    }
    @Override
    public void render(Renderer renderer, TrackedDevicePose.Buffer tdpb, double deltaTime){
        renderer.pushModel(new Matrix4f()
                .translate(x, y, z)
                .rotate((float)MathUtil.toRadians(yRot), 0, 1, 0)
                .rotate((float)MathUtil.toRadians(xRot), 1, 0, 0)
                .rotate((float)MathUtil.toRadians(zRot), 0, 0, 1));
        renderBackground(renderer);
        draw(renderer, tdpb);
        for(VRMenuComponent c : components){
            c.render(renderer, tdpb, deltaTime);
        }
        renderForeground(renderer);
        renderer.popModel();
    }
    public void onDeviceMoved(int device, Matrix4f matrix){
        Vector3f pos = matrix.getTranslation(new Vector3f());
        float x = pos.x;
        float y = pos.y;
        float z = pos.z;
        isDeviceOver.add(device);
        for(VRMenuComponent component : components){
            Vector3f p = MathUtil.convertPointInverted(x, y, z, component.x, component.y, component.z, component.xRot, component.yRot, component.zRot);
            Matrix4f newMatrix = new Matrix4f(matrix);
            newMatrix.setTranslation(p.x, p.y, p.z);
            if(MathUtil.isPointWithinBox(x, y, z, component.x, component.y, component.z, component.width, component.height, component.depth, component.xRot, component.yRot, component.zRot)){
                component.onDeviceMoved(device, newMatrix);
            }else{
                component.onDeviceMovedElsewhere(device, newMatrix);
            }
        }
    }
    public void onDeviceMovedElsewhere(int device, Matrix4f matrix){
        Vector3f pos = matrix.getTranslation(new Vector3f());
        float x = pos.x;
        float y = pos.y;
        float z = pos.z;
        isDeviceOver.remove(device);
        for(VRMenuComponent component : components){
            Vector3f p = MathUtil.convertPointInverted(x, y, z, component.x, component.y, component.z, component.xRot, component.yRot, component.zRot);
            Matrix4f newMatrix = new Matrix4f(matrix);
            newMatrix.setTranslation(p.x, p.y, p.z);
            component.onDeviceMovedElsewhere(device, newMatrix);
        }
    }
    public VRMenuComponent setTooltip(String tooltip){
        this.tooltip = tooltip;
        return this;
    }
    public String getTooltip(int device){
        return tooltip;
    }
}