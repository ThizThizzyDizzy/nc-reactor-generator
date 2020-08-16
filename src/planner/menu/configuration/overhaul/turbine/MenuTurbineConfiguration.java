package planner.menu.configuration.overhaul.turbine;
import org.lwjgl.opengl.Display;
import planner.Core;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import static simplelibrary.opengl.Renderer2D.drawText;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuTurbineConfiguration extends Menu{
    private final MenuComponentMinimalistButton coils = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Blocks", true, true));
    private final MenuComponentMinimalistButton blades = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Blades", true, true));
    private final MenuComponentMinimalistButton recipes = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Recipes", true, true));
    private final MenuComponentMinimalistTextBox minWidth = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter());
    private final MenuComponentMinimalistTextBox minLength = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter());
    private final MenuComponentMinimalistTextBox maxSize = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter());
    private final MenuComponentMinimalistTextBox fluidPerBlade = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter());
    private final MenuComponentMinimalistTextBox throughputEfficiencyLeniency = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistTextBox throughputFactor = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistTextBox powerBonus = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    public MenuTurbineConfiguration(GUI gui, Menu parent){
        super(gui, parent);
        coils.addActionListener((e) -> {
            gui.open(new MenuCoilsConfiguration(gui, this));
        });
        blades.addActionListener((e) -> {
            gui.open(new MenuBladesConfiguration(gui, this));
        });
        recipes.addActionListener((e) -> {
            gui.open(new MenuRecipesConfiguration(gui, this));
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
    }
    @Override
    public void onGUIOpened(){
        coils.label = "Coils ("+Core.configuration.overhaul.turbine.coils.size()+")";
        blades.label = "Blades ("+Core.configuration.overhaul.turbine.blades.size()+")";
        recipes.label = "Recipes ("+Core.configuration.overhaul.turbine.recipes.size()+")";
        minWidth.text = Core.configuration.overhaul.turbine.minWidth+"";
        minLength.text = Core.configuration.overhaul.turbine.minLength+"";
        maxSize.text = Core.configuration.overhaul.turbine.maxSize+"";
        fluidPerBlade.text = Core.configuration.overhaul.turbine.fluidPerBlade+"";
        throughputEfficiencyLeniency.text = Core.configuration.overhaul.turbine.throughputEfficiencyLeniency+"";
        throughputFactor.text = Core.configuration.overhaul.turbine.throughputFactor+"";
        powerBonus.text = Core.configuration.overhaul.turbine.powerBonus+"";
    }
    @Override
    public void onGUIClosed(){
        Core.configuration.overhaul.turbine.minWidth = Integer.parseInt(minWidth.text);
        Core.configuration.overhaul.turbine.minLength = Integer.parseInt(minLength.text);
        Core.configuration.overhaul.turbine.maxSize = Integer.parseInt(maxSize.text);
        Core.configuration.overhaul.turbine.fluidPerBlade = Integer.parseInt(fluidPerBlade.text);
        Core.configuration.overhaul.turbine.throughputEfficiencyLeniency = Float.parseFloat(throughputEfficiencyLeniency.text);
        Core.configuration.overhaul.turbine.throughputFactor = Float.parseFloat(throughputFactor.text);
        Core.configuration.overhaul.turbine.powerBonus = Float.parseFloat(powerBonus.text);
    }
    @Override
    public void render(int millisSinceLastTick){
        minWidth.width = minLength.width = maxSize.width = fluidPerBlade.width = throughputEfficiencyLeniency.width = throughputFactor.width = powerBonus.width = Display.getWidth()*.75;
        minWidth.x = minLength.x = maxSize.x = fluidPerBlade.x = throughputEfficiencyLeniency.x = throughputFactor.x = powerBonus.x = Display.getWidth()*.25;
        coils.width = blades.width = recipes.width = back.width = Display.getWidth();
        minWidth.height = minLength.height = maxSize.height = fluidPerBlade.height = throughputEfficiencyLeniency.height = throughputFactor.height = powerBonus.height = coils.height = blades.height = recipes.height = back.height = Display.getHeight()/16;
        blades.y = coils.height;
        recipes.y = blades.y+blades.height;
        minWidth.y = recipes.y+recipes.height;
        minLength.y = minWidth.y+minWidth.height;
        maxSize.y = minLength.y+minLength.height;
        fluidPerBlade.y = maxSize.y+maxSize.height;
        throughputEfficiencyLeniency.y = fluidPerBlade.y+fluidPerBlade.height;
        throughputFactor.y = throughputEfficiencyLeniency.y+throughputEfficiencyLeniency.height;
        powerBonus.y = throughputFactor.y+throughputFactor.height;
        back.y = Display.getHeight()-back.height;
        Core.applyColor(Core.theme.getTextColor());
        drawText(0, minWidth.y+Display.getHeight()/64, Display.getWidth()*.25, minWidth.y+minWidth.height-Display.getHeight()/64, "Minimum turbine diameter");
        drawText(0, minLength.y+Display.getHeight()/64, Display.getWidth()*.25, minLength.y+minLength.height-Display.getHeight()/64, "Minimum turbine length");
        drawText(0, maxSize.y+Display.getHeight()/64, Display.getWidth()*.25, maxSize.y+maxSize.height-Display.getHeight()/64, "Maximum reactor size");
        drawText(0, fluidPerBlade.y+Display.getHeight()/64, Display.getWidth()*.25, fluidPerBlade.y+fluidPerBlade.height-Display.getHeight()/64, "Fluid per blade (mb)");
        drawText(0, throughputEfficiencyLeniency.y+Display.getHeight()/64, Display.getWidth()*.25, throughputEfficiencyLeniency.y+throughputEfficiencyLeniency.height-Display.getHeight()/64, "Throughput Eff. Leniency");
        drawText(0, throughputFactor.y+Display.getHeight()/64, Display.getWidth()*.25, throughputFactor.y+throughputFactor.height-Display.getHeight()/64, "Throughput Factor");
        drawText(0, powerBonus.y+Display.getHeight()/64, Display.getWidth()*.25, powerBonus.y+powerBonus.height-Display.getHeight()/64, "Power Bonus");
        Core.applyWhite();
        super.render(millisSinceLastTick);
    }
}