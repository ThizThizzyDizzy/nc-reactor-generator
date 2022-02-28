package net.ncplanner.plannerator.planner.vr.menu;
import java.util.function.Consumer;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.multiblock.CuboidalMultiblock;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.FormattedText;
import net.ncplanner.plannerator.planner.vr.VRGUI;
import net.ncplanner.plannerator.planner.vr.VRMenu;
import net.ncplanner.plannerator.planner.vr.menu.component.VRMenuComponentButton;
import net.ncplanner.plannerator.planner.vr.menu.component.VRMenuComponentTextPanel;
import org.joml.Matrix4f;
import org.lwjgl.openvr.TrackedDevicePose;
public class VRMenuResize extends VRMenu{//TODO center the multiblock
    public VRMenuComponentButton done = add(new VRMenuComponentButton(-.25f, 1.75f, -1, .5f, .125f, .1f, 0, 0, 0, "Done", true, false));
    private VRMenuComponentTextPanel textPanel = add(new VRMenuComponentTextPanel(.25f, 1, 1.2f, .5f, .25f, .05f, 0, 180, 0, "Size"));
    private final CuboidalMultiblock multiblock;
    private boolean refreshNeeded;
    public VRMenuResize(VRGUI gui, VRMenu parent, CuboidalMultiblock multiblock){
        super(gui, parent);
        done.setTooltip("Finish resizing and return to the editor screen");
        done.addActionListener(() -> {
            gui.open(new VRMenuEdit(gui, multiblock).alreadyOpen());
        });
        this.multiblock = multiblock;
    }
    @Override
    public void onOpened(){
        refreshNeeded = true;
    }
    @Override
    public void render(Renderer renderer, TrackedDevicePose.Buffer tdpb, double deltaTime){
        if(refreshNeeded){
            components.clear();
            add(done);
            add(textPanel);
            float blockSize = 1f/Math.max(multiblock.getInternalWidth(), Math.max(multiblock.getInternalHeight(), multiblock.getInternalDepth()));
            for(int y = 0; y<multiblock.getInternalHeight(); y++){
                final int Y = y;
                add(new VRMenuComponentPlusButton(multiblock.getInternalWidth(), Y+.5f, multiblock.getInternalDepth(), blockSize, multiblock.getInternalHeight()<multiblock.getMaxY(), () -> {
                    insertY(Y);
                }, (rendrer) -> {
                    rendrer.drawCube(-.01f, Y+.49f, -.01f, multiblock.getInternalWidth()+.01f, Y+1.51f, multiblock.getInternalDepth()+.01f, null);
                }));
                add(new VRMenuComponentMinusButton(-1, Y, -1, blockSize, multiblock.getInternalHeight()>multiblock.getMinY(), true, () -> {
                    deleteY(Y);
                }, (rendrer) -> {
                    rendrer.drawCube(-.01f, Y-.01f, -.01f, multiblock.getInternalWidth()+.01f, Y+1.01f, multiblock.getInternalDepth()+.01f, null);
                }));
            }
            add(new VRMenuComponentPlusButton(multiblock.getInternalWidth(), -.5f, multiblock.getInternalDepth(), blockSize, multiblock.getInternalHeight()<multiblock.getMaxY(), () -> {
                expand(0, 1, 0);
            }, (rendrer) -> {
                rendrer.drawCube(-.01f, -.51f, -.01f, multiblock.getInternalWidth()+.01f, .51f, multiblock.getInternalDepth()+.01f, null);
            }));
            for(int x = 0; x<multiblock.getInternalWidth(); x++){
                final int X = x;
                add(new VRMenuComponentMinusButton(x, multiblock.getInternalHeight()/2f, -1, blockSize, true, true, () -> {
                    deleteX(X);
                }, (rendrer) -> {
                    rendrer.drawCube(X-.01f, -.01f, -.01f, X+1.01f, multiblock.getInternalHeight()+.01f, multiblock.getInternalDepth()+.01f, null);
                }));
            }
            for(int z = 0; z<multiblock.getInternalDepth(); z++){
                final int Z = z;
                add(new VRMenuComponentMinusButton(-1, multiblock.getInternalHeight()/2f, z, blockSize, true, false, () -> {
                    deleteZ(Z);
                }, (rendrer) -> {
                    rendrer.drawCube(-.01f, -.01f, Z-.01f, multiblock.getInternalWidth()+.01f, multiblock.getInternalHeight()+.01f, Z+1.01f, null);
                }));
            }
            add(new VRMenuComponentPlusButton(-2, multiblock.getInternalHeight()/2f-.5f, multiblock.getInternalDepth()/2f-.5f, blockSize, true, () -> {
                expand(-1, 0, 0);
            }, (rendrer) -> {
                rendrer.drawCube(-1.01f, -.01f, -.01f, .01f, multiblock.getInternalHeight()+.01f, multiblock.getInternalDepth()+.01f, null);
            }));
            add(new VRMenuComponentPlusButton(multiblock.getInternalWidth()/2f-.5f, multiblock.getInternalHeight()/2f-.5f, -2, blockSize, true, () -> {
                expand(0, 0, -1);
            }, (rendrer) -> {
                rendrer.drawCube(-.01f, -.01f, -1.01f, multiblock.getInternalWidth()+.01f, multiblock.getInternalHeight()+.01f, .01f, null);
            }));
            add(new VRMenuComponentPlusButton(multiblock.getInternalWidth()+1, multiblock.getInternalHeight()/2f-.5f, multiblock.getInternalDepth()/2f-.5f, blockSize, true, () -> {
                expand(1, 0, 0);
            }, (rendrer) -> {
                rendrer.drawCube(multiblock.getInternalWidth()-.01f, -.01f, -.01f, multiblock.getInternalWidth()+1.01f, multiblock.getInternalHeight()+.01f, multiblock.getInternalDepth()+.01f, null);
            }));
            add(new VRMenuComponentPlusButton(multiblock.getInternalWidth()/2f-.5f, multiblock.getInternalHeight()/2f-.5f, multiblock.getInternalDepth()+1, blockSize, true, () -> {
                expand(0, 0, 1);
            }, (rendrer) -> {
                rendrer.drawCube(-.01f, -.01f, multiblock.getInternalDepth()-.01f, multiblock.getInternalWidth()+.01f, multiblock.getInternalHeight()+.01f, multiblock.getInternalDepth()+1.01f, null);
            }));
            refreshNeeded = false;
        }
        float size = Math.max(multiblock.getInternalWidth(), Math.max(multiblock.getInternalHeight(), multiblock.getInternalDepth()));
        renderer.pushModel(new Matrix4f().translate(-.5f, .5f, -.5f).scale(1/size, 1/size, 1/size));
        multiblock.draw3D();
        renderer.setColor(Core.theme.get3DMultiblockOutlineColor());
        renderer.drawCubeOutline(-1/16f, -1/16f, -1/16f, multiblock.getInternalWidth()+1/16f, multiblock.getInternalHeight()+1/16f, multiblock.getInternalDepth()+1/16f, 1/16f);
        renderer.popModel();
        textPanel.text = new FormattedText(multiblock.getDimensionsStr());
        super.render(renderer, tdpb, deltaTime);
    }
    public void expand(int x, int y, int z){
        if(x>0)multiblock.expandRight(x);
        if(x<0)multiblock.expandLeft(-x);
        if(y>0)multiblock.expandUp(y);
        if(y<0)multiblock.exandDown(-y);
        if(z>0)multiblock.expandToward(z);
        if(z<0)multiblock.expandAway(-z);
        onOpened();
    }
    private void deleteX(int x){
        multiblock.deleteX(x);
        onOpened();
    }
    private void deleteY(int y){
        multiblock.deleteY(y);
        onOpened();
    }
    private void deleteZ(int z){
        multiblock.deleteZ(z);
        onOpened();
    }
    private void insertX(int x){
        multiblock.insertX(x);
        onOpened();
    }
    private void insertY(int y){
        multiblock.insertY(y);
        onOpened();
    }
    private void insertZ(int z){
        multiblock.insertZ(z);
        onOpened();
    }
    private static class VRMenuComponentPlusButton extends VRMenuComponentButton{
        private final Consumer<Renderer> highlight;
        private final float X;
        private final float Y;
        private final float Z;
        public VRMenuComponentPlusButton(float x, float y, float z, float size, boolean enabled, Runnable al, Consumer<Renderer> highlight){
            super(-.5f+x*size, .5f+y*size, -.5f+z*size, size, size, size, 0, 0, 0, "", enabled, false);
            this.X = x;
            this.Y = y;
            this.Z = z;
            addActionListener(al);
            this.highlight = highlight;
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
            renderer.drawCubeOutline(0, 0, 0, width, height, depth, .005f);//5mcm
            renderer.pushModel(new Matrix4f().translate(width/2, height/2, depth/2));
            renderer.setColor(Core.theme.getAddButtonTextColor());
            renderer.drawCube(-width/4, -.005f, -.005f, width/4, .005f, .005f, null);
            renderer.drawCube(-.005f, -width/4, -.005f, .005f, width/4, .005f, null);
            renderer.drawCube(-.005f, -.005f, -width/4, .005f, .005f, width/4, null);
            renderer.popModel();
        }
        @Override
        public void renderForeground(Renderer renderer){
            super.renderForeground(renderer);
            if(isDeviceOver.isEmpty())return;
            renderer.setColor(Core.theme.getAddButtonTextColor(), .5f);
            renderer.pushModel(new Matrix4f().scale(width, height, depth).translate(-X, -Y, -Z));
            highlight.accept(renderer);
            renderer.popModel();
        }
    }
    private static class VRMenuComponentMinusButton extends VRMenuComponentButton{
        private final Consumer<Renderer> highlight;
        private final float X;
        private final float Y;
        private final float Z;
        private final boolean isX;
        public VRMenuComponentMinusButton(float x, float y, float z, float size, boolean enabled, boolean isX, Runnable al, Consumer<Renderer> highlight){
            super(-.5f+x*size, .5f+y*size, -.5f+z*size, size, size, size, 0, 0, 0, "", enabled, false);
            this.X = x;
            this.Y = y;
            this.Z = z;
            addActionListener(al);
            this.isX = isX;
            this.highlight = highlight;
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
            renderer.drawCubeOutline(0, 0, 0, width, height, depth, .005f);//5mm
            renderer.pushModel(new Matrix4f().translate(width/2, height/2, depth/2));
            renderer.setColor(Core.theme.getDeleteButtonTextColor());
            if(isX)renderer.drawCube(-width/4, -.005f, -.005f, width/4, .005f, .005f, null);
            else renderer.drawCube(-.005f, -.005f, -width/4, .005f, .005f, width/4, null);
            renderer.popModel();
        }
        @Override
        public void renderForeground(Renderer renderer){
            super.renderForeground(renderer);
            if(isDeviceOver.isEmpty())return;
            renderer.setColor(Core.theme.getDeleteButtonTextColor(), .5f);
            renderer.pushModel(new Matrix4f().scale(width, height, depth).translate(-X, -Y, -Z));
            highlight.accept(renderer);
            renderer.popModel();
        }
    }
}