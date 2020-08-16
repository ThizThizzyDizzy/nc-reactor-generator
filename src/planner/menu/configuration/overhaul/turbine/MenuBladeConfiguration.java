package planner.menu.configuration.overhaul.turbine;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.lwjgl.opengl.Display;
import planner.Core;
import multiblock.configuration.overhaul.turbine.Blade;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuBladeConfiguration extends Menu{
    private final MenuComponentMinimalistTextBox name = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "Name", true));
    private final MenuComponentMinimalistButton texture = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Select Texture", true, true));
    private final MenuComponentMinimalistTextBox efficiency = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistTextBox expansion = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private final Blade blade;
    private final int numComps = 16;
    public MenuBladeConfiguration(GUI gui, Menu parent, Blade blade){
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
                            blade.setTexture(img);
                        }catch(IOException ex){}
                    }
                });
                chooser.showOpenDialog(null);
            }).start();
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
    }
    @Override
    public void onGUIClosed(){
        blade.name = name.text;
        blade.efficiency = Float.parseFloat(efficiency.text);
        blade.expansion = Float.parseFloat(expansion.text);
        super.onGUIClosed();
    }
    @Override
    public void render(int millisSinceLastTick){
        drawRect(0, Display.getHeight()/numComps, Display.getHeight()/numComps, Display.getHeight()/numComps*2, Core.getTexture(blade.texture));
        efficiency.width = expansion.width = Display.getWidth()*.75;
        efficiency.x = expansion.x = Display.getWidth()-efficiency.width;
        name.width = back.width = Display.getWidth();
        texture.x = texture.height = efficiency.height = expansion.height = name.height = back.height = Display.getHeight()/numComps;
        texture.width = Display.getWidth()-texture.x;
        texture.y = name.height;
        efficiency.y = texture.y+texture.height;
        expansion.y = efficiency.y+efficiency.height;
        back.y = Display.getHeight()-back.height;
        Core.applyColor(Core.theme.getTextColor());
        drawText(0, Display.getHeight()/numComps*2, Display.getWidth()*.25, Display.getHeight()/numComps*3, "Efficiency");
        drawText(0, Display.getHeight()/numComps*3, Display.getWidth()*.25, Display.getHeight()/numComps*4, "Expansion");
        Core.applyWhite();
        super.render(millisSinceLastTick);
    }
}