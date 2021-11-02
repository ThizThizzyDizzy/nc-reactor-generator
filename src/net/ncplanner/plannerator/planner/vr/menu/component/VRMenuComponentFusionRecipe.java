package net.ncplanner.plannerator.planner.vr.menu.component;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.multiblock.editor.action.SetFusionRecipeAction;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Recipe;
import net.ncplanner.plannerator.multiblock.overhaul.fusion.OverhaulFusionReactor;
import org.lwjgl.opengl.GL11;
import org.lwjgl.openvr.TrackedDevicePose;
import org.lwjgl.openvr.VR;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.vr.VRCore;
import net.ncplanner.plannerator.planner.vr.VRMenuComponent;
import net.ncplanner.plannerator.planner.vr.menu.VRMenuEdit;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.Renderer2D;
public class VRMenuComponentFusionRecipe extends VRMenuComponent{
    private final VRMenuEdit editor;
    private final Recipe recipe;
    private float textInset = 0;
    private double textOffset = .001f;//1mm
    public VRMenuComponentFusionRecipe(VRMenuEdit editor, double x, double y, double z, double width, double height, double depth, Recipe recipe){
        super(x, y, z, width, height, depth, 0, 0, 0);
        this.editor = editor;
        this.recipe = recipe;
    }
    @Override
    public void renderComponent(Renderer renderer, TrackedDevicePose.Buffer tdpb){
        renderer.setColor(isDeviceOver.isEmpty()?Core.theme.getVRComponentColor(Core.getThemeIndex(this)):Core.theme.getVRDeviceoverComponentColor(Core.getThemeIndex(this)));
        renderer.drawCube(0, 0, 0, width, height, depth, 0);
        renderer.setColor(Core.theme.getVRDeviceoverComponentColor(Core.getThemeIndex(this)));
        if(((OverhaulFusionReactor)editor.getMultiblock()).recipe.equals(recipe)){
            renderer.drawCubeOutline(-.0025, -.0025, -.0025, width+.0025, height+.0025, depth+.0025, .0025);//2.5mm
        }
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        drawText(recipe.getInputDisplayName());
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
                editor.getMultiblock().action(new SetFusionRecipeAction(editor, recipe), true, true);
            }
        }
    }
    @Override    
    public String getTooltip(int device){
        return "Efficiency: "+recipe.efficiency+"\n"
             + "Base Heat: "+recipe.heat+"\n"
             + "Fluxiness: "+recipe.fluxiness+"\n"
             + "Base Time: "+recipe.time;
    }
}