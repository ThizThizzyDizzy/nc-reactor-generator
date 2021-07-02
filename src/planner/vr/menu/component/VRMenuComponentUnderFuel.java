package planner.vr.menu.component;
import multiblock.action.SetFuelAction;
import multiblock.configuration.underhaul.fissionsfr.Fuel;
import multiblock.underhaul.fissionsfr.UnderhaulSFR;
import org.lwjgl.opengl.GL11;
import org.lwjgl.openvr.TrackedDevicePose;
import org.lwjgl.openvr.VR;
import planner.Core;
import planner.vr.VRCore;
import planner.vr.VRMenuComponent;
import planner.vr.menu.VRMenuEdit;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.Renderer2D;
public class VRMenuComponentUnderFuel extends VRMenuComponent{
    private final VRMenuEdit editor;
    private final Fuel fuel;
    private float textInset = 0;
    private double textOffset = .001f;//1mm
    public VRMenuComponentUnderFuel(VRMenuEdit editor, double x, double y, double z, double width, double height, double depth, Fuel fuel){
        super(x, y, z, width, height, depth, 0, 0, 0);
        this.editor = editor;
        this.fuel = fuel;
    }
    @Override
    public void renderComponent(TrackedDevicePose.Buffer tdpb){
        Core.applyColor(isDeviceOver.isEmpty()?Core.theme.getVRComponentColor(Core.getThemeIndex(this)):Core.theme.getVRDeviceoverComponentColor(Core.getThemeIndex(this)));
        VRCore.drawCube(0, 0, 0, width, height, depth, 0);
        Core.applyColor(Core.theme.getVRSelectedOutlineColor(Core.getThemeIndex(this)));
        if(((UnderhaulSFR)editor.getMultiblock()).fuel.equals(fuel)){
            VRCore.drawCubeOutline(-.0025, -.0025, -.0025, width+.0025, height+.0025, depth+.0025, .0025);//2.5mm
        }
        Core.applyColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        drawText(fuel.name);
    }
    public void drawText(String text){
        double textLength = FontManager.getLengthForStringWithHeight(text, height);
        double scale = Math.min(1, (width-textInset*2)/textLength);
        double textHeight = ((height-textInset*2)*scale)-.005;
        GL11.glPushMatrix();
        GL11.glTranslated(0, height/2, depth+textOffset);
        GL11.glScaled(1, -1, 1);
        Renderer2D.drawCenteredText(0, -textHeight/2, width, textHeight/2, text);
        GL11.glPopMatrix();
    }
    @Override
    public void keyEvent(int device, int button, boolean pressed){
        super.keyEvent(device, button, pressed);
        if(pressed){
            if(button==VR.EVRButtonId_k_EButton_SteamVR_Trigger){
                editor.getMultiblock().action(new SetFuelAction(editor, fuel), true, true);
            }
        }
    }
    @Override
    public String getTooltip(int device){
        return "Base Power: "+fuel.power+"\n"
             + "Base Heat: "+fuel.heat+"\n"
             + "Base Time: "+fuel.time;
    }
}