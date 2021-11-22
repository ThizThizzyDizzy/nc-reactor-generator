package net.ncplanner.plannerator.planner.vr.menu.component;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.vr.VRMenuComponent;
import net.ncplanner.plannerator.planner.vr.menu.VRMenuEdit;
import org.joml.Matrix4f;
import org.lwjgl.openvr.TrackedDevicePose;
import org.lwjgl.openvr.VR;
public class VRMenuComponentOverhaulSFRBlockRecipe extends VRMenuComponent{
    private final VRMenuEdit editor;
    private final Block block;
    private final BlockRecipe recipe;
    private final int id;
    private final int recipeID;
    private float textInset = 0;
    private float textOffset = .001f;//1mm
    public VRMenuComponentOverhaulSFRBlockRecipe(VRMenuEdit editor, int id, float x, float y, float z, float width, float height, float depth, Block block, BlockRecipe recipe, int recipeID){
        super(x, y, z, width, height, depth, 0, 0, 0);
        this.editor = editor;
        this.block = block;
        this.recipe = recipe;
        this.id = id;
        this.recipeID = recipeID;
    }
    @Override
    public void renderComponent(Renderer renderer, TrackedDevicePose.Buffer tdpb){
        renderer.setColor(isDeviceOver.isEmpty()?Core.theme.getVRComponentColor(Core.getThemeIndex(this)):Core.theme.getVRDeviceoverComponentColor(Core.getThemeIndex(this)));
        renderer.drawCube(0, 0, 0, width, height, depth, null);
        renderer.setColor(Core.theme.getVRSelectedOutlineColor(Core.getThemeIndex(this)));
        if(editor.getSelectedOverhaulSFRBlockRecipe(id).equals(recipe)){
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
        renderer.setModel(new Matrix4f()
                .translate(0, height/2, depth+textOffset)
                .scale(1, -1, 1));
        renderer.drawCenteredText(0, -textHeight/2, width, textHeight/2, text);
        renderer.resetModelMatrix();
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