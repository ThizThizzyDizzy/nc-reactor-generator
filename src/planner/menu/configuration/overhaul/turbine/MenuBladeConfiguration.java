package planner.menu.configuration.overhaul.turbine;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import planner.Core;
import multiblock.configuration.overhaul.turbine.Blade;
import planner.FileChooserResultListener;
import planner.FileFormat;
import planner.Main;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistOptionButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import simplelibrary.opengl.gui.GUI;
import planner.menu.Menu;
import simplelibrary.Sys;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorLevel;
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
        drawRect(0, Core.helper.displayHeight()/numComps, Core.helper.displayHeight()/numComps, Core.helper.displayHeight()/numComps*2, Core.getTexture(blade.texture));
        stator.width = Core.helper.displayWidth();
        efficiency.width = expansion.width = Core.helper.displayWidth()*.75;
        efficiency.x = expansion.x = Core.helper.displayWidth()-efficiency.width;
        name.width = back.width = Core.helper.displayWidth();
        texture.x = texture.height = stator.height = efficiency.height = expansion.height = name.height = back.height = Core.helper.displayHeight()/numComps;
        texture.width = Core.helper.displayWidth()-texture.x;
        texture.y = name.height;
        efficiency.y = texture.y+texture.height;
        stator.y = expansion.y+expansion.height;
        expansion.y = efficiency.y+efficiency.height;
        back.y = Core.helper.displayHeight()-back.height;
        Core.applyColor(Core.theme.getTextColor());
        drawText(0, Core.helper.displayHeight()/numComps*2, Core.helper.displayWidth()*.25, Core.helper.displayHeight()/numComps*3, "Efficiency");
        drawText(0, Core.helper.displayHeight()/numComps*3, Core.helper.displayWidth()*.25, Core.helper.displayHeight()/numComps*4, "Expansion");
        Core.applyWhite();
        super.render(millisSinceLastTick);
    }
}