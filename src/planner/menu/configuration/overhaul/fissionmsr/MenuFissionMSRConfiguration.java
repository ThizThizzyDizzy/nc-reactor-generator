package planner.menu.configuration.overhaul.fissionmsr;
import org.lwjgl.opengl.Display;
import planner.Core;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import static simplelibrary.opengl.Renderer2D.drawText;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuFissionMSRConfiguration extends Menu{
    private final MenuComponentMinimalistButton blocks = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Blocks", true, true));
    private final MenuComponentMinimalistButton fuels = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Fuels", true, true));
    private final MenuComponentMinimalistButton sources = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Sources", true, true));
    private final MenuComponentMinimalistButton irradiatorRecipes = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Irradiator Recipes", true, true));
    private final MenuComponentMinimalistTextBox minSize = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter());
    private final MenuComponentMinimalistTextBox maxSize = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter());
    private final MenuComponentMinimalistTextBox neutronReach = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter());
    private final MenuComponentMinimalistTextBox coolingEfficiencyLeniency = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter());
    private final MenuComponentMinimalistTextBox sparsityPenaltyMult = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistTextBox sparsityPenaltyThreshold = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    public MenuFissionMSRConfiguration(GUI gui, Menu parent){
        super(gui, parent);
        blocks.addActionListener((e) -> {
            gui.open(new MenuBlocksConfiguration(gui, this));
        });
        fuels.addActionListener((e) -> {
            gui.open(new MenuFuelsConfiguration(gui, this));
        });
        sources.addActionListener((e) -> {
            gui.open(new MenuSourcesConfiguration(gui, this));
        });
        irradiatorRecipes.addActionListener((e) -> {
            gui.open(new MenuIrradiatorRecipesConfiguration(gui, this));
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
    }
    @Override
    public void onGUIOpened(){
        blocks.label = "Blocks ("+Core.configuration.overhaul.fissionMSR.blocks.size()+")";
        fuels.label = "Fuels ("+Core.configuration.overhaul.fissionMSR.fuels.size()+")";
        sources.label = "Sources ("+Core.configuration.overhaul.fissionMSR.sources.size()+")";
        irradiatorRecipes.label = "Irradiator Recipes ("+Core.configuration.overhaul.fissionMSR.irradiatorRecipes.size()+")";
        minSize.text = Core.configuration.overhaul.fissionMSR.minSize+"";
        maxSize.text = Core.configuration.overhaul.fissionMSR.maxSize+"";
        neutronReach.text = Core.configuration.overhaul.fissionMSR.neutronReach+"";
        coolingEfficiencyLeniency.text = Core.configuration.overhaul.fissionMSR.coolingEfficiencyLeniency+"";
        sparsityPenaltyMult.text = Core.configuration.overhaul.fissionMSR.sparsityPenaltyMult+"";
        sparsityPenaltyThreshold.text = Core.configuration.overhaul.fissionMSR.sparsityPenaltyThreshold+"";
    }
    @Override
    public void onGUIClosed(){
        Core.configuration.overhaul.fissionMSR.minSize = Integer.parseInt(minSize.text); 
        Core.configuration.overhaul.fissionMSR.maxSize = Integer.parseInt(maxSize.text);
        Core.configuration.overhaul.fissionMSR.neutronReach = Integer.parseInt(neutronReach.text);
        Core.configuration.overhaul.fissionMSR.coolingEfficiencyLeniency = Integer.parseInt(coolingEfficiencyLeniency.text);
        Core.configuration.overhaul.fissionMSR.sparsityPenaltyMult = Float.parseFloat(sparsityPenaltyMult.text);
        Core.configuration.overhaul.fissionMSR.sparsityPenaltyThreshold = Float.parseFloat(sparsityPenaltyThreshold.text);
    }
    @Override
    public void render(int millisSinceLastTick){
        minSize.width = maxSize.width = neutronReach.width = coolingEfficiencyLeniency.width = sparsityPenaltyMult.width = sparsityPenaltyThreshold.width = Display.getWidth()*.75;
        minSize.x = maxSize.x = neutronReach.x = coolingEfficiencyLeniency.x = sparsityPenaltyMult.x = sparsityPenaltyThreshold.x = Display.getWidth()*.25;
        blocks.width = fuels.width = sources.width = irradiatorRecipes.width = back.width = Display.getWidth();
        minSize.height = maxSize.height = neutronReach.height = coolingEfficiencyLeniency.height = sparsityPenaltyMult.height = sparsityPenaltyThreshold.height = blocks.height = fuels.height = sources.height = irradiatorRecipes.height = back.height = Display.getHeight()/16;
        fuels.y = blocks.height;
        sources.y = fuels.y+fuels.height;
        irradiatorRecipes.y = sources.y+sources.height;
        minSize.y = irradiatorRecipes.y+irradiatorRecipes.height;
        maxSize.y = minSize.y+minSize.height;
        neutronReach.y = maxSize.y+maxSize.height;
        coolingEfficiencyLeniency.y = neutronReach.y+neutronReach.height;
        sparsityPenaltyMult.y = coolingEfficiencyLeniency.y+coolingEfficiencyLeniency.height;
        sparsityPenaltyThreshold.y = sparsityPenaltyMult.y+sparsityPenaltyMult.height;
        back.y = Display.getHeight()-back.height;
        Core.applyColor(Core.theme.getTextColor());
        drawText(0, minSize.y+Display.getHeight()/64, Display.getWidth()*.25, minSize.y+minSize.height-Display.getHeight()/64, "Minimum reactor size");
        drawText(0, maxSize.y+Display.getHeight()/64, Display.getWidth()*.25, maxSize.y+maxSize.height-Display.getHeight()/64, "Maximum reactor size");
        drawText(0, neutronReach.y+Display.getHeight()/64, Display.getWidth()*.25, neutronReach.y+neutronReach.height-Display.getHeight()/64, "Neutron reach");
        drawText(0, coolingEfficiencyLeniency.y+Display.getHeight()/64, Display.getWidth()*.25, coolingEfficiencyLeniency.y+coolingEfficiencyLeniency.height-Display.getHeight()/64, "Cooling Efficiency Leniency");
        drawText(0, sparsityPenaltyMult.y+Display.getHeight()/64, Display.getWidth()*.25, sparsityPenaltyMult.y+sparsityPenaltyMult.height-Display.getHeight()/64, "Sparsity Penalty Multiplier");
        drawText(0, sparsityPenaltyThreshold.y+Display.getHeight()/64, Display.getWidth()*.25, sparsityPenaltyThreshold.y+sparsityPenaltyThreshold.height-Display.getHeight()/64, "Sparsity Penalty Threshold");
        Core.applyWhite();
        super.render(millisSinceLastTick);
    }
}