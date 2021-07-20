package planner.menu.configuration.overhaul.fusion;
import java.util.ArrayList;
import multiblock.configuration.Configuration;
import multiblock.configuration.overhaul.fusion.Block;
import multiblock.configuration.overhaul.fusion.BlockRecipe;
import planner.menu.component.MenuComponentLabel;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import planner.menu.component.MenuComponentTextureButton;
import planner.menu.component.MenuComponentToggleBox;
import planner.menu.configuration.ConfigurationMenu;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuBlockRecipeConfiguration extends ConfigurationMenu{
    private final Block block;
    private final BlockRecipe blockRecipe;
    private final MenuComponentMinimalistButton inputTexture, outputTexture;
    private final MenuComponentMinimalistTextBox inputName, inputDisplayName, outputName, outputDisplayName, inputRate, outputRate,
            breedingBlanketEfficiency, breedingBlanketHeat, shieldingShieldiness, heatsinkCooling, reflectorEfficiency;
    private final MenuComponentToggleBox breedingBlanketAugmented;
    private final MenuComponentLabel inputLegacyNamesLabel, breedingBlanket, shielding, heatsink, reflector;
    private final MenuComponentMinimaList inputLegacyNames;
    public MenuBlockRecipeConfiguration(GUI gui, Menu parent, Configuration configuration, Block block, BlockRecipe blockRecipe){
        super(gui, parent, configuration, "Block Recipe");
        inputTexture = add(new MenuComponentTextureButton(sidebar.width, 0, 192, 192, "Input", true, true, ()->{return blockRecipe.inputTexture;}, blockRecipe::setInputTexture));
        inputName = add(new MenuComponentMinimalistTextBox(inputTexture.x+inputTexture.width, 0, 0, 48, "", true, "Input Name").setTooltip("The ingame name of the block recipe input. This should be the name of the fluid itself, not the fluid block or bucket."));
        inputDisplayName = add(new MenuComponentMinimalistTextBox(inputName.x, 0, 0, 48, "", true, "Input Display Name").setTooltip("The user-friendly name of the block recipe input."));
        inputLegacyNamesLabel = add(new MenuComponentLabel(inputName.x, 48, 0, 32, "Legacy Names", true).setTooltip("A list of old names for NCPF back-compatibility"));
        inputLegacyNames = add(new MenuComponentMinimaList(inputName.x, 48+32, 0, inputTexture.height-inputLegacyNamesLabel.height-inputName.height, 16));
        outputTexture = add(new MenuComponentTextureButton(sidebar.width, inputTexture.height, 128, 128, "Output", true, true, ()->{return blockRecipe.outputTexture;}, blockRecipe::setOutputTexture));
        outputName = add(new MenuComponentMinimalistTextBox(sidebar.width, inputTexture.height, 0, 48, "", true, "Output Name").setTooltip("The ingame name of the block recipe output. This should be the name of the fluid itself, not the fluid block or bucket."));
        outputDisplayName = add(new MenuComponentMinimalistTextBox(outputName.x, outputName.y+outputName.height, 0, 48, "", true, "Output Display Name").setTooltip("The user-friendly name of the block recipe output."));
        inputRate = add(new MenuComponentMinimalistTextBox(sidebar.width, inputTexture.height+outputTexture.height, 0, 48, "", true, "Input Rate").setIntFilter());
        outputRate = add(new MenuComponentMinimalistTextBox(sidebar.width, inputTexture.height+outputTexture.height, 0, 48, "", true, "Output Rate").setIntFilter());
        
        breedingBlanket = add(new MenuComponentLabel(sidebar.width, inputRate.y+inputRate.height, 0, block.breedingBlanket?48:0, "Breeding Blanket", true));
        breedingBlanketEfficiency = add(new MenuComponentMinimalistTextBox(breedingBlanket.x, breedingBlanket.y+breedingBlanket.height, 0, block.breedingBlanket?48:0, "", true, "Efficiency").setFloatFilter());
        breedingBlanketHeat = add(new MenuComponentMinimalistTextBox(breedingBlanket.x, breedingBlanketEfficiency.y+breedingBlanketEfficiency.height, 0, block.breedingBlanket?48:0, "", true, "Heat").setFloatFilter());
        breedingBlanketAugmented = add(new MenuComponentToggleBox(breedingBlanket.x, breedingBlanketHeat.y+breedingBlanketHeat.height, 0, block.breedingBlanket?32:0, "Augmented", false).setTooltip("If set, this block will be treated as an augmented breeding blanket for placement rules"));
        
        reflector = add(new MenuComponentLabel(sidebar.width, inputRate.y+inputRate.height, 0, block.reflector?48:0, "Reflector", true));
        reflectorEfficiency = add(new MenuComponentMinimalistTextBox(reflector.x, reflector.y+reflector.height, 0, block.reflector?48:0, "", true, "Efficiency").setFloatFilter());
        
        heatsink = add(new MenuComponentLabel(reflector.x, reflectorEfficiency.y+reflectorEfficiency.height, 0, block.heatsink?48:0, "Heatsink", true));
        heatsinkCooling = add(new MenuComponentMinimalistTextBox(heatsink.x, heatsink.y+heatsink.height, 0, block.heatsink?48:0, "", true, "Cooling").setIntFilter());
        
        shielding = add(new MenuComponentLabel(sidebar.width, inputRate.y+inputRate.height, 0, block.shielding?48:0, "Shielding", true));
        shieldingShieldiness = add(new MenuComponentMinimalistTextBox(shielding.x, shielding.y+shielding.height, 0, block.shielding?48:0, "", true, "Shieldiness").setIntFilter());
        
        this.block = block;
        this.blockRecipe = blockRecipe;
    }
    @Override
    public void onGUIOpened(){
        inputName.text = blockRecipe.inputName;
        inputDisplayName.text = blockRecipe.inputDisplayName==null?"":blockRecipe.inputDisplayName;
        inputLegacyNames.components.clear();
        for(String s : blockRecipe.inputLegacyNames){
            inputLegacyNames.add(new MenuComponentMinimalistTextBox(0, 0, 0, 48, s, true));
        }
        outputName.text = blockRecipe.outputName;
        outputDisplayName.text = blockRecipe.outputDisplayName==null?"":blockRecipe.outputDisplayName;
        inputRate.text = blockRecipe.inputRate+"";
        outputRate.text = blockRecipe.outputRate+"";
        breedingBlanketEfficiency.text = blockRecipe.breedingBlanketEfficiency+"";
        breedingBlanketHeat.text = blockRecipe.breedingBlanketHeat+"";
        breedingBlanketAugmented.isToggledOn = blockRecipe.breedingBlanketAugmented;
        reflectorEfficiency.text = blockRecipe.reflectorEfficiency+"";
        shieldingShieldiness.text = blockRecipe.shieldingShieldiness+"";
        heatsinkCooling.text = blockRecipe.heatsinkCooling+"";
    }
    @Override
    public void onGUIClosed(){
        blockRecipe.inputName = inputName.text;
        blockRecipe.inputDisplayName = inputDisplayName.text.trim().isEmpty()?null:inputDisplayName.text;
        blockRecipe.inputLegacyNames.clear();
        for(MenuComponent c : inputLegacyNames.components){
            if(c instanceof MenuComponentMinimalistTextBox){
                if(((MenuComponentMinimalistTextBox)c).text.trim().isEmpty())continue;
                blockRecipe.inputLegacyNames.add(((MenuComponentMinimalistTextBox)c).text);
            }
        }
        blockRecipe.outputName = outputName.text;
        blockRecipe.outputDisplayName = outputDisplayName.text.trim().isEmpty()?null:outputDisplayName.text;
        blockRecipe.inputRate = Integer.parseInt(inputRate.text);
        blockRecipe.outputRate = Integer.parseInt(outputRate.text);
        if(block.breedingBlanket){
            blockRecipe.breedingBlanketEfficiency = Float.parseFloat(breedingBlanketEfficiency.text);
            blockRecipe.breedingBlanketHeat = Float.parseFloat(breedingBlanketHeat.text);
            blockRecipe.breedingBlanketAugmented = breedingBlanketAugmented.isToggledOn;
        }
        if(block.shielding){
            blockRecipe.shieldingShieldiness = Float.parseFloat(shieldingShieldiness.text);
        }
        if(block.heatsink){
            blockRecipe.heatsinkCooling = Integer.parseInt(heatsinkCooling.text);
        }
        if(block.reflector){
            blockRecipe.reflectorEfficiency = Float.parseFloat(reflectorEfficiency.text);
        }
    }
    @Override
    public void tick(){
        ArrayList<MenuComponent> toRemove = new ArrayList<>();
        boolean hasEmpty = false;
        for(int i = 0; i<inputLegacyNames.components.size(); i++){
            MenuComponent comp = inputLegacyNames.components.get(i);
            if(comp instanceof MenuComponentMinimalistTextBox){
                if(((MenuComponentMinimalistTextBox)comp).text.trim().isEmpty()){
                    if(i==inputLegacyNames.components.size()-1)hasEmpty = true;
                    else toRemove.add(comp);
                }
            }
        }
        if(!hasEmpty)inputLegacyNames.add(new MenuComponentMinimalistTextBox(0, 0, 0, 48, "", true));
        inputLegacyNames.components.removeAll(toRemove);
        super.tick();
    }
    @Override
    public void render(int millisSinceLastTick){
        double w = gui.helper.displayWidth()-inputTexture.width-sidebar.width;
        inputName.width = inputDisplayName.width = w/2;
        inputDisplayName.x = inputName.x+inputName.width;
        inputLegacyNames.width = inputLegacyNamesLabel.width = w;
        outputTexture.x = gui.helper.displayWidth()-outputTexture.width;
        outputName.width = outputDisplayName.width = gui.helper.displayWidth()-outputTexture.width-sidebar.width;
        inputRate.width = outputRate.width = (gui.helper.displayWidth()-sidebar.width)/2;
        outputRate.x = inputRate.x+inputRate.width;
        
        breedingBlanket.width = breedingBlanketEfficiency.width = breedingBlanketHeat.width = breedingBlanketAugmented.width = 
                shielding.width = shieldingShieldiness.width = heatsink.width = heatsinkCooling.width = reflector.width = reflectorEfficiency.width = (gui.helper.displayWidth()-sidebar.width)/3;

        breedingBlanket.x = sidebar.width;//column 1
        reflector.x = heatsink.x = sidebar.width+(gui.helper.displayWidth()-sidebar.width)/3;//column 2
        shielding.x = sidebar.width+(gui.helper.displayWidth()-sidebar.width)*2/3;//column 3
        
        breedingBlanketEfficiency.x = breedingBlanketHeat.x = breedingBlanketAugmented.x = breedingBlanket.x;
        shieldingShieldiness.x = shielding.x;
        heatsinkCooling.x = heatsink.x;
        reflectorEfficiency.x = reflector.x;
        super.render(millisSinceLastTick);
    }
}