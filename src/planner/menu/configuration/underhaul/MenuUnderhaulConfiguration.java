package planner.menu.configuration.underhaul;
import multiblock.configuration.Configuration;
import multiblock.configuration.underhaul.fissionsfr.FissionSFRConfiguration;
import planner.Core;
import planner.menu.configuration.underhaul.fissionsfr.MenuFissionSFRConfiguration;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuUnderhaulConfiguration extends Menu{
    private final MenuComponentMinimalistButton fissionSFR;
    private final MenuComponentMinimalistButton deleteSFR = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Delete (Shift)", false, true).setTooltip("Delete the Underhaul SFR configuration"));
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private final Configuration configuration;
    public MenuUnderhaulConfiguration(GUI gui, Menu parent, Configuration configuration){
        super(gui, parent);
        fissionSFR = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Solid-Fueled Fission Configuration", configuration.underhaul.fissionSFR!=null, true).setTooltip("Modify the Underhaul SFR configuration"));
        fissionSFR.addActionListener((e) -> {
            gui.open(new MenuFissionSFRConfiguration(gui, this, configuration));
        });
        deleteSFR.addActionListener((e) -> {
            if(configuration.underhaul.fissionSFR==null){
                configuration.underhaul.fissionSFR = new FissionSFRConfiguration();
            }else{
                configuration.underhaul.fissionSFR = null;
            }
            onGUIOpened();
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
        this.configuration = configuration;
    }
    @Override
    public void onGUIOpened(){
        fissionSFR.enabled = configuration.underhaul.fissionSFR!=null&&Core.configuration.underhaul.fissionSFR!=null;
    }
    @Override
    public void render(int millisSinceLastTick){
        if(configuration.underhaul.fissionSFR==null){
            deleteSFR.enabled = (configuration.addon&&Core.configuration.underhaul.fissionSFR==null)?false:(Core.isShiftPressed());
        }else{
            deleteSFR.enabled = Core.isShiftPressed();
        }
        deleteSFR.label = (configuration.underhaul.fissionSFR==null?"Create":"Delete")+" (Shift)";
        back.width = gui.helper.displayWidth();
        fissionSFR.width = gui.helper.displayWidth()*3/4;
        deleteSFR.width = gui.helper.displayWidth()/4;
        deleteSFR.x = fissionSFR.width;
        deleteSFR.height = fissionSFR.height = back.height = gui.helper.displayHeight()/16;
        back.y = gui.helper.displayHeight()-back.height;
        super.render(millisSinceLastTick);
    }
}