package planner.menu.configuration.overhaul.fissionsfr;
import multiblock.configuration.Configuration;
import multiblock.configuration.overhaul.fissionsfr.Block;
import multiblock.configuration.overhaul.fissionsfr.BlockRecipe;
import planner.menu.configuration.PartConfigurationMenu;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuBlockRecipeConfiguration extends PartConfigurationMenu{
    public MenuBlockRecipeConfiguration(GUI gui, Menu parent, Configuration configuration, Block block, BlockRecipe blockRecipe){
        super(gui, parent, configuration, "Block Recipe");
        addMainSection("Input", blockRecipe::getInputTexture, blockRecipe::setInputTexture, "The ingame name of the block recipe input. Must be namespace:name or namespace:name:metadata\\n(Metadata should be included if and only if the item has metadata, regardless of wheather it's 0 or not)", "The user-friendly name of the block recipe input.", ()->{return blockRecipe.inputName;}, ()->{return blockRecipe.inputDisplayName;}, ()->{return blockRecipe.inputLegacyNames;}, blockRecipe::setInputName, blockRecipe::setInputDisplayName, blockRecipe::setInputLegacyNames);
        addSecondarySection("Output", blockRecipe::getOutputTexture, blockRecipe::setOutputTexture, "The ingame name of the block recipe output. Must be namespace:name or namespace:name:metadata\\n(Metadata should be included if and only if the item has metadata, regardless of wheather it's 0 or not)", "The user-friendly name of the block recipe output.", ()->{return blockRecipe.outputName;}, ()->{return blockRecipe.outputDisplayName;}, blockRecipe::setOutputName, blockRecipe::setOutputDisplayName);
        addSettingInt("Input Rate", blockRecipe::getInputRate, blockRecipe::setInputRate);
        addSettingInt("Output Rate", blockRecipe::getOutputRate, blockRecipe::setOutputRate);
        addColumnSectionLabel("Fuel Cell", block::isFuelCell);
        addColumnSettingFloat("Efficiency", blockRecipe::getFuelCellEfficiency, blockRecipe::setFuelCellEfficiency);
        addColumnSettingInt("Heat", blockRecipe::getFuelCellHeat, blockRecipe::setFuelCellHeat);
        addColumnSettingInt("Criticality", blockRecipe::getFuelCellCriticality, blockRecipe::setFuelCellCriticality);
        addColumnSettingBoolean("Self-Priming", blockRecipe::isFuelCellSelfPriming, blockRecipe::setFuelCellSelfPriming);
        addColumnSectionLabel("Heat Sink", block::isHeatsink);
        addColumnSettingInt("Cooling", blockRecipe::getHeatsinkCooling, blockRecipe::setHeatsinkCooling);
        finishColumn();
        addColumnSectionLabel("Reflector", block::isReflector);
        addColumnSettingFloat("Efficiency", blockRecipe::getReflectorEfficiency, blockRecipe::setReflectorEfficiency);
        addColumnSettingFloat("Reflectivity", blockRecipe::getReflectorReflectivity, blockRecipe::setReflectorReflectivity);
        addColumnSectionLabel("Moderator", block::isModerator);
        addColumnSettingFloat("Efficiency", blockRecipe::getModeratorEfficiency, blockRecipe::setModeratorEfficiency);
        addColumnSettingInt("Flux", blockRecipe::getModeratorFlux, blockRecipe::setModeratorFlux);
        addColumnSettingBoolean("Active", blockRecipe::isModeratorActive, blockRecipe::setModeratorActive);
        finishColumn();
        addColumnSectionLabel("Irradiator", block::isIrradiator);
        addColumnSettingFloat("Efficiency", blockRecipe::getIrradiatorEfficiency, blockRecipe::setIrradiatorEfficiency);
        addColumnSettingFloat("Heat", blockRecipe::getIrradiatorHeat, blockRecipe::setIrradiatorHeat);
        addColumnSectionLabel("Neutron Shield", block::isShield);
        addColumnSettingInt("Heat per Flux", blockRecipe::getShieldHeat, blockRecipe::setShieldHeat);
        addColumnSettingFloat("Efficiency", blockRecipe::getShieldEfficiency, blockRecipe::setShieldEfficiency);
        finishColumnSection();
    }
}