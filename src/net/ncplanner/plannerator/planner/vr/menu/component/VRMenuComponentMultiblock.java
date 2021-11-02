package net.ncplanner.plannerator.planner.vr.menu.component;
import java.util.Random;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.multiblock.BoundingBox;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.vr.VRMenuComponent;
import net.ncplanner.plannerator.planner.vr.menu.VRMenuMain;
import org.lwjgl.opengl.GL11;
import org.lwjgl.openvr.TrackedDevicePose;
import org.lwjgl.openvr.VR;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.Renderer2D;
public class VRMenuComponentMultiblock extends VRMenuComponent{
    private final VRMenuMain main;
    public final Multiblock multiblock;
    private static final double vel = 1/144f/25f;
    private Random rand = new Random();
    //this isn't confusing, I promise
    public double angle = rand.nextInt(360);
    public double angling = 0;
    public double anglingTo = 0;
    public double maxAng = .05;
    public double minAng = -.05;
    public int anglingFlipIn = 0;
    public double snappingFactor = .01f;//1% of distance per render
    public double dx = 0;//destination X (rotation, radians)
    public double dy = 1.25;//destination Y (location, meters)
    public float scale = 1;//used for animation
    public float boost;
    public VRMenuComponentMultiblock(VRMenuMain main, Multiblock multiblock){
        super(0, 2, 0, .25, .25, .25, 0, 0, 0);
        this.main = main;
        this.multiblock = multiblock;
    }
    @Override
    public void renderComponent(Renderer renderer, TrackedDevicePose.Buffer tdpb){
        angle+=angling;
        double vel = this.vel*Math.exp(boost/20f);
        boost*=.95;
        double[] dir = MathUtil.rotatePoint(0, vel, angle, 0, 0);
        
        dx+=dir[0];
        dy+=dir[1];
        double tRy = Math.toDegrees(dx);
        double ty = dy;
        double[] txz = MathUtil.rotatePoint(0, -1, tRy, 0, 0);
        double tx = txz[0];
        double tz = txz[1];
        x = MathUtil.getValueBetweenTwoValues(0, x, 1, tx, snappingFactor);
        y = MathUtil.getValueBetweenTwoValues(0, y, 1, ty, snappingFactor);
        z = MathUtil.getValueBetweenTwoValues(0, z, 1, tz, snappingFactor);
        yRot = MathUtil.getValueBetweenTwoValues(0, yRot, 1, tRy, snappingFactor);
        if(isDeviceOver.isEmpty())renderer.setColor(Core.theme.getComponentColor(Core.getThemeIndex(this)));
        else renderer.setColor(Core.theme.getMouseoverUnselectableComponentColor(Core.getThemeIndex(this)));
        ImageStash.instance.bindTexture(0);
        GL11.glPushMatrix();
        GL11.glTranslated(width/2, height/2, depth/2);
        GL11.glScaled(scale, scale, scale);
        GL11.glTranslated(-width/2, -height/2, -depth/2);
        GL11.glScaled(width, height, depth);
        BoundingBox bbox = multiblock.getBoundingBox();
        double size = Math.max(bbox.getWidth(), Math.max(bbox.getHeight(), bbox.getDepth()));
        GL11.glScaled(1/size, 1/size, 1/size);
        multiblock.draw3D();
        GL11.glPopMatrix();
    }
    @Override
    public void renderForeground(Renderer renderer){
        super.renderForeground(renderer);
        GL11.glPushMatrix();
        GL11.glTranslated(width/2, height/2, depth/2);
        GL11.glScaled(scale, scale, scale);
        GL11.glTranslated(-width/2, -height/2, -depth/2);
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        drawText();
        GL11.glPopMatrix();
    }
    @Override
    public void tick(){
        super.tick();
        anglingFlipIn--;
        if(anglingFlipIn<=0){
            anglingFlipIn+=rand.nextInt(200);
            anglingTo = Math.max(minAng,Math.min(maxAng,rand.nextGaussian()/2));
        }
        angling = MathUtil.getValueBetweenTwoValues(0, angling, 1, anglingTo, snappingFactor);
        if(dy>1.75)angle = 180;
        if(dy<.75)angle = 0;
    }
    private void drawText(){
        GL11.glPushMatrix();
        GL11.glTranslated(0, height, depth/2);
        GL11.glScaled(1, -1, 1);
        Renderer2D.drawCenteredText(-width, -.05, width*2, -.03, multiblock.getName());
        Renderer2D.drawCenteredText(-width, -.03, width*2, -.01, multiblock.getDefinitionName());
        GL11.glPopMatrix();
    }
    @Override
    public void keyEvent(int device, int button, boolean pressed){
        super.keyEvent(device, button, pressed);
        if(pressed&&button==VR.EVRButtonId_k_EButton_SteamVR_Trigger){
            main.opening = multiblock;
        }
    }
}