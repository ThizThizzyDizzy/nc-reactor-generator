package net.ncplanner.plannerator.planner.menu.configuration.overhaul.fissionmsr;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe;
import net.ncplanner.plannerator.planner.menu.configuration.PartConfigurationMenu;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuBlockRecipeConfiguration extends PartConfigurationMenu{
    public MenuBlockRecipeConfiguration(GUI gui, Menu parent, Configuration configuration, Block block, BlockRecipe blockRecipe){
        super(gui, parent, configuration, "Block Recipe");
        addMainSection("Input", blockRecipe::getInputTexture, blockRecipe::setInputTexture, "The ingame name of the block recipe input. This should be the name of the fluid itself, not the fluid block or bucket.", "The user-friendly name of the block recipe input.", ()->{return blockRecipe.inputName;}, ()->{return blockRecipe.inputDisplayName;}, ()->{return blockRecipe.inputLegacyNames;}, blockRecipe::setInputName, blockRecipe::setInputDisplayName, blockRecipe::setInputLegacyNames);
        addSecondarySection("Output", blockRecipe::getOutputTexture, blockRecipe::setOutputTexture, "The ingame name of the block recipe output. This should be the name of the fluid itself, not the fluid block or bucket.", "The user-friendly name of the block recipe output.", ()->{return blockRecipe.outputName;}, ()->{return blockRecipe.outputDisplayName;}, blockRecipe::setOutputName, blockRecipe::setOutputDisplayName);
        addSettingInt("Input Rate", blockRecipe::getInputRate, blockRecipe::setInputRate);
        addSettingInt("Output Rate", blockRecipe::getOutputRate, blockRecipe::setOutputRate);
        addColumnSectionLabel("Fuel Vessel", block::isFuelVessel);
        addColumnSettingFloat("Efficiency", blockRecipe::getFuelVesselEfficiency, blockRecipe::setFuelVesselEfficiency);
        addColumnSettingInt("Heat", blockRecipe::getFuelVesselHeat, blockRecipe::setFuelVesselHeat);
        addColumnSettingInt("Criticality", blockRecipe::getFuelVesselCriticality, blockRecipe::setFuelVesselCriticality);
        addColumnSettingBoolean("Self-Priming", blockRecipe::isFuelVesselSelfPriming, blockRecipe::setFuelVesselSelfPriming);
        addColumnSectionLabel("Heater", block::isHeater);
        addColumnSettingInt("Cooling", blockRecipe::getHeaterCooling, blockRecipe::setHeaterCooling);
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