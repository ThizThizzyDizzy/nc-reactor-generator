package planner.menu.configuration.overhaul.turbine;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import planner.Core;
import multiblock.configuration.overhaul.turbine.Blade;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistOptionButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuBladeConfiguration extends Menu{
    private final MenuComponentMinimalistTextBox name = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "Name", true));
    private final MenuComponentMinimalistButton texture = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Select Texture", true, true));
    private final MenuComponentMinimalistTextBox efficiency = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistTextBox expansion = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistOptionButton stator = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Stator", true, true, 0, "FALSE", "TRUE"));
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