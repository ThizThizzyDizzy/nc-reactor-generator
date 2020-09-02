package planner.menu.configuration;
import planner.Core;
import multiblock.configuration.Configuration;
import multiblock.configuration.overhaul.OverhaulConfiguration;
import multiblock.configuration.underhaul.UnderhaulConfiguration;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import planner.menu.configuration.underhaul.MenuUnderhaulConfiguration;
import planner.menu.configuration.overhaul.MenuOverhaulConfiguration;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuConfiguration extends Menu{
    private final Configuration configuration;
    private final MenuComponentMinimalistTextBox name;
    private final MenuComponentMinimalistTextBox overhaulVersion;
    private final MenuComponentMinimalistTextBox underhaulVersion;
    private final MenuComponentMinimalistButton underhaul;
    private final MenuComponentMinimalistButton overhaul;
    private final MenuComponentMinimalistButton deleteUnderhaul = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Delete (Ctrl+Shift)", false, true));
    private final MenuComponentMinimalistButton deleteOverhaul = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Delete (Alt+Shift)", false, true));
    private final MenuComponentMinimalistButton addons = new MenuComponentMinimalistButton(0, 0, 0, 0, "Addons", true, true);
    private final MenuComponentMinimalistButton done = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Done", true, true));
    public MenuConfiguration(GUI gui, Menu parent, Configuration configuration){
        super(gui, parent);
        this.configuration = configuration;
        if(!configuration.addon)add(addons);
        name = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, configuration.name, true));
        overhaulVersion = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, configuration.overhaulVersion, true));
        underhaulVersion = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, configuration.underhaulVersion, true));
        underhaul = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Underhaul Configuration", configuration.underhaul!=null, true));
        overhaul = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Overhaul Configuration", configuration.overhaul!=null, true));
        underhaul.addActionListener((e) -> {
            gui.open(new MenuUnderhaulConfiguration(gui, this, configuration));
        });
        overhaul.addActionListener((e) -> {
            gui.open(new MenuOverhaulConfiguration(gui, this, configuration));
        });
        deleteOverhaul.addActionListener((e) -> {
            if(configuration.overhaul==null){
                configuration.overhaul = new OverhaulConfiguration();
                configuration.overhaulVersion = "0";
            }else{
                configuration.overhaul = null;
                configuration.overhaulVersion = null;
            }
            onGUIOpened();
        });
        deleteUnderhaul.addActionListener((e) -> {
            if(configuration.underhaul==null){
                configuration.underhaul = new UnderhaulConfiguration();
                configuration.underhaulVersion = "0";
            }else{
                configuration.underhaul = null;
                configuration.underhaulVersion = null;
            }
            onGUIOpened();
        });
        addons.addActionListener((e) -> {
            gui.open(new MenuAddonsConfiguration(gui, this));
        });
        done.addActionListener((e) -> {
            gui.open(parent);
        });
    }
    @Override
    public void onGUIOpened(){
        underhaulVersion.editable = underhaul.enabled = configuration.underhaul!=null;
        overhaulVersion.editable = overhaul.enabled = configuration.overhaul!=null;
        name.text = configuration.name==null?"":configuration.name;
        overhaulVersion.text = configuration.overhaulVersion==null?"":configuration.overhaulVersion;
        underhaulVersion.text = configuration.underhaulVersion==null?"":configuration.underhaulVersion;
        addons.label = "Addons ("+configuration.addons.size()+")";
    }
    @Override
    public void onGUIClosed(){
        configuration.name = name.text.trim().isEmpty()?null:name.text;
        if(configuration.overhaul!=null)configuration.overhaulVersion = overhaulVersion.text.trim().isEmpty()?null:overhaulVersion.text;
        if(configuration.underhaul!=null)configuration.underhaulVersion = underhaulVersion.text.trim().isEmpty()?null:underhaulVersion.text;
    }
    @Override
    public void render(int millisSinceLastTick){
        deleteOverhaul.enabled = Core.isAltPressed()&&Core.isShiftPressed();
        deleteUnderhaul.enabled = Core.isControlPressed()&&Core.isShiftPressed();
        deleteOverhaul.label = (configuration.overhaul==null?"Create":"Delete")+" (Alt+Shift)";
        deleteUnderhaul.label = (configuration.underhaul==null?"Create":"Delete")+" (Ctrl+Shift)";
        underhaul.width = overhaul.width = done.width = addons.width = Core.helper.displayWidth();
        name.height = overhaulVersion.height = underhaulVersion.height = addons.height = underhaul.height = overhaul.height = done.height = deleteUnderhaul.height = deleteOverhaul.height = Core.helper.displayHeight()/16;
        name.width = Core.helper.displayWidth()*.75;
        overhaulVersion.width = underhaulVersion.width = Core.helper.displayWidth()/3;
        name.x = overhaulVersion.x = underhaulVersion.x = Core.helper.displayWidth()*.25;
        overhaul.y = name.y+name.height;
        deleteOverhaul.y = overhaulVersion.y = overhaul.y+overhaul.height;
        underhaul.y = overhaulVersion.y+overhaulVersion.height;
        deleteUnderhaul.y = underhaulVersion.y = underhaul.y+underhaul.height;
        deleteUnderhaul.x = underhaulVersion.x+underhaulVersion.width;
        deleteOverhaul.x = overhaulVersion.x+overhaulVersion.width;
        deleteOverhaul.width = Core.helper.displayWidth()-deleteOverhaul.x;
        deleteUnderhaul.width = Core.helper.displayWidth()-deleteUnderhaul.x;
        addons.y = deleteUnderhaul.y+deleteUnderhaul.height;
        done.y = Core.helper.displayHeight()-done.height;
        Core.applyColor(Core.theme.getTextColor());
        drawText(0, name.y, name.x, name.y+name.height, "Name");
        drawText(0, overhaulVersion.y, overhaulVersion.x, overhaulVersion.y+overhaulVersion.height, "Version");
        drawText(0, underhaulVersion.y, underhaulVersion.x, underhaulVersion.y+underhaulVersion.height, "Version");
        super.render(millisSinceLastTick);
    }
}