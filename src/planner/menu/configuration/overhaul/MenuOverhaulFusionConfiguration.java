package planner.menu.configuration.overhaul;
import multiblock.configuration.Configuration;
import multiblock.configuration.overhaul.fusion.Block;
import multiblock.configuration.overhaul.fusion.CoolantRecipe;
import multiblock.configuration.overhaul.fusion.Recipe;
import planner.Core;
import planner.menu.component.MenuComponentLabel;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import planner.menu.configuration.ConfigurationMenu;
import planner.menu.configuration.overhaul.fusion.MenuAddonBlockConfiguration;
import planner.menu.configuration.overhaul.fusion.MenuBlockConfiguration;
import planner.menu.configuration.overhaul.fusion.MenuComponentAddonBlock;
import planner.menu.configuration.overhaul.fusion.MenuComponentBlock;
import planner.menu.configuration.overhaul.fusion.MenuComponentCoolantRecipe;
import planner.menu.configuration.overhaul.fusion.MenuComponentPossibleAddonBlock;
import planner.menu.configuration.overhaul.fusion.MenuComponentRecipe;
import planner.menu.configuration.overhaul.fusion.MenuCoolantRecipeConfiguration;
import planner.menu.configuration.overhaul.fusion.MenuRecipeConfiguration;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuOverhaulFusionConfiguration extends ConfigurationMenu{
    private final MenuComponentMinimalistTextBox minInnerRadius, maxInnerRadius, minCoreSize, maxCoreSize, minToroidWidth, maxToroidWidth, minLiningThickness, maxLiningThickness, sparsityPenaltyMult, sparsityPenaltyThreshold, coolingEfficiencyLeniency;
    private final MenuComponentLabel blocksLabel, coolantRecipesLabel, recipesLabel;
    private final MenuComponentMinimaList blocksList, coolantRecipesList, recipesList;
    private final MenuComponentMinimalistButton addBlock, addCoolantRecipe, addRecipe;
    private boolean refreshNeeded = false;
    public MenuOverhaulFusionConfiguration(GUI gui, Menu parent, Configuration configuration){
        super(gui, parent, configuration, "Overhaul Fusion");
        minInnerRadius = add(new MenuComponentMinimalistTextBox(sidebar.width, 0, 0, configuration.addon?0:48, "", true, "Minimum Inner Radius").setIntFilter());
        maxInnerRadius = add(new MenuComponentMinimalistTextBox(sidebar.width, minInnerRadius.height, 0, configuration.addon?0:48, "", true, "Maximum Inner Radius").setIntFilter());
        minCoreSize = add(new MenuComponentMinimalistTextBox(sidebar.width, 0, 0, configuration.addon?0:48, "", true, "Minimum Core Size").setIntFilter());
        maxCoreSize = add(new MenuComponentMinimalistTextBox(sidebar.width, minCoreSize.height, 0, configuration.addon?0:48, "", true, "Maximum Core Size").setIntFilter());
        minToroidWidth = add(new MenuComponentMinimalistTextBox(sidebar.width, 0, 0, configuration.addon?0:48, "", true, "Minimum Toroid Width").setIntFilter());
        maxToroidWidth = add(new MenuComponentMinimalistTextBox(sidebar.width, minToroidWidth.height, 0, configuration.addon?0:48, "", true, "Maximum Toroid Width").setIntFilter());
        minLiningThickness = add(new MenuComponentMinimalistTextBox(sidebar.width, 0, 0, configuration.addon?0:48, "", true, "Minimum Lining Thickness").setIntFilter());
        maxLiningThickness = add(new MenuComponentMinimalistTextBox(sidebar.width, minLiningThickness.height, 0, configuration.addon?0:48, "", true, "Maximum Lining Thickness").setIntFilter());
        sparsityPenaltyMult = add(new MenuComponentMinimalistTextBox(sidebar.width, maxInnerRadius.y+maxInnerRadius.height, 0, configuration.addon?0:48, "", true, "Sparsity Penalty Mult").setFloatFilter());
        sparsityPenaltyThreshold = add(new MenuComponentMinimalistTextBox(sidebar.width, maxInnerRadius.y+maxInnerRadius.height, 0, configuration.addon?0:48, "", true, "Sparsity Penalty Threshold").setFloatFilter());
        coolingEfficiencyLeniency = add(new MenuComponentMinimalistTextBox(sidebar.width, maxInnerRadius.y+maxInnerRadius.height, 0, configuration.addon?0:48, "", true, "Cooling Efficiency Leniency").setIntFilter()).setTooltip("The size of the \"safe zone\" around 0 H/t before you get overheating and overcooling penalties");
        blocksLabel = add(new MenuComponentLabel(sidebar.width, sparsityPenaltyMult.y+sparsityPenaltyMult.height, 0, 48, "Blocks"));
        coolantRecipesLabel = add(new MenuComponentLabel(sidebar.width, sparsityPenaltyMult.y+sparsityPenaltyMult.height, 0, 48, "Coolant Recipes"));
        recipesLabel = add(new MenuComponentLabel(sidebar.width, sparsityPenaltyMult.y+sparsityPenaltyMult.height, 0, 48, "Recipes"));
        blocksList = add(new MenuComponentMinimaList(sidebar.width, blocksLabel.y+blocksLabel.height, 0, 0, 16));
        coolantRecipesList = add(new MenuComponentMinimaList(sidebar.width, coolantRecipesLabel.y+coolantRecipesLabel.height, 0, 0, 16));
        recipesList = add(new MenuComponentMinimaList(sidebar.width, recipesLabel.y+recipesLabel.height, 0, 0, 16));
        addBlock = add(new MenuComponentMinimalistButton(sidebar.width, 0, 0, 48, "Add Block", true, true));
        addCoolantRecipe = add(new MenuComponentMinimalistButton(sidebar.width, 0, 0, 48, "Add Coolant Recipe", true, true));
        addRecipe = add(new MenuComponentMinimalistButton(sidebar.width, 0, 0, 48, "Add Recipe", true, true));
        addBlock.addActionListener((e) -> {
            Block b = new Block("nuclearcraft:new_block");
            configuration.overhaul.fusion.blocks.add(b);
            Core.configuration.overhaul.fusion.allBlocks.add(b);
            gui.open(new MenuBlockConfiguration(gui, this, configuration, b));
        });
        addCoolantRecipe.addActionListener((e) -> {
            CoolantRecipe r = new CoolantRecipe("input_fluid", "output_fluid", 0, 0);
            configuration.overhaul.fusion.coolantRecipes.add(r);
            Core.configuration.overhaul.fusion.allCoolantRecipes.add(r);
            gui.open(new MenuCoolantRecipeConfiguration(gui, this, configuration, r));
        });
        addRecipe.addActionListener((e) -> {
            Recipe r = new Recipe("input_fluid", "output_fluid", 0, 0, 0, 0);
            configuration.overhaul.fusion.recipes.add(r);
            Core.configuration.overhaul.fusion.allRecipes.add(r);
            gui.open(new MenuRecipeConfiguration(gui, this, configuration, r));
        });
    }
    @Override
    public void onGUIOpened(){
        if(configuration.overhaul.fusion.blocks.size()>0)blocksLabel.text = "Blocks ("+configuration.overhaul.fusion.blocks.size()+")";
        if(configuration.overhaul.fusion.coolantRecipes.size()>0)coolantRecipesLabel.text = "Coolant Recipes ("+configuration.overhaul.fusion.coolantRecipes.size()+")";
        if(configuration.overhaul.fusion.recipes.size()>0)recipesLabel.text = "Recipes ("+configuration.overhaul.fusion.recipes.size()+")";
        minInnerRadius.text = configuration.overhaul.fusion.minInnerRadius+"";
        maxInnerRadius.text = configuration.overhaul.fusion.maxInnerRadius+"";
        minCoreSize.text = configuration.overhaul.fusion.minCoreSize+"";
        maxCoreSize.text = configuration.overhaul.fusion.maxCoreSize+"";
        minToroidWidth.text = configuration.overhaul.fusion.minToroidWidth+"";
        maxToroidWidth.text = configuration.overhaul.fusion.maxToroidWidth+"";
        minLiningThickness.text = configuration.overhaul.fusion.minLiningThickness+"";
        maxLiningThickness.text = configuration.overhaul.fusion.maxLiningThickness+"";
        sparsityPenaltyMult.text = configuration.overhaul.fusion.sparsityPenaltyMult+"";
        sparsityPenaltyThreshold.text = configuration.overhaul.fusion.sparsityPenaltyThreshold+"";
        coolingEfficiencyLeniency.text = configuration.overhaul.fusion.coolingEfficiencyLeniency+"";
        blocksList.components.clear();
        if(configuration.addon){
            FOR:for(Block b : Core.configuration.overhaul.fusion.allBlocks){
                if(b.recipes.isEmpty())continue;//no recipes
                if(configuration.overhaul.fusion.blocks.contains(b))continue;//that's a block from this addon
                for(multiblock.configuration.overhaul.fusion.Block bl : configuration.overhaul.fusion.allBlocks){
                    if(bl.name.equals(b.name)){
                        blocksList.add(new MenuComponentAddonBlock(b, bl));
                        continue FOR;
                    }
                }
                blocksList.add(new MenuComponentPossibleAddonBlock(b));
            }
        }
        for(Block b : configuration.overhaul.fusion.blocks){
            blocksList.add(new MenuComponentBlock(b));
        }
        coolantRecipesList.components.clear();
        for(CoolantRecipe f : configuration.overhaul.fusion.coolantRecipes){
            coolantRecipesList.add(new MenuComponentCoolantRecipe(f));
        }
        recipesList.components.clear();
        for(Recipe f : configuration.overhaul.fusion.recipes){
            recipesList.add(new MenuComponentRecipe(f));
        }
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
        configuration.overhaul.fusion.sparsityPenaltyMult = Float.parseFloat(sparsityPenaltyMult.text);
        configuration.overhaul.fusion.sparsityPenaltyThreshold = Float.parseFloat(sparsityPenaltyThreshold.text);
        configuration.overhaul.fusion.coolingEfficiencyLeniency = Integer.parseInt(coolingEfficiencyLeniency.text);
    }
    @Override
    public void tick(){
        if(refreshNeeded){
            onGUIOpened();
            refreshNeeded = false;
        }
        super.tick();
    }
    @Override
    public void render(int millisSinceLastTick){
        double w = gui.helper.displayWidth()-sidebar.width;
        minInnerRadius.width = maxInnerRadius.width = minCoreSize.width = maxCoreSize.width = minToroidWidth.width = maxToroidWidth.width = minLiningThickness.width = maxLiningThickness.width = w/4;
        sparsityPenaltyMult.width = sparsityPenaltyThreshold.width = coolingEfficiencyLeniency.width = w/3;
        maxInnerRadius.y = maxCoreSize.y = maxToroidWidth.y = maxLiningThickness.y = minInnerRadius.y+minInnerRadius.height;
        minCoreSize.x = maxCoreSize.x = sidebar.width+w/4;
        minToroidWidth.x = maxToroidWidth.x = sidebar.width+w*2/4;
        minLiningThickness.x = maxLiningThickness.x = sidebar.width+w*3/4;
        sparsityPenaltyMult.y = sparsityPenaltyThreshold.y = coolingEfficiencyLeniency.y = maxInnerRadius.y+maxInnerRadius.height;
        sparsityPenaltyThreshold.x = sidebar.width+w/3;
        coolingEfficiencyLeniency.x = sidebar.width+w*2/3;
        addBlock.width = addCoolantRecipe.width = addRecipe.width = blocksLabel.width = coolantRecipesLabel.width = recipesLabel.width = blocksList.width = coolantRecipesList.width = recipesList.width = w/2;
        addCoolantRecipe.x = coolantRecipesLabel.x = coolantRecipesList.x = blocksLabel.x+blocksLabel.width;
        addRecipe.x = recipesLabel.x = recipesList.x = coolantRecipesLabel.x+coolantRecipesLabel.width;
        addBlock.y = addCoolantRecipe.y = addRecipe.y = Core.helper.displayHeight()-addBlock.height;
        blocksList.height = coolantRecipesList.height = recipesLabel.height = addBlock.y-(coolantRecipesLabel.y+coolantRecipesLabel.height);
        super.render(millisSinceLastTick);
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        for(simplelibrary.opengl.gui.components.MenuComponent c : blocksList.components){
            if(c instanceof MenuComponentBlock){
                if(button==((MenuComponentBlock) c).delete){
                    configuration.overhaul.fusion.blocks.remove(((MenuComponentBlock) c).block);
                    Core.configuration.overhaul.fusion.allBlocks.remove(((MenuComponentBlock) c).block);
                    refreshNeeded = true;
                    return;
                }
                if(button==((MenuComponentBlock) c).edit){
                    gui.open(new MenuBlockConfiguration(gui, this, configuration, ((MenuComponentBlock) c).block));
                    return;
                }
            }
            if(c instanceof MenuComponentPossibleAddonBlock){
                if(button==((MenuComponentPossibleAddonBlock) c).add){
                    Block b = new Block(((MenuComponentPossibleAddonBlock)c).block.name);
                    b.breedingBlanket = ((MenuComponentPossibleAddonBlock)c).block.breedingBlanket;
                    b.shielding = ((MenuComponentPossibleAddonBlock)c).block.shielding;
                    b.reflector = ((MenuComponentPossibleAddonBlock)c).block.reflector;
                    b.heatsink = ((MenuComponentPossibleAddonBlock)c).block.heatsink;
                    configuration.overhaul.fusion.allBlocks.add(b);
                    refreshNeeded = true;
                    return;
                }
            }
            if(c instanceof MenuComponentAddonBlock){
                if(button==((MenuComponentAddonBlock) c).delete){
                    configuration.overhaul.fusion.allBlocks.remove(((MenuComponentAddonBlock)c).block);
                    refreshNeeded = true;
                    return;
                }
                if(button==((MenuComponentAddonBlock)c).edit){
                    gui.open(new MenuAddonBlockConfiguration(gui, this, configuration, ((MenuComponentAddonBlock)c).parent, ((MenuComponentAddonBlock) c).block));
                    return;
                }
            }
        }
        for(simplelibrary.opengl.gui.components.MenuComponent c : coolantRecipesList.components){
            if(c instanceof MenuComponentCoolantRecipe){
                if(button==((MenuComponentCoolantRecipe) c).delete){
                    configuration.overhaul.fusion.coolantRecipes.remove(((MenuComponentCoolantRecipe) c).coolantRecipe);
                    Core.configuration.overhaul.fusion.allCoolantRecipes.remove(((MenuComponentCoolantRecipe) c).coolantRecipe);
                    refreshNeeded = true;
                    return;
                }
                if(button==((MenuComponentCoolantRecipe) c).edit){
                    gui.open(new MenuCoolantRecipeConfiguration(gui, this, configuration, ((MenuComponentCoolantRecipe) c).coolantRecipe));
                    return;
                }
            }
        }
        for(simplelibrary.opengl.gui.components.MenuComponent c : recipesList.components){
            if(c instanceof MenuComponentRecipe){
                if(button==((MenuComponentRecipe) c).delete){
                    configuration.overhaul.fusion.recipes.remove(((MenuComponentRecipe) c).recipe);
                    Core.configuration.overhaul.fusion.allRecipes.remove(((MenuComponentRecipe) c).recipe);
                    refreshNeeded = true;
                    return;
                }
                if(button==((MenuComponentRecipe) c).edit){
                    gui.open(new MenuRecipeConfiguration(gui, this, configuration, ((MenuComponentRecipe) c).recipe));
                    return;
                }
            }
        }
        super.buttonClicked(button);
    }
}