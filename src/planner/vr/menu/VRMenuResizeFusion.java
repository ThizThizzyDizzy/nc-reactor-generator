package planner.vr.menu;
import multiblock.BoundingBox;
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
import simplelibrary.image.Color;
public class VRMenuResizeFusion extends VRMenu{
    public VRMenuComponentButton done = add(new VRMenuComponentButton(-.25, 1.75, -1, .5, .125, .1, 0, 0, 0, "Done", true, false));
    private VRMenuComponentTextPanel textPanel = add(new VRMenuComponentTextPanel(.25, 1, 1.2, .5, .25, .05, 0, 180, 0, "Size"));
    private final OverhaulFusionReactor multiblock;
    public VRMenuResizeFusion(VRGUI gui, VRMenu parent, OverhaulFusionReactor multiblock){
        super(gui, parent);
        done.setTooltip("Finish resizing and return to the editor screen");
        done.addActionListener(() -> {
            gui.open(new VRMenuEdit(gui, multiblock).alreadyOpen());
        });
        this.multiblock = multiblock;
        add(new VRMenuComponentPlusButton(-.25, 1.25, -.875, .125, .125, .125, true, () -> {
            multiblock.increaseInnerRadius();
            onGUIOpened();
        }).setTooltip("Increase Inner Radius"));
        add(new VRMenuComponentPlusButton(-.125, 1.25, -.875, .125, .125, .125, true, () -> {
            multiblock.increaseCoreSize();
            onGUIOpened();
        }).setTooltip("Increase Core Size"));
        add(new VRMenuComponentPlusButton(0, 1.25, -.875, .125, .125, .125, true, () -> {
            multiblock.increaseToroidWidth();
            onGUIOpened();
        }).setTooltip("Increase Toroid Width"));
        add(new VRMenuComponentPlusButton(.125, 1.25, -.875, .125, .125, .125, true, () -> {
            multiblock.increaseLiningThickness();
            onGUIOpened();
        }).setTooltip("Increase Lining Thickness"));
        add(new VRMenuComponentMinusButton(-.25, 1.125, -.875, .125, .125, .125, true, () -> {
            multiblock.decreaseInnerRadius();
            onGUIOpened();
        }).setTooltip("Decrease Inner Radius"));
        add(new VRMenuComponentMinusButton(-.125, 1.125, -.875, .125, .125, .125, true, () -> {
            multiblock.decreaseCoreSize();
            onGUIOpened();
        }).setTooltip("Decrase Core Size"));
        add(new VRMenuComponentMinusButton(0, 1.125, -.875, .125, .125, .125, true, () -> {
            multiblock.decreaseToroidWidth();
            onGUIOpened();
        }).setTooltip("Decrease Toroid Width"));
        add(new VRMenuComponentMinusButton(.125, 1.125, -.875, .125, .125, .125, true, () -> {
            multiblock.decreaseLiningThickness();
            onGUIOpened();
        }).setTooltip("Decrease Lining Thickness"));
    }
    @Override
    public void render(TrackedDevicePose.Buffer tdpb){
        GL11.glPushMatrix();
        GL11.glTranslated(-.5, .5, -.5);
        BoundingBox bbox = multiblock.getBoundingBox();
        double size = Math.max(bbox.getWidth(), Math.max(bbox.getHeight(), bbox.getDepth()));
        GL11.glScaled(1/size, 1/size, 1/size);
        multiblock.draw3D();
        Core.applyColor(Core.theme.get3DMultiblockOutlineColor());
        VRCore.drawCubeOutline(-1/16f, -1/16f, -1/16f, bbox.getWidth()+1/16f, bbox.getHeight()+1/16f, bbox.getDepth()+1/16f, 1/16f);//TODO perhaps individual block grids?
        GL11.glPopMatrix();
        textPanel.text = new FormattedText("["+multiblock.innerRadius+","+multiblock.coreSize+","+multiblock.toroidWidth+","+multiblock.liningThickness+"]\n"+bbox.getWidth()+"x"+bbox.getHeight()+"x"+bbox.getDepth());
        super.render(tdpb);
    }
    private static class VRMenuComponentPlusButton extends VRMenuComponentButton{
        public VRMenuComponentPlusButton(double x, double y, double z, double width, double height, double depth, boolean enabled, Runnable al){
            super(x, y, z, width, height, depth, 0, 0, 0, "", enabled, false);
            addActionListener(al);
        }
        @Override
        public void renderComponent(TrackedDevicePose.Buffer tdpb){
            Color col = Core.theme.getComponentColor(Core.getThemeIndex(this));
            if(enabled){
                if(isPressed)col = Core.theme.getComponentPressedColor(Core.getThemeIndex(this));
                else if(!isDeviceOver.isEmpty())col = Core.theme.getComponentMouseoverColor(Core.getThemeIndex(this));
            }else{
                col = Core.theme.getComponentDisabledColor(Core.getThemeIndex(this));
            }
            Core.applyColor(col);
            VRCore.drawCubeOutline(0, 0, 0, width, height, depth, .01);//1cm
            GL11.glPushMatrix();
            GL11.glTranslated(width/2, height/2, depth/2);//center
            Core.applyColor(Core.theme.getAddButtonTextColor());
            VRCore.drawCube(-width/4, -.01, -.01, width/4, .01, .01, 0);
            VRCore.drawCube(-.01, -width/4, -.01, .01, width/4, .01, 0);
            VRCore.drawCube(-.01, -.01, -width/4, .01, .01, width/4, 0);
            GL11.glPopMatrix();
        }
    }
    private static class VRMenuComponentMinusButton extends VRMenuComponentButton{
        public VRMenuComponentMinusButton(double x, double y, double z, double width, double height, double depth, boolean enabled, Runnable al){
            super(x, y, z, width, height, depth, 0, 0, 0, "", enabled, false);
            addActionListener(al);
        }
        @Override
        public void renderComponent(TrackedDevicePose.Buffer tdpb){
            Color col = Core.theme.getComponentColor(Core.getThemeIndex(this));
            if(enabled){
                if(isPressed)col = Core.theme.getComponentPressedColor(Core.getThemeIndex(this));
                else if(!isDeviceOver.isEmpty())col = Core.theme.getComponentMouseoverColor(Core.getThemeIndex(this));
            }else{
                col = Core.theme.getComponentDisabledColor(Core.getThemeIndex(this));
            }
            Core.applyColor(col);
            VRCore.drawCubeOutline(0, 0, 0, width, height, depth, .01);//1cm
            GL11.glPushMatrix();
            GL11.glTranslated(width/2, height/2, depth/2);//center
            Core.applyColor(Core.theme.getDeleteButtonTextColor());
            VRCore.drawCube(-width/4, -.01, -.01, width/4, .01, .01, 0);
            GL11.glPopMatrix();
        }
    }
}