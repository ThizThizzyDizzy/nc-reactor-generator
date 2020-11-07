package planner.menu.configuration.underhaul.fissionsfr;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import planner.Core;
import multiblock.configuration.underhaul.fissionsfr.Block;
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
public class MenuBlockConfiguration extends Menu{
    private final MenuComponentMinimalistTextBox name = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "Name", true)).setTooltip("The name of this block. This should never change");
    private final MenuComponentMinimalistButton texture = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Select Texture", true, true).setTooltip("Change the texture of this block"));
    private final MenuComponentMinimalistTextBox cooling = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter());
    private final MenuComponentMinimalistOptionButton fuelCell = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Fuel Cell", true, true, 0, "FALSE", "TRUE"));
    private final MenuComponentMinimalistOptionButton moderator = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Moderator", true, true, 0, "FALSE", "TRUE"));
    private final MenuComponentMinimalistTextBox active = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true)).setTooltip("If set, this block is an active cooler\nThe value here is the fluid it takes an an input");
    private final MenuComponentMinimalistButton rules = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Placement Rules", true, true));
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private final Block block;
    private final int numComps = 16;
    public MenuBlockConfiguration(GUI gui, Menu parent, Block block){
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
                    block.setTexture(img);
                }catch(IOException ex){}
            }, FileFormat.PNG);
        });
        rules.addActionListener((e) -> {
            gui.open(new MenuPlacementRulesConfiguration(gui, this, block));
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
        this.block = block;
    }
    @Override
    public void onGUIOpened(){
        name.text = block.name;
        cooling.text = block.cooling+"";
        fuelCell.setIndex(block.fuelCell?1:0);
        moderator.setIndex(block.moderator?1:0);
        active.text = block.active==null?"":block.active;
    }
    @Override
    public void onGUIClosed(){
        block.name = name.text;
        block.cooling = Integer.parseInt(cooling.text);
        block.fuelCell = fuelCell.getIndex()==1;
        block.moderator = moderator.getIndex()==1;
        block.active = active.text.trim().isEmpty()?null:active.text;
        super.onGUIClosed();
    }
    @Override
    public void render(int millisSinceLastTick){
        drawRect(0, gui.helper.displayHeight()/numComps, gui.helper.displayHeight()/numComps, gui.helper.displayHeight()/numComps*2, Core.getTexture(block.texture));
        active.width = cooling.width = gui.helper.displayWidth()*.75;
        active.x = cooling.x = gui.helper.displayWidth()-cooling.width;
        moderator.width = fuelCell.width = name.width = rules.width = back.width = gui.helper.displayWidth();
        texture.x = texture.height = active.height = moderator.height = fuelCell.height = cooling.height = name.height = rules.height = back.height = gui.helper.displayHeight()/numComps;
        texture.width = gui.helper.displayWidth()-texture.x;
        texture.y = name.height;
        cooling.y = texture.y+texture.height;
        fuelCell.y = cooling.y+cooling.height;
        moderator.y = fuelCell.y+fuelCell.height;
        active.y = moderator.y+moderator.height;
        rules.y = active.y+active.height;
        back.y = gui.helper.displayHeight()-back.height;
        Core.applyColor(Core.theme.getTextColor());
        drawText(0, gui.helper.displayHeight()/numComps*2, gui.helper.displayWidth()*.25, gui.helper.displayHeight()/numComps*3, "Cooling");
        drawText(0, gui.helper.displayHeight()/numComps*5, gui.helper.displayWidth()*.25, gui.helper.displayHeight()/numComps*6, "Active");
        Core.applyWhite();
        super.render(millisSinceLastTick);
    }
}