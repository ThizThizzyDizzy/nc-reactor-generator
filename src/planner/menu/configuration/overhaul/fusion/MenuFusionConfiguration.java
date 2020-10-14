package planner.menu.configuration.overhaul.fusion;
import multiblock.configuration.Configuration;
import planner.Core;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import static simplelibrary.opengl.Renderer2D.drawText;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuFusionConfiguration extends Menu{
    private final MenuComponentMinimalistButton blocks = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Blocks", true, true).setTooltip("Add, remove, or modify blocks"));
    private final MenuComponentMinimalistButton breedingBlanketRecipes = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Breeding Blanket Recipes", true, true).setTooltip("Add, remove, or modify breeding blanket recipes"));
    private final MenuComponentMinimalistButton recipes = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Recipes", true, true).setTooltip("Add, remove, or modify recipes"));
    private final MenuComponentMinimalistButton coolantRecipes = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Coolant Recipes", true, true).setTooltip("Add, remove, or modify coolant recipes"));
    private final MenuComponentMinimalistTextBox minInnerRadius = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter()).setTooltip("The minimum inner radius of the fusion reactor");
    private final MenuComponentMinimalistTextBox maxInnerRadius = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter()).setTooltip("The maximum inner radius of the fusion reactor");
    private final MenuComponentMinimalistTextBox minCoreSize = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter()).setTooltip("The minimum core size of the fusion reactor");
    private final MenuComponentMinimalistTextBox maxCoreSize = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter()).setTooltip("The maximum core size of the fusion reactor");
    private final MenuComponentMinimalistTextBox minToroidWidth = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter()).setTooltip("The minimum toroid width of the fusion reactor");
    private final MenuComponentMinimalistTextBox maxToroidWidth = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter()).setTooltip("The maximum toroid width of the fusion reactor");
    private final MenuComponentMinimalistTextBox minLiningThickness = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter()).setTooltip("The minimum lining thickness of the fusion reactor");
    private final MenuComponentMinimalistTextBox maxLiningThickness = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter()).setTooltip("The maximum lining thickness of the fusion reactor");
    private final MenuComponentMinimalistTextBox coolingEfficiencyLeniency = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter()).setTooltip("The size of the \"safe zone\" around 0 H/t before you get overheating and overcooling penalties");
    private final MenuComponentMinimalistTextBox sparsityPenaltyMult = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistTextBox sparsityPenaltyThreshold = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private final Configuration configuration;
    public MenuFusionConfiguration(GUI gui, Menu parent, Configuration configuration){
        super(gui, parent);
        blocks.addActionListener((e) -> {
            gui.open(new MenuBlocksConfiguration(gui, this, configuration));
        });
        breedingBlanketRecipes.addActionListener((e) -> {
            gui.open(new MenuBreedingBlanketRecipesConfiguration(gui, this, configuration));
        });
        recipes.addActionListener((e) -> {
            gui.open(new MenuRecipesConfiguration(gui, this, configuration));
        });
        coolantRecipes.addActionListener((e) -> {
            gui.open(new MenuCoolantRecipesConfiguration(gui, this, configuration));
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
        this.configuration = configuration;
    }
    @Override
    public void onGUIOpened(){
        blocks.label = "Blocks ("+configuration.overhaul.fusion.blocks.size()+")";
        breedingBlanketRecipes.label = "Breeding Blanket Recipes ("+configuration.overhaul.fusion.breedingBlanketRecipes.size()+")";
        recipes.label = "Recipes ("+configuration.overhaul.fusion.recipes.size()+")";
        coolantRecipes.label = "Coolant Recipes ("+configuration.overhaul.fusion.coolantRecipes.size()+")";
        minInnerRadius.text = configuration.overhaul.fusion.minInnerRadius+"";
        maxInnerRadius.text = configuration.overhaul.fusion.maxInnerRadius+"";
        minCoreSize.text = configuration.overhaul.fusion.minCoreSize+"";
        maxCoreSize.text = configuration.overhaul.fusion.maxCoreSize+"";
        minToroidWidth.text = configuration.overhaul.fusion.minToroidWidth+"";
        maxToroidWidth.text = configuration.overhaul.fusion.maxToroidWidth+"";
        minLiningThickness.text = configuration.overhaul.fusion.minLiningThickness+"";
        maxLiningThickness.text = configuration.overhaul.fusion.maxLiningThickness+"";
        coolingEfficiencyLeniency.text = configuration.overhaul.fusion.coolingEfficiencyLeniency+"";
        sparsityPenaltyMult.text = configuration.overhaul.fusion.sparsityPenaltyMult+"";
        sparsityPenaltyThreshold.text = configuration.overhaul.fusion.sparsityPenaltyThreshold+"";
    }
    @Override
    public void onGUIClosed(){
        configuration.overhaul.fusion.minInnerRadius = Integer.parseInt(minInnerRadius.text);
        configuration.overhaul.fusion.maxInnerRadius = Integer.parseInt(maxInnerRadius.text);
        configuration.overhaul.fusion.minCoreSize = Integer.parseInt(minCoreSize.text);
        configuration.overhaul.fusion.maxCoreSize = Integer.parseInt(maxCoreSize.text);
        configuration.overhaul.fusion.minToroidWidth = Integer.parseInt(minToroidWidth.text);
        configuration.overhaul.fusion.maxToroidWidth = Integer.parseInt(maxToroidWidth.text);
        configuration.overhaul.fusion.minLiningThickness = Integer.parseInt(minLiningThickness.text);
        configuration.overhaul.fusion.maxLiningThickness = Integer.parseInt(maxLiningThickness.text);
        configuration.overhaul.fusion.coolingEfficiencyLeniency = Integer.parseInt(coolingEfficiencyLeniency.text);
        configuration.overhaul.fusion.sparsityPenaltyMult = Float.parseFloat(sparsityPenaltyMult.text);
        configuration.overhaul.fusion.sparsityPenaltyThreshold = Float.parseFloat(sparsityPenaltyThreshold.text);
    }
    @Override
    public void render(int millisSinceLastTick){
        minInnerRadius.width = maxInnerRadius.width = minCoreSize.width = maxCoreSize.width = minToroidWidth.width = maxToroidWidth.width = minLiningThickness.width = maxLiningThickness.width = coolingEfficiencyLeniency.width = sparsityPenaltyMult.width = sparsityPenaltyThreshold.width = gui.helper.displayWidth()*.75;
        minInnerRadius.x = maxInnerRadius.x = minCoreSize.x = maxCoreSize.x = minToroidWidth.x = maxToroidWidth.x = minLiningThickness.x = maxLiningThickness.x = coolingEfficiencyLeniency.x = sparsityPenaltyMult.x = sparsityPenaltyThreshold.x = gui.helper.displayWidth()*.25;
        coolantRecipes.width = blocks.width = breedingBlanketRecipes.width = recipes.width = back.width = gui.helper.displayWidth();
        coolantRecipes.height = minInnerRadius.height = maxInnerRadius.height = minCoreSize.height = maxCoreSize.height = minToroidWidth.height = maxToroidWidth.height = minLiningThickness.height = maxLiningThickness.height = coolingEfficiencyLeniency.height = sparsityPenaltyMult.height = sparsityPenaltyThreshold.height = blocks.height = breedingBlanketRecipes.height = recipes.height = back.height = gui.helper.displayHeight()/16;
        breedingBlanketRecipes.y = blocks.height;
        recipes.y = breedingBlanketRecipes.y+breedingBlanketRecipes.height;
        coolantRecipes.y = recipes.y+recipes.height;
        minInnerRadius.y = coolantRecipes.y+coolantRecipes.height;
        maxInnerRadius.y = minInnerRadius.y+minInnerRadius.height;
        minCoreSize.y = maxInnerRadius.y+maxInnerRadius.height;
        maxCoreSize.y = minCoreSize.y+minCoreSize.height;
        minToroidWidth.y = maxCoreSize.y+maxCoreSize.height;
        maxToroidWidth.y = minToroidWidth.y+minToroidWidth.height;
        minLiningThickness.y = maxToroidWidth.y+maxToroidWidth.height;
        maxLiningThickness.y = minLiningThickness.y+minLiningThickness.height;
        coolingEfficiencyLeniency.y = maxLiningThickness.y+maxLiningThickness.height;
        sparsityPenaltyMult.y = coolingEfficiencyLeniency.y+coolingEfficiencyLeniency.height;
        sparsityPenaltyThreshold.y = sparsityPenaltyMult.y+sparsityPenaltyMult.height;
        back.y = gui.helper.displayHeight()-back.height;
        if(configuration.addon){
            minInnerRadius.y = maxInnerRadius.y = minCoreSize.y = maxCoreSize.y = minToroidWidth.y = maxToroidWidth.y = minLiningThickness.y = maxLiningThickness.y = coolingEfficiencyLeniency.y = sparsityPenaltyMult.y = sparsityPenaltyThreshold.y = -minInnerRadius.height;
        }
        Core.applyColor(Core.theme.getTextColor());
        drawText(0, minInnerRadius.y+gui.helper.displayHeight()/64, gui.helper.displayWidth()*.25, minInnerRadius.y+minInnerRadius.height-gui.helper.displayHeight()/64, "Minimum inner radius");
        drawText(0, maxInnerRadius.y+gui.helper.displayHeight()/64, gui.helper.displayWidth()*.25, maxInnerRadius.y+maxInnerRadius.height-gui.helper.displayHeight()/64, "Maximum inner radius");
        drawText(0, minCoreSize.y+gui.helper.displayHeight()/64, gui.helper.displayWidth()*.25, minCoreSize.y+minCoreSize.height-gui.helper.displayHeight()/64, "Minimum core size");
        drawText(0, maxCoreSize.y+gui.helper.displayHeight()/64, gui.helper.displayWidth()*.25, maxCoreSize.y+maxCoreSize.height-gui.helper.displayHeight()/64, "Maximum core size");
        drawText(0, minToroidWidth.y+gui.helper.displayHeight()/64, gui.helper.displayWidth()*.25, minToroidWidth.y+minToroidWidth.height-gui.helper.displayHeight()/64, "Minimum toroid width");
        drawText(0, maxToroidWidth.y+gui.helper.displayHeight()/64, gui.helper.displayWidth()*.25, maxToroidWidth.y+maxToroidWidth.height-gui.helper.displayHeight()/64, "Maximum toroid width");
        drawText(0, minLiningThickness.y+gui.helper.displayHeight()/64, gui.helper.displayWidth()*.25, minLiningThickness.y+minLiningThickness.height-gui.helper.displayHeight()/64, "Minimum lining thickness");
        drawText(0, maxLiningThickness.y+gui.helper.displayHeight()/64, gui.helper.displayWidth()*.25, maxLiningThickness.y+maxLiningThickness.height-gui.helper.displayHeight()/64, "Maximum lining thickness");
        drawText(0, coolingEfficiencyLeniency.y+gui.helper.displayHeight()/64, gui.helper.displayWidth()*.25, coolingEfficiencyLeniency.y+coolingEfficiencyLeniency.height-gui.helper.displayHeight()/64, "Cooling Efficiency Leniency");
        drawText(0, sparsityPenaltyMult.y+gui.helper.displayHeight()/64, gui.helper.displayWidth()*.25, sparsityPenaltyMult.y+sparsityPenaltyMult.height-gui.helper.displayHeight()/64, "Sparsity Penalty Multiplier");
        drawText(0, sparsityPenaltyThreshold.y+gui.helper.displayHeight()/64, gui.helper.displayWidth()*.25, sparsityPenaltyThreshold.y+sparsityPenaltyThreshold.height-gui.helper.displayHeight()/64, "Sparsity Penalty Threshold");
        Core.applyWhite();
        super.render(millisSinceLastTick);
    }
}