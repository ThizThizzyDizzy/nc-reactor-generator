package planner.menu.configuration.overhaul.fusion;
import java.util.ArrayList;
import multiblock.configuration.AbstractPlacementRule;
import multiblock.configuration.Configuration;
import multiblock.configuration.overhaul.fusion.Block;
import multiblock.configuration.overhaul.fusion.BlockRecipe;
import multiblock.configuration.overhaul.fusion.PlacementRule;
import planner.Core;
import planner.menu.component.MenuComponentLabel;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import planner.menu.component.MenuComponentTextureButton;
import planner.menu.component.MenuComponentToggleBox;
import planner.menu.configuration.ConfigurationMenu;
import planner.menu.configuration.MenuComponentPlacementRule;
import planner.menu.configuration.MenuPlacementRuleConfiguration;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuBlockConfiguration extends ConfigurationMenu{
    private final Block block;
    private final MenuComponentMinimalistButton texture;
    private final MenuComponentMinimalistTextBox name, displayName, breedingBlanketEfficiency, breedingBlanketHeat, shieldingShieldiness, reflectorEfficiency, heatsinkCooling;
    private final MenuComponentToggleBox functional, core, connector, electromagnet, cluster, conductor, createsCluster, heatingBlanket, breedingBlanket, breedingBlanketHasBaseStats, breedingBlanketAugmented, reflector, reflectorHasBaseStats, shielding, shieldingHasBaseStats, heatsink, heatsinkHasBaseStats;
    private final MenuComponentLabel legacyNamesLabel, placementRulesLabel, blockRecipesLabel;
    private final MenuComponentMinimaList legacyNames, placementRules, blockRecipes;
    private final MenuComponentMinimalistButton addRule, addBlockRecipe;
    private boolean refreshNeeded = false;
    public MenuBlockConfiguration(GUI gui, Menu parent, Configuration configuration, Block block){
        super(gui, parent, configuration, block.getDisplayName());
        texture = add(new MenuComponentTextureButton(sidebar.width, 0, 192, 192, null, true, true, ()->{return block.texture;}, block::setTexture));
        name = add(new MenuComponentMinimalistTextBox(texture.x+texture.width, 0, 0, 48, "", true, "Name").setTooltip("The ingame name of this block. Must be namespace:name or namespace:name:metadata\n(Metadata should be included if and only if the item has metadata, regardless of wheather it's 0 or not)"));
        displayName = add(new MenuComponentMinimalistTextBox(name.x, 0, 0, 48, "", true, "Display Name").setTooltip("The user-friendly name of this block."));
        legacyNamesLabel = add(new MenuComponentLabel(name.x, 48, 0, 32, "Legacy Names", true).setTooltip("A list of old names for NCPF back-compatibility"));
        legacyNames = add(new MenuComponentMinimaList(name.x, 48+32, 0, texture.height-legacyNamesLabel.height-name.height, 16));
        functional = add(new MenuComponentToggleBox(sidebar.width, texture.height, 0, 36, "Functional", false).setTooltip("If set, this block will count against the sparsity penalty"));
        core = add(new MenuComponentToggleBox(sidebar.width, texture.height, 0, 36, "Core", false));
        connector = add(new MenuComponentToggleBox(sidebar.width, texture.height, 0, 36, "Connector", false));
        electromagnet = add(new MenuComponentToggleBox(sidebar.width, texture.height, 0, 36, "Electromagnet", false));
        heatingBlanket = add(new MenuComponentToggleBox(sidebar.width, functional.y+functional.height, 0, 36, "Heating Blanket", false));
        conductor = add(new MenuComponentToggleBox(sidebar.width, functional.y+functional.height, 0, 36, "Conductor", false).setTooltip("If set, this block will connect clusters to the casing, but will not connect them together"));
        cluster = add(new MenuComponentToggleBox(sidebar.width, functional.y+functional.height, 0, 36, "Can Cluster", false).setTooltip("If set, this block can be part of a cluster"));
        createsCluster = add(new MenuComponentToggleBox(sidebar.width, functional.y+functional.height, 0, 36, "Creates Cluster", false).setTooltip("If set, this block will create a cluster"));
        breedingBlanket = add(new MenuComponentToggleBox(sidebar.width, createsCluster.y+createsCluster.height, 0, 48, "Breeding Blanket", false, true));
        breedingBlanketHasBaseStats = add(new MenuComponentToggleBox(breedingBlanket.x, breedingBlanket.y+breedingBlanket.height, 0, 32, "Has base stats", false));
        breedingBlanketEfficiency = add(new MenuComponentMinimalistTextBox(breedingBlanket.x, breedingBlanketHasBaseStats.y+breedingBlanketHasBaseStats.height, 0, 48, "", true, "Efficiency").setFloatFilter());
        breedingBlanketHeat = add(new MenuComponentMinimalistTextBox(breedingBlanket.x, breedingBlanketEfficiency.y+breedingBlanketEfficiency.height, 0, 48, "", true, "Flux").setFloatFilter());
        breedingBlanketAugmented = add(new MenuComponentToggleBox(breedingBlanket.x, breedingBlanketHeat.y+breedingBlanketHeat.height, 0, 32, "Augmented", false).setTooltip("If set, this block will be treated as an augmented breeding blanket for placement rules"));
        
        reflector = add(new MenuComponentToggleBox(sidebar.width, createsCluster.y+createsCluster.height, 0, 48, "Reflector", false, true));
        reflectorHasBaseStats = add(new MenuComponentToggleBox(reflector.x, reflector.y+reflector.height, 0, 32, "Has base stats", false));
        reflectorEfficiency = add(new MenuComponentMinimalistTextBox(reflector.x, reflectorHasBaseStats.y+reflectorHasBaseStats.height, 0, 48, "", true, "Efficiency").setFloatFilter());
        heatsink = add(new MenuComponentToggleBox(sidebar.width, reflectorEfficiency.y+reflectorEfficiency.height, 0, 48, "Heatsink", false, true));
        heatsinkHasBaseStats = add(new MenuComponentToggleBox(heatsink.x, heatsink.y+heatsink.height, 0, 32, "Has base stats", false));
        heatsinkCooling = add(new MenuComponentMinimalistTextBox(heatsink.x, heatsinkHasBaseStats.y+heatsinkHasBaseStats.height, 0, 48, "", true, "Cooling").setIntFilter());
        
        shielding = add(new MenuComponentToggleBox(sidebar.width, createsCluster.y+createsCluster.height, 0, 48, "Shielding", false, true));
        shieldingHasBaseStats = add(new MenuComponentToggleBox(shielding.x, shielding.y+shielding.height, 0, 32, "Has base stats", false));
        shieldingShieldiness = add(new MenuComponentMinimalistTextBox(shielding.x, shieldingHasBaseStats.y+shieldingHasBaseStats.height, 0, 48, "", true, "Shieldiness").setFloatFilter());

        blockRecipesLabel = add(new MenuComponentLabel(sidebar.width, Math.max(breedingBlanketAugmented.y+breedingBlanketAugmented.height, Math.max(heatsinkCooling.y+heatsinkCooling.height, shieldingShieldiness.y+shieldingShieldiness.height)), 0, 48, "Block Recipes", true));
        blockRecipes = add(new MenuComponentMinimaList(sidebar.width, blockRecipesLabel.y+blockRecipesLabel.height, 0, 0, 16));
        addBlockRecipe = add(new MenuComponentMinimalistButton(sidebar.width, 0, 0, 48, "New Recipe", true, true));
        placementRulesLabel = add(new MenuComponentLabel(sidebar.width, Math.max(breedingBlanketAugmented.y+breedingBlanketAugmented.height, Math.max(heatsinkCooling.y+heatsinkCooling.height, shieldingShieldiness.y+shieldingShieldiness.height)), 0, 48, "Placement Rules", true));
        placementRules = add(new MenuComponentMinimaList(sidebar.width, placementRulesLabel.y+placementRulesLabel.height, 0, 0, 16));
        addRule = add(new MenuComponentMinimalistButton(sidebar.width, 0, 0, 48, "New Rule", true, true));
        addRule.addActionListener((e) -> {
            PlacementRule rule;
            block.rules.add(rule = new PlacementRule());
            gui.open(new MenuPlacementRuleConfiguration(
                    gui, this, configuration, rule,Core.configuration.overhaul.fusion,
                    multiblock.configuration.overhaul.fusion.PlacementRule.BlockType.values()
            ));
        });
        addBlockRecipe.addActionListener((e) -> {
            BlockRecipe recipe = new BlockRecipe("nuclearcraft:input", "nuclearcraft:output");
            block.allRecipes.add(recipe);
            block.recipes.add(recipe);
            onGUIClosed();
            gui.open(new MenuBlockRecipeConfiguration(gui, this, configuration, block, recipe));
        });
        this.block = block;
    }
    @Override
    public void onGUIOpened(){
        if(block.recipes.size()>0)blockRecipesLabel.text = "Block Recipes ("+block.recipes.size()+")";
        name.text = block.name;
        displayName.text = block.displayName==null?"":block.displayName;
        legacyNames.components.clear();
        for(String s : block.legacyNames){
            legacyNames.add(new MenuComponentMinimalistTextBox(0, 0, 0, 48, s, true));
        }
        functional.isToggledOn = block.functional;
        core.isToggledOn = block.core;
        connector.isToggledOn = block.connector;
        electromagnet.isToggledOn = block.electromagnet;
        cluster.isToggledOn = block.cluster;
        conductor.isToggledOn = block.conductor;
        createsCluster.isToggledOn = block.createCluster;
        heatingBlanket.isToggledOn = block.heatingBlanket;
        breedingBlanket.isToggledOn = block.breedingBlanket;
        breedingBlanketHasBaseStats.isToggledOn = block.breedingBlanketHasBaseStats;
        breedingBlanketEfficiency.text = block.breedingBlanketEfficiency+"";
        breedingBlanketHeat.text = block.breedingBlanketHeat+"";
        breedingBlanketAugmented.isToggledOn = block.breedingBlanketAugmented;
        reflector.isToggledOn = block.reflector;
        reflectorHasBaseStats.isToggledOn = block.reflectorHasBaseStats;
        reflectorEfficiency.text = block.reflectorEfficiency+"";
        shielding.isToggledOn = block.shielding;
        shieldingHasBaseStats.isToggledOn = block.shieldingHasBaseStats;
        shieldingShieldiness.text = block.shieldingShieldiness+"";
        heatsink.isToggledOn = block.heatsink;
        heatsinkHasBaseStats.isToggledOn = block.heatsinkHasBaseStats;
        heatsinkCooling.text = block.heatsinkCooling+"";
        placementRules.components.clear();
        for(AbstractPlacementRule<PlacementRule.BlockType, Block> rule : block.rules){
            placementRules.add(new MenuComponentPlacementRule(rule));
        }
        blockRecipes.components.clear();
        for(BlockRecipe recipe : block.recipes){
            blockRecipes.add(new MenuComponentBlockRecipe(block, recipe));
        }
    }
    @Override
    public void onGUIClosed(){
        block.name = name.text;
        block.displayName = displayName.text.trim().isEmpty()?null:displayName.text;
        block.legacyNames.clear();
        for(MenuComponent c : legacyNames.components){
            if(c instanceof MenuComponentMinimalistTextBox){
                if(((MenuComponentMinimalistTextBox)c).text.trim().isEmpty())continue;
                block.legacyNames.add(((MenuComponentMinimalistTextBox)c).text);
            }
        }
        block.functional = functional.isToggledOn;
        block.core = core.isToggledOn;
        block.connector = connector.isToggledOn;
        block.electromagnet = electromagnet.isToggledOn;
        block.cluster = cluster.isToggledOn;
        block.conductor = conductor.isToggledOn;
        block.createCluster = createsCluster.isToggledOn;
        block.heatingBlanket = heatingBlanket.isToggledOn;
        block.reflector = reflector.isToggledOn;
        block.breedingBlanket = breedingBlanket.isToggledOn;
        block.heatsink = heatsink.isToggledOn;
        block.shielding = shielding.isToggledOn;
        if(block.reflector){
            block.reflectorHasBaseStats = reflectorHasBaseStats.isToggledOn;
            if(block.reflectorHasBaseStats){
                block.reflectorEfficiency = Float.parseFloat(reflectorEfficiency.text);
            }else{
                block.reflectorEfficiency = 0;
            }
        }else{
            block.reflectorEfficiency = 0;
            block.reflectorHasBaseStats = false;
        }
        if(block.breedingBlanket){
            block.breedingBlanketHasBaseStats = breedingBlanketHasBaseStats.isToggledOn;
            if(block.breedingBlanketHasBaseStats){
                block.breedingBlanketEfficiency = Float.parseFloat(breedingBlanketEfficiency.text);
                block.breedingBlanketHeat = Float.parseFloat(breedingBlanketHeat.text);
                block.breedingBlanketAugmented = breedingBlanketAugmented.isToggledOn;
            }else{
                block.breedingBlanketEfficiency = block.breedingBlanketHeat = 0;
                block.breedingBlanketAugmented = false;
            }
        }else{
            block.breedingBlanketEfficiency = block.breedingBlanketHeat = 0;
            block.breedingBlanketHasBaseStats = false;
            block.breedingBlanketAugmented = false;
        }
        if(block.heatsink){
            block.heatsinkHasBaseStats = heatsinkHasBaseStats.isToggledOn;
            if(block.heatsinkHasBaseStats)block.heatsinkCooling = Integer.parseInt(heatsinkCooling.text);
            else block.heatsinkCooling = 0;
        }else{
            block.heatsinkCooling = 0;
            block.heatsinkHasBaseStats = false;
        }
        if(block.shielding){
            block.shieldingHasBaseStats = shieldingHasBaseStats.isToggledOn;
            if(block.shieldingHasBaseStats){
                block.shieldingShieldiness = Float.parseFloat(shieldingShieldiness.text);
            }else{
                block.shieldingShieldiness = 0;
            }
        }else{
            block.shieldingShieldiness = 0;
            block.shieldingHasBaseStats = false;
        }
    }
    @Override
    public void tick(){
        ArrayList<MenuComponent> toRemove = new ArrayList<>();
        boolean hasEmpty = false;
        for(int i = 0; i<legacyNames.components.size(); i++){
            MenuComponent comp = legacyNames.components.get(i);
            if(comp instanceof MenuComponentMinimalistTextBox){
                if(((MenuComponentMinimalistTextBox)comp).text.trim().isEmpty()){
                    if(i==legacyNames.components.size()-1)hasEmpty = true;
                    else toRemove.add(comp);
                }
            }
        }
        if(!hasEmpty)legacyNames.add(new MenuComponentMinimalistTextBox(0, 0, 0, 48, "", true));
        legacyNames.components.removeAll(toRemove);
        if(refreshNeeded){
            onGUIOpened();
            refreshNeeded = false;
        }
        super.tick();
    }
    @Override
    public void render(int millisSinceLastTick){
        double w = gui.helper.displayWidth()-texture.width-sidebar.width;
        name.width = displayName.width = w/2;
        displayName.x = name.x+name.width;
        legacyNames.width = legacyNamesLabel.width = w;
        functional.width = connector.width = core.width = electromagnet.width = heatingBlanket.width = conductor.width = cluster.width = createsCluster.width = (gui.helper.displayWidth()-sidebar.width)/4;
        core.x = conductor.x = functional.x+functional.width;
        connector.x = cluster.x = core.x+core.width;
        electromagnet.x = createsCluster.x = connector.x+connector.width;
                shielding.width = shieldingHasBaseStats.width = shieldingShieldiness.width = heatsink.width = heatsinkHasBaseStats.width = heatsinkCooling.width = reflector.width = reflectorHasBaseStats.width = reflectorEfficiency.width = 
                breedingBlanket.width = breedingBlanketHasBaseStats.width = breedingBlanketEfficiency.width = breedingBlanketHeat.width = breedingBlanketAugmented.width = (gui.helper.displayWidth()-sidebar.width)/3;

        breedingBlanket.x = sidebar.width;//column 1
        reflector.x = heatsink.x = sidebar.width+(gui.helper.displayWidth()-sidebar.width)/3;//column 2
        shielding.x = sidebar.width+(gui.helper.displayWidth()-sidebar.width)*2/3;//column 3
        
        breedingBlanketHasBaseStats.x = breedingBlanketEfficiency.x = breedingBlanketHeat.x = breedingBlanketAugmented.x = breedingBlanket.x;
        shieldingHasBaseStats.x = shieldingShieldiness.x = shielding.x;
        heatsinkHasBaseStats.x = heatsinkCooling.x = heatsink.x;
        reflectorHasBaseStats.x = reflectorEfficiency.x = reflector.x;
        
        shieldingHasBaseStats.height = shielding.isToggledOn?32:0;
        shieldingShieldiness.height = shielding.isToggledOn&&shieldingHasBaseStats.isToggledOn?48:0;
        heatsinkHasBaseStats.height = heatsink.isToggledOn?32:0;
        heatsinkCooling.height = heatsink.isToggledOn&&heatsinkHasBaseStats.isToggledOn?48:0;
        reflectorHasBaseStats.height = reflector.isToggledOn?32:0;
        reflectorEfficiency.height = reflector.isToggledOn&&reflectorHasBaseStats.isToggledOn?48:0;
        breedingBlanketHasBaseStats.height = breedingBlanket.isToggledOn?32:0;
        breedingBlanketEfficiency.height = breedingBlanketHeat.height = breedingBlanket.isToggledOn&&breedingBlanketHasBaseStats.isToggledOn?48:0;
        breedingBlanketAugmented.height = breedingBlanket.isToggledOn&&breedingBlanketHasBaseStats.isToggledOn?32:0;
        
        functional.y = core.y = connector.y = electromagnet.y = texture.height;
        heatingBlanket.y = conductor.y = cluster.y = createsCluster.y = functional.y+functional.height;
        
        breedingBlanket.y = heatingBlanket.y+heatingBlanket.height;
        breedingBlanketHasBaseStats.y = breedingBlanket.y+breedingBlanket.height;
        breedingBlanketEfficiency.y = breedingBlanketHasBaseStats.y+breedingBlanketHasBaseStats.height;
        breedingBlanketHeat.y = breedingBlanketEfficiency.y+breedingBlanketEfficiency.height;
        breedingBlanketAugmented.y = breedingBlanketHeat.y+breedingBlanketHeat.height;
        
        reflector.y = heatingBlanket.y+heatingBlanket.height;
        reflectorHasBaseStats.y = reflector.y+reflector.height;
        reflectorEfficiency.y = reflectorHasBaseStats.y+reflectorHasBaseStats.height;
        
        heatsink.y = reflectorEfficiency.y+reflectorEfficiency.height;
        heatsinkHasBaseStats.y = heatsink.y+heatsink.height;
        heatsinkCooling.y = heatsinkHasBaseStats.y+heatsinkHasBaseStats.height;
        
        shielding.y = heatingBlanket.y+heatingBlanket.height;
        shieldingHasBaseStats.y = shielding.y+shielding.height;
        shieldingShieldiness.y = shieldingHasBaseStats.y+shieldingHasBaseStats.height;
        
        placementRules.width = placementRulesLabel.width = addRule.width = blockRecipes.width = blockRecipesLabel.width = addBlockRecipe.width = (gui.helper.displayWidth()-sidebar.width)/2;
        blockRecipesLabel.y = placementRulesLabel.y = Math.max(breedingBlanketAugmented.y+breedingBlanketAugmented.height, Math.max(heatsinkCooling.y+heatsinkCooling.height, shieldingShieldiness.y+shieldingShieldiness.height));
        blockRecipes.y = placementRules.y =  blockRecipesLabel.y+blockRecipesLabel.height;
        addRule.y = addBlockRecipe.y = gui.helper.displayHeight()-addRule.height;
        placementRules.height = blockRecipes.height = addRule.y-placementRules.y;
        placementRules.x = addRule.x = placementRulesLabel.x = blockRecipesLabel.x+blockRecipesLabel.width;
        super.render(millisSinceLastTick);
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        for(simplelibrary.opengl.gui.components.MenuComponent c : placementRules.components){
            if(c instanceof MenuComponentPlacementRule){
                if(button==((MenuComponentPlacementRule) c).delete){
                    block.rules.remove(((MenuComponentPlacementRule)c).rule);
                    refreshNeeded = true;
                    return;
                }
                if(button==((MenuComponentPlacementRule) c).edit){
                    gui.open(new MenuPlacementRuleConfiguration(
                            gui, this, configuration,
                            ((MenuComponentPlacementRule<PlacementRule.BlockType, Block, PlacementRule>) c).rule,
                            Core.configuration.overhaul.fusion, PlacementRule.BlockType.values())
                    );
                    return;
                }
            }
        }
        for(simplelibrary.opengl.gui.components.MenuComponent c : blockRecipes.components){
            if(c instanceof MenuComponentBlockRecipe){
                if(button==((MenuComponentBlockRecipe) c).delete){
                    block.allRecipes.remove(((MenuComponentBlockRecipe)c).blockRecipe);
                    block.recipes.remove(((MenuComponentBlockRecipe)c).blockRecipe);
                    refreshNeeded = true;
                    return;
                }
                if(button==((MenuComponentBlockRecipe) c).edit){
                    onGUIClosed();
                    gui.open(new MenuBlockRecipeConfiguration(gui, this, configuration, block, ((MenuComponentBlockRecipe) c).blockRecipe));
                    return;
                }
            }
        }
        super.buttonClicked(button);
    }
}