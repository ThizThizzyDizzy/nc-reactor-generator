package planner.menu.configuration.overhaul.fissionmsr;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import planner.Core;
import multiblock.configuration.overhaul.fissionmsr.Block;
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
    private final MenuComponentMinimalistButton texture = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Select Texture", true, true).setTooltip("Change the texture for this block"));
    private final MenuComponentMinimalistButton closedTexture = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Select Closed Texture", true, true).setTooltip("Change the closed texture for this block\nThis texture is displayed when a neutron shield is closed"));
    private final MenuComponentMinimalistTextBox cooling = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter()).setTooltip("The amount of cooling the block provides as a heater");
    private final MenuComponentMinimalistTextBox input = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true)).setTooltip("The fluid this block takes as an input\nFor Heaters ONLY (to specify coolant type)");
    private final MenuComponentMinimalistTextBox output = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true)).setTooltip("The fluid this block takes as an input\nFor Heaters ONLY (to specify coolant type)");
    private final MenuComponentMinimalistOptionButton cluster = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Can Cluster", true, true, 0, "FALSE", "TRUE")).setTooltip("If true, this block can be part of a cluster");
    private final MenuComponentMinimalistOptionButton createCluster = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Creates Cluster", true, true, 0, "FALSE", "TRUE")).setTooltip("If true, this block will create a cluster");
    private final MenuComponentMinimalistOptionButton conductor = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Conductor", true, true, 0, "FALSE", "TRUE")).setTooltip("If true, this block will connect clusters to the casing, but will not connect them together");
    private final MenuComponentMinimalistOptionButton fuelVessel = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Fuel Vessel", true, true, 0, "FALSE", "TRUE"));
    private final MenuComponentMinimalistOptionButton reflector = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Reflector", true, true, 0, "FALSE", "TRUE"));
    private final MenuComponentMinimalistOptionButton irradiator = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Irradiator", true, true, 0, "FALSE", "TRUE"));
    private final MenuComponentMinimalistOptionButton moderator = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Moderator", true, true, 0, "FALSE", "TRUE"));
    private final MenuComponentMinimalistOptionButton activeModerator = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Active Moderator", true, true, 0, "FALSE", "TRUE")).setTooltip("If false, this block will never be treated as an active moderator for placement rules");
    private final MenuComponentMinimalistOptionButton shield = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Neutron Shield", true, true, 0, "FALSE", "TRUE"));
    private final MenuComponentMinimalistTextBox flux = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter()).setTooltip("The amount of neutron flux this block provides");
    private final MenuComponentMinimalistTextBox efficiency = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistTextBox reflectivity = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setFloatFilter());
    private final MenuComponentMinimalistTextBox heatMult = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "", true).setIntFilter());
    private final MenuComponentMinimalistOptionButton blocksLOS = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Block Line of Sight", true, true, 0, "FALSE", "TRUE")).setTooltip("If true, this block will block line of sight for neutron sources");
    private final MenuComponentMinimalistOptionButton functional = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Functional", true, true, 0, "FALSE", "TRUE")).setTooltip("If true, this block will count against the sparsity penalty");
    private final MenuComponentMinimalistButton rules = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Placement Rules", true, true).setTooltip("Add, remove, or modify placement rules"));
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private final Block block;
    private final int numComps = 23;
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
                            Sys.error(ErrorLevel.minor, "Image is not square!", null, ErrorCategory.fileIO);
                        }
                        return;
                    }
                    block.setTexture(img);
                }catch(IOException ex){}
            }, FileFormat.PNG);
        });
        closedTexture.addActionListener((e) -> {
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
                    block.setClosedTexture(img);
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
        input.text = block.input==null?"":block.input;
        output.text = block.output==null?"":block.output;
        cluster.setIndex(block.cluster?1:0);
        createCluster.setIndex(block.createCluster?1:0);
        conductor.setIndex(block.conductor?1:0);
        fuelVessel.setIndex(block.fuelVessel?1:0);
        reflector.setIndex(block.reflector?1:0);
        irradiator.setIndex(block.irradiator?1:0);
        moderator.setIndex(block.moderator?1:0);
        activeModerator.setIndex(block.activeModerator?1:0);
        closedTexture.enabled = block.shield;
        shield.setIndex(block.shield?1:0);
        flux.text = block.flux+"";
        efficiency.text = block.efficiency+"";
        reflectivity.text = block.reflectivity+"";
        heatMult.text = block.heatMult+"";
        blocksLOS.setIndex(block.blocksLOS?1:0);
        functional.setIndex(block.functional?1:0);
    }
    @Override
    public void onGUIClosed(){
        block.name = name.text;
        block.cooling = Integer.parseInt(cooling.text);
        block.input = input.text.trim().isEmpty()?null:input.text;
        block.output = output.text.trim().isEmpty()?null:output.text;
        block.cluster = cluster.getIndex()==1;
        block.createCluster = createCluster.getIndex()==1;
        block.conductor = conductor.getIndex()==1;
        block.fuelVessel = fuelVessel.getIndex()==1;
        block.reflector = reflector.getIndex()==1;
        block.irradiator = irradiator.getIndex()==1;
        block.moderator = moderator.getIndex()==1;
        block.activeModerator = activeModerator.getIndex()==1;
        block.shield = shield.getIndex()==1;
        block.flux = Integer.parseInt(flux.text);
        block.efficiency = Float.parseFloat(efficiency.text);
        block.reflectivity = Float.parseFloat(reflectivity.text);
        block.heatMult = Integer.parseInt(heatMult.text);
        block.blocksLOS = blocksLOS.getIndex()==1;
        block.functional = functional.getIndex()==1;
    }
    @Override
    public void render(int millisSinceLastTick){
        drawRect(0, gui.helper.displayHeight()/numComps, gui.helper.displayHeight()/numComps, gui.helper.displayHeight()/numComps*2, Core.getTexture(block.texture));
        if(block.closedTexture!=null)drawRect(0, gui.helper.displayHeight()/numComps*2, gui.helper.displayHeight()/numComps, gui.helper.displayHeight()/numComps*3, Core.getTexture(block.closedTexture));
        cluster.enabled = conductor.getIndex()==0;
        conductor.enabled = cluster.getIndex()==0;
        createCluster.enabled = cluster.getIndex()==1;
        activeModerator.enabled = moderator.getIndex()==1;
        flux.editable = moderator.getIndex()==1||shield.getIndex()==1;
        efficiency.editable = moderator.getIndex()==1||shield.getIndex()==1||reflector.getIndex()==1;
        reflectivity.editable = reflector.getIndex()==1;
        heatMult.editable = shield.getIndex()==1;
        fuelVessel.enabled = reflector.getIndex()==0;
        reflector.enabled = fuelVessel.getIndex()==0;
        input.width = output.width = cooling.width = flux.width = efficiency.width = reflectivity.width = heatMult.width = gui.helper.displayWidth()*.75;
        input.x = output.x = cooling.x = flux.x = efficiency.x = reflectivity.x = heatMult.x = gui.helper.displayWidth()-cooling.width;
        functional.width = blocksLOS.width = shield.width = activeModerator.width = moderator.width = irradiator.width = reflector.width = fuelVessel.width = conductor.width = createCluster.width = cluster.width = name.width = rules.width = back.width = gui.helper.displayWidth();
        closedTexture.x = closedTexture.height = texture.x = texture.height = functional.height = blocksLOS.height = heatMult.height = reflectivity.height = efficiency.height = flux.height = shield.height = activeModerator.height = moderator.height = irradiator.height = reflector.height = fuelVessel.height = conductor.height = createCluster.height = cluster.height = input.height = output.height = cooling.height = name.height = rules.height = back.height = gui.helper.displayHeight()/numComps;
        closedTexture.width = texture.width = gui.helper.displayWidth()-texture.x;
        texture.y = name.height;
        closedTexture.y = texture.y+texture.height;
        cooling.y = closedTexture.y+closedTexture.height;
        input.y = cooling.y+cooling.height;
        output.y = input.y+input.height;
        cluster.y = output.y+output.height;
        createCluster.y = cluster.y+cluster.height;
        conductor.y = createCluster.y+createCluster.height;
        fuelVessel.y = conductor.y+conductor.height;
        reflector.y = fuelVessel.y+fuelVessel.height;
        irradiator.y = reflector.y+reflector.height;
        moderator.y = irradiator.y+irradiator.height;
        activeModerator.y = moderator.y+moderator.height;
        shield.y = activeModerator.y+activeModerator.height;
        flux.y = shield.y+shield.height;
        efficiency.y = flux.y+flux.height;
        reflectivity.y = efficiency.y+efficiency.height;
        heatMult.y = reflectivity.y+reflectivity.height;
        blocksLOS.y = heatMult.y+heatMult.height;
        functional.y = blocksLOS.y+blocksLOS.height;
        rules.y = functional.y+functional.height;
        back.y = gui.helper.displayHeight()-back.height;
        Core.applyColor(Core.theme.getTextColor());
        drawText(0, gui.helper.displayHeight()/numComps*3, gui.helper.displayWidth()*.25, gui.helper.displayHeight()/numComps*4, "Cooling");
        drawText(0, gui.helper.displayHeight()/numComps*4, gui.helper.displayWidth()*.25, gui.helper.displayHeight()/numComps*5, "Input Fluid");
        drawText(0, gui.helper.displayHeight()/numComps*5, gui.helper.displayWidth()*.25, gui.helper.displayHeight()/numComps*6, "Output Fluid");
        drawText(0, gui.helper.displayHeight()/numComps*15, gui.helper.displayWidth()*.25, gui.helper.displayHeight()/numComps*16, "Neutron Flux");
        drawText(0, gui.helper.displayHeight()/numComps*16, gui.helper.displayWidth()*.25, gui.helper.displayHeight()/numComps*17, "Efficiency");
        drawText(0, gui.helper.displayHeight()/numComps*17, gui.helper.displayWidth()*.25, gui.helper.displayHeight()/numComps*18, "Reflectivity");
        drawText(0, gui.helper.displayHeight()/numComps*18, gui.helper.displayWidth()*.25, gui.helper.displayHeight()/numComps*19, "Heat Multiplier");
        Core.applyWhite();
        super.render(millisSinceLastTick);
    }
}