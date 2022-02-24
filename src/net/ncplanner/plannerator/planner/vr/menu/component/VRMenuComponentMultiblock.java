package net.ncplanner.plannerator.planner.vr.menu.component;
import java.util.Random;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.BoundingBox;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.vr.VRMenuComponent;
import net.ncplanner.plannerator.planner.vr.menu.VRMenuMain;
import org.joml.Matrix4f;
import org.lwjgl.openvr.TrackedDevicePose;
import org.lwjgl.openvr.VR;
public class VRMenuComponentMultiblock extends VRMenuComponent{
    private final VRMenuMain main;
    public final Multiblock multiblock;
    private static final float vel = 1/144f/25f;
    private Random rand = new Random();
    //this isn't confusing, I promise
    public float angle = rand.nextInt(360);
    public float angling = 0;
    public float anglingTo = 0;
    public float maxAng = .05f;
    public float minAng = -.05f;
    public float anglingFlipIn = 0;
    public float snappingFactor = .01f;//1% of distance per render
    public float dx = 0;//destination X (rotation, radians)
    public float dy = 1.25f;//destination Y (location, meters)
    public float scale = 1;//used for animation
    public float boost;
    public VRMenuComponentMultiblock(VRMenuMain main, Multiblock multiblock){
        super(0, 2, 0, .25f, .25f, .25f, 0, 0, 0);
        this.main = main;
        this.multiblock = multiblock;
    }
    @Override
    public void render(Renderer renderer, TrackedDevicePose.Buffer tdpb, double deltaTime){
        anglingFlipIn-=deltaTime*20;
        if(anglingFlipIn<=0){
            anglingFlipIn+=rand.nextInt(200);
            anglingTo = (float)Math.max(minAng,Math.min(maxAng,rand.nextGaussian()/2));
        }
        angling = MathUtil.getValueBetweenTwoValues(0, angling, 1, anglingTo, snappingFactor);
        if(dy>1.75f)angle = 180;
        if(dy<.75f)angle = 0;
        super.render(renderer, tdpb, deltaTime);
    }
    @Override
    public void renderComponent(Renderer renderer, TrackedDevicePose.Buffer tdpb){
        angle+=angling;
        float vel = (float)(this.vel*Math.exp(boost/20f));
        boost*=.95f;
        float[] dir = MathUtil.rotatePoint(0, vel, angle, 0, 0);
        
        dx+=dir[0];
        dy+=dir[1];
        float tRy = (float)Math.toDegrees(dx);
        float ty = dy;
        float[] txz = MathUtil.rotatePoint(0, -1, tRy, 0, 0);
        float tx = txz[0];
        float tz = txz[1];
        x = MathUtil.getValueBetweenTwoValues(0, x, 1, tx, snappingFactor);
        y = MathUtil.getValueBetweenTwoValues(0, y, 1, ty, snappingFactor);
        z = MathUtil.getValueBetweenTwoValues(0, z, 1, tz, snappingFactor);
        yRot = MathUtil.getValueBetweenTwoValues(0, yRot, 1, tRy, snappingFactor);
        if(isDeviceOver.isEmpty())renderer.setColor(Core.theme.getComponentColor(Core.getThemeIndex(this)));
        else renderer.setColor(Core.theme.getMouseoverUnselectableComponentColor(Core.getThemeIndex(this)));
        renderer.unbindTexture();
        BoundingBox bbox = multiblock.getBoundingBox();
        float size = Math.max(bbox.getWidth(), Math.max(bbox.getHeight(), bbox.getDepth()));
        renderer.setModel(new Matrix4f()
                .translate(width/2, height/2, depth/2)
                .scale(scale, scale, scale)
                .translate(-width/2, -height/2, -depth/2)
                .scale(width, height, depth)
                .scale(1/size, 1/size, 1/size));
        multiblock.draw3D();
        renderer.resetModelMatrix();
    }
    @Override
    public void renderForeground(Renderer renderer){
        super.renderForeground(renderer);
        renderer.setModel(new Matrix4f()
                .translate(width/2, height/2, depth/2)
                .scale(scale, scale, scale)
                .translate(-width/2, -height/2, -depth/2));
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        drawText();
        renderer.resetModelMatrix();
    }
    private void drawText(){
        Renderer renderer = new Renderer();
        renderer.setModel(new Matrix4f()
                .translate(0, height, depth/2)
                .scale(1, -1, 1));
        renderer.drawCenteredText(-width, -.05f, width*2, -.03f, multiblock.getName());
        renderer.drawCenteredText(-width, -.03f, width*2, -.01f, multiblock.getDefinitionName());
        renderer.resetModelMatrix();
    }
    @Override
    public void keyEvent(int device, int button, boolean pressed){
        super.keyEvent(device, button, pressed);
        if(pressed&&button==VR.EVRButtonId_k_EButton_SteamVR_Trigger){
            main.opening = multiblock;
        }
    }
}