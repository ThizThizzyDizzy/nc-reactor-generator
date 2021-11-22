package net.ncplanner.plannerator.planner.vr.menu;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.multiblock.BoundingBox;
import net.ncplanner.plannerator.multiblock.overhaul.fusion.OverhaulFusionReactor;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.FormattedText;
import net.ncplanner.plannerator.planner.vr.VRGUI;
import net.ncplanner.plannerator.planner.vr.VRMenu;
import net.ncplanner.plannerator.planner.vr.menu.component.VRMenuComponentButton;
import net.ncplanner.plannerator.planner.vr.menu.component.VRMenuComponentTextPanel;
import org.joml.Matrix4f;
import org.lwjgl.openvr.TrackedDevicePose;
public class VRMenuResizeFusion extends VRMenu{
    public VRMenuComponentButton done = add(new VRMenuComponentButton(-.25f, 1.75f, -1, .5f, .125f, .1f, 0, 0, 0, "Done", true, false));
    private VRMenuComponentTextPanel textPanel = add(new VRMenuComponentTextPanel(.25f, 1, 1.2f, .5f, .25f, .05f, 0, 180, 0, "Size"));
    private final OverhaulFusionReactor multiblock;
    public VRMenuResizeFusion(VRGUI gui, VRMenu parent, OverhaulFusionReactor multiblock){
        super(gui, parent);
        done.setTooltip("Finish resizing and return to the editor screen");
        done.addActionListener(() -> {
            gui.open(new VRMenuEdit(gui, multiblock).alreadyOpen());
        });
        this.multiblock = multiblock;
        add(new VRMenuComponentPlusButton(-.25f, 1.25f, -.875f, .125f, .125f, .125f, true, () -> {
            multiblock.increaseInnerRadius();
            onOpened();
        }).setTooltip("Increase Inner Radius"));
        add(new VRMenuComponentPlusButton(-.125f, 1.25f, -.875f, .125f, .125f, .125f, true, () -> {
            multiblock.increaseCoreSize();
            onOpened();
        }).setTooltip("Increase Core Size"));
        add(new VRMenuComponentPlusButton(0, 1.25f, -.875f, .125f, .125f, .125f, true, () -> {
            multiblock.increaseToroidWidth();
            onOpened();
        }).setTooltip("Increase Toroid Width"));
        add(new VRMenuComponentPlusButton(.125f, 1.25f, -.875f, .125f, .125f, .125f, true, () -> {
            multiblock.increaseLiningThickness();
            onOpened();
        }).setTooltip("Increase Lining Thickness"));
        add(new VRMenuComponentMinusButton(-.25f, 1.125f, -.875f, .125f, .125f, .125f, true, () -> {
            multiblock.decreaseInnerRadius();
            onOpened();
        }).setTooltip("Decrease Inner Radius"));
        add(new VRMenuComponentMinusButton(-.125f, 1.125f, -.875f, .125f, .125f, .125f, true, () -> {
            multiblock.decreaseCoreSize();
            onOpened();
        }).setTooltip("Decrase Core Size"));
        add(new VRMenuComponentMinusButton(0, 1.125f, -.875f, .125f, .125f, .125f, true, () -> {
            multiblock.decreaseToroidWidth();
            onOpened();
        }).setTooltip("Decrease Toroid Width"));
        add(new VRMenuComponentMinusButton(.125f, 1.125f, -.875f, .125f, .125f, .125f, true, () -> {
            multiblock.decreaseLiningThickness();
            onOpened();
        }).setTooltip("Decrease Lining Thickness"));
    }
    @Override
    public void render(Renderer renderer, TrackedDevicePose.Buffer tdpb, double deltaTime){
        BoundingBox bbox = multiblock.getBoundingBox();
        float size = Math.max(bbox.getWidth(), Math.max(bbox.getHeight(), bbox.getDepth()));
        renderer.setModel(new Matrix4f().translate(-.5f, .5f, -.5f).scale(1/size, 1/size, 1/size));
        multiblock.draw3D();
        renderer.setColor(Core.theme.get3DMultiblockOutlineColor());
        renderer.drawCubeOutline(-1/16f, -1/16f, -1/16f, bbox.getWidth()+1/16f, bbox.getHeight()+1/16f, bbox.getDepth()+1/16f, 1/16f);//TODO perhaps individual block grids?
        renderer.resetModelMatrix();
        textPanel.text = new FormattedText("["+multiblock.innerRadius+","+multiblock.coreSize+","+multiblock.toroidWidth+","+multiblock.liningThickness+"]\n"+bbox.getWidth()+"x"+bbox.getHeight()+"x"+bbox.getDepth());
        super.render(renderer, tdpb, deltaTime);
    }
    private static class VRMenuComponentPlusButton extends VRMenuComponentButton{
        public VRMenuComponentPlusButton(float x, float y, float z, float width, float height, float depth, boolean enabled, Runnable al){
            super(x, y, z, width, height, depth, 0, 0, 0, "", enabled, false);
            addActionListener(al);
        }
        @Override
        public void renderComponent(Renderer renderer, TrackedDevicePose.Buffer tdpb){
            Color col = Core.theme.getComponentColor(Core.getThemeIndex(this));
            if(enabled){
                if(isPressed)col = Core.theme.getComponentPressedColor(Core.getThemeIndex(this));
                else if(!isDeviceOver.isEmpty())col = Core.theme.getComponentMouseoverColor(Core.getThemeIndex(this));
            }else{
                col = Core.theme.getComponentDisabledColor(Core.getThemeIndex(this));
            }
            renderer.setColor(col);
            renderer.drawCubeOutline(0, 0, 0, width, height, depth, .01f);//1cm
            renderer.setModel(new Matrix4f().translate(width/2, height/2, depth/2));
            renderer.setColor(Core.theme.getAddButtonTextColor());
            renderer.drawCube(-width/4, -.01f, -.01f, width/4, .01f, .01f, null);
            renderer.drawCube(-.01f, -width/4, -.01f, .01f, width/4, .01f, null);
            renderer.drawCube(-.01f, -.01f, -width/4, .01f, .01f, width/4, null);
            renderer.resetModelMatrix();
        }
    }
    private static class VRMenuComponentMinusButton extends VRMenuComponentButton{
        public VRMenuComponentMinusButton(float x, float y, float z, float width, float height, float depth, boolean enabled, Runnable al){
            super(x, y, z, width, height, depth, 0, 0, 0, "", enabled, false);
            addActionListener(al);
        }
        @Override
        public void renderComponent(Renderer renderer, TrackedDevicePose.Buffer tdpb){
            Color col = Core.theme.getComponentColor(Core.getThemeIndex(this));
            if(enabled){
                if(isPressed)col = Core.theme.getComponentPressedColor(Core.getThemeIndex(this));
                else if(!isDeviceOver.isEmpty())col = Core.theme.getComponentMouseoverColor(Core.getThemeIndex(this));
            }else{
                col = Core.theme.getComponentDisabledColor(Core.getThemeIndex(this));
            }
            renderer.setColor(col);
            renderer.drawCubeOutline(0, 0, 0, width, height, depth, .01f);//1cm
            renderer.setModel(new Matrix4f().setTranslation(width/2, height/2, depth/2));
            renderer.setColor(Core.theme.getDeleteButtonTextColor());
            renderer.drawCube(-width/4, -.01f, -.01f, width/4, .01f, .01f, null);
            renderer.resetModelMatrix();
        }
    }
}