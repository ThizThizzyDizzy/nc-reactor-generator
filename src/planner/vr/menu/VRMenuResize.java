package planner.vr.menu;
import java.awt.Color;
import java.awt.event.ActionListener;
import multiblock.Multiblock;
import org.lwjgl.opengl.GL11;
import org.lwjgl.openvr.TrackedDevicePose;
import planner.Core;
import planner.FormattedText;
import planner.vr.VRCore;
import planner.vr.VRGUI;
import planner.vr.VRMenu;
import planner.vr.menu.component.VRMenuComponentButton;
import planner.vr.menu.component.VRMenuComponentTextPanel;
public class VRMenuResize extends VRMenu{//TODO center the multiblock
    public VRMenuComponentButton done = add(new VRMenuComponentButton(-.25, 1.75, -1, .5, .125, .1, 0, 0, 0, "Done", true, false));
    private VRMenuComponentTextPanel textPanel = add(new VRMenuComponentTextPanel(.25, 1, 1.2, .5, .25, .05, 0, 180, 0, "Size"));
    private final Multiblock multiblock;
    private boolean refreshNeeded;
    public VRMenuResize(VRGUI gui, VRMenu parent, Multiblock multiblock){
        super(gui, parent);
        done.setTooltip("Finish resizing and return to the editor screen");
        done.addActionListener((e) -> {
            gui.open(new VRMenuEdit(gui, multiblock).alreadyOpen());
        });
        this.multiblock = multiblock;
    }
    @Override
    public void onGUIOpened(){
        refreshNeeded = true;
    }
    @Override
    public void tick(){
        if(refreshNeeded){
            components.clear();
            add(done);
            add(textPanel);
            double blockSize = 1d/Math.max(multiblock.getX(), Math.max(multiblock.getY(), multiblock.getZ()));
            for(int y = 0; y<multiblock.getY(); y++){
                final int Y = y;
                add(new VRMenuComponentPlusButton(multiblock.getX(), Y+.5, multiblock.getZ(), blockSize, multiblock.getY()<multiblock.getMaxY(), (e) -> {
                    insertY(Y);
                }, () -> {
                    VRCore.drawCube(-.01, Y+.49, -.01, multiblock.getX()+.01, Y+1.51, multiblock.getZ()+.01, 0);
                }));
                add(new VRMenuComponentMinusButton(-1, Y, -1, blockSize, multiblock.getY()>multiblock.getMinY(), true, (e) -> {
                    deleteY(Y);
                }, () -> {
                    VRCore.drawCube(-.01, Y-.01, -.01, multiblock.getX()+.01, Y+1.01, multiblock.getZ()+.01, 0);
                }));
            }
            add(new VRMenuComponentPlusButton(multiblock.getX(), -.5, multiblock.getZ(), blockSize, multiblock.getY()<multiblock.getMaxY(), (e) -> {
                expand(0, 1, 0);
            }, () -> {
                VRCore.drawCube(-.01, -.51, -.01, multiblock.getX()+.01, .51, multiblock.getZ()+.01, 0);
            }));
            for(int x = 0; x<multiblock.getX(); x++){
                final int X = x;
                add(new VRMenuComponentMinusButton(x, multiblock.getY()/2d, -1, blockSize, true, true, (e) -> {
                    deleteX(X);
                }, () -> {
                    VRCore.drawCube(X-.01, -.01, -.01, X+1.01, multiblock.getY()+.01, multiblock.getZ()+.01, 0);
                }));
            }
            for(int z = 0; z<multiblock.getZ(); z++){
                final int Z = z;
                add(new VRMenuComponentMinusButton(-1, multiblock.getY()/2d, z, blockSize, true, false, (e) -> {
                    deleteZ(Z);
                }, () -> {
                    VRCore.drawCube(-.01, -.01, Z-.01, multiblock.getX()+.01, multiblock.getY()+.01, Z+1.01, 0);
                }));
            }
            add(new VRMenuComponentPlusButton(-2, multiblock.getY()/2d-.5, multiblock.getZ()/2d-.5, blockSize, true, (e) -> {
                expand(-1, 0, 0);
            }, () -> {
                VRCore.drawCube(-1.01, -.01, -.01, .01, multiblock.getY()+.01, multiblock.getZ()+.01, 0);
            }));
            add(new VRMenuComponentPlusButton(multiblock.getX()/2d-.5, multiblock.getY()/2d-.5, -2, blockSize, true, (e) -> {
                expand(0, 0, -1);
            }, () -> {
                VRCore.drawCube(-.01, -.01, -1.01, multiblock.getX()+.01, multiblock.getY()+.01, .01, 0);
            }));
            add(new VRMenuComponentPlusButton(multiblock.getX()+1, multiblock.getY()/2d-.5, multiblock.getZ()/2d-.5, blockSize, true, (e) -> {
                expand(1, 0, 0);
            }, () -> {
                VRCore.drawCube(multiblock.getX()-.01, -.01, -.01, multiblock.getX()+1.01, multiblock.getY()+.01, multiblock.getZ()+.01, 0);
            }));
            add(new VRMenuComponentPlusButton(multiblock.getX()/2d-.5, multiblock.getY()/2d-.5, multiblock.getZ()+1, blockSize, true, (e) -> {
                expand(0, 0, 1);
            }, () -> {
                VRCore.drawCube(-.01, -.01, multiblock.getZ()-.01, multiblock.getX()+.01, multiblock.getY()+.01, multiblock.getZ()+1.01, 0);
            }));
            refreshNeeded = false;
        }
        super.tick();
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
        textPanel.text = new FormattedText(multiblock.getX()+"x"+multiblock.getY()+"x"+multiblock.getDisplayZ());
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
    private void deleteX(int x){
        multiblock.deleteX(x);
        onGUIOpened();
    }
    private void deleteY(int y){
        multiblock.deleteY(y);
        onGUIOpened();
    }
    private void deleteZ(int z){
        multiblock.deleteZ(z);
        onGUIOpened();
    }
    private void insertX(int x){
        multiblock.insertX(x);
        onGUIOpened();
    }
    private void insertY(int y){
        multiblock.insertY(y);
        onGUIOpened();
    }
    private void insertZ(int z){
        multiblock.insertZ(z);
        onGUIOpened();
    }
    private static class VRMenuComponentPlusButton extends VRMenuComponentButton{
        private final Runnable highlight;
        private final double X;
        private final double Y;
        private final double Z;
        public VRMenuComponentPlusButton(double x, double y, double z, double size, boolean enabled, ActionListener al, Runnable highlight){
            super(-.5+x*size, .5+y*size, -.5+z*size, size, size, size, 0, 0, 0, "", enabled, false);
            this.X = x;
            this.Y = y;
            this.Z = z;
            addActionListener(al);
            this.highlight = highlight;
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
            VRCore.drawCubeOutline(0, 0, 0, width, height, depth, .005);//5mcm
            GL11.glPushMatrix();
            GL11.glTranslated(width/2, height/2, depth/2);//center
            Core.applyColor(Core.theme.getGreen());
            VRCore.drawCube(-width/4, -.005, -.005, width/4, .005, .005, 0);
            VRCore.drawCube(-.005, -width/4, -.005, .005, width/4, .005, 0);
            VRCore.drawCube(-.005, -.005, -width/4, .005, .005, width/4, 0);
            GL11.glPopMatrix();
        }
        @Override
        public void renderForeground(){
            super.renderForeground();
            if(isDeviceOver.isEmpty())return;
            Core.applyColor(Core.theme.getGreen(), .5f);
            GL11.glPushMatrix();
            GL11.glScaled(width, height, depth);
            GL11.glTranslated(-X, -Y, -Z);
            highlight.run();
            GL11.glPopMatrix();
        }
    }
    private static class VRMenuComponentMinusButton extends VRMenuComponentButton{
        private final Runnable highlight;
        private final double X;
        private final double Y;
        private final double Z;
        private final boolean isX;
        public VRMenuComponentMinusButton(double x, double y, double z, double size, boolean enabled, boolean isX, ActionListener al, Runnable highlight){
            super(-.5+x*size, .5+y*size, -.5+z*size, size, size, size, 0, 0, 0, "", enabled, false);
            this.X = x;
            this.Y = y;
            this.Z = z;
            addActionListener(al);
            this.isX = isX;
            this.highlight = highlight;
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
            VRCore.drawCubeOutline(0, 0, 0, width, height, depth, .005);//5mm
            GL11.glPushMatrix();
            GL11.glTranslated(width/2, height/2, depth/2);//center
            Core.applyColor(Core.theme.getRed());
            if(isX)VRCore.drawCube(-width/4, -.005, -.005, width/4, .005, .005, 0);
            else VRCore.drawCube(-.005, -.005, -width/4, .005, .005, width/4, 0);
            GL11.glPopMatrix();
        }
        @Override
        public void renderForeground(){
            super.renderForeground();
            if(isDeviceOver.isEmpty())return;
            Core.applyColor(Core.theme.getRed(), .5f);
            GL11.glPushMatrix();
            GL11.glScaled(width, height, depth);
            GL11.glTranslated(-X, -Y, -Z);
            highlight.run();
            GL11.glPopMatrix();
        }
    }
}