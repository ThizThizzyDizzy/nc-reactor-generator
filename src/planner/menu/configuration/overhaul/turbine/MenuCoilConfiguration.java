package planner.menu.configuration.overhaul.turbine;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import planner.Core;
import multiblock.configuration.overhaul.turbine.Coil;
import planner.file.FileFormat;
import planner.Main;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistOptionButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.Sys;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorLevel;
public class MenuCoilConfiguration extends Menu{
    private final MenuComponentMinimalistTextBox name = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "Name", true)).setTooltip("The name of this coil. This should never change");
    private final MenuComponentMinimalistButton texture = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Select Texture", true, true)).setTooltip("Change the texture for this coil");
    private final MenuComponentMinimalistTextBox efficiency = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistOptionButton connector = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Connector", true, true, 0, "FALSE", "TRUE"));
    private final MenuComponentMinimalistOptionButton bearing = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Bearing", true, true, 0, "FALSE", "TRUE"));
    private final MenuComponentMinimalistButton rules = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Placement Rules", true, true).setTooltip("Add, remove, or modify placement rules"));
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private final Coil coil;
    private final int numComps = 16;
    public MenuCoilConfiguration(GUI gui, Menu parent, Coil coil){
        super(gui, parent);
        texture.addActionListener((e) -> {
            Core.createFileChooser((file, format) -> {
                try{
                    BufferedImage img = ImageIO.read(file);
                    if(img==null)return;
                    if(img.getWidth()!=img.getHeight()){
                        if(Main.hasAWT){
                            javax.swing.JOptionPane.showMessageDialog(null, "Image is not square!", "Error loading image", javax.swing.JOptionPane.ERROR_MESSAGE);
                        }else{
                            Sys.error(ErrorLevel.minor, "Image is not square!", null, ErrorCategory.fileIO);
                        }
                        return;
                    }
                    coil.setTexture(img);
                }catch(IOException ex){}
            }, FileFormat.PNG);
        });
        rules.addActionListener((e) -> {
            gui.open(new MenuPlacementRulesConfiguration(gui, this, coil));
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
        this.coil = coil;
    }
    @Override
    public void onGUIOpened(){
        name.text = coil.name;
        efficiency.text = coil.efficiency+"";
        connector.setIndex(coil.connector?1:0);
        bearing.setIndex(coil.bearing?1:0);
    }
    @Override
    public void onGUIClosed(){
        coil.name = name.text;
        coil.efficiency = Float.parseFloat(efficiency.text);
        coil.connector = connector.getIndex()==1;
        coil.bearing = bearing.getIndex()==1;
        super.onGUIClosed();
    }
    @Override
    public void render(int millisSinceLastTick){
        drawRect(0, gui.helper.displayHeight()/numComps, gui.helper.displayHeight()/numComps, gui.helper.displayHeight()/numComps*2, Core.getTexture(coil.texture));
        efficiency.width = gui.helper.displayWidth()*.75;
        efficiency.x = gui.helper.displayWidth()-efficiency.width;
        bearing.width = connector.width = name.width = rules.width = back.width = gui.helper.displayWidth();
        texture.x = texture.height = bearing.height = connector.height = efficiency.height = name.height = rules.height = back.height = gui.helper.displayHeight()/numComps;
        texture.width = gui.helper.displayWidth()-texture.x;
        texture.y = name.height;
        efficiency.y = texture.y+texture.height;
        connector.y = efficiency.y+efficiency.height;
        bearing.y = connector.y+connector.height;
        rules.y = bearing.y+bearing.height;
        back.y = gui.helper.displayHeight()-back.height;
        Core.applyColor(Core.theme.getTextColor());
        drawText(0, gui.helper.displayHeight()/numComps*2, gui.helper.displayWidth()*.25, gui.helper.displayHeight()/numComps*3, "Efficiency");
        Core.applyWhite();
        super.render(millisSinceLastTick);
    }
}