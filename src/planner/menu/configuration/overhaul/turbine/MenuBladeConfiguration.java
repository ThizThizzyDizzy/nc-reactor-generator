package planner.menu.configuration.overhaul.turbine;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import multiblock.configuration.overhaul.turbine.Blade;
import planner.Core;
import planner.Main;
import planner.file.FileFormat;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistOptionButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import simplelibrary.Sys;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorLevel;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuBladeConfiguration extends Menu{
    private final MenuComponentMinimalistTextBox name = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "Name", true)).setTooltip("The name of this blade. This should never change");
    private final MenuComponentMinimalistButton texture = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Select Texture", true, true).setTooltip("Change the texture for this block"));
    private final MenuComponentMinimalistTextBox efficiency = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistTextBox expansion = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistOptionButton stator = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Stator", true, true, 0, "FALSE", "TRUE")).setTooltip("If true, this block will count as a Stator for turbine calculations");
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private final Blade blade;
    private final int numComps = 16;
    public MenuBladeConfiguration(GUI gui, Menu parent, Blade blade){
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
                            Sys.error(ErrorLevel.minor, "Image is not square!", null, ErrorCategory.fileIO, false);
                        }
                        return;
                    }
                    blade.setTexture(img);
                }catch(IOException ex){}
            }, FileFormat.PNG);
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
        this.blade = blade;
    }
    @Override
    public void onGUIOpened(){
        name.text = blade.name;
        efficiency.text = blade.efficiency+"";
        expansion.text = blade.expansion+"";
        stator.setIndex(blade.stator?1:0);
    }
    @Override
    public void onGUIClosed(){
        blade.name = name.text;
        blade.efficiency = Float.parseFloat(efficiency.text);
        blade.expansion = Float.parseFloat(expansion.text);
        blade.stator = stator.getIndex()==1;
        super.onGUIClosed();
    }
    @Override
    public void render(int millisSinceLastTick){
        drawRect(0, gui.helper.displayHeight()/numComps, gui.helper.displayHeight()/numComps, gui.helper.displayHeight()/numComps*2, Core.getTexture(blade.texture));
        stator.width = gui.helper.displayWidth();
        efficiency.width = expansion.width = gui.helper.displayWidth()*.75;
        efficiency.x = expansion.x = gui.helper.displayWidth()-efficiency.width;
        name.width = back.width = gui.helper.displayWidth();
        texture.x = texture.height = stator.height = efficiency.height = expansion.height = name.height = back.height = gui.helper.displayHeight()/numComps;
        texture.width = gui.helper.displayWidth()-texture.x;
        texture.y = name.height;
        efficiency.y = texture.y+texture.height;
        stator.y = expansion.y+expansion.height;
        expansion.y = efficiency.y+efficiency.height;
        back.y = gui.helper.displayHeight()-back.height;
        Core.applyColor(Core.theme.getTextColor());
        drawText(0, gui.helper.displayHeight()/numComps*2, gui.helper.displayWidth()*.25, gui.helper.displayHeight()/numComps*3, "Efficiency");
        drawText(0, gui.helper.displayHeight()/numComps*3, gui.helper.displayWidth()*.25, gui.helper.displayHeight()/numComps*4, "Expansion");
        Core.applyWhite();
        super.render(millisSinceLastTick);
    }
}