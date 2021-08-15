package planner.menu.configuration.overhaul.fusion;
import multiblock.configuration.Configuration;
import multiblock.configuration.overhaul.fusion.CoolantRecipe;
import planner.menu.configuration.PartConfigurationMenu;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuCoolantRecipeConfiguration extends PartConfigurationMenu{
    public MenuCoolantRecipeConfiguration(GUI gui, Menu parent, Configuration configuration, CoolantRecipe coolantRecipe){
        super(gui, parent, configuration, "Coolant Recipe");
        addMainSection("Input", coolantRecipe::getInputTexture, coolantRecipe::setInputTexture, "The ingame name of the coolant recipe input. This should be the name of the fluid itself, not the fluid block or bucket.", "The user-friendly name of the coolant recipe input.", ()->{return coolantRecipe.inputName;}, ()->{return coolantRecipe.inputDisplayName;}, ()->{return coolantRecipe.inputLegacyNames;}, coolantRecipe::setInputName, coolantRecipe::setInputDisplayName, coolantRecipe::setInputLegacyNames);
        addSecondarySection("Output", coolantRecipe::getOutputTexture, coolantRecipe::setOutputTexture, "The ingame name of the coolant recipe output. This should be the name of the fluid itself, not the fluid block or bucket.", "The user-friendly name of the coolant recipe output.", ()->{return coolantRecipe.outputName;}, ()->{return coolantRecipe.outputDisplayName;}, coolantRecipe::setOutputName, coolantRecipe::setOutputDisplayName);
        addSettingFloat("Output Ratio", coolantRecipe::getOutputRatio, coolantRecipe::setOutputRatio);
        addSettingInt("Heat", coolantRecipe::getHeat, coolantRecipe::setHeat);
        finishSettingRow();
    }
}