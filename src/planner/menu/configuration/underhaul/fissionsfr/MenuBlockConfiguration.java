package planner.menu.configuration.underhaul.fissionsfr;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.lwjgl.opengl.Display;
import planner.Core;
import planner.configuration.underhaul.fissionsfr.Block;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistOptionButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuBlockConfiguration extends Menu{
    private final MenuComponentMinimalistTextBox name = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "Name", true));
    private final MenuComponentMinimalistButton texture = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Select Texture", true, true));
    private final MenuComponentMinimalistTextBox cooling = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter());
    private final MenuComponentMinimalistOptionButton fuelCell = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Fuel Cell", true, true, 0, "FALSE", "TRUE"));
    private final MenuComponentMinimalistOptionButton moderator = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Moderator", true, true, 0, "FALSE", "TRUE"));
    private final MenuComponentMinimalistTextBox active = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true));
    private final MenuComponentMinimalistButton rules = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Placement Rules", true, true));
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private final Block block;
    private final int numComps = 16;
    public MenuBlockConfiguration(GUI gui, Menu parent, Block block){
        super(gui, parent);
        texture.addActionListener((e) -> {
            new Thread(() -> {
                JFileChooser chooser = new JFileChooser(new File("file").getAbsoluteFile().getParentFile());
                chooser.setFileFilter(new FileNameExtensionFilter("PNG (.png)", "png"));
                chooser.addActionListener((event) -> {
                    if(event.getActionCommand().equals("ApproveSelection")){
                        File file = chooser.getSelectedFile();
                        try{
                            BufferedImage img = ImageIO.read(file);
                            if(img==null)return;
                            if(img.getWidth()!=img.getHeight()){
                                JOptionPane.showMessageDialog(null, "Image is not square!", "Error loading image", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            block.setTexture(img);
                        }catch(IOException ex){}
                    }
                });
                chooser.showOpenDialog(null);
            }).start();
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
        drawRect(0, Display.getHeight()/numComps, Display.getHeight()/numComps, Display.getHeight()/numComps*2, Core.getTexture(block.texture));
        active.width = cooling.width = Display.getWidth()*.75;
        active.x = cooling.x = Display.getWidth()-cooling.width;
        moderator.width = fuelCell.width = name.width = rules.width = back.width = Display.getWidth();
        texture.x = texture.height = active.height = moderator.height = fuelCell.height = cooling.height = name.height = rules.height = back.height = Display.getHeight()/numComps;
        texture.width = Display.getWidth()-texture.x;
        texture.y = name.height;
        cooling.y = texture.y+texture.height;
        fuelCell.y = cooling.y+cooling.height;
        moderator.y = fuelCell.y+fuelCell.height;
        active.y = moderator.y+moderator.height;
        rules.y = active.y+active.height;
        back.y = Display.getHeight()-back.height;
        Core.applyColor(Core.theme.getTextColor());
        drawText(0, Display.getHeight()/numComps*2, Display.getWidth()*.25, Display.getHeight()/numComps*3, "Cooling");
        drawText(0, Display.getHeight()/numComps*5, Display.getWidth()*.25, Display.getHeight()/numComps*6, "Active");
        Core.applyWhite();
        super.render(millisSinceLastTick);
    }
}