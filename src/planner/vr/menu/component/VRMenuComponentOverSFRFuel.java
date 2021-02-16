package planner.vr.menu.component;
import java.awt.Color;
import multiblock.configuration.overhaul.fissionsfr.Fuel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.openvr.TrackedDevicePose;
import org.lwjgl.openvr.VR;
import planner.Core;
import planner.vr.VRCore;
import planner.vr.VRMenuComponent;
import planner.vr.menu.VRMenuEdit;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.Renderer2D;
public class VRMenuComponentOverSFRFuel extends VRMenuComponent{
    private final VRMenuEdit editor;
    private final Fuel fuel;
    private final int id;
    private final int fuelID;
    private float textInset = 0;
    private double textOffset = .001f;//1mm
    public VRMenuComponentOverSFRFuel(VRMenuEdit editor, int id, double x, double y, double z, double width, double height, double depth, Fuel fuel, int fuelID){
        super(x, y, z, width, height, depth, 0, 0, 0);
        this.editor = editor;
        this.fuel = fuel;
        this.id = id;
        this.fuelID = fuelID;
    }
    @Override
    public void renderComponent(TrackedDevicePose.Buffer tdpb){
        Color col = Core.theme.getEditorListBorderColor();
        if(!isDeviceOver.isEmpty()){
            col = col.brighter();
        }
        Core.applyColor(col);
        VRCore.drawCube(0, 0, 0, width, height, depth, 0);
        Core.applyColor(Core.theme.getTextColor());
        if(editor.getSelectedOverSFRFuel(id).equals(fuel)){
            VRCore.drawCubeOutline(-.0025, -.0025, -.0025, width+.0025, height+.0025, depth+.0025, .0025);//2.5mm
        }
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
                editor.selectedOverSFRFuel.put(id, fuelID);
            }
        }
    }
    @Override    
    public String getTooltip(int device){
        return "Efficiency: "+fuel.efficiency+"\n"
             + "Base Heat: "+fuel.heat+"\n"
             + "Criticality: "+fuel.criticality+"\n"
             + "Base Time: "+fuel.time+(fuel.selfPriming?"\nSelf-Priming":"");
    }
}