package net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.turbine;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Recipe;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.configuration.PartConfigurationMenu;
public class MenuRecipeConfiguration extends PartConfigurationMenu{
    public MenuRecipeConfiguration(GUI gui, Menu parent, Configuration configuration, Recipe recipe){
        super(gui, parent, configuration, "Recipe");
        addMainSection("Input", recipe::getInputTexture, recipe::setInputTexture, "The ingame name of the recipe input. This should be the name of the fluid itself, not the fluid block or bucket.", "The user-friendly name of the recipe input.", ()->{return recipe.inputName;}, ()->{return recipe.inputDisplayName;}, ()->{return recipe.inputLegacyNames;}, recipe::setInputName, recipe::setInputDisplayName, recipe::setInputLegacyNames);
        addSecondarySection("Output", recipe::getOutputTexture, recipe::setOutputTexture, "The ingame name of the recipe output. This should be the name of the fluid itself, not the fluid block or bucket.","The user-friendly name of the recipe output.", ()->{return recipe.outputName;}, ()->{return recipe.outputDisplayName;}, recipe::setOutputName, recipe::setOutputDisplayName);
        addSettingDouble("Power", recipe::getPower, recipe::setPower);
        addSettingDouble("Expansion Coefficient", recipe::getCoefficient, recipe::setCoefficient);
        finishSettingRow();
    }
}