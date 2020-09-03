package planner.menu.configuration.overhaul.fissionmsr;
import multiblock.configuration.Configuration;
import planner.Core;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import static simplelibrary.opengl.Renderer2D.drawText;
import simplelibrary.opengl.gui.GUI;
import planner.menu.Menu;
public class MenuFissionMSRConfiguration extends Menu{
    private final MenuComponentMinimalistButton blocks = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Blocks", true, true).setTooltip("Add, remove, or modify blocks"));
    private final MenuComponentMinimalistButton fuels = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Fuels", true, true).setTooltip("Add, remove, or modify fuels"));
    private final MenuComponentMinimalistButton sources = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Sources", true, true).setTooltip("Add, remove, or modify sources"));
    private final MenuComponentMinimalistButton irradiatorRecipes = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Irradiator Recipes", true, true).setTooltip("Add, remove, or modify irradiator recipes"));
    private final MenuComponentMinimalistTextBox minSize = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter()).setTooltip("The minimum size of this multiblock");
    private final MenuComponentMinimalistTextBox maxSize = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter()).setTooltip("The maximum size of this multiblock");
    private final MenuComponentMinimalistTextBox neutronReach = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter()).setTooltip("The maximum length of moderator lines");
    private final MenuComponentMinimalistTextBox coolingEfficiencyLeniency = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter()).setTooltip("The size of the \"safe zone\" around 0 H/t before you get overheating and overcooling penalties");
    private final MenuComponentMinimalistTextBox sparsityPenaltyMult = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());//TODO sparsity penalty mult tooltip
    private final MenuComponentMinimalistTextBox sparsityPenaltyThreshold = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());//TODO sparsity penalty threshold tooltip
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private final Configuration configuration;
    public MenuFissionMSRConfiguration(GUI gui, Menu parent, Configuration configuration){
        super(gui, parent);
        this.configuration = configuration;
        blocks.addActionListener((e) -> {
            gui.open(new MenuBlocksConfiguration(gui, this, configuration));
        });
        fuels.addActionListener((e) -> {
            gui.open(new MenuFuelsConfiguration(gui, this, configuration));
        });
        sources.addActionListener((e) -> {
            gui.open(new MenuSourcesConfiguration(gui, this, configuration));
        });
        irradiatorRecipes.addActionListener((e) -> {
            gui.open(new MenuIrradiatorRecipesConfiguration(gui, this, configuration));
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
    }
    @Override
    public void onGUIOpened(){
        blocks.label = "Blocks ("+configuration.overhaul.fissionMSR.blocks.size()+")";
        fuels.label = "Fuels ("+configuration.overhaul.fissionMSR.fuels.size()+")";
        sources.label = "Sources ("+configuration.overhaul.fissionMSR.sources.size()+")";
        irradiatorRecipes.label = "Irradiator Recipes ("+configuration.overhaul.fissionMSR.irradiatorRecipes.size()+")";
        minSize.text = configuration.overhaul.fissionMSR.minSize+"";
        maxSize.text = configuration.overhaul.fissionMSR.maxSize+"";
        neutronReach.text = configuration.overhaul.fissionMSR.neutronReach+"";
        coolingEfficiencyLeniency.text = configuration.overhaul.fissionMSR.coolingEfficiencyLeniency+"";
        sparsityPenaltyMult.text = configuration.overhaul.fissionMSR.sparsityPenaltyMult+"";
        sparsityPenaltyThreshold.text = configuration.overhaul.fissionMSR.sparsityPenaltyThreshold+"";
    }
    @Override
    public void onGUIClosed(){
        configuration.overhaul.fissionMSR.minSize = Integer.parseInt(minSize.text); 
        configuration.overhaul.fissionMSR.maxSize = Integer.parseInt(maxSize.text);
        configuration.overhaul.fissionMSR.neutronReach = Integer.parseInt(neutronReach.text);
        configuration.overhaul.fissionMSR.coolingEfficiencyLeniency = Integer.parseInt(coolingEfficiencyLeniency.text);
        configuration.overhaul.fissionMSR.sparsityPenaltyMult = Float.parseFloat(sparsityPenaltyMult.text);
        configuration.overhaul.fissionMSR.sparsityPenaltyThreshold = Float.parseFloat(sparsityPenaltyThreshold.text);
    }
    @Override
    public void render(int millisSinceLastTick){
        minSize.width = maxSize.width = neutronReach.width = coolingEfficiencyLeniency.width = sparsityPenaltyMult.width = sparsityPenaltyThreshold.width = Core.helper.displayWidth()*.75;
        minSize.x = maxSize.x = neutronReach.x = coolingEfficiencyLeniency.x = sparsityPenaltyMult.x = sparsityPenaltyThreshold.x = Core.helper.displayWidth()*.25;
        blocks.width = fuels.width = sources.width = irradiatorRecipes.width = back.width = Core.helper.displayWidth();
        minSize.height = maxSize.height = neutronReach.height = coolingEfficiencyLeniency.height = sparsityPenaltyMult.height = sparsityPenaltyThreshold.height = blocks.height = fuels.height = sources.height = irradiatorRecipes.height = back.height = Core.helper.displayHeight()/16;
        fuels.y = blocks.height;
        sources.y = fuels.y+fuels.height;
        irradiatorRecipes.y = sources.y+sources.height;
        minSize.y = irradiatorRecipes.y+irradiatorRecipes.height;
        maxSize.y = minSize.y+minSize.height;
        neutronReach.y = maxSize.y+maxSize.height;
        coolingEfficiencyLeniency.y = neutronReach.y+neutronReach.height;
        sparsityPenaltyMult.y = coolingEfficiencyLeniency.y+coolingEfficiencyLeniency.height;
        sparsityPenaltyThreshold.y = sparsityPenaltyMult.y+sparsityPenaltyMult.height;
        back.y = Core.helper.displayHeight()-back.height;
        if(configuration.addon){
            minSize.y = maxSize.y = neutronReach.y = coolingEfficiencyLeniency.y = sparsityPenaltyMult.y = sparsityPenaltyThreshold.y = -minSize.height;
        }
        Core.applyColor(Core.theme.getTextColor());
        drawText(0, minSize.y+Core.helper.displayHeight()/64, Core.helper.displayWidth()*.25, minSize.y+minSize.height-Core.helper.displayHeight()/64, "Minimum reactor size");
        drawText(0, maxSize.y+Core.helper.displayHeight()/64, Core.helper.displayWidth()*.25, maxSize.y+maxSize.height-Core.helper.displayHeight()/64, "Maximum reactor size");
        drawText(0, neutronReach.y+Core.helper.displayHeight()/64, Core.helper.displayWidth()*.25, neutronReach.y+neutronReach.height-Core.helper.displayHeight()/64, "Neutron reach");
        drawText(0, coolingEfficiencyLeniency.y+Core.helper.displayHeight()/64, Core.helper.displayWidth()*.25, coolingEfficiencyLeniency.y+coolingEfficiencyLeniency.height-Core.helper.displayHeight()/64, "Cooling Efficiency Leniency");
        drawText(0, sparsityPenaltyMult.y+Core.helper.displayHeight()/64, Core.helper.displayWidth()*.25, sparsityPenaltyMult.y+sparsityPenaltyMult.height-Core.helper.displayHeight()/64, "Sparsity Penalty Multiplier");
        drawText(0, sparsityPenaltyThreshold.y+Core.helper.displayHeight()/64, Core.helper.displayWidth()*.25, sparsityPenaltyThreshold.y+sparsityPenaltyThreshold.height-Core.helper.displayHeight()/64, "Sparsity Penalty Threshold");
        Core.applyWhite();
        super.render(millisSinceLastTick);
    }
}