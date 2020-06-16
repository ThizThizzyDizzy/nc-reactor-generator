package planner.menu.configuration.overhaul.fissionsfr;
import org.lwjgl.opengl.Display;
import planner.Core;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuFissionSFRConfiguration extends Menu{
    private final MenuComponentMinimalistButton blocks = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Blocks", true, true));
    private final MenuComponentMinimalistButton fuels = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Fuels", true, true));
    private final MenuComponentMinimalistButton sources = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Sources", true, true));
    private final MenuComponentMinimalistButton irrecipes = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Irradiator Recipes", true, true));
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    public MenuFissionSFRConfiguration(GUI gui, Menu parent){
        super(gui, parent);
        blocks.addActionListener((e) -> {
            gui.open(new MenuBlocksConfiguration(gui, this));
        });
        fuels.addActionListener((e) -> {
            gui.open(new MenuFuelsConfiguration(gui, this));
        });
        sources.addActionListener((e) -> {
            gui.open(new MenuSourcesConfiguration(gui, this));
        });
        irrecipes.addActionListener((e) -> {
            gui.open(new MenuIrradiatorRecipesConfiguration(gui, this));
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
    }
    @Override
    public void onGUIOpened(){
        blocks.label = "Blocks ("+Core.configuration.overhaul.fissionSFR.blocks.size()+")";
        fuels.label = "Fuels ("+Core.configuration.overhaul.fissionSFR.fuels.size()+")";
        sources.label = "Sources ("+Core.configuration.overhaul.fissionSFR.sources.size()+")";
        irrecipes.label = "Irradiator Recipes ("+Core.configuration.overhaul.fissionSFR.irradiatorRecipes.size()+")";
    }
    @Override
    public void render(int millisSinceLastTick){
        blocks.width = fuels.width = sources.width = irrecipes.width = back.width = Display.getWidth();
        blocks.height = fuels.height = sources.height = irrecipes.height = back.height = Display.getHeight()/16;
        fuels.y = blocks.height;
        sources.y = fuels.y+fuels.height;
        irrecipes.y = sources.y+sources.height;
        back.y = Display.getHeight()-back.height;
        super.render(millisSinceLastTick);
    }
}