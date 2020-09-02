package planner.menu.configuration.overhaul.turbine;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import planner.Core;
import multiblock.configuration.overhaul.turbine.Coil;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistOptionButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuCoilConfiguration extends Menu{
    private final MenuComponentMinimalistTextBox name = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "Name", true));
    private final MenuComponentMinimalistButton texture = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Select Texture", true, true));
    private final MenuComponentMinimalistTextBox efficiency = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistOptionButton connector = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Connector", true, true, 0, "FALSE", "TRUE"));
    private final MenuComponentMinimalistOptionButton bearing = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Bearing", true, true, 0, "FALSE", "TRUE"));
    private final MenuComponentMinimalistButton rules = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Placement Rules", true, true));
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private final Coil coil;
    private final int numComps = 16;
    public MenuCoilConfiguration(GUI gui, Menu parent, Coil coil){
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
                            coil.setTexture(img);
                        }catch(IOException ex){}
                    }
                });
                chooser.showOpenDialog(null);
            }).start();
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
        drawRect(0, Core.helper.displayHeight()/numComps, Core.helper.displayHeight()/numComps, Core.helper.displayHeight()/numComps*2, Core.getTexture(coil.texture));
        efficiency.width = Core.helper.displayWidth()*.75;
        efficiency.x = Core.helper.displayWidth()-efficiency.width;
        bearing.width = connector.width = name.width = rules.width = back.width = Core.helper.displayWidth();
        texture.x = texture.height = bearing.height = connector.height = efficiency.height = name.height = rules.height = back.height = Core.helper.displayHeight()/numComps;
        texture.width = Core.helper.displayWidth()-texture.x;
        texture.y = name.height;
        efficiency.y = texture.y+texture.height;
        connector.y = efficiency.y+efficiency.height;
        bearing.y = connector.y+connector.height;
        rules.y = bearing.y+bearing.height;
        back.y = Core.helper.displayHeight()-back.height;
        Core.applyColor(Core.theme.getTextColor());
        drawText(0, Core.helper.displayHeight()/numComps*2, Core.helper.displayWidth()*.25, Core.helper.displayHeight()/numComps*3, "Efficiency");
        Core.applyWhite();
        super.render(millisSinceLastTick);
    }
}