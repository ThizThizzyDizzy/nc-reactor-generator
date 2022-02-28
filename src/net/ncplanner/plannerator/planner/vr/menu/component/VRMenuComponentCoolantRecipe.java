package net.ncplanner.plannerator.planner.vr.menu.component;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.CoolantRecipe;
import net.ncplanner.plannerator.multiblock.editor.action.SetCoolantRecipeAction;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.vr.VRMenuComponent;
import net.ncplanner.plannerator.planner.vr.menu.VRMenuEdit;
import org.joml.Matrix4f;
import org.lwjgl.openvr.TrackedDevicePose;
import org.lwjgl.openvr.VR;
public class VRMenuComponentCoolantRecipe extends VRMenuComponent{
    private final VRMenuEdit editor;
    private final CoolantRecipe recipe;
    private float textInset = 0;
    private float textOffset = .001f;//1mm
    public VRMenuComponentCoolantRecipe(VRMenuEdit editor, float x, float y, float z, float width, float height, float depth, CoolantRecipe recipe){
        super(x, y, z, width, height, depth, 0, 0, 0);
        this.editor = editor;
        this.recipe = recipe;
    }
    @Override
    public void renderComponent(Renderer renderer, TrackedDevicePose.Buffer tdpb){
        renderer.setColor(isDeviceOver.isEmpty()?Core.theme.getVRComponentColor(Core.getThemeIndex(this)):Core.theme.getVRDeviceoverComponentColor(Core.getThemeIndex(this)));
        renderer.drawCube(0, 0, 0, width, height, depth, null);
        renderer.setColor(Core.theme.getVRSelectedOutlineColor(Core.getThemeIndex(this)));
        if(((OverhaulSFR)editor.getMultiblock()).coolantRecipe.equals(recipe)){
            renderer.drawCubeOutline(-.0025f, -.0025f, -.0025f, width+.0025f, height+.0025f, depth+.0025f, .0025f);//2.5fmm
        }
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        drawText(recipe.getInputDisplayName());
    }
    public void drawText(String text){
        Renderer renderer = new Renderer();
        float textLength = renderer.getStringWidth(text, height);
        float scale = Math.min(1, (width-textInset*2)/textLength);
        float textHeight = ((height-textInset*2)*scale)-.005f;
        renderer.pushModel(new Matrix4f().translate(0, height/2, depth+textOffset).scale(1, -1, 1));
        renderer.drawCenteredText(0, -textHeight/2, width, textHeight/2, text);
        renderer.popModel();
    }
    @Override
    public void keyEvent(int device, int button, boolean pressed){
        super.keyEvent(device, button, pressed);
        if(pressed){
            if(button==VR.EVRButtonId_k_EButton_SteamVR_Trigger){
                editor.getMultiblock().action(new SetCoolantRecipeAction(editor, recipe), true, true);
            }
        }
    }
    @Override
    public String getTooltip(int device){
        return "Heat: "+recipe.heat+"\n"
             + "Output Ratio: "+recipe.outputRatio;
    }
}