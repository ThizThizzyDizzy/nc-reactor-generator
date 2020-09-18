package planner.menu.configuration.overhaul.turbine;
import multiblock.configuration.Configuration;
import planner.Core;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import static simplelibrary.opengl.Renderer2D.drawText;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuTurbineConfiguration extends Menu{
    private final MenuComponentMinimalistButton coils = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Coils", true, true).setTooltip("Add, remove, or modify Coils"));
    private final MenuComponentMinimalistButton blades = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Blades", true, true).setTooltip("Add, remove, or modify Blades"));
    private final MenuComponentMinimalistButton recipes = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Recipes", true, true).setTooltip("Add, remove, or modify Recipes"));
    private final MenuComponentMinimalistTextBox minWidth = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter()).setTooltip("The minimum width of this turbine (diameter)");
    private final MenuComponentMinimalistTextBox minLength = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter()).setTooltip("The minimum length of this turbine");
    private final MenuComponentMinimalistTextBox maxSize = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter()).setTooltip("The maximum size of this turbine");
    private final MenuComponentMinimalistTextBox fluidPerBlade = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter());
    private final MenuComponentMinimalistTextBox throughputEfficiencyLeniency = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistTextBox throughputFactor = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistTextBox powerBonus = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private final Configuration configuration;
    public MenuTurbineConfiguration(GUI gui, Menu parent, Configuration configuration){
        super(gui, parent);
        coils.addActionListener((e) -> {
            gui.open(new MenuCoilsConfiguration(gui, this, configuration));
        });
        blades.addActionListener((e) -> {
            gui.open(new MenuBladesConfiguration(gui, this, configuration));
        });
        recipes.addActionListener((e) -> {
            gui.open(new MenuRecipesConfiguration(gui, this, configuration));
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
        this.configuration = configuration;
    }
    @Override
    public void onGUIOpened(){
        coils.label = "Coils ("+configuration.overhaul.turbine.coils.size()+")";
        blades.label = "Blades ("+configuration.overhaul.turbine.blades.size()+")";
        recipes.label = "Recipes ("+configuration.overhaul.turbine.recipes.size()+")";
        minWidth.text = configuration.overhaul.turbine.minWidth+"";
        minLength.text = configuration.overhaul.turbine.minLength+"";
        maxSize.text = configuration.overhaul.turbine.maxSize+"";
        fluidPerBlade.text = configuration.overhaul.turbine.fluidPerBlade+"";
        throughputEfficiencyLeniency.text = configuration.overhaul.turbine.throughputEfficiencyLeniency+"";
        throughputFactor.text = configuration.overhaul.turbine.throughputFactor+"";
        powerBonus.text = configuration.overhaul.turbine.powerBonus+"";
    }
    @Override
    public void onGUIClosed(){
        configuration.overhaul.turbine.minWidth = Integer.parseInt(minWidth.text);
        configuration.overhaul.turbine.minLength = Integer.parseInt(minLength.text);
        configuration.overhaul.turbine.maxSize = Integer.parseInt(maxSize.text);
        configuration.overhaul.turbine.fluidPerBlade = Integer.parseInt(fluidPerBlade.text);
        configuration.overhaul.turbine.throughputEfficiencyLeniency = Float.parseFloat(throughputEfficiencyLeniency.text);
        configuration.overhaul.turbine.throughputFactor = Float.parseFloat(throughputFactor.text);
        configuration.overhaul.turbine.powerBonus = Float.parseFloat(powerBonus.text);
    }
    @Override
    public void render(int millisSinceLastTick){
        minWidth.width = minLength.width = maxSize.width = fluidPerBlade.width = throughputEfficiencyLeniency.width = throughputFactor.width = powerBonus.width = gui.helper.displayWidth()*.75;
        minWidth.x = minLength.x = maxSize.x = fluidPerBlade.x = throughputEfficiencyLeniency.x = throughputFactor.x = powerBonus.x = gui.helper.displayWidth()*.25;
        coils.width = blades.width = recipes.width = back.width = gui.helper.displayWidth();
        minWidth.height = minLength.height = maxSize.height = fluidPerBlade.height = throughputEfficiencyLeniency.height = throughputFactor.height = powerBonus.height = coils.height = blades.height = recipes.height = back.height = gui.helper.displayHeight()/16;
        blades.y = coils.height;
        recipes.y = blades.y+blades.height;
        minWidth.y = recipes.y+recipes.height;
        minLength.y = minWidth.y+minWidth.height;
        maxSize.y = minLength.y+minLength.height;
        fluidPerBlade.y = maxSize.y+maxSize.height;
        throughputEfficiencyLeniency.y = fluidPerBlade.y+fluidPerBlade.height;
        throughputFactor.y = throughputEfficiencyLeniency.y+throughputEfficiencyLeniency.height;
        powerBonus.y = throughputFactor.y+throughputFactor.height;
        back.y = gui.helper.displayHeight()-back.height;
        if(configuration.addon){
            minWidth.y = minLength.y = maxSize.y = fluidPerBlade.y = throughputEfficiencyLeniency.y = throughputFactor.y = powerBonus.y = -minWidth.height;
        }
        Core.applyColor(Core.theme.getTextColor());
        drawText(0, minWidth.y+gui.helper.displayHeight()/64, gui.helper.displayWidth()*.25, minWidth.y+minWidth.height-gui.helper.displayHeight()/64, "Minimum turbine diameter");
        drawText(0, minLength.y+gui.helper.displayHeight()/64, gui.helper.displayWidth()*.25, minLength.y+minLength.height-gui.helper.displayHeight()/64, "Minimum turbine length");
        drawText(0, maxSize.y+gui.helper.displayHeight()/64, gui.helper.displayWidth()*.25, maxSize.y+maxSize.height-gui.helper.displayHeight()/64, "Maximum reactor size");
        drawText(0, fluidPerBlade.y+gui.helper.displayHeight()/64, gui.helper.displayWidth()*.25, fluidPerBlade.y+fluidPerBlade.height-gui.helper.displayHeight()/64, "Fluid per blade (mb)");
        drawText(0, throughputEfficiencyLeniency.y+gui.helper.displayHeight()/64, gui.helper.displayWidth()*.25, throughputEfficiencyLeniency.y+throughputEfficiencyLeniency.height-gui.helper.displayHeight()/64, "Throughput Eff. Leniency");
        drawText(0, throughputFactor.y+gui.helper.displayHeight()/64, gui.helper.displayWidth()*.25, throughputFactor.y+throughputFactor.height-gui.helper.displayHeight()/64, "Throughput Factor");
        drawText(0, powerBonus.y+gui.helper.displayHeight()/64, gui.helper.displayWidth()*.25, powerBonus.y+powerBonus.height-gui.helper.displayHeight()/64, "Power Bonus");
        Core.applyWhite();
        super.render(millisSinceLastTick);
    }
}