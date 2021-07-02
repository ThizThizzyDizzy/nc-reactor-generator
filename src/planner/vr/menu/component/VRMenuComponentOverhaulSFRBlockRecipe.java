package planner.vr.menu.component;
import multiblock.configuration.overhaul.fissionsfr.Block;
import multiblock.configuration.overhaul.fissionsfr.BlockRecipe;
import org.lwjgl.opengl.GL11;
import org.lwjgl.openvr.TrackedDevicePose;
import org.lwjgl.openvr.VR;
import planner.Core;
import planner.vr.VRCore;
import planner.vr.VRMenuComponent;
import planner.vr.menu.VRMenuEdit;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.Renderer2D;
public class VRMenuComponentOverhaulSFRBlockRecipe extends VRMenuComponent{
    private final VRMenuEdit editor;
    private final Block block;
    private final BlockRecipe recipe;
    private final int id;
    private final int recipeID;
    private float textInset = 0;
    private double textOffset = .001f;//1mm
    public VRMenuComponentOverhaulSFRBlockRecipe(VRMenuEdit editor, int id, double x, double y, double z, double width, double height, double depth, Block block, BlockRecipe recipe, int recipeID){
        super(x, y, z, width, height, depth, 0, 0, 0);
        this.editor = editor;
        this.block = block;
        this.recipe = recipe;
        this.id = id;
        this.recipeID = recipeID;
    }
    @Override
    public void renderComponent(TrackedDevicePose.Buffer tdpb){
        Core.applyColor(isDeviceOver.isEmpty()?Core.theme.getVRComponentColor(Core.getThemeIndex(this)):Core.theme.getVRDeviceoverComponentColor(Core.getThemeIndex(this)));
        VRCore.drawCube(0, 0, 0, width, height, depth, 0);
        Core.applyColor(Core.theme.getVRSelectedOutlineColor(Core.getThemeIndex(this)));
        if(editor.getSelectedOverhaulSFRBlockRecipe(id).equals(recipe)){
            VRCore.drawCubeOutline(-.0025, -.0025, -.0025, width+.0025, height+.0025, depth+.0025, .0025);//2.5mm
        }
        Core.applyColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
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
                editor.selectedSFRBlockRecipe.put(id, recipeID);
            }
        }
    }
    @Override    
    public String getTooltip(int device){
        String ttp = "";
        if(block.heatsink){
            ttp+="Heatsink Cooling: "+recipe.heatsinkCooling+"\n";
        }
        if(block.fuelCell){
            ttp+="Fuel Efficiency: "+recipe.fuelCellEfficiency+"\n";
            ttp+="Fuel Heat: "+recipe.fuelCellHeat+"\n";
            ttp+="Fuel Criticality: "+recipe.fuelCellCriticality+"\n";
            if(recipe.fuelCellSelfPriming)ttp+="Fuel Self-Priming\n";
        }
        if(block.reflector){
            ttp+="Reflector Efficiency: "+recipe.reflectorEfficiency+"\n";
            ttp+="Reflector Reflectivity: "+recipe.reflectorReflectivity+"\n";
        }
        if(block.irradiator){
            ttp+="Irradiator Efficiency: "+recipe.irradiatorEfficiency+"\n";
            ttp+="Irradiator Heat: "+recipe.irradiatorHeat+"\n";
        }
        if(block.moderator){
            ttp+="Moderator Flux: "+recipe.moderatorFlux+"\n";
            ttp+="Moderator Efficiency: "+recipe.moderatorEfficiency+"\n";
            if(recipe.moderatorActive)ttp+="Moderator Active"+"\n";
        }
        if(block.shield){
            ttp+="Shield Efficiency: "+recipe.shieldEfficiency+"\n";
            ttp+="Shield Heat: "+recipe.shieldHeat+"\n";
        }
        return ttp.trim();
    }
}