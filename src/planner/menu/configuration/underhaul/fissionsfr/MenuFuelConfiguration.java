package planner.menu.configuration.underhaul.fissionsfr;
import planner.core.PlannerImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import multiblock.configuration.Configuration;
import multiblock.configuration.underhaul.fissionsfr.Fuel;
import planner.Core;
import planner.Main;
import planner.file.FileFormat;
import planner.menu.component.MenuComponentLabel;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import planner.menu.configuration.ConfigurationMenu;
import simplelibrary.Sys;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorLevel;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuFuelConfiguration extends ConfigurationMenu{
    private final Fuel fuel;
    private final MenuComponentMinimalistButton texture;
    private final MenuComponentMinimalistTextBox name, displayName, power, heat, time;
    private final MenuComponentLabel legacyNamesLabel;
    private final MenuComponentMinimaList legacyNames;
    public MenuFuelConfiguration(GUI gui, Menu parent, Configuration configuration, Fuel fuel){
        super(gui, parent, configuration, fuel.getDisplayName());
        texture = add(new MenuComponentMinimalistButton(sidebar.width, 0, 192, 192, "Set Texture", true, true){
            @Override
            public void render(){
                if(fuel.texture!=null){
                    Core.applyWhite();
                    drawRect(x, y, x+width, y+height, Core.getTexture(fuel.texture));
                    return;
                }
                super.render();
            }
            @Override
            public boolean onFilesDropped(double x, double y, String[] files){
                for(String s : files){
                    if(s.endsWith(".png")){
                        try{
                            PlannerImage img = PlannerImage.fromAWT(ImageIO.read(new File(s)));
                            if(img==null)continue;
                            if(img.getWidth()!=img.getHeight()){
                                if(Main.hasAWT){
                                    javax.swing.JOptionPane.showMessageDialog(null, "Image is not square!", "Error loading image", javax.swing.JOptionPane.ERROR_MESSAGE);
                                }else{
                                    Sys.error(ErrorLevel.minor, "Image is not square!", null, ErrorCategory.fileIO, false);
                                }
                                continue;
                            }
                            fuel.setTexture(img);
                        }catch(IOException ex){}
                    }
                }
                return super.onFilesDropped(x, y, files);
            }
        }.setTooltip("Click to change texture\nYou can also drag-and-drop texture files here"));
        name = add(new MenuComponentMinimalistTextBox(texture.x+texture.width, 0, 0, 48, "", true, "Name").setTooltip("The ingame name of this fuel. Must be namespace:name or namespace:name:metadata\n(Metadata should be included if and only if the item has metadata, regardless of wheather it's 0 or not)"));
        displayName = add(new MenuComponentMinimalistTextBox(name.x, 0, 0, 48, "", true, "Display Name").setTooltip("The user-friendly name of this fuel."));
        legacyNamesLabel = add(new MenuComponentLabel(name.x, 48, 0, 32, "Legacy Names", true).setTooltip("A list of old names for NCPF back-compatibility"));
        legacyNames = add(new MenuComponentMinimaList(name.x, 48+32, 0, texture.height-legacyNamesLabel.height-name.height, 16));
        power = add(new MenuComponentMinimalistTextBox(sidebar.width, texture.height, 0, 48, "", true, "Power").setFloatFilter());
        heat = add(new MenuComponentMinimalistTextBox(sidebar.width, texture.height, 0, 48, "", true, "Heat").setFloatFilter());
        time = add(new MenuComponentMinimalistTextBox(sidebar.width, texture.height, 0, 48, "", true, "Time").setIntFilter());
        texture.addActionListener((e) -> {
            Core.createFileChooser((file, format) -> {
                try{
                    PlannerImage img = PlannerImage.fromAWT(ImageIO.read(file));
                    if(img==null)return;
                    if(img.getWidth()!=img.getHeight()){
                        if(Main.hasAWT){
                            javax.swing.JOptionPane.showMessageDialog(null, "Image is not square!", "Error loading image", javax.swing.JOptionPane.ERROR_MESSAGE);
                        }else{
                            Sys.error(ErrorLevel.minor, "Image is not square!", null, ErrorCategory.fileIO, false);
                        }
                        return;
                    }
                    fuel.setTexture(img);
                }catch(IOException ex){}
            }, FileFormat.PNG);
        });
        this.fuel = fuel;
    }
    @Override
    public void onGUIOpened(){
        name.text = fuel.name;
        displayName.text = fuel.displayName==null?"":fuel.displayName;
        legacyNames.components.clear();
        for(String s : fuel.legacyNames){
            legacyNames.add(new MenuComponentMinimalistTextBox(0, 0, 0, 48, s, true));
        }
        power.text = fuel.power+"";
        heat.text = fuel.heat+"";
        time.text = fuel.time+"";
    }
    @Override
    public void onGUIClosed(){
        fuel.name = name.text;
        fuel.displayName = displayName.text.trim().isEmpty()?null:displayName.text;
        fuel.legacyNames.clear();
        for(MenuComponent c : legacyNames.components){
            if(c instanceof MenuComponentMinimalistTextBox){
                if(((MenuComponentMinimalistTextBox)c).text.trim().isEmpty())continue;
                fuel.legacyNames.add(((MenuComponentMinimalistTextBox)c).text);
            }
        }
        fuel.power = Float.parseFloat(power.text);
        fuel.heat = Float.parseFloat(heat.text);
        fuel.time = Integer.parseInt(time.text);
    }
    @Override
    public void tick(){
        ArrayList<MenuComponent> toRemove = new ArrayList<>();
        boolean hasEmpty = false;
        for(int i = 0; i<legacyNames.components.size(); i++){
            MenuComponent comp = legacyNames.components.get(i);
            if(comp instanceof MenuComponentMinimalistTextBox){
                if(((MenuComponentMinimalistTextBox)comp).text.trim().isEmpty()){
                    if(i==legacyNames.components.size()-1)hasEmpty = true;
                    else toRemove.add(comp);
                }
            }
        }
        if(!hasEmpty)legacyNames.add(new MenuComponentMinimalistTextBox(0, 0, 0, 48, "", true));
        legacyNames.components.removeAll(toRemove);
        super.tick();
    }
    @Override
    public void render(int millisSinceLastTick){
        double w = gui.helper.displayWidth()-texture.width-sidebar.width;
        name.width = displayName.width = w/2;
        displayName.x = name.x+name.width;
        legacyNames.width = legacyNamesLabel.width = w;
        power.width = heat.width = time.width = (w+texture.width)/3;
        heat.x = power.x+power.width;
        time.x = heat.x+heat.width;
        super.render(millisSinceLastTick);
    }
}