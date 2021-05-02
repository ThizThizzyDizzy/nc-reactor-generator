package planner.vr;
import simplelibrary.image.Color;
import java.util.HashSet;
import java.util.Set;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.openvr.TrackedDevicePose;
public abstract class VRMenuComponent extends VRMenu{
    public Color color = Color.WHITE;
    public Set<Integer> isDeviceOver = new HashSet<>();
    public double width;
    public double height;
    public double depth;
    public double x;
    public double y;
    public double z;
    public double xRot;
    public double yRot;
    public double zRot;
    public String tooltip;
    public VRMenuComponent(double x, double y, double z, double width, double height, double depth, double rx, double ry, double rz){
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
    public void draw(TrackedDevicePose.Buffer tdpb){
        if(color!=Color.WHITE){
            GL11.glColor3f(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f);
        }
        renderComponent(tdpb);
        if(color!=Color.WHITE){
            GL11.glColor3f(1, 1, 1);
        }
    }
    public void onAdded(){
        gui = parent.gui;
        for(VRMenuComponent c : components){
            c.onAdded();
        }
    }
    public abstract void renderComponent(TrackedDevicePose.Buffer tdpb);
    public VRMenuComponent setColor(Color color){
        this.color = color;
        return this;
    }
    @Override
    public void render(TrackedDevicePose.Buffer tdpb){
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glRotated(yRot, 0, 1, 0);
        GL11.glRotated(xRot, 1, 0, 0);
        GL11.glRotated(zRot, 0, 0, 1);
        renderBackground();
        draw(tdpb);
        for(VRMenuComponent c : components){
            c.render(tdpb);
        }
        renderForeground();
        GL11.glPopMatrix();
    }
    public void onDeviceMoved(int device, Matrix4f matrix){
        Vector3f pos = matrix.getTranslation(new Vector3f());
        float x = pos.x;
        float y = pos.y;
        float z = pos.z;
        isDeviceOver.add(device);
        for(VRMenuComponent component : components){
            Vector3f p = VRCore.convertPointInverted(x, y, z, component.x, component.y, component.z, component.xRot, component.yRot, component.zRot);
            Matrix4f newMatrix = new Matrix4f(matrix);
            newMatrix.setTranslation(p.x, p.y, p.z);
            if(VRCore.isPointWithinBox(x, y, z, component.x, component.y, component.z, component.width, component.height, component.depth, component.xRot, component.yRot, component.zRot)){
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
            Vector3f p = VRCore.convertPointInverted(x, y, z, component.x, component.y, component.z, component.xRot, component.yRot, component.zRot);
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