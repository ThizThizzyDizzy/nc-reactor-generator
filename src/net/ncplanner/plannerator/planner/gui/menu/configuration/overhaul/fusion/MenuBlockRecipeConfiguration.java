package net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fusion;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Block;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.BlockRecipe;
import net.ncplanner.plannerator.planner.gui.menu.configuration.PartConfigurationMenu;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
public class MenuBlockRecipeConfiguration extends PartConfigurationMenu{
    public MenuBlockRecipeConfiguration(GUI gui, Menu parent, Configuration configuration, Block block, BlockRecipe blockRecipe){
        super(gui, parent, configuration, "Block Recipe");
        addMainSection("Input", blockRecipe::getInputTexture, blockRecipe::setInputTexture, "The ingame name of the block recipe input. Must be namespace:name or namespace:name:metadata\\n(Metadata should be included if and only if the item has metadata, regardless of wheather it's 0 or not)", "The user-friendly name of the block recipe input.", ()->{return blockRecipe.inputName;}, ()->{return blockRecipe.inputDisplayName;}, ()->{return blockRecipe.inputLegacyNames;}, blockRecipe::setInputName, blockRecipe::setInputDisplayName, blockRecipe::setInputLegacyNames);
        addSecondarySection("Output", blockRecipe::getOutputTexture, blockRecipe::setOutputTexture, "The ingame name of the block recipe output. Must be namespace:name or namespace:name:metadata\\n(Metadata should be included if and only if the item has metadata, regardless of wheather it's 0 or not)", "The user-friendly name of the block recipe output.", ()->{return blockRecipe.outputName;}, ()->{return blockRecipe.outputDisplayName;}, blockRecipe::setOutputName, blockRecipe::setOutputDisplayName);
        addSettingInt("Input Rate", blockRecipe::getInputRate, blockRecipe::setInputRate);
        addSettingInt("Output Rate", blockRecipe::getOutputRate, blockRecipe::setOutputRate);
        addColumnSectionLabel("Breeding Blanket", block::isBreedingBlanket);
        addColumnSettingFloat("Efficiency", block::getBreedingBlanketEfficiency, block::setBreedingBlanketEfficiency);
        addColumnSettingFloat("Heat", block::getBreedingBlanketHeat, block::setBreedingBlanketHeat);
        addColumnSettingBoolean("Augmented", block::isBreedingBlanketAugmented, block::setBreedingBlanketAugmented);
        finishColumn();
        addColumnSectionLabel("Reflector", block::isReflector);
        addColumnSettingFloat("Efficiency", block::getReflectorEfficiency, block::setReflectorEfficiency);
        addColumnSectionLabel("Heat Sink", block::isHeatsink);
        addColumnSettingInt("Cooling", block::getHeatsinkCooling, block::setHeatsinkCooling);
        finishColumn();
        addColumnSectionLabel("Shielding", block::isShielding);
        addColumnSettingFloat("Shieldiness", block::getShieldingShieldiness, block::setShieldingShieldiness);
        finishColumnSection();
    }
}