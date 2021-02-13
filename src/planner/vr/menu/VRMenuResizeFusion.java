package planner.vr.menu;
import java.awt.Color;
import java.awt.event.ActionListener;
import multiblock.overhaul.fusion.OverhaulFusionReactor;
import org.lwjgl.opengl.GL11;
import org.lwjgl.openvr.TrackedDevicePose;
import planner.Core;
import planner.FormattedText;
import planner.vr.VRCore;
import planner.vr.VRGUI;
import planner.vr.VRMenu;
import planner.vr.menu.component.VRMenuComponentButton;
import planner.vr.menu.component.VRMenuComponentTextPanel;
public class VRMenuResizeFusion extends VRMenu{
    public VRMenuComponentButton done = add(new VRMenuComponentButton(-.25, 1.75, -1, .5, .125, .1, 0, 0, 0, "Done", true, false));
    private VRMenuComponentTextPanel textPanel = add(new VRMenuComponentTextPanel(.25, 1, 1.2, .5, .25, .05, 0, 180, 0, "Size"));
    private final OverhaulFusionReactor multiblock;
    public VRMenuResizeFusion(VRGUI gui, VRMenu parent, OverhaulFusionReactor multiblock){
        super(gui, parent);
        done.addActionListener((e) -> {
            gui.open(new VRMenuEdit(gui, multiblock).alreadyOpen());
        });
        this.multiblock = multiblock;
        add(new VRMenuComponentPlusButton(-.25, 1.25, -.875, .125, .125, .125, true, (e) -> {
            multiblock.increaseInnerRadius();
            onGUIOpened();
        }));
        add(new VRMenuComponentPlusButton(-.125, 1.25, -.875, .125, .125, .125, true, (e) -> {
            multiblock.increaseCoreSize();
            onGUIOpened();
        }));
        add(new VRMenuComponentPlusButton(0, 1.25, -.875, .125, .125, .125, true, (e) -> {
            multiblock.increaseToroidWidth();
            onGUIOpened();
        }));
        add(new VRMenuComponentPlusButton(.125, 1.25, -.875, .125, .125, .125, true, (e) -> {
            multiblock.increaseLiningThickness();
            onGUIOpened();
        }));
        add(new VRMenuComponentMinusButton(-.25, 1.125, -.875, .125, .125, .125, true, (e) -> {
            multiblock.decreaseInnerRadius();
            onGUIOpened();
        }));
        add(new VRMenuComponentMinusButton(-.125, 1.125, -.875, .125, .125, .125, true, (e) -> {
            multiblock.decreaseCoreSize();
            onGUIOpened();
        }));
        add(new VRMenuComponentMinusButton(0, 1.125, -.875, .125, .125, .125, true, (e) -> {
            multiblock.decreaseToroidWidth();
            onGUIOpened();
        }));
        add(new VRMenuComponentMinusButton(.125, 1.125, -.875, .125, .125, .125, true, (e) -> {
            multiblock.decreaseLiningThickness();
            onGUIOpened();
        }));
    }
    @Override
    public void render(TrackedDevicePose.Buffer tdpb){
        GL11.glPushMatrix();
        GL11.glTranslated(-.5, .5, -.5);
        double size = Math.max(multiblock.getX(), Math.max(multiblock.getY(), multiblock.getZ()));
        GL11.glScaled(1/size, 1/size, 1/size);
        multiblock.draw3D();
        Core.applyColor(Core.theme.getEditorListBorderColor());
        VRCore.drawCubeOutline(-1/16f, -1/16f, -1/16f, multiblock.getX()+1/16f, multiblock.getY()+1/16f, multiblock.getZ()+1/16f, 1/16f);
        GL11.glPopMatrix();
        textPanel.text = new FormattedText("["+multiblock.innerRadius+","+multiblock.coreSize+","+multiblock.toroidWidth+","+multiblock.liningThickness+"]\n"+multiblock.getX()+"x"+multiblock.getY()+"x"+multiblock.getDisplayZ());
        super.render(tdpb);
    }
    public void expand(int x, int y, int z){
        if(x>0)multiblock.expandRight(x);
        if(x<0)multiblock.expandLeft(-x);
        if(y>0)multiblock.expandUp(y);
        if(y<0)multiblock.exandDown(-y);
        if(z>0)multiblock.expandToward(z);
        if(z<0)multiblock.expandAway(-z);
        onGUIOpened();
    }
    private static class VRMenuComponentPlusButton extends VRMenuComponentButton{
        public VRMenuComponentPlusButton(double x, double y, double z, double width, double height, double depth, boolean enabled, ActionListener al){
            super(x, y, z, width, height, depth, 0, 0, 0, "", enabled, false);
            addActionListener(al);
        }
        @Override
        public void renderComponent(TrackedDevicePose.Buffer tdpb){
            Color col = Core.theme.getButtonColor();
            if(enabled){
                if(isPressed)col = col.darker();
                else if(!isDeviceOver.isEmpty())col = col.brighter();
            }else{
                col = col.darker();
            }
            Core.applyColor(col);
            VRCore.drawCubeOutline(0, 0, 0, width, height, depth, .01);//1cm
            GL11.glPushMatrix();
            GL11.glTranslated(width/2, height/2, depth/2);//center
            Core.applyColor(Core.theme.getGreen());
            VRCore.drawCube(-width/4, -.01, -.01, width/4, .01, .01, 0);
            VRCore.drawCube(-.01, -width/4, -.01, .01, width/4, .01, 0);
            VRCore.drawCube(-.01, -.01, -width/4, .01, .01, width/4, 0);
            GL11.glPopMatrix();
        }
    }
    private static class VRMenuComponentMinusButton extends VRMenuComponentButton{
        public VRMenuComponentMinusButton(double x, double y, double z, double width, double height, double depth, boolean enabled, ActionListener al){
            super(x, y, z, width, height, depth, 0, 0, 0, "", enabled, false);
            addActionListener(al);
        }
        @Override
        public void renderComponent(TrackedDevicePose.Buffer tdpb){
            Color col = Core.theme.getButtonColor();
            if(enabled){
                if(isPressed)col = col.darker();
                else if(!isDeviceOver.isEmpty())col = col.brighter();
            }else{
                col = col.darker();
            }
            Core.applyColor(col);
            VRCore.drawCubeOutline(0, 0, 0, width, height, depth, .01);//1cm
            GL11.glPushMatrix();
            GL11.glTranslated(width/2, height/2, depth/2);//center
            Core.applyColor(Core.theme.getRed());
            VRCore.drawCube(-width/4, -.01, -.01, width/4, .01, .01, 0);
            GL11.glPopMatrix();
        }
    }
}